package com.ee.eval.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class EESCExceptionHandler extends ResponseEntityExceptionHandler {


    private class ErrorMessage {
        private String message;
        private String furtherActions;

        public ErrorMessage(String message, String furtherActions) {
            this.message = message;
            this.furtherActions = furtherActions;
        }

        public String getMessage() {
            return message;
        }

        public String getFurtherActions() {
            return furtherActions;
        }
    }

    @ExceptionHandler(EESCInputException.class)
    public final ResponseEntity<ErrorMessage> inputExceptionHandler(EESCInputException EESCInputException) {
        ErrorMessage errorMessage = new ErrorMessage(EESCInputException.getMessage(),
                "Please provide correct input");

        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EESCInternalException.class)
    public final ResponseEntity<ErrorMessage> internalExceptionHandler(EESCInternalException EESCInternalException) {
        ErrorMessage errorMessage = new ErrorMessage(EESCInternalException.getMessage(),
                "We Apologise for this. Kindly contact Support at <+++>");

        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EESCDaoException.class)
    public final ResponseEntity<ErrorMessage> daoExceptionHandler(EESCDaoException EESCDaoException) {
        ErrorMessage errorMessage = new ErrorMessage(EESCDaoException.getMessage(),
                "Inernal Server Error while retrieveing Data, Contact Support at <+++>");

        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorMessage> defaultExceptionHandler(Exception exception) {
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(),
                "Unknown error occurred, Kindly contact Support at <+++>");

        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
