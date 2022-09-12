package com.infotech.docyard.exceptions;

@SuppressWarnings("serial")
public class NoDataFoundException extends RuntimeException {

    String message = "Data not found";

    public NoDataFoundException() {
        super();
    }

    public NoDataFoundException(String message) {
        super(message);
        this.message = message;
    }

    public NoDataFoundException(Throwable cause) {
        super(cause);
    }

    public NoDataFoundException(String message, Throwable cause) {
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
