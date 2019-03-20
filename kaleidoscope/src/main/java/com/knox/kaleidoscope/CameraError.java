package com.knox.kaleidoscope;

/**
 * @author Knox.Tsang
 * @time 2019/3/14  12:11
 * @desc ${TODD}
 */

public class CameraError {
    private final boolean success;
    private final String message;

    public CameraError() {
        this(true, "");
    }

    public CameraError(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return message;
    }
}
