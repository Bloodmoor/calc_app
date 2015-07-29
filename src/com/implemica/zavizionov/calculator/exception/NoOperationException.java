package com.implemica.zavizionov.calculator.exception;

/**
 * Occurs when trying to get result of operation
 * when operation is not set.
 * @author Zavizionov Andrii
 */
public class NoOperationException extends Throwable {
    public NoOperationException(String message) {
        super(message);
    }
    public NoOperationException(){
        super();
    }
}
