package com.implemica.zavizionov.calculator;

import com.implemica.zavizionov.calculator.exception.DivideByZeroException;
import com.implemica.zavizionov.calculator.exception.NoOperationException;
import com.implemica.zavizionov.calculator.exception.NumberOverflowException;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.Assert.fail;

/**
 * Created by Suff on 14.07.2015.
 */
public class CalculatorTest {

    private void assertOperation(BigDecimal leftOperand, BigDecimal rightOperand, Operation op, BigDecimal expectedResult) throws Exception, NoOperationException {
        Calculator calc = new Calculator();
        calc.setOperation(leftOperand, op);
        assertEqualsBD(expectedResult, calc.getResult(rightOperand));
    }

    private void assertOperation(BigDecimal leftOperand, Operation op, BigDecimal expectedResult) throws Exception, NoOperationException {
        Calculator calc = new Calculator();
        calc.setOperation(leftOperand, op);
        assertEqualsBD(expectedResult, calc.getResult());
    }

    /**
     * Asserts if two big decs are really equal.
     *
     * @param expectedResult
     * @param result
     */
    private void assertEqualsBD(BigDecimal expectedResult, BigDecimal result) {
        if (expectedResult.compareTo(result) != 0) {
            fail("\nExpected :" + expectedResult + "\nActual   :" + result);
        }
    }

    private void assertOperation(double leftOperand, double rightOperand, Operation op, double expectedResult) throws Exception, NoOperationException {
        assertOperation(asBD(leftOperand), asBD(rightOperand), op, asBD(expectedResult));
    }

    private void assertOperation(String leftOperand, String rightOperand, Operation op, String expectedResult) throws Exception, NoOperationException {
        assertOperation(asBD(leftOperand), asBD(rightOperand), op, asBD(expectedResult));
    }

    private void assertOperation(double leftOperand, Operation op, double expectedResult) throws Exception, NoOperationException {
        assertOperation(asBD(leftOperand), op, asBD(expectedResult));
    }

    private void assertOperation(String leftOperand, Operation op, String expectedResult) throws Exception, NoOperationException {
        assertOperation(asBD(leftOperand), op, asBD(expectedResult));
    }

    /**
     * Converts given number to BigDecimal.
     *
     * @param d
     * @return BigDecomal object
     */
    private BigDecimal asBD(double d) {
        return BigDecimal.valueOf(d);
    }

    /**
     * Converts given string with number to BigDecimal.
     *
     * @param s
     * @return BigDecimal object
     */
    private BigDecimal asBD(String s) {
        return new BigDecimal(s);
    }


    private void assertBadArguments(BigDecimal leftOperand, BigDecimal rightOperand, Operation op) throws Exception, NoOperationException {
        Calculator calc = new Calculator();
        calc.setOperation(leftOperand, op);
        try {
            calc.getResult(rightOperand);
            fail(String.format("Expected IllegalArgumentException for arguments:\nleftOperand  : %s\nrightOperand :%s", leftOperand, rightOperand));
        }catch(IllegalArgumentException e){
            //correct for this arguments
        }
    }

    private void assertDivideByZero(String leftOperand, Operation op) throws NumberOverflowException, NoOperationException {
        assertDivideByZero(asBD(leftOperand), op);
    }

    private void assertDivideByZero(BigDecimal leftOperand, Operation op) throws NumberOverflowException, NoOperationException {
        Calculator calc = new Calculator();
        calc.setOperation(leftOperand, op);
        try {
            calc.getResult();
            fail(String.format("Expected DivideByZeroException for arguments:\nleftOperand  : %s\nrightOperand :%s", leftOperand));
        }catch(DivideByZeroException e){
            //correct for this arguments
        }
    }

    private void assertDivideByZero(String leftOperand, String rightOperand, Operation op) throws Exception, NoOperationException {
        assertDivideByZero(asBD(leftOperand), asBD(rightOperand), op);
    }

    private void assertDivideByZero(BigDecimal leftOperand, BigDecimal rightOperand, Operation op) throws Exception, NoOperationException {
        Calculator calc = new Calculator();
        calc.setOperation(leftOperand, op);
        try {
            calc.getResult(rightOperand);
            fail(String.format("Expected DivideByZeroException for arguments:\nleftOperand  : %s\nrightOperand :%s", leftOperand, rightOperand));
        }catch(DivideByZeroException e){
            //correct for this arguments
        }
    }

    private void assertBadArguments(BigDecimal leftOperand, Operation op) throws Exception, NoOperationException {
        Calculator calc = new Calculator();
        calc.setOperation(leftOperand, op);
        try {
            calc.getResult();
            fail(String.format("Expected IllegalArgumentException for arguments:\nleftOperand  : %s", leftOperand));
        }catch(IllegalArgumentException e){
            //correct for this arguments
        }
    }

    private void assertBadArguments(String leftOperand, String rightOperand, Operation op) throws Exception, NoOperationException {
        assertBadArguments(asBD(leftOperand), asBD(rightOperand), op);
    }

    private void assertBadArguments(String leftOperand, Operation op) throws Exception, NoOperationException {
        assertBadArguments(asBD(leftOperand), op);
    }


    @Test
    public void testPlus() throws Exception, NoOperationException {
        assertOperation(10, 20, Operation.PLUS, 30);
        assertOperation(10, 15.1, Operation.PLUS, 25.1);
        assertOperation("0.000000000000001", "0.000000000000002", Operation.PLUS, "0.000000000000003");
        assertOperation("10000000000", "1", Operation.PLUS, "10000000001");
        assertOperation("-10000000000", "10000000000", Operation.PLUS, "0");
        assertOperation("10000000000", "1", Operation.PLUS, "10000000001");
        assertOperation("99999999999999999999", "3", Operation.PLUS, "100000000000000000002");
    }

    @Test
    public void testMinus() throws Exception, NoOperationException {
        assertOperation(10, 20, Operation.MINUS, -10);
        assertOperation(20, 15.1, Operation.MINUS, 4.9);
        assertOperation("0.000000000000001", "0.000000000000002", Operation.MINUS, "-0.000000000000001");
        assertOperation("10000000000", "1", Operation.MINUS, "9999999999");
        assertOperation("99999999999999999999", "-3", Operation.MINUS, "100000000000000000002");
    }

    @Test
    public void testDivide() throws Exception, NoOperationException {
        assertOperation("10", "20", Operation.DIVIDE, "0.5");
        assertOperation("1", "4", Operation.DIVIDE, "0.25");
        assertOperation("0.001", "-100", Operation.DIVIDE, "-0.00001");
        assertOperation("35684", "1", Operation.DIVIDE, "35684");
        assertOperation("99999999999999999999", "99999999999999999999", Operation.DIVIDE, "1");
    }

    @Test
    public void testDivideBadArguments() throws Exception, NoOperationException {
        assertDivideByZero("5", "0", Operation.DIVIDE);
        assertDivideByZero("0", "0", Operation.DIVIDE);
        assertDivideByZero("-5", "0", Operation.DIVIDE);
        assertDivideByZero("0.000000000001", "0", Operation.DIVIDE);
    }

    @Test
    public void testMultiply() throws Exception, NoOperationException {
        assertOperation(10, 20, Operation.MULTIPLY, 200);
        assertOperation(20, 15.13, Operation.MULTIPLY, 302.6);
        assertOperation("0.000000000000001", "-0.2", Operation.MULTIPLY, "-0.0000000000000002");
        assertOperation("10000000000", "1", Operation.MULTIPLY, "10000000000");
        assertOperation("99999999999999999999", "0.1", Operation.MULTIPLY, "9999999999999999999.9");
    }

    @Test
    public void testInvert() throws Exception, NoOperationException {
        assertOperation(10, Operation.INVERT, -10);
        assertOperation(15.13, Operation.INVERT, -15.13);
        assertOperation("0.000000000000001", Operation.INVERT, "-0.000000000000001");
        assertOperation("10000000000", Operation.INVERT, "-10000000000");
        assertOperation("99999999999999999999", "0.1", Operation.INVERT, "-99999999999999999999");
    }

    @Test
    public void testSqrt() throws Exception, NoOperationException {
        assertOperation(100, Operation.SQRT, 10);
        assertOperation(9, Operation.SQRT, 3);
        assertOperation("0.01", Operation.SQRT, "0.1");
        assertOperation("0.00000001", Operation.SQRT, "0.0001");
        assertOperation("999999999999", Operation.SQRT, "999999.9999995");
        assertOperation("123456789123", Operation.SQRT, "351364.18303947826");
        assertOperation("100000000000000000000", Operation.SQRT, "10000000000");
    }

    @Test
    public void testSqrtBadArguments() throws Exception, NoOperationException {
        assertBadArguments("-1", Operation.SQRT);
        assertBadArguments("-123456789", Operation.SQRT);
    }

    @Test
    public void testReverse() throws Exception, NoOperationException {
        assertOperation("10", Operation.REVERSE, "0.1");
        assertOperation("4", Operation.REVERSE, "0.25");
        assertOperation("-4", Operation.REVERSE, "-0.25");
        assertOperation("0.001", Operation.REVERSE, "1000");
        assertOperation("1", Operation.REVERSE, "1");
    }

    @Test
    public void testReverseBadArguments() throws Exception, NoOperationException {
        assertDivideByZero("0", Operation.REVERSE);
    }


    @Test
    public void testPercent() throws Exception {
        Calculator calc = new Calculator();
        calc.setOperation(asBD(200), Operation.PLUS);
        assertEqualsBD(asBD(40), calc.getPercent(asBD(20)));
        assertEqualsBD(asBD(-40) ,calc.getPercent(asBD(-20)));
        assertEqualsBD(asBD(400) ,calc.getPercent(asBD(200)));
        assertEqualsBD(asBD(1) ,calc.getPercent(asBD(0.5)));
        assertEqualsBD(asBD("0.00001"), calc.getPercent(asBD("0.000005")));
    }

    @Test
    public void testMemoryStore() throws Exception {
        Calculator calc = new Calculator();
        calc.memoryStore(asBD("125"));
        assertEqualsBD(asBD("125"), calc.memoryRecall());
    }

    @Test
    public void testMemoryAdd() throws Exception {
        Calculator calc = new Calculator();
        calc.memoryAdd(asBD("10"));

        assertEqualsBD(asBD("10"), calc.memoryRecall());

        calc.memoryAdd(asBD("25"));

        assertEqualsBD(asBD("35"), calc.memoryRecall());

        calc.memoryAdd(asBD("0.25"));

        assertEqualsBD(asBD("35.25"), calc.memoryRecall());
    }

    @Test
    public void testMemorySubtract() throws Exception {
        Calculator calc = new Calculator();
        calc.memoryStore(asBD("100"));
        calc.memorySubtract(asBD("10"));

        assertEqualsBD(asBD("90"), calc.memoryRecall());

        calc.memorySubtract(asBD("0.25"));

        assertEqualsBD(asBD("89.75"), calc.memoryRecall());
    }

    @Test
    public void testMemoryClear() throws Exception {
        Calculator calc = new Calculator();
        calc.memoryStore(asBD("125"));
        calc.memoryClear();

        assertEqualsBD(asBD("0"), calc.memoryRecall());
    }

    @Test
    public void testMemoryDefault() throws Exception{
        Calculator calc = new Calculator();

        assertEqualsBD(asBD("0"), calc.memoryRecall());
    }

    @Test
    public void testGetResultOnGo() throws NoOperationException, NumberOverflowException, DivideByZeroException {
        Calculator calc = new Calculator();
        calc.setOperation(asBD("3"), Operation.MULTIPLY);

        assertEqualsBD(asBD("6"), calc.getResult(asBD("2")));
        assertEqualsBD(asBD("18"), calc.getResultOnGo(asBD("3")));
    }

    @Test
    public void testResultAfterEquals() throws NumberOverflowException, DivideByZeroException, NoOperationException {
        Calculator calc = new Calculator();
        calc.setOperation(asBD("3"), Operation.MULTIPLY);

        assertEqualsBD(asBD("6"), calc.getResult(asBD("2")));
        assertEqualsBD(asBD("8"), calc.getResultAfterEqual(asBD("4")));
    }

    @Test
    public void testNoOperationException() throws NumberOverflowException, DivideByZeroException {
        Calculator calc = new Calculator();
        try{
            calc.getResult();
            fail();
        }catch (NoOperationException e){
            //correct for this state of calculator
        }
    }
}