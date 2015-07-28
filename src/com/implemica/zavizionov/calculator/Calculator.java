package com.implemica.zavizionov.calculator;


import com.implemica.zavizionov.calculator.exception.DivideByZeroException;
import com.implemica.zavizionov.calculator.exception.NumberOverflowException;

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
     * Max calculator scale. Any operation
     * that will need a bigger scale, will cause an exception.
     */
    private static final int MAX_SCALE = 10000;
    private static final BigDecimal NEGATIVE_ONE = BigDecimal.valueOf(-1);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    /**
     * Stored value for memory operations of calculator.
     */
    private BigDecimal memory = BigDecimal.ZERO;

    /**
     * Holds a left operand of any operation.
     */
    private BigDecimal leftOperand = BigDecimal.ZERO;

    /**
     * Holds a right operand of operations, that need such one.
     */
    private BigDecimal rightOperand = BigDecimal.ZERO;

    /**
     * Holds an operation, that that should be performed.
     */
    private Operation operation = Operation.NOOP;

    /**
     * Is true, if some operation was already done.
     */
    private boolean nextOperation;

    /**
     * Performs stored operation on stored operands.
     *
     * @return reesult of operation.
     * @throws NumberOverflowException - occurs when resulting number overflows maximal scale.
     * @throws DivideByZeroException - if divide by zero was performed
     */
    private BigDecimal performOperation() throws NumberOverflowException, DivideByZeroException {
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
            throw new NumberOverflowException("Overflow");
        }
        return result;
    }

    /**
     * Sets a left operand and operation.
     * @param leftOperand - left operand.
     * @param operation - operation to be performed.
     */
    public void setOperation(BigDecimal leftOperand, Operation operation) {
        nextOperation = false;
        this.leftOperand = leftOperand;
        this.operation = operation;
    }

    /**
     * Returns a result of operation.
     * Should be called for one-operand operations.
     * Example: sqrt(3)
     * @return result of one-operand operation.
     * @throws NumberOverflowException - occurs when resulting number overflows maximal scale.
     * @throws DivideByZeroException - if divide by zero was performed
     */
    public BigDecimal getResult() throws NumberOverflowException, DivideByZeroException {
        leftOperand = performOperation();
        return leftOperand;
    }

    /**
     * Returns a result of operation.
     * Should be called for two-operand operations
     * Example 3+5
     * @param rightOperand - right operand
     * @return result of two-operand operation
     * @throws NumberOverflowException - occurs when resulting number overflows maximal scale.
     * @throws DivideByZeroException - if divide by zero was performed
     */
    public BigDecimal getResult(BigDecimal rightOperand) throws NumberOverflowException, DivideByZeroException {
        if (!nextOperation) {
            this.rightOperand = rightOperand;
        }
        nextOperation = true;
        leftOperand = performOperation();
        return leftOperand;
    }

    /**
     * Returns a result of next operation, should be called
     * for sequences of operations.
     * Example: 3+5*
     *          (result)
     *          6-
     *          (result)
     *          8
     * @param rightOperand - next right operand
     * @return result of operation
     * @throws NumberOverflowException - occurs when resulting number overflows maximal scale.
     * @throws DivideByZeroException - if divide by zero was performed
     */
    public BigDecimal getResultOnGo(BigDecimal rightOperand) throws NumberOverflowException, DivideByZeroException {
        nextOperation = false;
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
     * @throws DivideByZeroException - when dividing by zero
     */
    private BigDecimal performDivide() throws DivideByZeroException {
        if (rightOperand.compareTo(BigDecimal.ZERO) == 0) {
            throw new DivideByZeroException("Can't divide by zero. Right operand expected : non-zero, actual: " + rightOperand);
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
        return leftOperand.multiply(NEGATIVE_ONE);
    }

    /**
     * Extracts the square root of left operand
     * @return square root of left operand
     * @throws IllegalArgumentException - if left operand is negative
     * number or to big for calculating square root
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
     * @throws DivideByZeroException - if left operand is zero.
     */
    private BigDecimal performReverse() throws DivideByZeroException {
        if (leftOperand.compareTo(BigDecimal.ZERO) == 0)
            throw new DivideByZeroException("Can't divide by zero. Left operand expected : non-zero, actual: " + rightOperand);
        return BigDecimal.ONE.divide(leftOperand, DIVIDE_SCALE, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
    }

    /**
     * Calculates the given percent from left operand
     * @param percent - percent to be calculated
     * @return percent from left operand
     */
    public BigDecimal getPercent(BigDecimal percent) {
        return leftOperand.multiply(percent.divide(HUNDRED, DIVIDE_SCALE, BigDecimal.ROUND_HALF_UP)).stripTrailingZeros();
    }

    /**
     * Resets calculator fields to default value.
     */
    public void clear() {
        operation = Operation.NOOP;
        leftOperand = BigDecimal.ZERO;
        rightOperand = BigDecimal.ZERO;
        nextOperation = false;
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
     * Example: 3+5=
     *          result is 8
     *          1=
     *          result is 6
     * @param newLeftOperand - new left operand
     * @return result of operation
     * @throws NumberOverflowException - occurs when resulting number overflows maximal scale.
     * @throws DivideByZeroException - if divide by zero was performed
     */
    public BigDecimal getResultAfterEqual(BigDecimal newLeftOperand) throws NumberOverflowException, DivideByZeroException {
        this.leftOperand = newLeftOperand;
        this.leftOperand = performOperation();
        return this.leftOperand;
    }
}

