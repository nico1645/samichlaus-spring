package com.samichlaus.api.exception;

import java.io.Serial;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InternalServerErrorException extends Exception {

  @Serial private static final long serialVersionUID = 1L;

  public InternalServerErrorException(String message) {
    super(message);
  }
}
