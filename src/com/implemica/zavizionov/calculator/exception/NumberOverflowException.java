package com.implemica.zavizionov.calculator.exception;

/**
 * Occurs when resulting number overflows the max scale.
 * @author Zavizionov Andrii
 */
public class NumberOverflowException extends Exception{
    public NumberOverflowException(String message){
        super(message);
    }

    public NumberOverflowException(){
        super();
    }
}
