package com.zoo.boardback.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

    private int code;
    private HttpStatus status;
    private String message;
    private String field;
    private T data;

    public ApiResponse(HttpStatus status, String message, T data, String field) {
        this.code = status.value();
        this.status = status;
        this.message = message;
        this.data = data;
        this.field = field;
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, String message, T data) {
        return new ApiResponse<>(httpStatus, message, data, null);
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, T data) {
        return new ApiResponse<>(httpStatus, httpStatus.name(), data, null);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return of(HttpStatus.OK, HttpStatus.OK.name(), data);
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, String message, String field) {
        return new ApiResponse<>(httpStatus, message, null, field);
    }

    public static <T> ApiResponse<T> noContent() {
        return of(HttpStatus.NO_CONTENT, HttpStatus.NO_CONTENT.name(), null);
    }

    public static <T> ApiResponse<T> create() {
        return of(HttpStatus.CREATED, HttpStatus.CREATED.name(), null);
    }
}
