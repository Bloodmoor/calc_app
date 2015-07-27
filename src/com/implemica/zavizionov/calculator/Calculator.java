package com.implemica.zavizionov.calculator;


import java.math.BigDecimal;

/**
 * Class represents a calculator for most popular operations.
 *
 * @author Zavizionov Andrii
 */
public class Calculator {

    /**
     * Scale for divide operation, set bigger to increase precision.
     */
    private static final int DIVIDE_SCALE = 10000;

    /**
     * Maximal calculator scale. Any operation,
     * that will need a bigger scale, will cause an exception.
     */
    private static final int MAX_SCALE = 10000;

    /**
     * Stored value for memory operations of calculator.
     */
    BigDecimal memory = BigDecimal.ZERO;

    /**
     * Holds a left operand of any operation.
     */
    BigDecimal leftOperand = BigDecimal.ZERO;

    /**
     * Holds a right operand of operations, that need such one.
     */
    BigDecimal rightOperand = BigDecimal.ZERO;

    /**
     * Holds an operation, that that should be performed.
     */
    Operation operation = Operation.NOOP;

    /**
     * Is true, if some operation was done.
     */
    boolean done;

//    public void setLeftOperand(BigDecimal operand){
//        leftOperand = operand;
//    }
//
//    public void setRightOperand(BigDecimal operand){
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

    /**
     * Performs stored operation on stored operands.
     *
     * @return reesult of operation.
     * @throws Exception - occures when resulting number overflows maximal scale.
     */
    private BigDecimal performOperation() throws Exception {
        BigDecimal result = BigDecimal.ZERO;
        switch (operation) {
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
        if (Math.abs(result.scale()) > MAX_SCALE) {
            throw new Exception("Overflow");
        }
        return result;
    }

    /**
     * Sets a left operand and operation.
     * @param leftOperand - left operand.
     * @param operation - operation to be performed.
     */
    public void setOperation(BigDecimal leftOperand, Operation operation) {
        done = false;
        this.leftOperand = leftOperand;
        this.operation = operation;
    }

    /**
     * Returns a result of operation.
     * Should be called for one-operand operations.
     * @return result of one-operand operation.
     */
    public BigDecimal getResult() throws Exception {
        leftOperand = performOperation();
        return leftOperand;
    }

    /**
     * Returns a result of operation.
     * Should be called for two-operand operations
     * @param rightOperand - right operand
     * @return result of two-operand operation
     * @throws Exception - occurs when resulting number overflows maximal scale.
     */
    public BigDecimal getResult(BigDecimal rightOperand) throws Exception {
        if (!done) {
            this.rightOperand = rightOperand;
        }
        done = true;
        leftOperand = performOperation();
        return leftOperand;
    }

    /**
     * Returns a result of next operation, should be called
     * for sequences of operations.
     * @param rightOperand - next right operand
     * @return result of operation
     * @throws Exception - occurs when resulting number overflows maximal scale.
     */
    public BigDecimal getResultOnGo(BigDecimal rightOperand) throws Exception {
        done = false;
        this.rightOperand = rightOperand;
        leftOperand = performOperation();
        return leftOperand;
    }

    /**
     * Performs an add operation for stored operands.
     * @return sum of operands
     */
    private BigDecimal performPlus() {
        return leftOperand.add(rightOperand);
    }

    /**
     * Performs a subtract operation for stored operands.
     * @return difference of operands
     */
    private BigDecimal performMinus() {
        return leftOperand.subtract(rightOperand);
    }

    /**
     * Performs a divide operation for stored operands.
     * @return result ov division
     * @throws IllegalArgumentException - when dividing by zero
     */
    private BigDecimal performDivide() {
        if (rightOperand.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Can't divide by zero. Right operand expected : non-zero, actual: " + rightOperand);
        }
        return leftOperand.divide(rightOperand, DIVIDE_SCALE, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
    }

    /**
     * Performs a multiply operation for stored operands.
     * @return result of multiplying
     */
    private BigDecimal performMultiply() {
        return leftOperand.multiply(rightOperand);
    }

    /**
     * Inverts left operand, same as (-1)*leftOperand
     * @return result of inverting
     */
    private BigDecimal performInvert() {
        return leftOperand.multiply(BigDecimal.valueOf(-1));
    }

    /**
     * Extracts the square root of left operand
     * @return square root of left operand
     * @throws IllegalArgumentException - if left operand is negative number
     */
    private BigDecimal performSqrt() {
        if (Double.isInfinite(leftOperand.doubleValue())) {
            throw new IllegalArgumentException("To big value");
        }
        if (leftOperand.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Expected: non-negative, actual: " + leftOperand);
        }
        return BigDecimal.valueOf(Math.sqrt(leftOperand.doubleValue())).stripTrailingZeros();
    }

    /**
     * Returns a result of dividing one by the left operand.
     * @return result of dividing
     * @throws IllegalArgumentException - if left operand is zero.
     */
    private BigDecimal performReverse() {
        if (leftOperand.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Can't divide by zero. Left operand expected : non-zero, actual: " + rightOperand);
        }
        return BigDecimal.ONE.divide(leftOperand, DIVIDE_SCALE, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
    }

    /**
     * Calculates the given percent from left operand
     * @param percent - percent to be calculated
     * @return percent from left operand
     */
    public BigDecimal getPercent(BigDecimal percent) {
        return leftOperand.multiply(percent.divide(BigDecimal.valueOf(100))).stripTrailingZeros();
    }

    /**
     * Resets calculator fields to default value.
     */
    public void clear() {
        operation = Operation.NOOP;
        leftOperand = BigDecimal.ZERO;
        rightOperand = BigDecimal.ZERO;
        done = false;
    }

    /**
     * Clears calculator memory field.
     */
    public void memoryClear() {
        memory = BigDecimal.ZERO;
    }

    /**
     * Returns number from memory
     * @return number from memory
     */
    public BigDecimal memoryRecall() {
        return memory;
    }

    /**
     * Store given number in memory.
     * @param value - number to store
     */
    public void memoryStore(BigDecimal value) {
        memory = value;
    }

    /**
     * Adds a given number to number, stored in memory.
     * @param value - number to add
     */
    public void memoryAdd(BigDecimal value) {
        memory = memory.add(value);
    }

    /**
     * Subtracts a given number from number, stored in memory.
     * @param value - number to subtract
     */
    public void memorySubtract(BigDecimal value) {
        memory = memory.subtract(value);
    }

    /**
     * Returns an operation, currently stored in calculator.
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * Sets a given operation to calculator.
     * @param operation - operation to set.
     */
    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    /**
     * Method should be used for reusing a right operand
     * is the same operation after getting a result of
     * operation for another left operand.
     * @param newLeftOperand - new left operand
     * @return result of operation
     * @throws Exception - occurs when resulting number overflows maximal scale.
     */
    public BigDecimal getResultAfterEqual(BigDecimal newLeftOperand) throws Exception {
        this.leftOperand = newLeftOperand;
        this.leftOperand = performOperation();
        return this.leftOperand;
    }
}

