package com.banca.project.exception;

import lombok.Getter;

@Getter
public class GetBalanceBadRequestException extends BadRequestException {
    public GetBalanceBadRequestException(String message, String code) {
        super(message, code);
        this.code = code;
    }
}
