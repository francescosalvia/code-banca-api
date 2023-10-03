package com.banca.project.exception;

import lombok.Getter;

@Getter
public abstract class InternalServerErrorException extends Exception {

    protected String code;

    public InternalServerErrorException(Exception e, String code) {
        super(e);
        this.code = code;
    }

    public InternalServerErrorException(Exception e, String message, String code) {
        super(message, e);
        this.code = code;
    }

    public InternalServerErrorException(String message, String code) {
        super(message);
        this.code = code;
    }
}
