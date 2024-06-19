package com.samichlaus.api.exception;

import java.io.Serial;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class IllegalCSVFileException extends Exception {
  @Serial private static final long serialVersionUID = 4L;

  public IllegalCSVFileException(String message) {
    super(message);
  }
}
