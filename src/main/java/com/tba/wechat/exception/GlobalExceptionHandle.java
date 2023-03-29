package com.tba.wechat.exception;

import com.tba.wechat.web.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * <p>
 * 全局异常处理
 * </p>
 *
 * @author 1050696985@qq.com
 * @since 2023-3-28 10:47
 */

@RestControllerAdvice
public class GlobalExceptionHandle {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandle.class);

    @ExceptionHandler(CustomException.class)
    public Result businessException(CustomException e) {
        LOGGER.info("GlobalExceptionHandle: {}", e.getMessage());
        if (e.getCode() == null) {
            return Result.error(e.getMessage());
        }
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Result handlerNoFoundException(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return Result.error(HttpStatus.NOT_FOUND.value(), "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result validExceptionHandler(MethodArgumentNotValidException e) {
        LOGGER.error(e.getMessage(), e);
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError == null ? "" : fieldError.getDefaultMessage();
        return Result.error(message);
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return Result.error(e.getMessage());
    }

}
