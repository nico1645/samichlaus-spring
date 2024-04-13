package com.samichlaus.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;


@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UsernameAlreadyExistsException extends Exception {

  @Serial
  private static final long serialVersionUID = 3L;

  public UsernameAlreadyExistsException(String message) {
    super(message);
  }
}
