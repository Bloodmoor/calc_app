package com.implemica.zavizionov.calculator;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Tests for calculators GUI and logic.
 *
 * @author Zavivionov Andrii
 */
public class CalculatorViewTest extends GuiTest {

    //Деление на ноль
    private static final String DIVIDE_BY_ZERO_MESSAGE = "\u0414\u0435\u043B\u0435\u043D\u0438\u0435 \u043D\u0430 \u043D\u043E\u043B\u044C \u043D\u0435\u0432\u043E\u0437\u043C\u043E\u0436\u043D\u043E";
    //Переполнение
    private static final String OVERFLOW_MESSAGE = "\u041F\u0435\u0440\u0435\u043F\u043E\u043B\u043D\u0435\u043D\u0438\u0435";
    //Недопустимый ввод
    private static final String INVALID_INPUT_MESSAGE = "\u041D\u0435\u0434\u043E\u043F\u0443\u0441\u0442\u0438\u043C\u044B\u0439 \u0432\u0432\u043E\u0434";
    private static final String SQRT_BUTTON_LABEL = "\u221A";
    private static final String INVERT_BUTTON_LABEL = "\u00B1";
    private static final String BACKSPACE_BUTTON_LABEL = "\u2190";
    private static final String SCREEN_OVERFLOW_SYMBOL = "\u2039\u2039";
    private static final int FIRST_SCREEN_BIG_FONT_SIZE = 22;
    private static final int FIRST_SCREEN_MEDIUM_FONT_SIZE = 18;
    private static final int FIRST_SCREEN_SMALL_FONT_SIZE = 12;
    private static final long DELAY = 100;
    private static final String DOT_BUTTON_ID = ",";


    private TextField firstScreen;
    private TextField secondScreen;
    private Label memoryScreen;
    private CalculatorFormatter formatter;

    public Parent getRootNode() {
        CalculatorView view = new CalculatorView();
        BorderPane root = view.getRoot();
        firstScreen = view.getFirstScreen();
        secondScreen = view.getSecondScreen();
        memoryScreen = view.getMemoryScreen();
        formatter = view.getFormatter();
        root.getStylesheets().removeAll();
        root.getStylesheets().add("testStyle.css");
        return root;
    }

    @Before
    public void initialize() {
        formatter.pressClearButton();
        formatter.pressOperationButton(Operation.MC);
    }

    private void assertDigitButton(String buttonId) {
        if (buttonId.equals("0")) {
            assertSequence("10", "10");
        } else {
            String result = buttonId + buttonId;
            assertSequence(result, result);
        }
    }

    private void clickSequence(String sequence) {
        for (int i = 0; i < sequence.length(); i++) {
            String b = sequence.substring(i, i + 1);

            if (b.equals("(")) {
                int indexOfBrace = sequence.indexOf(')', i);
                b = sequence.substring(i + 1, indexOfBrace);

                i = indexOfBrace;
            }
            if (b.equals("+/-")) {
                b = INVERT_BUTTON_LABEL;
            }
            if (b.equals(".")) {
                b = DOT_BUTTON_ID;
            }
            if (b.equals("<")) {
                b = BACKSPACE_BUTTON_LABEL;
            }
            if (b.equals("s")) {
                b = SQRT_BUTTON_LABEL;
            }
            for (Node n : ((GridPane) ((BorderPane) GuiTest.getWindowByIndex(0).getScene().getRoot()).getCenter()).getChildren()) {
                javafx.scene.control.Button button = (javafx.scene.control.Button) n;
                if (button.getText().equals(b)) {
                    button.fire();
                }
            }

        }
    }

    private void clickSequence(String[] sequence) {
        for (String s : sequence) {
            if (s.equals(".")) s = DOT_BUTTON_ID;
            click(s);
        }
    }

    private void assertSequence(String expectedFirstScreen, String expectedSecondScreen, String buttonSequence) {
        formatter.pressClearButton();
        clickSequence(buttonSequence);

        assertFirstScreen(expectedFirstScreen);
        assertSecondScreen(expectedSecondScreen);
    }

    private void assertSequence(String expectedFirstScreen, String buttonSequence) {
        formatter.pressClearButton();
        clickSequence(buttonSequence);

        assertFirstScreen(expectedFirstScreen);
    }

    private void assertExpression(String expression) {
        formatter.pressClearButton();
        String sequence = expression.substring(0, expression.lastIndexOf("=") + 1);
        String expectedResult = expression.substring(expression.lastIndexOf("=") + 1, expression.length());
        assertSequence(expectedResult, sequence);
    }

    @Test
    public void testDigitButtons() {
        for (int i = 0; i < 9; i++) {
            assertDigitButton(Integer.toString(i));
        }
    }

    @Test
    public void testClearEntryButton() {
        assertSequence("0", "", "5(CE)");
        assertSequence("0", "", "9999999999999999(CE)");
        assertSequence("0", "", "0.00000000000001(CE)");
        assertSequence("0", "", "9999999999999999+1=(CE)");

        assertSequence("0", "1 +", "1+3(CE)");
        assertSequence("0", "1 +", "1+9999999999999999(CE)");
        assertSequence("0", "1 +", "1+0.00000000000001(CE)");
        assertSequence("0", "1 + 9999999999999999 + 1 +", "1+9999999999999999+1+(CE)");

        assertSequence("5", "1 +", "1+3(CE)5");
        assertSequence("15", "1 +", "1+9999999999999999(CE)15");
        assertSequence("0.5", "1 +", "1+0.00000000000001(CE)0.5");
        assertSequence("1.5", "1 + 9999999999999999 + 1 +", "1+9999999999999999+1+(CE)1.5");

    }


    @Test
    public void testClearButton() {
        assertSequence("0", "", "5C");
        assertSequence("0", "", "9999999999999999C");
        assertSequence("0", "", "0.00000000000001C");
        assertSequence("0", "", "9999999999999999+1=C");

        assertSequence("0", "", "1+3C");
        assertSequence("0", "", "1+9999999999999999C");
        assertSequence("0", "", "1+0.00000000000001C");
        assertSequence("0", "", "1+9999999999999999+1+C");

        assertSequence("5", "", "1+3C5");
        assertSequence("15", "", "1+9999999999999999C15");
        assertSequence("0.5", "", "1+0.00000000000001C0.5");
        assertSequence("1.5", "", "1+9999999999999999+1+C1.5");
    }

    @Test
    public void testPlus() {
        assertExpression("-1+1=0");
        assertExpression("-1+2=1");
        assertExpression("1+1(+/-)=0");
        assertExpression("0+1(+/-)=-1");
        assertExpression("1+2(+/-)=-1");

        assertExpression("8+5(+/-)=3");
        assertExpression("8+5(+/-)===-7");
        assertExpression("3+5=8");
        assertExpression("0+3=3");
        assertExpression("3+==9");
        assertExpression("+5=5");
        assertExpression("+5(+/-)=-5");
        assertExpression("+5=====25");

        assertExpression("0+0=0");
        assertExpression("1+0=1");
        assertExpression("1+0(+/-)=1");

        assertExpression("0.5+14=14.5");
        assertExpression("12345.6789+98765.4321=111111.111");

        assertExpression("0.00000000000001+0.00000000000001=0.00000000000002");
        assertExpression("0.00000000000001+0.00000000000001(+/-)=0");
        assertExpression("0.00000000000001+0.00000000000002(+/-)=-0.00000000000001");

        assertExpression("9999999999999999+9999999999999999(+/-)=0");
        assertExpression("9999999999999999+9999999999999998(+/-)=1");
        assertExpression("9999999999999998+9999999999999999(+/-)=-1");
        assertExpression("9999999999999999+9999999999999999=2e+16");
        assertExpression("9999999999999999+1=1e+16");
        assertExpression("9999999999999999+1=+1(+/-)=9999999999999999");

        assertExpression("999.99999999999+0.00000000001=1000");
    }

    @Test
    public void testMinus() {
        assertExpression("-1-1=-2");
        assertExpression("1-2=-1");
        assertExpression("0-1=-1");
        assertExpression("1-1(+/-)=2");
        assertExpression("0-1(+/-)=1");
        assertExpression("-1-1(+/-)=0");
        assertExpression("-1-2(+/-)=1");

        assertExpression("8-5=3");
        assertExpression("18-5==8");
        assertExpression("8-5===-7");
        assertExpression("0-3=-3");
        assertExpression("3-==-3");
        assertExpression("-5=-5");
        assertExpression("-5(+/-)=5");
        assertExpression("-5=====-25");
        assertExpression("0-0=0");
        assertExpression("1-0=1");
        assertExpression("1-0(+/-)=1");

        assertExpression("0.5-14=-13.5");
        assertExpression("12345.6789-98765.4321=-86419.7532");

        assertExpression("0.00000000000001-0.00000000000001=0");
        assertExpression("0.00000000000001-0.00000000000001(+/-)=0.00000000000002");
        assertExpression("0.00000000000001-0.00000000000002=-0.00000000000001");
        assertExpression("-0.00000000000001-0.00000000000002(+/-)=0.00000000000001");

        assertExpression("9999999999999999-9999999999999999=0");
        assertExpression("9999999999999999-9999999999999998=1");
        assertExpression("9999999999999998-9999999999999999=-1");
        assertExpression("9999999999999999-9999999999999999(+/-)=2e+16");
        assertExpression("9999999999999999-1(+/-)=1e+16");
        assertExpression("9999999999999999-1(+/-)=-1=9999999999999999");

        assertExpression("1000-0.00000000001=999.99999999999");
    }

    @Test
    public void testDivide() {
        assertExpression("0/3=0");
        assertExpression("0/1=0");
        assertExpression("0/0.00000000000001=0");
        assertExpression("0/9999999999999999=0");

        assertExpression("1/1=1");
        assertExpression("3/1=3");
        assertExpression("0.00000000000001/1=0.00000000000001");
        assertExpression("9999999999999999/1=9999999999999999");

        assertExpression("0.00000000000002/2=0.00000000000001");
        assertExpression("0.00000000000001/2=0.000000000000005");
        assertExpression("0.00000000000001/10=0.000000000000001");
        assertExpression("0.00000000000001/3=3.333333333333333e-15");
        assertExpression("0.00000000000001/0.00000000000001=1");
        assertExpression("0.00000000000001/0.00000000000002=0.5");
        assertExpression("0.00000000000002/0.00000000000001=2");
        assertExpression("0.00000000000001/9999999999999999=1e-30");

        assertExpression("9999999999999999/2=5000000000000000");
        assertExpression("9999999999999999/9=1111111111111111");
        assertExpression("9999999999999999/7=1.428571428571428e+15");
        assertExpression("9999999999999999/0.1=9.999999999999999e+16");
        assertExpression("9999999999999999/0.00000000000001=9.999999999999999e+29");
        assertExpression("9999999999999999/9999999999999999=1");
        assertExpression("9999999999999999/8888888888888888=1.125");
        assertExpression("-9999999999999999/8888888888888888=-1.125");

        assertExpression("10/2=5");
        assertExpression("-10/2=-5");
        assertExpression("10/2(+/-)=-5");
        //0.3333333333333333
        assertExpression("1/3=3.333333333333333e-1");
        assertExpression("10/2==2.5");
        assertExpression("10/2===1.25");

        assertExpression("10/5/2=1");
        assertExpression("15/6/8/0.4=0.78125");

        assertSequence("2.5", "5/2=");
        assertSequence("0", "0/2=");
        assertSequence("0.2", "5/==");
    }

    @Test
    public void testDivideByZero() {
        assertSequence(DIVIDE_BY_ZERO_MESSAGE, "0/0=");
        assertSequence(DIVIDE_BY_ZERO_MESSAGE, "3/0=");
        assertSequence(DIVIDE_BY_ZERO_MESSAGE, "9999999999999999/0=");
        assertSequence(DIVIDE_BY_ZERO_MESSAGE, "0.00000000000001/0=");
        assertSequence(DIVIDE_BY_ZERO_MESSAGE, "-3/0=");
        assertSequence(DIVIDE_BY_ZERO_MESSAGE, "-9999999999999999/0=");
        assertSequence(DIVIDE_BY_ZERO_MESSAGE, "-0.00000000000001/0=");
    }

    @Test
    public void testMultiply() {
        assertExpression("0*0=0");
        assertExpression("0*1=0");
        assertExpression("0*1(+/-)=0");
        assertExpression("*0=0");
        assertExpression("*1=0");
        assertExpression("*5=0");
        assertExpression("*9999999999999999=0");
        assertExpression("*0.00000000000001=0");
        assertExpression("5*0=0");
        assertExpression("-5*0=0");
        assertExpression("5*0(+/-)=0");
        assertExpression("9999999999999999*0=0");
        assertExpression("0*9999999999999999=0");
        assertExpression("0.00000000000001*0=0");
        assertExpression("0*0.00000000000001=0");

        assertExpression("1*1=1");
        assertExpression("2*1=2");
        assertExpression("-2*1=-2");
        assertExpression("2.25*1=2.25");
        assertExpression("-2.25*1=-2.25");
        assertExpression("9999999999999999*1=9999999999999999");
        assertExpression("-9999999999999999*1=-9999999999999999");
        assertExpression("0.00000000000001*1=0.00000000000001");
        assertExpression("-0.00000000000001*1=-0.00000000000001");
        assertExpression("1*1(+/-)=-1");
        assertExpression("2*1(+/-)=-2");
        assertExpression("-2*1(+/-)=2");
        assertExpression("2.25*1(+/-)=-2.25");
        assertExpression("-2.25*1(+/-)=2.25");
        assertExpression("9999999999999999*1(+/-)=-9999999999999999");
        assertExpression("-9999999999999999*1(+/-)=9999999999999999");
        assertExpression("0.00000000000001*1(+/-)=-0.00000000000001");
        assertExpression("-0.00000000000001*1(+/-)=0.00000000000001");

        assertExpression("1*2=2");
        assertExpression("1*2(+/-)=-2");
        assertExpression("1*2.25=2.25");
        assertExpression("1*2.25(+/-)=-2.25");
        assertExpression("1*9999999999999999=9999999999999999");
        assertExpression("1*9999999999999999(+/-)=-9999999999999999");
        assertExpression("1*0.00000000000001=0.00000000000001");
        assertExpression("1*0.00000000000001(+/-)=-0.00000000000001");

        assertExpression("12345679*9=111111111");
        assertExpression("12345679*9(+/-)=-111111111");
        assertExpression("20*0.5=10");
        assertExpression("20*0.5(+/-)=-10");
        assertExpression("3*0.3=0.9");
        assertExpression("3*0.3(+/-)=-0.9");
        assertExpression("9999999999999999*9999999999999999=9.999999999999998e+31");
        assertExpression("9999999999999999*9999999999999999(+/-)=-9.999999999999998e+31");
        assertExpression("0.00000000000001*0.00000000000001=1e-28");
        assertExpression("0.00000000000001*0.00000000000001(+/-)=-1e-28");

        assertExpression("5*=25");
        assertExpression("5*==125");
        assertExpression("3*=*=81");
        assertExpression("0.1*=*10=0.1");
        assertExpression("3*===81");
    }

    @Test
    public void testPercent() {
        assertExpression("100+0%=100");
        assertExpression("100+1%=101");
        assertExpression("100+1(+/-)%=99");
        assertExpression("100-1%=99");
        assertExpression("100+0.1%=100.1");
        assertExpression("100+100%=200");
        assertExpression("100-100%=0");
        assertExpression("100*2%=200");
        assertExpression("100/20%=5");
        assertExpression("100+500%=600");

        assertExpression("-100+10%=-110");
        assertExpression("-100*10%=1000");

        assertExpression("0.1+100%=0.2");
        assertExpression("0.1-100%=0");
        assertExpression("0.1+20%=0.12");

        assertExpression("9999999999999999+9999999999999999%=1.00000000000001e+30");
        assertExpression("9999999999999999+0%=9999999999999999");
        assertExpression("9999999999999999*9999999999999999%=9.999999999999997e+45");
        assertExpression("9999999999999999-9999999999999999%=-9.999999999999898e+29");
        assertExpression("9999999999999999+0.00000000000001%=1e+16");

        assertExpression("0.00000000000001+1%=0.00000000000001");
        assertExpression("0.00000000000001-1%=9.9e-15");
        assertExpression("0.00000000000001-100%=0");
        assertExpression("0.00000000000001+100%=0.00000000000002");
        assertExpression("0.00000000000001+9999999999999999%=1");
        assertExpression("0.00000000000001+9999999999999999%=1");
        assertExpression("0.00000000000001+0.00000000000001%=1e-14");

        assertExpression("0+1%=0");
        assertExpression("0+10%=0");
        assertExpression("0+9999999999999999%=0");
        assertExpression("0+0.00000000000001%=0");

        assertExpression("0%=0");
        assertExpression("20%=0");
        assertExpression("9999999999999999%=0");
        assertExpression("0.00000000000001%=0");

        assertExpression("100+10%%%=110");
        assertExpression("200+10%%=240");
        assertExpression("200+10%%%=280");

        //for second screen assertion
        assertSequence("1", "50 + 1", "50+2%");
        assertSequence("40", "200 + 40", "200+10%%");
        assertSequence("0", "0", "20%");
    }

    @Test
    public void testInvert() {
        assertExpression("0(+/-)=0");
        assertExpression("1(+/-)=-1");
        assertExpression("-1(+/-)=1");
        assertExpression("0.1(+/-)=-0.1");
        assertExpression("-0.1(+/-)=0.1");
        assertExpression("9999999999999999(+/-)=-9999999999999999");
        assertExpression("-9999999999999999(+/-)=9999999999999999");
        assertExpression("0.00000000000001(+/-)=-0.00000000000001");
        assertExpression("-0.00000000000001(+/-)=0.00000000000001");
        assertExpression("5(+/-)=-5");
        assertExpression("-5(+/-)=5");

        assertExpression("5+0(+/-)=5");
        assertExpression("1+1(+/-)=0");
        assertExpression("1-1(+/-)=2");
        assertExpression("0.1+0.1(+/-)=0");
        assertExpression("0.1-0.1(+/-)=0.2");
        assertExpression("1+9999999999999999(+/-)=-9999999999999998");
        assertExpression("1+-9999999999999999(+/-)=1e+16");
        assertExpression("1+0.00000000000001(+/-)=0.99999999999999");
        assertExpression("1-0.00000000000001(+/-)=1.00000000000001");
        assertExpression("3+5(+/-)=-2");
        assertExpression("3-5(+/-)=8");

        assertExpression("3(+/-)(+/-)=3");
        assertExpression("-2(+/-)(+/-)=-2");
        assertExpression("3(+/-)(+/-)(+/-)=-3");
        assertExpression("-2(+/-)(+/-)(+/-)=2");
    }

    @Test
    public void testReverse() {
        assertExpression("0+1(1/x)=1");
        assertExpression("0+1(+/-)(1/x)=-1");
        assertExpression("0+2(1/x)=0.5");
        assertExpression("0+2(+/-)(1/x)=-0.5");
        assertExpression("0+0.1(1/x)=10");
        assertExpression("0+0.1(+/-)(1/x)=-10");
        assertExpression("1+2(1/x)=1.5");
        assertExpression("5+10(1/x)(1/x)=15");
        assertExpression("5+10(1/x)(1/x)(1/x)=5.1");

        assertExpression("0+9999999999999999(1/x)=1e-16");
        assertExpression("0+9999999999999999(+/-)(1/x)=-1e-16");
        assertExpression("0+9999999999999999(1/x)(1/x)=9999999999999999");
        assertExpression("0+0.00000000000001(1/x)=100000000000000");
        assertExpression("0+0.00000000000001(+/-)(1/x)=-100000000000000");
        assertExpression("0+0.00000000000001(1/x)(1/x)=0.00000000000001");


        //for second screen
        assertSequence("0.2", "reciproc(5)", "5(1/x)");
        assertSequence("5", "reciproc(reciproc(5))", "5(1/x)(1/x)");
        assertSequence("0.2", "1 + reciproc(5)", "1+5(1/x)");
        assertSequence("5", "1 + reciproc(reciproc(5))", "1+5(1/x)(1/x)");

        //for divide by zero
        assertSequence(DIVIDE_BY_ZERO_MESSAGE, "reciproc(0)", "0(1/x)");
        assertSequence(DIVIDE_BY_ZERO_MESSAGE, "1 + reciproc(0)", "1+0(1/x)");
    }

    @Test
    public void testSqrt() {
        assertExpression("9s=3");
        assertExpression("81ss=3");
        assertExpression("6561sss=3");
        assertExpression("9s+81s=12");

        assertExpression("0.09s=0.3");
        assertExpression("0.0081ss=0.3");
        assertExpression("0.00006561sss=0.3");

        assertExpression("0.00000000000001s=0.0000001");
        assertExpression("0.00000000000001ss=0.000316227766017");

        assertExpression("9999999999999999s=100000000");
        assertExpression("9999999999999999ss=10000");
        assertExpression("9999999999999999sssss=3.16227766016838");


        //for second screen
        assertSequence("10", "sqrt(100)", "100s");
        assertSequence("3", "5 + sqrt(9)", "5+9s");
        assertSequence("3", "sqrt(sqrt(81))", "81ss");
        assertSequence("3", "5 + sqrt(sqrt(81))", "5+81ss");
        assertSequence("3", "", "9s=");
        assertSequence("1", "sqrt(9) +", "9s+1");

        //for bad input
        assertSequence(INVALID_INPUT_MESSAGE, "sqrt(-100)", "100(+/-)s");
        assertSequence(INVALID_INPUT_MESSAGE, "sqrt(-0.09)", "0.09(+/-)s");
        assertSequence(INVALID_INPUT_MESSAGE, "sqrt(-5)", "3-8=s");
        assertSequence(INVALID_INPUT_MESSAGE, "sqrt(-9999999999999999)", "9999999999999999(+/-)s");
        assertSequence(INVALID_INPUT_MESSAGE, "sqrt(-0.00000000000001)", "0.00000000000001(+/-)s");

    }

    @Test
    public void testOperatorCombinations() {
        assertExpression("5+9-1=13");
        assertExpression("5-9+5-2=-1");
        assertExpression("2*3/4=1.5");
        assertExpression("-5*2/4=-2.5");
        assertExpression("3+5/2=4");
        assertExpression("5-1*4=16");
        assertExpression("3+4-5/2*8=8");

        assertExpression("0.00000000000001+0.00000000000001/0.00000000000001=2");
        assertExpression("9999999999999999+5-4/10=1000000000000000");
        assertExpression("9999999999999999+9999999999999999/9999999999999999=2");

        assertExpression("9s+5=8");
        assertExpression("5+9s=8");
        assertExpression("5+81ss=8");
        assertExpression("81ss+5=8");

        assertExpression("5(1/x)+1=1.2");
        assertExpression("1+5(1/x)=1.2");


        //for second screen
        //5 + 9 * 2 - 1 / 3 =
        assertSequence("27", "5 + 9 * 2 - 1 /", "5+9*2-1/");

        clickSequence("3=");

        assertFirstScreen("9");
        assertSecondScreen("");

        //3 + 5 + + -
        assertSequence("8", "3 + 5 -", "3+5++-");

        click("*");

        assertFirstScreen("8");
        assertSecondScreen("3 + 5 *");

        click("/");

        assertFirstScreen("8");
        assertSecondScreen("3 + 5 /");

        click("2");
        click("=");

        assertFirstScreen("4");
        assertSecondScreen("");

        //100+10%=
        assertSequence("110", "100+10%=");

        assertSequence("5", "10 +", "10+9s5");
        assertSequence("5", "10 +", "10+10%5");

    }

    @Test
    public void testBackSpace() {
        //one symbol
        assertSequence("0", "", "1<");

        //many symbols
        assertSequence("0", "", "123<<<");

        //for second operand
        assertSequence("12", "12 +", "12+");

        clickSequence("<");
        assertFirstScreen("1");

        clickSequence("<");
        assertFirstScreen("0");

        clickSequence("34");
        assertFirstScreen("34");

        clickSequence("<");
        assertFirstScreen("3");

        clickSequence("<");
        assertFirstScreen("0");

        assertSequence("0.", "", "0.00000000000001<<<<<<<<<<<<<<");
        clickSequence("<");
        assertFirstScreen("0");
    }

    @Test
    @Ignore
    public void testEquals() {
        assertSequence("0", "", "==");
        assertSequence("13", "", "3+5==");
    }

    @Test
    //@Ignore
    public void testFirstScreenOverflow() {
        assertSequence("9999999999999999", "9999999999999999");
        String text = firstScreen.getText();
        click("1");
        assertFirstScreen(text);

        click("+");
        click("5");

        assertFirstScreen("5");

    }

    @Test
    //@Ignore
    public void testSecondScreenOverflow() {
        assertSequence("1.00000001e+16", SCREEN_OVERFLOW_SYMBOL + "9999999999999 + 99999999 + 1 +", "9999999999999999+99999999+1+");
        assertSequence("987654321", SCREEN_OVERFLOW_SYMBOL + "6789 * 987654321 / 123456789 -", "123456789*987654321/123456789-");
    }

    @Test
    public void testMemoryButtonsOnZero() {
        click("MS");

        assertMemoryScreen("");

        click("M+");

        assertMemoryScreen("");

        click("M-");

        assertMemoryScreen("");

    }

    @Test
    //@Ignore
    public void testMemoryStoreAndRecall() {
        // for single number
        assertSequence("5", "", "5(MS)");
        assertMemoryScreen("M");

        click("C");

        assertFirstScreen("0");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("MR");

        assertFirstScreen("5");
        assertSecondScreen("");
        assertMemoryScreen("M");

        formatter.pressClearButton();
        click("MC");

        //in expression
        assertSequence("8", "5 +", "5+8(MS)");
        assertMemoryScreen("M");

        click("C");

        assertFirstScreen("0");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("MR");

        assertFirstScreen("8");
        assertSecondScreen("");
        assertMemoryScreen("M");

        formatter.pressClearButton();
        click("MC");

        //for result
        assertSequence("13", "", "5+8=(MS)");
        assertMemoryScreen("M");

        click("C");

        assertFirstScreen("0");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("MR");

        assertFirstScreen("13");
        assertSecondScreen("");
        assertMemoryScreen("M");
    }

    @Test
    //@Ignore
    public void testMemoryPlus() {
        assertSequence("5", "", "5(M+)");
        assertMemoryScreen("M");

        clickSequence("C(MR)");

        assertFirstScreen("5");
        assertSecondScreen("");
        assertMemoryScreen("M");

        clickSequence("C3(M+)C(MR)");


        assertFirstScreen("8");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("1");

        assertFirstScreen("1");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("MR");

        assertFirstScreen("8");
        assertSecondScreen("");
        assertMemoryScreen("M");

    }

    @Test
    //@Ignore
    public void testMemoryMinus() {
        assertSequence("5", "", "5(M-)");
        assertMemoryScreen("M");

        clickSequence("C(MR)");

        assertFirstScreen("-5");
        assertSecondScreen("");
        assertMemoryScreen("M");

        clickSequence("C3(M-)C(MR)");

        assertFirstScreen("-8");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("1");

        assertFirstScreen("1");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("MR");

        assertFirstScreen("-8");
        assertSecondScreen("");
        assertMemoryScreen("M");

    }

    @Test
    //@Ignore
    public void testMemoryClear() {
        assertSequence("5", "", "5(MS)");
        assertMemoryScreen("M");

        click("C");

        assertFirstScreen("0");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("MR");

        assertFirstScreen("5");
        assertSecondScreen("");
        assertMemoryScreen("M");

        clickSequence("(MC)C(MR)");

        assertFirstScreen("0");
        assertSecondScreen("");
        assertMemoryScreen("");
    }

    @Test
    public void testInputAfterResult() {
        assertSequence("0.", "", "5+8=.");

        assertSequence("58", "55+3=");

        click("7");
        assertFirstScreen("7");
        assertSecondScreen("");

        click("=");
        assertFirstScreen("10");
        assertSecondScreen("");

        //after sqrt
        assertSequence("1", "50 + 1", "50+2%");

        click("5");
        assertFirstScreen("5");
        assertSecondScreen("50 +");


    }

    @Test
    //@Ignore
    public void testScienceRepresentation() {

        assertExpression("9999999999999999+1=1e+16");
        assertExpression("9999999999999999+1=-1=9999999999999999");
        assertExpression("9999999999999999*999=9.989999999999999e+18");
        assertExpression("0.00000000000001/10==1e-16");
        assertExpression("0.00000000000001/10==*10=0.000000000000001");

        assertExpression("1/365==7.506098705197973e-6");
        assertExpression("9999999999*=9.999999998e+19");

        //for second screen
        assertSequence("9.999999998e+19", "9.999999998e+19 +", "9999999999*=+");
        assertSequence("7.506098705197973e-6", "7.506098705197973e-6 +", "1/365==+");


    }

    @Test
    //@Ignore
    public void testTextSizing() {
        clickSequence("999999999999");

        assertEquals(FIRST_SCREEN_BIG_FONT_SIZE, firstScreen.getFont().getSize(), 0.1);

        click("9");

        assertEquals(FIRST_SCREEN_MEDIUM_FONT_SIZE, firstScreen.getFont().getSize(), 0.1);

        click("1/x");

        assertEquals(FIRST_SCREEN_SMALL_FONT_SIZE, firstScreen.getFont().getSize(), 0.1);
    }

    @Test
    public void testOperationAfterResult() {
        assertExpression("55-5=50-7=43");
        assertExpression("10+5=12-3=9");
        assertExpression("10+5=12-9s=9");
        assertExpression("1+9s=12-3=9");
        assertExpression("9999999999999999-1=+1=9999999999999999");
        assertExpression("0.00000000000001+0.00000000000001=-1=-0.99999999999998");
    }

    @Test
    //@Ignore
    public void testRounding() {
        assertExpression("3s*=3");
        assertExpression("7s*=7");
        assertExpression("9999999999999999/568=1.76056338028169e+13");
        assertExpression("1-0.00000000000001=0.99999999999999");
        assertExpression("1000-0.00000000000001=1000");
    }

    @Test
    //@Ignore
    public void testKeyboardKeys() throws Exception {
        for (CalculatorView.ButtonEnum b : CalculatorView.ButtonEnum.values()) {
            KeyCode keyCode = null;
            try {
                keyCode = b.getKeyCode();
            } catch (Exception e) {
                if (e.getMessage().contains("No key")) {
                    //ignoring buttons with no keys
                    return;
                } else {
                    throw e;
                }
            }
            switch (keyCode) {
                case NUMPAD0:
                    pressKey(KeyCode.NUMPAD1);
                    pressKey(KeyCode.NUMPAD0);
                    assertFirstScreen("10");
                    break;
                case NUMPAD1:
                    pressKey(keyCode);
                    assertFirstScreen("1");
                    break;
                case NUMPAD2:
                    pressKey(keyCode);
                    assertFirstScreen("2");
                    break;
                case NUMPAD3:
                    pressKey(keyCode);
                    assertFirstScreen("3");
                    break;
                case NUMPAD4:
                    pressKey(keyCode);
                    assertFirstScreen("4");
                    break;
                case NUMPAD5:
                    pressKey(keyCode);
                    assertFirstScreen("5");
                    break;
                case NUMPAD6:
                    pressKey(keyCode);
                    assertFirstScreen("6");
                    break;
                case NUMPAD7:
                    pressKey(keyCode);
                    assertFirstScreen("7");
                    break;
                case NUMPAD8:
                    pressKey(keyCode);
                    assertFirstScreen("8");
                    break;
                case NUMPAD9:
                    pressKey(keyCode);
                    assertFirstScreen("9");
                    break;
                case MULTIPLY:
                    pressKey(keyCode);
                    assertSecondScreen("0 *");
                    break;
                case ADD:
                    pressKey(keyCode);
                    assertSecondScreen("0 +");
                    break;
                case SUBTRACT:
                    pressKey(keyCode);
                    assertSecondScreen("0 -");
                    break;
                case DECIMAL:
                    pressKey(keyCode);
                    assertFirstScreen("0.");
                    break;
                case DIVIDE:
                    pressKey(keyCode);
                    assertSecondScreen("0 /");
                    break;
                case BACK_SPACE:
                    pressKey(KeyCode.NUMPAD1);
                    pressKey(KeyCode.NUMPAD2);
                    assertFirstScreen("12");
                    pressKey(KeyCode.BACK_SPACE);
                    assertFirstScreen("1");
                    pressKey(KeyCode.BACK_SPACE);
                    assertFirstScreen("0");
                    break;
                case ENTER:
                    pressKey(KeyCode.NUMPAD1);
                    pressKey(KeyCode.ADD);
                    pressKey(KeyCode.NUMPAD2);
                    pressKey(KeyCode.ENTER);
                    assertFirstScreen("3");
                    break;
                case ESCAPE:
                    pressKey(KeyCode.NUMPAD1);
                    pressKey(KeyCode.NUMPAD2);
                    pressKey(KeyCode.ESCAPE);

                    assertFirstScreen("0");

                    pressKey(KeyCode.NUMPAD1);
                    pressKey(KeyCode.ADD);
                    pressKey(KeyCode.NUMPAD2);

                    assertFirstScreen("2");
                    assertSecondScreen("1 +");

                    pressKey(KeyCode.ESCAPE);

                    assertFirstScreen("0");
                    assertSecondScreen("");
            }
            formatter.pressClearButton();
        }

    }

    @Test
    //@Ignore
    public void testNumberOverflow() {
        assertSequence(OVERFLOW_MESSAGE, "1*0.0000000001==========*=========*=========*0.1=");
        assertSequence(OVERFLOW_MESSAGE, "1*10000000000==========*=========*=========*10==");
    }

    @Test
    //@Ignore
    public void testClipboard() throws InterruptedException, IOException, UnsupportedFlavorException {
        String clip;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();

        //simple
        pressKey(KeyCode.NUMPAD1);
        pressKey(KeyCode.NUMPAD2);
        press(KeyCode.CONTROL, KeyCode.C);
        release(KeyCode.CONTROL, KeyCode.C);
        sleep(DELAY);

        assertFirstScreen("12");
        assertSecondScreen("");
        clip = (String) clipboard.getData(DataFlavor.stringFlavor);
        assertEquals("12", clip);

        formatter.pressClearButton();

        press(KeyCode.CONTROL, KeyCode.V);
        release(KeyCode.CONTROL, KeyCode.V);
        sleep(DELAY);

        assertFirstScreen("12");
        assertSecondScreen("");

        formatter.pressClearButton();

        //with science
        for (int i = 0; i < 16; i++) {
            pressKey(KeyCode.NUMPAD9);
        }
        pressKey(KeyCode.MULTIPLY);
        for (int i = 0; i < 16; i++) {
            pressKey(KeyCode.NUMPAD9);
        }
        pressKey(KeyCode.ENTER);

        assertFirstScreen("9.999999999999998e+31");
        assertSecondScreen("");

        press(KeyCode.CONTROL, KeyCode.C);
        release(KeyCode.CONTROL, KeyCode.C);
        sleep(DELAY);

        clip = (String) clipboard.getData(DataFlavor.stringFlavor);
        assertEquals("9.999999999999998e+31", clip);

        formatter.pressClearButton();

        press(KeyCode.CONTROL, KeyCode.V);
        release(KeyCode.CONTROL, KeyCode.V);
        sleep(DELAY);

        assertFirstScreen("9.999999999999998");
        assertSecondScreen("");

    }

    private void assertMemoryScreen(String s) {
        assertEquals(s, memoryScreen.getText());
    }

    private void assertFirstScreen(String s) {
        assertEquals(s, firstScreen.getText());
    }

    private void assertSecondScreen(String s) {
        assertEquals(s, secondScreen.getText());
    }

    private void pressKey(KeyCode keyCode) throws InterruptedException {
        press(keyCode);
        release(keyCode);
        sleep(DELAY);
    }
}