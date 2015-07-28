package com.implemica.zavizionov.calculator.exception;

/**
 * Created by Suff on 28.07.2015.
 */
public class DivideByZeroException extends Exception {
    public DivideByZeroException(String message){
        super(message);
    }

    public DivideByZeroException(){
        super();
    }
}
