package com.heb.eg.interview.imagedetectorapi.service.exception;

/**
 * This exception is thrown from the service layer if the third party image object detection service is down.
 */
public class ServiceDetectorDownException extends ServiceException{
    public ServiceDetectorDownException(final String msg){
        super(msg);
    }

    public ServiceDetectorDownException(final String msg, final Throwable throwable){
        super(msg, throwable);
    }
}
