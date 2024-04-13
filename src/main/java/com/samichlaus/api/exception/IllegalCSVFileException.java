package com.samichlaus.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class IllegalCSVFileException extends Exception {
    @Serial
    private static final long serialVersionUID = 4L;

    public IllegalCSVFileException(String message) {
        super(message);
    }
}
