package com.infotech.docyard.um.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class DataValidationException extends RuntimeException {

    String message = "Data is not valid";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DataValidationException() {
        super();
    }

    public DataValidationException(String message) {
        super(message);
        this.message = message;
        logger.error(message);
    }

    public DataValidationException(Throwable cause) {
        super(cause);
    }

    public DataValidationException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        logger.error(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
