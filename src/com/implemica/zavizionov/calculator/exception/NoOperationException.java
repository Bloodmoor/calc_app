package com.implemica.zavizionov.calculator.exception;

/**
 * Created by Suff on 28.07.2015.
 */
public class NoOperationException extends Throwable {
    public NoOperationException(String message) {
        super(message);
    }
    public NoOperationException(){
        super();
    }
}
