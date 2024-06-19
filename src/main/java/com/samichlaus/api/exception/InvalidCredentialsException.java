package com.samichlaus.api.exception;

import java.io.Serial;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidCredentialsException extends Exception {

  @Serial private static final long serialVersionUID = 3L;

  public InvalidCredentialsException(String message) {
    super(message);
  }
}
