package com.knox.kismet;

/**
 * @author Knox.Tsang
 * @time 2019/3/12  16:22
 * @desc ${TODD}
 */

public class NativeWrapper {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib-kismet");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native String stringFromJNI();
}
