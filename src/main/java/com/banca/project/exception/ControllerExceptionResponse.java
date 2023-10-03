package com.banca.project.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ControllerExceptionResponse {
    private final String message;
    private final String code;
    private List<DetailError> detailErrorList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final ZonedDateTime dateTime;
}
