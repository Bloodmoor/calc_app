package com.implemica.zavizionov.calculator;


import java.math.BigDecimal;

/**
 * Created by Suff on 13.07.2015.
 */
public class Calculator {
    private static final int SCALE = 30;
    BigDecimal memory = BigDecimal.ZERO;
    BigDecimal leftOperand = BigDecimal.ZERO;
    BigDecimal rightOperand = BigDecimal.ZERO;
    Operation operation = Operation.NOOP;
    boolean done;

//    public void setLeftOperand(BigDecimal operand){
//        leftOperand = operand;
//    }
//
//    public void setRightOperand(BigDecimal opeerand){
//        rightOperand = opeerand;
//    }
//
//    public void setOperation(Operation operation){
//        this.operation = operation;
//    }
//
//    public BigDecimal result(){
//        leftOperand = rightOperand;
//        return performOperation();
//    }

    private BigDecimal performOperation(){
        BigDecimal result = BigDecimal.ZERO;
        switch (operation){
            case PLUS:
                result = performPlus();
                break;
            case MINUS:
                result = performMinus();
                break;
            case DIVIDE:
                result = performDivide();
                break;
            case MULTIPLY:
                result = performMultiply();
                break;
            case INVERT:
                result = performInvert();
                break;
            case SQRT:
                result = performSqrt();
                break;
            case REVERSE:
                result = performReverse();
                break;
            case MC:
                break;
            case MR:
                break;
            case MS:
                break;
            case MPLUS:
                break;
            case MMINUS:
                break;
        }
        return result;
    }

    public void setOperation(BigDecimal leftOperand, Operation operation){
        done = false;
        this.leftOperand = leftOperand;
        this.operation = operation;
    }

    /**
     * Should be called for one-operand operations
     */
    public BigDecimal getResult(){
        leftOperand = performOperation();
        return leftOperand;
    }

    public BigDecimal getResult(BigDecimal rightOperand){
        if (!done){
            this.rightOperand = rightOperand;
        }
        done = true;
        leftOperand = performOperation();
        return leftOperand;
    }

    public BigDecimal getResultOnGo(BigDecimal rightOperand){
        done = false;
        this.rightOperand = rightOperand;
        leftOperand = performOperation();
        return leftOperand;
    }

    private BigDecimal performPlus(){
        return leftOperand.add(rightOperand) ;
    }

    private BigDecimal performMinus(){
        return leftOperand.subtract(rightOperand);
    }

    private BigDecimal performDivide(){
        if (rightOperand.compareTo(BigDecimal.ZERO) == 0){
            throw new IllegalArgumentException("Can't divide by zero. Right operand expected : non-zero, actual: " + rightOperand);
        }
        return leftOperand.divide(rightOperand, SCALE,BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
    }

    private BigDecimal performMultiply(){
        return leftOperand.multiply(rightOperand);
    }

    private BigDecimal performInvert(){
        return leftOperand.multiply(BigDecimal.valueOf(-1));
    }

    private BigDecimal performSqrt(){
        if (Double.isInfinite(leftOperand.doubleValue())){
            throw new IllegalArgumentException("To big value");
        }
        if (leftOperand.compareTo(BigDecimal.ZERO)<0){
            throw new  IllegalArgumentException("Expected: non-negative, actual: " + leftOperand);
        }
        return BigDecimal.valueOf(Math.sqrt(leftOperand.doubleValue())).stripTrailingZeros();
    }

    private BigDecimal performReverse(){
        if (leftOperand.compareTo(BigDecimal.ZERO) == 0){
            throw new IllegalArgumentException("Can't divide by zero. Left operand expected : non-zero, actual: " + rightOperand);
        }
        return BigDecimal.ONE.divide(leftOperand,SCALE ,BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
    }

    public BigDecimal getPercent(BigDecimal percent) {
        return leftOperand.multiply(percent.divide(BigDecimal.valueOf(100))).stripTrailingZeros();
    }

    public void clear(){
        leftOperand = BigDecimal.ZERO;
        rightOperand = BigDecimal.ZERO;
        done = false;
    }

    public void memoryClear(){
        memory = BigDecimal.ZERO;
    }

    public BigDecimal memoryRecall(){
        return memory;
    }

    public void memoryStore(BigDecimal value){
        memory = value;
    }

    public void memoryAdd(BigDecimal value){
        memory = memory.add(value);
    }

    public void memorySubtract(BigDecimal value){
        memory = memory.subtract(value);
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public BigDecimal getResultAfterEqual(BigDecimal newLeftOperand){
        this.leftOperand = newLeftOperand;
        this.leftOperand = performOperation();
        return this.leftOperand;
    }
}

