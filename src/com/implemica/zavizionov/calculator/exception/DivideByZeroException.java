package com.implemica.zavizionov.calculator.exception;

/**
 * Occurs when divide by zero operation is performed.
 * @author Zavizionov Andrii
 */
public class DivideByZeroException extends Exception {
    public DivideByZeroException(String message){
        super(message);
    }

    public DivideByZeroException(){
        super();
    }
}
