package com.knox.kaleidoscope.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import androidx.annotation.NonNull;

import com.knox.kaleidoscope.CameraError;
import com.knox.kaleidoscope.CameraUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.knox.kaleidoscope.CameraUtil.generateTimestamp;

/**
 * @author Knox.Tsang
 * @time 2019/3/14  12:06
 * @desc ${TODD}
 */

public class CamT {

    private static final String TAG = "CamT";

    /**
     * Camera state: Device is closed.
     */
    private static final int STATE_CLOSED = 0;

    /**
     * Camera state: Device is opened, but is not capturing.
     */
    private static final int STATE_OPENED = 1;

    /**
     * Camera state: Showing camera preview.
     */
    private static final int STATE_PREVIEW = 2;

    /**
     * Camera state: Waiting for 3A convergence before capturing a photo.
     */
    private static final int STATE_WAITING_FOR_3A_CONVERGENCE = 3;

    /**
     * 预设Preview的Size是1280x960, 这是通过google demo获取的
     */
    private static final int PREFER_PREVIEW_WIDTH = 1280;
    private static final int PREFER_PREVIEW_HEIGHT = 960;

    /**
     * 预设Record的Size是800x600, 这是通过google demo获取的
     */
    private static final int PREFER_RECORD_WIDTH = 800;
    private static final int PREFER_RECORD_HEIGHT = 600;

    /**
     * Timeout for the pre-capture sequence.
     */
    private static final long PRECAPTURE_TIMEOUT_MS = 1000;

    private final Context mContext;
    private TextureView mTextureView;

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     * This is used for all callbacks from the {@link android.hardware.camera2.CameraDevice} and {@link android.hardware.camera2.CameraCaptureSession}s
     */
    private HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    /**
     * A lock protecting camera state.
     */
    private final Object mCameraStateLock = new Object();

    // 由于同时存在UI-thread和background-thread, 所以需要保证两条thread同时会用到变量线程同步
    //----------------------------------------------------------------------------------------------
    // State protected by mCameraStateLock.
    //
    // The following state is used across both the UI and background threads. Methods with "Locked"
    // in the name expect mCameraStateLock to be held while calling.

    /**
     * ID of the current {@link android.hardware.camera2.CameraDevice}
     */
    private String mCameraId;

    /**
     * The {@link CameraCharacteristics} for the currently configured camera device.
     */
    private CameraCharacteristics mCharacteristics;

    /**
     * A reference to the open {@link CameraDevice}
     */
    private CameraDevice mCameraDevice;

    /**
     * A {@link CameraCaptureSession} for camera preview.
     */
    private CameraCaptureSession mCaptureSession;

    /**
     * The state of the camera device.
     */
    private int mState = STATE_CLOSED;

    /**
     * A reference counted holder wrapping the {@link ImageReader} that handles JPEG image
     * captures. This is used to allow us to clean up the {@link ImageReader} when all background
     * tasks using its {@link android.media.Image}s have completed.
     */
    private CameraUtil.RefCountedAutoCloseable<ImageReader> mJpegImageReader;

    /**
     * MediaRecorder
     */
    private MediaRecorder mMediaRecorder;

    private String mNextVideoAbsolutePath;

    /**
     * Number of pending user requests to capture a photo.
     */
    private int mPendingUserCaptures = 0;

    /**
     * Timer to use with pre-capture sequence to ensure a timely capture if 3A convergence is taking
     * too long.
     */
    private long mCaptureTimer;

    private boolean mRecording = false;

    private CaptureRequest.Builder mRecodeBuilder;

    private CameraCaptureSession mRecordSession;

    // 回调
    //----------------------------------------------------------------------------------------------
    /**
     * 所有回调都是在background-thread中
     *
     * {@link CameraDevice.StateCallback} is called when the currently active {@link CameraDevice}
     * changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            // This method is called when the camera is opened. We start camera preview here if
            // the TextureView displaying this has been set up.
            synchronized (mCameraStateLock) {
                mCameraDevice = camera;
                mState = STATE_OPENED;
                createCameraPreviewSessionLocked();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            synchronized (mCameraStateLock) {
                mState = STATE_CLOSED;
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            synchronized (mCameraStateLock) {
                mState = STATE_CLOSED;
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }
    };

    private final ImageReader.OnImageAvailableListener mOnJpegImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            synchronized (mCameraStateLock) {
                // 等待onImageAvailable回调, 说明抓图成功, 避免应用进入退出时, 而reader又在写文件, reader意外关掉
                // 这里使用mJpegImageReader替代回调返回的reader
                if (mJpegImageReader == null || mJpegImageReader.getAndRetain() == null) {
                    Log.e(TAG, "onImageAvailable: Paused the activity before we could save the image, ImageReader already closed.");
                    return;
                }
                final Image image = mJpegImageReader.get().acquireNextImage();
                if (image == null) {
                    Log.e(TAG, "onImageAvailable: can't acquire image");
                    return;
                }
                // TODO 避免线程太多, 应该用线程池, 任务也应用放到队列里边
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String currentDateTime = generateTimestamp();
                        File jpegFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                                "JPEG_" + currentDateTime + ".jpg");
                        // 有了image和file, 开始写文件, 且减少reader的引用
                        if (dumpJpegToFile(jpegFile, image)) {
                            Log.e(TAG, "run: dumpJpeg success [path]=[" + jpegFile.getAbsolutePath() + "]");
                        }
                        mJpegImageReader.close();
                    }
                }).start();
            }
        }
    };

    private final CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            synchronized (mCameraStateLock) {
                switch (mState) {
                    case STATE_WAITING_FOR_3A_CONVERGENCE:
                        // 等待auto-focus, auto-exposure, auto-white-balance结束
                        boolean readyToCapture = false;

                        // It we have hit our maximum wait timeout, too bad! Begin capture anyway.
                        if (!hitTimeoutLocked()) {
                            readyToCapture = true;
                        } else {
                            // 如果AF的状态是LOCKED, 说明已经完成
                            Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                            if (afState == null) {
                                break;
                            }
                            readyToCapture = (afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED
                                    || afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED);

                            // 如果AE的状态是CONVERGED, 说明已经完成
                            Integer aeSate = result.get(CaptureResult.CONTROL_AE_STATE);
                            if (aeSate == null) {
                                break;
                            }
                            readyToCapture = readyToCapture && (aeSate == CaptureResult.CONTROL_AE_STATE_CONVERGED);

                            // 如果AWB的状态是CONVERGED, 说明已经完成
                            Integer awbState = result.get(CaptureResult.CONTROL_AWB_STATE);
                            if (awbState == null) {
                                break;
                            }
                            readyToCapture = readyToCapture && (awbState == CaptureResult.CONTROL_AWB_STATE_CONVERGED);
                        }

                        if (readyToCapture && mPendingUserCaptures > 0) {
                            while (mPendingUserCaptures > 0) {
                                captureStillPictureLocked();
                                mPendingUserCaptures--;
                            }
                            // After this, the camera will go back to the normal state of preview.
                            mState = STATE_PREVIEW;
                        }

                        break;
                }
            }
        }

        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session,
                @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            Log.e(TAG, "onCaptureStarted: ");
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                @NonNull CaptureRequest request,
                @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                @NonNull CaptureRequest request,
                @NonNull TotalCaptureResult result) {
            process(result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session,
                @NonNull CaptureRequest request,
                @NonNull CaptureFailure failure) {
            Log.e(TAG, "onCaptureFailed: ");
            super.onCaptureFailed(session, request, failure);
        }
    };

    //----------------------------------------------------------------------------------------------

    public CamT(Context context) {
        mContext = context;
    }

    public CameraError prepare(@NonNull TextureView textureView) {
        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        if (manager == null) {
            return new CameraError(false, "Can't get Camera service");
        }

        try {
            for (String cameraId : manager.getCameraIdList()) {
                // 先选第一个Camera
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                if (TextUtils.isEmpty(mCameraId)) {
                    mCameraId = cameraId;
                    mCharacteristics = characteristics;
                }

                // 如果有后置Camera, 就选择它
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && CameraCharacteristics.LENS_FACING_FRONT == facing) {
                    mCameraId = cameraId;
                    mCharacteristics = characteristics;
                }
            }

            if (TextUtils.isEmpty(mCameraId)) {
                return new CameraError(false, "Device haven't one Camera");
            }

            // 预览用
            mTextureView = textureView;

            // 拍照用
            prepareJpegReader();

            return new CameraError();
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return new CameraError(false, "CameraAccessException");
        }
    }

    @SuppressLint("MissingPermission")
    public CameraError open() {
        startBackgroundThread();

        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId;
            Handler backgroundHandler;
            synchronized (mCameraStateLock) {
                cameraId = mCameraId;
                backgroundHandler = mBackgroundHandler;
            }

            // Attempt to open the camera. mStateCallback will be called on the background handler's
            // thread when this succeeds or fails.
            manager.openCamera(cameraId, mStateCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return new CameraError();
    }

    public CameraError close() {
        stopBackgroundThread();

        synchronized (mCameraStateLock) {
            if (mCaptureSession != null) {
                // 在CameraCaptureSession.StateCallback#onConfigured中创建
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }

        return new CameraError();
    }

    /**
     * Initiate a still image capture.
     *
     * This function sends a capture request that initiates a pre-capture sequence in our state
     * machine that waits for auto-focus to finish, ending in a "locked" state where the len is no
     * longer moving, waits for auto-exposure to choose a good exposure value, and waits for
     * auto-white-balance to converge.
     */
    public void takePicture() {
        synchronized (mCameraStateLock) {
            mPendingUserCaptures++;

            // If we already triggered a pre-capture sequence, or are in a state where we cannot do
            // this, return immediately.
            if (mState != STATE_PREVIEW) {
                return;
            }

            try {
                // 在预览请求的基础上, 添加自动对焦, 自动曝光, 自动白平衡请求
                CaptureRequest.Builder previewRequestBuilder = createPreviewRequestBuilder(mCameraDevice, createPreviewSurfaceLocked(mTextureView));
                // 添加AF请求
                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);

                // 添加AE请求
                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);

                // 添加AWB请求
                previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);

                // Update state machine to wait for auto-focus, auto-exposure, and auto-white-balance (aka. "3A") to converge.
                mState = STATE_WAITING_FOR_3A_CONVERGENCE;

                // Start a timer for the pre-capture sequence.
                startTimerLocked();

                // Replace the existing repeating request with one with updated 3A triggers.
                // 替代原先仅仅预览的请求, 向会话发出预览加3A的请求
                mCaptureSession.capture(previewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void startRecordingVideo() {
        synchronized (mCameraStateLock) {
            if (null == mCameraDevice || !mTextureView.isAvailable()) {
                return;
            }
            if (!mRecording) {
                mRecording = true;
            }
            try {
                // 关闭预览会话
                closePreviewSessionLocked();
                // 准备MediaRecorder用来编码和封装
                setUpMediaRecorder();

                // 创建录制请求
                SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
                surfaceTexture.setDefaultBufferSize(PREFER_PREVIEW_WIDTH, PREFER_PREVIEW_HEIGHT);

                mRecodeBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                List<Surface> surfaces = new ArrayList<>();

                // Set up Surface for the camera preview.
                Surface previewSurface = createPreviewSurfaceLocked(mTextureView);
                surfaces.add(previewSurface);
                mRecodeBuilder.addTarget(previewSurface);

                // Set up Surface for the MediaRecorder.
                Surface recorderSurface = mMediaRecorder.getSurface();
                surfaces.add(recorderSurface);
                mRecodeBuilder.addTarget(recorderSurface);

                // Start a capture session
                // Once the session starts, we can update the UI and start recording.
                // 创建录制会话
                mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        synchronized (mCameraStateLock) {
                            mRecordSession = session;
                            // 向录制会话发送录制请求
                            updatePreviewLocked();
                            // TODO 回调到外面, 如果必须是主线程的话
                            // getActivity().runOnUiThread(new Runnable() {
                            //     @Override
                            //     public void run() {
                            //         // Start recording
                            //         // 启动MediaRecorder
                            //         mMediaRecorder.start();
                            //     }
                            // });
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                    }
                }, mBackgroundHandler);

            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopRecordingVideo() {
        synchronized (mCameraStateLock) {
            // Stop recording
            // 释放MediaRecorder
            if (null != mMediaRecorder) {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;

                Log.e(TAG, "stopRecordingVideoLocked: Video saved: " + mNextVideoAbsolutePath);
                mNextVideoAbsolutePath = null;
            }

            // 关闭录制会话
            closeRecordSessionLocked();

            // 重新开始预览会话和发送预览请求
            startPreviewLocked();
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Starts the background thread and its {@link Handler}
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("BGD");
        mBackgroundThread.start();
        synchronized (mCameraStateLock) {
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        }
    }

    /**
     * Stops the background thread and its {@link Handler}
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            synchronized (mCameraStateLock) {
                mBackgroundHandler = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a new {@link android.hardware.camera2.CameraCaptureSession} for camera preview.
     *
     * Call this only with {@link #mCameraStateLock} held.
     */
    private void createCameraPreviewSessionLocked() {
        try {
            // 与Camera创建会话createCaptureSession, 要说明会话中有哪些surface, 譬如预览和抓图
            final Surface surface = createPreviewSurfaceLocked(mTextureView);
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mJpegImageReader.get().getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    synchronized (mCameraStateLock) {
                        // 配置成功
                        if (null == mCameraDevice) {
                            // 相机已经关闭
                            return;
                        }

                        try {
                            // 会话连接成功后, 构建预览请求, 添加目标surface
                            final CaptureRequest.Builder previewRequestBuilder = createPreviewRequestBuilder(mCameraDevice, surface);
                            // 利用会话发送预览请求
                            session.setRepeatingRequest(previewRequestBuilder.build(), null, mBackgroundHandler);
                            // CameraCaptureSession可用来截图
                            mCaptureSession = session;
                            mState = STATE_PREVIEW;
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.e(TAG, "onConfigureFailed: ");
                }
            }, mBackgroundHandler);

        } catch (CameraAccessException e) {
            Log.e(TAG, "createCameraPreviewSessionLocked: CameraAccessException");
            e.printStackTrace();
        }
    }

    private Surface createPreviewSurfaceLocked(TextureView textureView) {
        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(PREFER_PREVIEW_WIDTH, PREFER_PREVIEW_HEIGHT);
        return new Surface(surfaceTexture);
    }

    private CaptureRequest.Builder createPreviewRequestBuilder(CameraDevice cameraDevice, Surface surface) throws CameraAccessException {
        CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        builder.addTarget(surface);
        return builder;
    }

    private void prepareJpegReader() {
        // 找到JPEG格式和RAW格式的最大分辨率
        StreamConfigurationMap map = mCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map == null) {
            return;
        }

        // 使用Collections和Comparator<Size>, Arrays.asList将数组转成集合
        Size largestJpeg = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CameraUtil.CompareSizesByArea());

        synchronized (mCameraStateLock) {
            // Camera2抓图需要ImageReader, 避免ImageReader在写文件的时候, 其他流程把资源关闭, 采用RefCountedAutoCloseable包装ImageReader
            if (mJpegImageReader == null || mJpegImageReader.getAndRetain() == null) {
                mJpegImageReader = new CameraUtil.RefCountedAutoCloseable<>(ImageReader.newInstance(largestJpeg.getWidth(),
                        largestJpeg.getHeight(), ImageFormat.JPEG, 5));
            }
            mJpegImageReader.get().setOnImageAvailableListener(mOnJpegImageAvailableListener, mBackgroundHandler);
        }
    }

    private boolean dumpJpegToFile(File jpegFile, Image image) {
        boolean result = true;
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        if (buffer.remaining() == 0) {
            result = false;
        }
        buffer.get(bytes);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(jpegFile);
            output.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        } finally {
            image.close();
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private void setUpMediaRecorder() throws IOException {
        if (null == mMediaRecorder) {
            mMediaRecorder = new MediaRecorder();
        }
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
            mNextVideoAbsolutePath = getMp4FilePath(mContext);
        }
        mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
        mMediaRecorder.setVideoEncodingBitRate(10_000_000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(PREFER_RECORD_WIDTH, PREFER_RECORD_HEIGHT);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // TODO
        // int rotation = mContext.getWindowManager().getDefaultDisplay().getRotation();
        // mSensorOrientation = mCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        // switch (mSensorOrientation) {
        //     case SENSOR_ORIENTATION_DEFAULT_DEGREES:
        //         mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
        //         break;
        //     case SENSOR_ORIENTATION_INVERSE_DEGREES:
        //         mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
        //         break;
        // }
        mMediaRecorder.prepare();
    }

    private static String getMp4FilePath(Context context) {
        final File dir = context.getExternalFilesDir(null);
        return (dir == null ? "" : (dir.getAbsolutePath() + "/") + System.currentTimeMillis() + ".mp4");
    }

    /**
     * Start the timer for the pre-capture sequence.
     *
     * Call this only with {@link #mCameraStateLock} held.
     */
    private void startTimerLocked() {
        mCaptureTimer = SystemClock.elapsedRealtime();
    }

    /**
     * Check if the timer for the pre-capture sequence has been hit.
     *
     * Call this only with {@link #mCameraStateLock} held.
     *
     * @return true if the timeout occurred.
     */
    private boolean hitTimeoutLocked() {
        return (SystemClock.elapsedRealtime() - mCaptureTimer) > PRECAPTURE_TIMEOUT_MS;
    }

    /**
     * Send a capture request to the camera device that initiates a capture targeting the JPEG outputs.
     *
     * Call this only with {@link #mCameraStateLock} held.
     */
    private void captureStillPictureLocked() {
        try {
            // TODO
            // final Activity activity = getActivity();
            // if (null == activity || null == mCameraDevice) {
            //     return;
            // }

            // 创建新的请求, 用来抓图
            // 要添加ImageReader的Surface作为Target
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder = createCaptureRequestBuilder(mCameraDevice, mJpegImageReader.get().getSurface());

            // 旋转保证抓图与预览同方向
            // int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            // captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, sensorToDeviceRotation(mCharacteristics, rotation));

            // 向会话发送抓图请求
            mCaptureSession.capture(captureBuilder.build(), null, mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CaptureRequest.Builder createCaptureRequestBuilder(CameraDevice cameraDevice, Surface surface) throws CameraAccessException {
        CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        builder.addTarget(surface);
        return builder;
    }

    private void closePreviewSessionLocked() {
        if (mCaptureSession != null) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
    }

    private void updatePreviewLocked() {
        mRecodeBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            mRecordSession.setRepeatingRequest(mRecodeBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeRecordSessionLocked() {
        if (mRecordSession != null) {
            mRecordSession.close();
            mRecordSession = null;
        }
    }

    private void startPreviewLocked() {
        createCameraPreviewSessionLocked();
    }
}
