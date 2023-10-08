package com.cirt.ctf.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/error")
public class AppErrorController implements ErrorController {

    @RequestMapping(produces = "text/html")
    public String errorHtml(HttpServletRequest request, Model model, Exception ex) {
        HttpStatus status = getStatus(request);
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    @RequestMapping
    public  ResponseEntity<?>  handleNoHandlerFound(RuntimeException exception, HttpServletRequest request) {
        System.out.println("inside rst error...");
        var guid = UUID.randomUUID().toString();
        HttpStatus status= getStatus(request);
        var response = new ApiErrorResponse(
                guid,
                exception.getMessage(),
                status.value(),
                status.name(),
                request.getRequestURI(),
                request.getMethod(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, status);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception e) {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }
}

