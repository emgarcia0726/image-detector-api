package com.heb.eg.interview.imagedetectorapi.service.exception;

/**
 * This exception is thrown from the service layer when an image is unable to be found from the database.
 */
public class ServiceMissingImageException extends ServiceException{
    public ServiceMissingImageException(final String msg){
        super(msg);
    }

    public ServiceMissingImageException(final String msg, final Throwable throwable){
        super(msg, throwable);
    }
}
