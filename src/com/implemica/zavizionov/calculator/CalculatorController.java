package com.implemica.zavizionov.calculator;

import com.implemica.zavizionov.calculator.exception.DivideByZeroException;
import com.implemica.zavizionov.calculator.exception.NoOperationException;
import com.implemica.zavizionov.calculator.exception.NumberOverflowException;

import java.math.BigDecimal;

/**
 * Created by Suff on 28.07.2015.
 */
public class CalculatorController {

    Calculator calculator = new Calculator();

    private CalculatorController(){

    }

    public static CalculatorController getInstance() {
        return new CalculatorController();
    }

    public void setOperation(Operation operation) {
        calculator.setOperation(operation);
    }

    public BigDecimal getResultAfterEqual(BigDecimal newLeftOperand) throws NumberOverflowException, DivideByZeroException, NoOperationException {
        return calculator.getResultAfterEqual(newLeftOperand);
    }

    public BigDecimal getResult(BigDecimal rightOperand) throws NumberOverflowException, DivideByZeroException, NoOperationException {
        return calculator.getResult(rightOperand);
    }

    public BigDecimal getResult() throws NumberOverflowException, DivideByZeroException, NoOperationException {
        return calculator.getResult();
    }

    public void setOperation(BigDecimal leftOperand, Operation operation) {
        calculator.setOperation(leftOperand, operation);
    }

    public BigDecimal getResultOnGo(BigDecimal rightOperand) throws NumberOverflowException, DivideByZeroException, NoOperationException {
        return calculator.getResultOnGo(rightOperand);
    }

    public BigDecimal memoryRecall() {
        return calculator.memoryRecall();
    }

    public void memoryStore(BigDecimal value) {
        calculator.memoryStore(value);
    }

    public void memoryAdd(BigDecimal value) {
        calculator.memoryAdd(value);
    }

    public void memorySubtract(BigDecimal value) {
        calculator.memorySubtract(value);
    }

    public BigDecimal getPercent(BigDecimal percent) {
        return calculator.getPercent(percent);
    }

    public void clear() {
        calculator.clear();
    }

    public void memoryClear() {
        calculator.memoryClear();
    }

    public BigDecimal getInverted(BigDecimal value) {
        return calculator.getInverted(value);
    }

    public BigDecimal getSqrt(BigDecimal value) {
        return calculator.getSqrt(value);
    }


    public Operation getOperation() {
        return calculator.getOperation();
    }
}
