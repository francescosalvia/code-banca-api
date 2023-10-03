package com.banca.project.exception;

import lombok.Getter;

@Getter
public class GetBalanceBadRequestException extends InternalServerErrorException {
    public GetBalanceBadRequestException(String message, String code) {
        super(message, code);
        this.code = code;
    }
}
