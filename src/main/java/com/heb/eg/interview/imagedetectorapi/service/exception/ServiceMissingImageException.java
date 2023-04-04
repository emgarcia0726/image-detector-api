package com.heb.eg.interview.imagedetectorapi.service.exception;

public class ServiceMissingImageException extends Exception{
    public ServiceMissingImageException(final String msg){
        super(msg);
    }

    public ServiceMissingImageException(final String msg, final Throwable throwable){
        super(msg, throwable);
    }
}
