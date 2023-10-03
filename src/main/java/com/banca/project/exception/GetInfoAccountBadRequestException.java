package com.banca.project.exception;

import lombok.Getter;

@Getter
public class GetInfoAccountBadRequestException extends BadRequestException {
    public GetInfoAccountBadRequestException(String message, String code) {
        super(message, code);
        this.code = code;
    }
}
