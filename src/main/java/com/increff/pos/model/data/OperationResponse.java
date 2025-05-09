package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperationResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public OperationResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> OperationResponse<T> success(T data, String message) {
        return new OperationResponse<>(true, message, data);
    }

    public static <T> OperationResponse<T> failure(T data, String message) {
        return new OperationResponse<>(false, message, data);
    }

}


