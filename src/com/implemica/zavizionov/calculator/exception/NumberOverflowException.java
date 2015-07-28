package com.implemica.zavizionov.calculator.exception;

/**
 * Created by Suff on 28.07.2015.
 */
public class NumberOverflowException extends Exception{
    public NumberOverflowException(String message){
        super(message);
    }

    public NumberOverflowException(){
        super();
    }
}
