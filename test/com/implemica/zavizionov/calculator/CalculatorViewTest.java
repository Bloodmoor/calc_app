package com.implemica.zavizionov.calculator;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import static org.junit.Assert.assertEquals;

public class CalculatorViewTest extends GuiTest {

    private static final String DIVIDE_BY_ZERO_MESSAGE = "\u0414\u0435\u043B\u0435\u043D\u0438\u0435 \u043D\u0430 \u043D\u043E\u043B\u044C \u043D\u0435\u0432\u043E\u0437\u043C\u043E\u0436\u043D\u043E";
    private static final String OVERFLOW_MESSAGE = "\u041F\u0435\u0440\u0435\u043F\u043E\u043B\u043D\u0435\u043D\u0438\u0435";
    private static final String SQRT_BUTTON_LABEL = "\u221A";
    private static final String INVERT_BUTTOD_LABEL = "\u00B1";
    private static final String BACKSPACE_BUTTON_LABEL = "\u2190";
    private static final String SCREEN_OVRFLOW_SYMBOL = "\u2039\u2039";
    private static final int FIRST_SCREEN_BIG_FONT_SIZE = 22;
    private static final int FIRST_SCREEN_MEDIUM_FONT_SIZE = 18;
    private static final int FIRST_SCREEN_SMALL_FONT_SIZE = 13;
    private static final long DELAY = 500;
    private static final String DOT_BUTTON_ID = ",";

    Parent root;
    TextField firstScreen;
    TextField secondScreen;
    Label memoryScreen;
    CalculatorController controller;

    public Parent getRootNode() {
        CalculatorView view = new CalculatorView();
        root = view.getRoot();
        firstScreen = view.firstScreen;
        secondScreen = view.secondScreen;
        memoryScreen = view.memoryScreen;
        controller = view.getController();
        return root;
    }

    @Before
    public void initialize() {
        controller.pressClearButton();
        controller.pressClearMemoryButton();
        sleep(100);
    }

    private void assertDigitButton(String buttonId) {
        Button b = find(buttonId);
        if (buttonId.equals("0")) {
            click(b);
            assertFirstScreen(b.getText());
            click(b);
            assertFirstScreen(b.getText());
            firstScreen.clear();
            click((Button) find("1"));
            click(b);
            assertFirstScreen("10");
            firstScreen.clear();
        } else {
            click(b);
            assertFirstScreen(b.getText());
            click(b);
            assertFirstScreen(b.getText() + b.getText());
            firstScreen.clear();
        }
    }

    @Test
    public void testDigitButtons() {
        for (int i = 0; i < 9; i++) {
            assertDigitButton(Integer.toString(i));
        }
    }

    @Test
    public void testClearEntryButton() {
        //firstScreen clearing
        firstScreen.setText("123");
        secondScreen.setText("123");
        click("CE");
        assertFirstScreen("0");
        assertSecondScreen("123");

        controller.pressClearButton();

        //
        click("1");
        click("+");

        assertFirstScreen("1");
        assertSecondScreen("1 +");

        click("5");

        assertFirstScreen("5");
        assertSecondScreen("1 +");

        click("CE");

        assertFirstScreen("0");
        assertSecondScreen("1 +");

        click("2");
        click("=");

        assertFirstScreen("3");
        assertSecondScreen("");

    }

    @Test
    public void testClearButton() {
        //firstScreen clearing
        firstScreen.setText("Some text");
        secondScreen.setText("Some text");
        click("C");
        assertFirstScreen("0");
        assertSecondScreen("");

        controller.pressClearButton();

        //
        click("1");
        click("+");

        assertFirstScreen("1");
        assertSecondScreen("1 +");

        click("5");

        assertFirstScreen("5");
        assertSecondScreen("1 +");

        click("C");

        assertFirstScreen("0");
        assertSecondScreen("");

    }

    @Test
    public void testPlus() {
        //5 + 3
        click("5");
        click("+");

        assertFirstScreen("5");
        assertSecondScreen("5 +");

        click("3");
        click("=");

        assertFirstScreen("8");
        assertSecondScreen("");

        controller.pressClearButton();

        //0 + 3
        click("+");

        assertFirstScreen("0");
        assertSecondScreen("0 +");

        click("3");
        click("=");

        assertFirstScreen("3");
        assertSecondScreen("");

        controller.pressClearButton();

        //3 + = =
        click("3");
        click("+");

        assertFirstScreen("3");
        assertSecondScreen("3 +");

        click("=");

        assertFirstScreen("6");
        assertSecondScreen("");

        click("=");

        assertFirstScreen("9");
        assertSecondScreen("");

    }

    @Test
    public void testMinus() {
        //5 - 3
        click("5");
        click("-");

        assertFirstScreen("5");
        assertSecondScreen("5 -");

        click("3");
        click("=");

        assertFirstScreen("2");
        assertSecondScreen("");

        controller.pressClearButton();

        //0 - 3
        click("-");

        assertFirstScreen("0");
        assertSecondScreen("0 -");

        click("3");
        click("=");

        assertFirstScreen("-3");
        assertSecondScreen("");

        controller.pressClearButton();

        //3 - = =
        click("3");
        click("-");

        assertFirstScreen("3");
        assertSecondScreen("3 -");

        click("=");

        assertFirstScreen("0");
        assertSecondScreen("");

        click("=");

        assertFirstScreen("-3");
        assertSecondScreen("");
    }

    @Test
    public void testDivide() {
        //5/2
        click("5");
        click("/");

        assertFirstScreen("5");
        assertSecondScreen("5 /");

        click("2");
        click("=");

        assertFirstScreen("2.5");
        assertSecondScreen("");

        controller.pressClearButton();

        //0/2
        click("/");

        assertFirstScreen("0");
        assertSecondScreen("0 /");

        click("2");
        click("=");

        assertFirstScreen("0");
        assertSecondScreen("");

        controller.pressClearButton();

        //5 / = =
        click("5");
        click("/");

        assertFirstScreen("5");
        assertSecondScreen("5 /");

        click("=");

        assertFirstScreen("1");
        assertSecondScreen("");

        click("=");

        assertFirstScreen("0.2");
        assertSecondScreen("");
    }

    @Test
    public void testDivideByZero() {
        //0/0
        click("/");

        assertFirstScreen("0");
        assertSecondScreen("0 /");

        click("0");
        click("=");

        assertFirstScreen(DIVIDE_BY_ZERO_MESSAGE);
        assertSecondScreen("0 /");

        controller.pressClearButton();

        //3/0
        click("5");
        click("/");

        assertFirstScreen("5");
        assertSecondScreen("5 /");

        click("0");
        click("=");

        assertFirstScreen(DIVIDE_BY_ZERO_MESSAGE);
        assertSecondScreen("5 /");
    }

    @Test
    public void testMultiply() {
        //5 * 3
        click("5");
        click("*");

        assertFirstScreen("5");
        assertSecondScreen("5 *");

        click("3");
        click("=");

        assertFirstScreen("15");
        assertSecondScreen("");

        controller.pressClearButton();

        //5 * 0
        click("5");
        click("*");

        assertFirstScreen("5");
        assertSecondScreen("5 *");

        click("0");
        click("=");

        assertFirstScreen("0");
        assertSecondScreen("");

        controller.pressClearButton();

        //5 * = =
        click("5");
        click("*");

        assertFirstScreen("5");
        assertSecondScreen("5 *");

        click("=");

        assertFirstScreen("25");
        assertSecondScreen("");

        click("=");

        assertFirstScreen("125");
        assertSecondScreen("");

    }

    @Test
    public void testPercent() {
        //50 + 2%
        click("5");
        click("0");
        click("+");

        assertFirstScreen("50");
        assertSecondScreen("50 +");

        click("2");
        click("%");

        assertFirstScreen("1");
        assertSecondScreen("50 + 1");

        controller.pressClearButton();

        //200 + 10 % %
        click("2");
        click("0");
        click("0");
        click("+");

        assertFirstScreen("200");
        assertSecondScreen("200 +");

        click("1");
        click("0");
        click("%");

        assertFirstScreen("20");
        assertSecondScreen("200 + 20");

        click("%");

        assertFirstScreen("40");
        assertSecondScreen("200 + 40");

        controller.pressClearButton();

        //input after result
        click("5");
        click("0");
        click("+");
        click("2");
        click("%");

        assertFirstScreen("1");
        assertSecondScreen("50 + 1");

        click("5");

        assertFirstScreen("5");
        //TODO
//        assertSecondScreen("50 +");

        controller.pressClearButton();

        //20%
        click("2");
        click("0");
        click("%");

        assertFirstScreen("0");
        assertSecondScreen("0");
    }

    @Test
    public void testInvert() {
        //(±)5
        click("5");
        click(INVERT_BUTTOD_LABEL);

        assertFirstScreen("-5");
        assertSecondScreen("");

        controller.pressClearButton();

        //1 + (±)5
        click("1");
        click("+");
        click("5");
        click(INVERT_BUTTOD_LABEL);

        assertFirstScreen("-5");
        assertSecondScreen("1 +");

        controller.pressClearButton();

        //(±)0
        click("0");
        click(INVERT_BUTTOD_LABEL);

        assertFirstScreen("0");
        assertSecondScreen("");
    }

    @Test
    public void testReverse() {
        //1/5
        click("5");
        click("1/x");

        assertFirstScreen("0.2");
        assertSecondScreen("reciproc(5)");

        controller.pressClearButton();

        //1+ 1/5
        click("1");
        click("+");
        click("5");
        click("1/x");

        assertFirstScreen("0.2");
        assertSecondScreen("1 + reciproc(5)");

        controller.pressClearButton();

        //1/0
        click("0");
        click("1/x");

        assertFirstScreen(DIVIDE_BY_ZERO_MESSAGE);
        assertSecondScreen("reciproc(0)");

        controller.pressClearButton();

        //1 + 1/0
        //1/0
        click("1");
        click("+");
        click("0");
        click("1/x");

        assertFirstScreen(DIVIDE_BY_ZERO_MESSAGE);
        assertSecondScreen("1 + reciproc(0)");
    }

    @Test
    public void testSqrt() {
        //single sqrt
        click("1");
        click("0");
        click("0");
        click(SQRT_BUTTON_LABEL);

        assertFirstScreen("10");
        assertSecondScreen("sqrt(100)");

        controller.pressClearButton();

        //sqrt after +
        click("5");
        click("+");
        click("9");
        click(SQRT_BUTTON_LABEL);

        assertFirstScreen("3");
        assertSecondScreen("5 + sqrt(9)");

        controller.pressClearButton();

        //sqrt after sqrt
        click("8");
        click("1");
        click(SQRT_BUTTON_LABEL);
        click(SQRT_BUTTON_LABEL);

        assertFirstScreen("3");
        assertSecondScreen("sqrt(sqrt(81))");

        controller.pressClearButton();

        //sqrt after sqrt after +
        click("5");
        click("+");
        click("8");
        click("1");
        click(SQRT_BUTTON_LABEL);
        click(SQRT_BUTTON_LABEL);

        assertFirstScreen("3");
        assertSecondScreen("5 + sqrt(sqrt(81))");

        controller.pressClearButton();

        //sqrt and =
        click("9");
        click(SQRT_BUTTON_LABEL);

        assertFirstScreen("3");
        assertSecondScreen("sqrt(9)");

        click("=");

        assertFirstScreen("3");
        assertSecondScreen("");

        click("+");
        click("5");
        click("=");

        assertFirstScreen("8");
        assertSecondScreen("");

    }

    @Test
    public void testOperatorCombinations() {
        //5 + 9 * 2 - 1 / 3 =
        click("5");
        click("+");
        click("9");
        click("*");

        assertFirstScreen("14");
        assertSecondScreen("5 + 9 *");

        click("2");
        click("-");

        assertFirstScreen("28");
        assertSecondScreen("5 + 9 * 2 -");

        click("1");
        click("/");

        assertFirstScreen("27");
        assertSecondScreen("5 + 9 * 2 - 1 /");

        click("3");
        click("=");

        assertFirstScreen("9");
        assertSecondScreen("");

        controller.pressClearButton();

        //3 + 5 + + -
        click("3");
        click("+");
        click("5");
        click("+");

        assertFirstScreen("8");
        assertSecondScreen("3 + 5 +");

        click("+");

        assertFirstScreen("8");
        assertSecondScreen("3 + 5 +");

        click("-");

        assertFirstScreen("8");
        assertSecondScreen("3 + 5 -");

        click("*");

        assertFirstScreen("8");
        assertSecondScreen("3 + 5 *");

        click("/");

        assertFirstScreen("8");
        assertSecondScreen("3 + 5 /");

        click("2");

        click("-");

        assertFirstScreen("4");
        assertSecondScreen("3 + 5 / 2 -");

        click("1");
        click("=");

        assertFirstScreen("3");
        assertSecondScreen("");

    }

    @Test
    public void testEquals() {
        //= =  after init

        click("=");

        assertFirstScreen("0");
        assertSecondScreen("");

        click("=");

        assertFirstScreen("0");
        assertSecondScreen("");

        controller.pressClearButton();

        //3 + 5 = =
        click("3");
        click("+");
        click("5");
        click("=");

        assertFirstScreen("8");
        assertSecondScreen("");

        click("=");

        assertFirstScreen("13");
        assertSecondScreen("");

    }

    @Test
    public void testBackSpace() {
        //one symbol
        click("1");

        assertFirstScreen("1");

        click(BACKSPACE_BUTTON_LABEL);

        assertFirstScreen("0");

        controller.pressClearButton();

        //many symbols
        click("1");
        click("2");
        click("3");

        assertFirstScreen("123");

        click(BACKSPACE_BUTTON_LABEL);

        assertFirstScreen("12");

        click(BACKSPACE_BUTTON_LABEL);

        assertFirstScreen("1");

        click("5");

        assertFirstScreen("15");

        click(BACKSPACE_BUTTON_LABEL);
        click(BACKSPACE_BUTTON_LABEL);

        assertFirstScreen("0");

        controller.pressClearButton();

        //for second operand
        click("1");
        click("2");
        click("+");

        assertFirstScreen("12");
        assertSecondScreen("12 +");

        click(BACKSPACE_BUTTON_LABEL);

        assertFirstScreen("1");
        assertSecondScreen("12 +");

        click("3");
        click("4");

        assertFirstScreen("34");
        assertSecondScreen("12 +");

        click(BACKSPACE_BUTTON_LABEL);

        assertFirstScreen("3");
        assertSecondScreen("12 +");

        click(BACKSPACE_BUTTON_LABEL);

        assertFirstScreen("0");
        assertSecondScreen("12 +");

    }

    @Test
    public void testFirstScreenOverflow() {
        for (int i = 0; i < 16; i++) {
            click("9");
        }
        String text = firstScreen.getText();
        click("1");
        assertFirstScreen(text);

        click("+");
        click("5");

        assertFirstScreen("5");

    }

    @Test
    public void testSecondScreenOverflow() {
        for(int i = 0; i < 16; i++){
            click("9");
        }
        click("+");
        for(int i = 0; i < 8; i++){
            click("9");
        }
        click("+");
        assertSecondScreen("9999999999999999 + 99999999 +");
        click("1");
        click("+");
        assertSecondScreen(SCREEN_OVRFLOW_SYMBOL + "9999999999999 + 99999999 + 1 +");
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
    public void testMemoryStoreAndRecall() {
        // for single number
        click("5");
        click("MS");

        assertFirstScreen("5");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("C");

        assertFirstScreen("0");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("MR");

        assertFirstScreen("5");
        assertSecondScreen("");
        assertMemoryScreen("M");

        controller.pressClearButton();
        controller.pressClearMemoryButton();

        //in expression
        click("5");
        click("+");
        click("8");
        click("MS");

        assertFirstScreen("8");
        assertSecondScreen("5 +");
        assertMemoryScreen("M");

        click("C");

        assertFirstScreen("0");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("MR");

        assertFirstScreen("8");
        assertSecondScreen("");
        assertMemoryScreen("M");

        controller.pressClearButton();
        controller.pressClearMemoryButton();

        //for result
        click("5");
        click("+");
        click("8");
        click("=");
        click("MS");

        assertFirstScreen("13");
        assertSecondScreen("");
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
    public void testMemoryPlus() {
        click("5");
        click("M+");

        assertFirstScreen("5");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("C");
        click("MR");

        assertFirstScreen("5");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("C");
        click("3");
        click("M+");
        click("C");
        click("MR");

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
    public void testMemoryMinus() {
        click("5");
        click("M-");

        assertFirstScreen("5");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("C");
        click("MR");

        assertFirstScreen("-5");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("C");
        click("3");
        click("M-");
        click("C");
        click("MR");

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
    public void testMemoryClear() {
        click("5");
        click("MS");

        assertFirstScreen("5");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("C");

        assertFirstScreen("0");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("MR");

        assertFirstScreen("5");
        assertSecondScreen("");
        assertMemoryScreen("M");

        click("MC");
        click("C");
        click("MR");

        assertFirstScreen("0");
        assertSecondScreen("");
        assertMemoryScreen("");
    }

    @Test
    public void testInputAfterResult() {
        click("5");
        click("5");
        click("+");
        click("3");
        click("=");

        assertFirstScreen("58");
        assertSecondScreen("");

        click("3");

        assertFirstScreen("3");
        assertSecondScreen("");

        click("5");

        assertFirstScreen("35");
        assertSecondScreen("");

        click("=");

        assertFirstScreen("38");
        assertSecondScreen("");
    }

    @Test
    public void testScienceRepresentation() {
        for (int i = 0; i < 16; i++) {
            click("9");
        }
        click("+");
        click("1");
        click("=");

        assertFirstScreen("1e+16");
        assertSecondScreen("");

        click("-");

        assertFirstScreen("1e+16");
        assertSecondScreen("1e+16 -");

        click("1");
        click("=");

        assertFirstScreen("9999999999999999");
        assertSecondScreen("");

        click("*");
        click("9");
        click("9");
        click("9");
        click("=");

        assertFirstScreen("9.989999999999999e+18");
        assertSecondScreen("");

        controller.pressClearButton();

        click("1");
        click("/");
        click("3");
        click("6");
        click("5");
        click("=");
        click("=");

        assertFirstScreen("7.506098705197973e-6");
        assertSecondScreen("");

        controller.pressClearButton();

        for (int i = 0; i < 10; i++) {
            click("9");
        }

        click("*");
        click("=");

        assertFirstScreen("9.999999998e+19");
        assertSecondScreen("");

    }

    @Test
    public void testTextSizing() {
        for (int i = 0; i < 12; i++) {
            click("9");
        }

        assertEquals(FIRST_SCREEN_BIG_FONT_SIZE, firstScreen.getFont().getSize(), 0.1);

        click("9");

        assertEquals(FIRST_SCREEN_MEDIUM_FONT_SIZE, firstScreen.getFont().getSize(), 0.1);

        controller.pressClearButton();

        click("1/x");

        assertEquals(FIRST_SCREEN_SMALL_FONT_SIZE, firstScreen.getFont().getSize(), 0.1);
    }

    @Test
    public void testOperationAfterResult() {
        click("5");
        click("5");
        click("-");
        click("5");
        click("=");

        assertFirstScreen("50");
        assertSecondScreen("");

        click("-");

        assertFirstScreen("50");
        assertSecondScreen("50 -");

        click("7");
        click("=");

        assertFirstScreen("43");
        assertSecondScreen("");

    }

    @Test
    public void testRounding() {
        click("3");
        click(SQRT_BUTTON_LABEL);

        assertFirstScreen("1.732050807568877");
        assertSecondScreen("sqrt(3)");

        click("*");
        click("=");

        assertFirstScreen("3");
        assertSecondScreen("");

        controller.pressClearButton();

        click("7");
        click(SQRT_BUTTON_LABEL);

        assertFirstScreen("2.645751311064591");
        assertSecondScreen("sqrt(7)");

        click("*");
        click("=");

        assertFirstScreen("7");
        assertSecondScreen("");


    }

    @Test
    public void testKeyboardKeys() throws Exception {
        for (CalculatorView.ButtonEnum b : CalculatorView.ButtonEnum.values()) {
            KeyCode keyCode = null;
            try {
                keyCode = b.getKeyCode();
            } catch (Exception e) {
                if (e.getMessage().contains("No key")){
                    //ignoring buttons with no keys
                    return;
                }else{
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
            }
            controller.pressClearButton();
        }

    }

    @Test
    public void testNumberOverflow(){
        click("1");
        click("*");
        click("0");
        click(DOT_BUTTON_ID);
        for(int i = 0; i<9; i++){
            click("0");
        }
        click("1");
        for(int i = 0; i<10; i++){
            click("=");
        }
        click("*");
        for(int i = 0; i<9; i++){
            click("=");
        }
        click("*");
        for(int i = 0; i<9; i++){
            click("=");
        }

        assertFirstScreen("1e-10000");

        click("*");
        click("0");
        click(DOT_BUTTON_ID);
        click("1");
        click("=");

        assertFirstScreen(OVERFLOW_MESSAGE);
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

    private void pressFXRobot(KeyCode keyCode) throws InterruptedException {
        pressKey(keyCode);
        Thread.sleep(DELAY);
    }

    private void pressKey(KeyCode keyCode) throws InterruptedException {
        press(keyCode);
        release(keyCode);
        sleep(DELAY);
    }
}