package com.ee.eval.exception;

import org.springframework.dao.DataAccessException;

public class EESCDaoException extends DataAccessException {
    public EESCDaoException(String messsage) {
        super(messsage);
    }
}
