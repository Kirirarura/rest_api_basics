package com.epam.esm.exceptions;

/**
 * An Exception that thrown in case Dao layer encounters DataAccessException while processing request to DB.
 */
public class DaoException extends Exception{
    public DaoException() {
    }
    public DaoException(String messageCode) {
        super(messageCode);
    }

    public DaoException(String messageCode, Throwable cause) {
        super(messageCode, cause);
    }

    public DaoException(Throwable cause) {
        super(cause);
    }
}
