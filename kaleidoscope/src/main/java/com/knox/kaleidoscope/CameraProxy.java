package com.knox.kaleidoscope;

/**
 * @author Knox.Tsang
 * @time 2019/3/14  11:31
 * @desc ${TODD}
 */

public class CameraProxy {

    private final CameraType mType;

    public CameraProxy(CameraType type) {
        mType = type;
    }

    // 开启相机
    public void open() {
        switch (mType) {
            case CAMERA2:
                openCamera2();
                break;
        }
    }

    // 关闭相机
    public void close() {
        switch (mType) {
            case CAMERA2:
                closeCamera2();
                break;
        }
    }

    // 开始预览
    public void startPreview() {
        switch (mType) {
            case CAMERA2:
                startPreviewCamera2();
                break;
        }
    }

    // 停止预览
    public void stopPreview() {
        switch (mType) {
            case CAMERA2:
                stopPreviewCamera2();
                break;
        }
    }

    // 拍照
    public void capture() {
        switch (mType) {
            case CAMERA2:
                captureCamera2();
                break;
        }
    }

    // 开始录制
    public void startRecord() {
        switch (mType) {
            case CAMERA2:
                startRecordCamera2();
                break;
        }
    }

    // 停止录制
    public void stopRecord() {
        switch (mType) {
            case CAMERA2:
                stopRecordCamera2();
                break;
        }
    }

    //----------------------------------------------------------------------------------------------

    private void openCamera2() {

    }

    private void closeCamera2() {

    }

    private void startPreviewCamera2() {

    }

    private void stopPreviewCamera2() {

    }

    private void captureCamera2() {

    }

    private void startRecordCamera2() {

    }

    private void stopRecordCamera2() {

    }

}
