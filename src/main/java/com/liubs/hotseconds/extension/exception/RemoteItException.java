package com.liubs.hotseconds.extension.exception;

/**
 * 抛出这个异常，会移除这个handler
 * @author Liubsyy
 * @date 2023/7/8 9:00 PM
 ***/
public class RemoteItException extends RuntimeException {
    public RemoteItException() {
    }

    public RemoteItException(String message) {
        super(message);
    }

    public RemoteItException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteItException(Throwable cause) {
        super(cause);
    }

    public RemoteItException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
