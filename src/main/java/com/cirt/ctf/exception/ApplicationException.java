package com.cirt.ctf.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ApplicationException extends RuntimeException {
    private final String message;
    private final HttpStatus httpStatus;
}
