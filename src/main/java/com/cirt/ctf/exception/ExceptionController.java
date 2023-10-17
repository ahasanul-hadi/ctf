package com.cirt.ctf.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.DisabledException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.naming.AuthenticationException;

@ControllerAdvice
public class ExceptionController {

    @Value("${spring.servlet.multipart.max-file-size:5MB}")
    private String MAX_UPLOAD_SIZE;

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
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String fileUploadError(Model model, Exception exp){
        model.addAttribute("message", "Maximum File upload size "+MAX_UPLOAD_SIZE+" Exceeded!");
        model.addAttribute("reason",exp.getMessage());
        return "error/error";
    }




}
