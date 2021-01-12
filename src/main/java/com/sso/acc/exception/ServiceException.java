package com.sso.acc.exception;

/**
 * @author Lee
 * Date: 2020/6/5 10:11
 * Description: service exception
 */
public class ServiceException extends RuntimeException {

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable throwable) {
        super(throwable);
    }

    public ServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
