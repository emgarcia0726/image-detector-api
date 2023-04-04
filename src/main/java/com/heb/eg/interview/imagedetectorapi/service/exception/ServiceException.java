package com.heb.eg.interview.imagedetectorapi.service.exception;

/**
 * This is the base exception for any expections thrown from the service layer.
 */
public class ServiceException extends Exception{
    public ServiceException(final String msg){
        super(msg);
    }

    public ServiceException(final String msg, final Throwable throwable){
        super(msg, throwable);
    }
}
