package com.tba.wechat.exception;

import com.tba.wechat.web.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * <p>
 * 全局异常处理
 * </p>
 *
 * @author 1050696985@qq.com
 * @since 2023-3-28 10:47
 */

@ControllerAdvice
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

}
