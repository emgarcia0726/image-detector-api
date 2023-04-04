package com.heb.eg.interview.imagedetectorapi.service.exception;

/**
 * This exception is thrown from the service layer when any field from an image is invalid based on the business rules.
 */
public class ServiceInvalidImageException extends ServiceException{
    public ServiceInvalidImageException(final String msg){
        super(msg);
    }

    public ServiceInvalidImageException(final String msg, final Throwable throwable){
        super(msg, throwable);
    }
}
