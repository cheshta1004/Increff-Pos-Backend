package com.increff.pos.exception;

public class FieldErrorData {
    private String field;
    private String message;
    private String code;

    // Getters and Setters
    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}
