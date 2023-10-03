package com.banca.project.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class GenericRestEntityResponse {

  private int statusCode;
  private String statusMessage;
  private Object data;

  public static GenericRestEntityResponse ok() {
    return ok(null);
  }

  public static GenericRestEntityResponse ok(Object data) {
    return GenericRestEntityResponse.builder()
        .statusMessage(HttpStatus.OK.getReasonPhrase())
        .statusCode(HttpStatus.OK.value())
        .data(data)
        .build();
  }

  public static GenericRestEntityResponse badRequest(Object data) {
    return GenericRestEntityResponse.builder()
        .statusMessage("KO")
        .data(data)
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .build();
  }

  public static GenericRestEntityResponse internalServerError(Object data) {
    return GenericRestEntityResponse.builder()
        .statusMessage("KO")
        .data(data)
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .build();
  }
}
