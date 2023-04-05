package com.jixiebackstage.springboot.Exception;

import com.jixiebackstage.springboot.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Result handel(ServiceException se) {
        return Result.error(se.getCode(), se.getMessage());
    }
}
