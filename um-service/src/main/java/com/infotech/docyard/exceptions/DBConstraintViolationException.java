package com.infotech.docyard.exceptions;

@SuppressWarnings("serial")
public class DBConstraintViolationException extends RuntimeException {

    String message = "Record already exist";

    public DBConstraintViolationException(String message) {
        super(message);
        this.message = message;
    }

    public DBConstraintViolationException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;

    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
