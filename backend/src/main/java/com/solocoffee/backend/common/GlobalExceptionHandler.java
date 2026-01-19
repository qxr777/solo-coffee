package com.solocoffee.backend.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 处理业务异常
    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResponse<?>> handleBizException(BizException e) {
        logger.error("业务异常: {}, 错误码: {}", e.getMessage(), e.getCode(), e);
        
        ApiResponse<?> response = new ApiResponse<>(
                e.getCode(),
                e.getMessage(),
                null
        );
        
        HttpStatus status = getHttpStatusByCode(e.getCode());
        return ResponseEntity.status(status).body(response);
    }

    // 处理404异常
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        logger.error("资源不存在: {}", e.getMessage(), e);
        
        ApiResponse<?> response = new ApiResponse<>(
                ErrorCode.RESOURCE_NOT_FOUND.getCode(),
                "请求的资源不存在",
                null
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 处理其他所有异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        logger.error("系统内部错误: {}", e.getMessage(), e);
        
        ApiResponse<?> response = new ApiResponse<>(
                ErrorCode.SYSTEM_ERROR.getCode(),
                "系统内部错误，请稍后重试",
                null
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 根据错误码获取对应的HTTP状态码
    private HttpStatus getHttpStatusByCode(int code) {
        if (code >= 40000 && code < 41000) {
            return HttpStatus.BAD_REQUEST;
        } else if (code >= 41000 && code < 42000) {
            return HttpStatus.UNAUTHORIZED;
        } else if (code >= 43000 && code < 44000) {
            return HttpStatus.FORBIDDEN;
        } else if (code >= 44000 && code < 45000) {
            return HttpStatus.NOT_FOUND;
        } else if (code >= 50000) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            return HttpStatus.OK;
        }
    }
}
