package com.weizi.common.response;

import com.weizi.common.constants.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WeiZiResult> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> validationMessages = new ArrayList<>();

        // 从异常中提取验证失败的详细信息并添加到错误响应中
        ex.getBindingResult().getAllErrors().forEach(error -> {
//            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationMessages.add(errorMessage);
        });

        WeiZiResult result = WeiZiResult.error("数据异常，验证不通过！", validationMessages);
        return ResponseEntity.status(HttpStatus.SUCCESS).body(result);
    }
}
