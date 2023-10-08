package com.cirt.ctf.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ApiError {
    private  int status;
    private String message;
    private List<String> fieldErrors;
    ApiError(int status, String message) {
        this.status = status;
        this.message = message;
        this.fieldErrors= new ArrayList<>();
    }
}
