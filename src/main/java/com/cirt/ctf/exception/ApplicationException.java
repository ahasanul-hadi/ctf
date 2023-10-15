package com.cirt.ctf.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ApplicationException extends Exception{
    private String message;
    private HttpStatus status;

}
