package com.github.ompc.greys.common.di;

/**
 * DI异常
 * Created by vlinux on 16/2/2.
 */
public class DiException extends Exception {

    public DiException(String message, Throwable cause) {
        super(message, cause);
    }

    public DiException(String message) {
        super(message);
    }
}
