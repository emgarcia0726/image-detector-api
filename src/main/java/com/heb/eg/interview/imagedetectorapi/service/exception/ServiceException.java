package com.heb.eg.interview.imagedetectorapi.service.exception;

public class ServiceException extends Exception{
    public ServiceException(final String msg){
        super(msg);
    }

    public ServiceException(final String msg, final Throwable throwable){
        super(msg, throwable);
    }
}
