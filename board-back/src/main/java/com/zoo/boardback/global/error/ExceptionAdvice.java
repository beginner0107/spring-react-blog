package com.zoo.boardback.global.error;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.zoo.boardback.domain.ApiResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> bindException(BindException e) {
        List<ApiResponse<Object>> errorMessage = getErrorMessage(e);
        return ResponseEntity.status(BAD_REQUEST).body(errorMessage.get(0));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> businessException(BusinessException e) {
        return ResponseEntity.status(e.getHttpStatus())
            .body(ApiResponse.of(e.getHttpStatus(), e.getMessage(), e.getFieldName()));
    }

    private static List<ApiResponse<Object>> getErrorMessage(BindException e) {
        BindingResult bindingResult = e.getBindingResult();

        return bindingResult.getFieldErrors()
            .stream()
            .map(error -> ApiResponse.of(BAD_REQUEST, error.getDefaultMessage(), error.getField()))
            .collect(Collectors.toList());
    }
}
