package com.cirt.ctf.exception;

import org.springframework.security.authentication.DisabledException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ApplicationException.class)
    public String appError(Model model, ApplicationException exp){
        model.addAttribute("message", exp.getMessage());
        model.addAttribute("reason", "App thrown message");
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    public String generalError(Model model, Exception exp){
        model.addAttribute("message", "Some error has occurred!");
        model.addAttribute("reason",exp.getMessage());
        return "error/error";
    }

}
