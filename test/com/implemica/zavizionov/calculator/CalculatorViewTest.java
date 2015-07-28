package com.implemica.zavizionov.calculator;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CalculatorViewTest extends GuiTest {

    private static final String DIVIDE_BY_ZERO_MESSAGE = "\u0414\u0435\u043B\u0435\u043D\u0438\u0435 \u043D\u0430 \u043D\u043E\u043B\u044C \u043D\u0435\u0432\u043E\u0437\u043C\u043E\u0436\u043D\u043E";
    private static final String OVERFLOW_MESSAGE = "\u041F\u0435\u0440\u0435\u043F\u043E\u043B\u043D\u0435\u043D\u0438\u0435";
    private static final String SQRT_BUTTON_LABEL = "\u221A";
    private static final String INVERT_BUTTON_LABEL = "\u00B1";
    private static final String BACKSPACE_BUTTON_LABEL = "\u2190";
    private static final String SCREEN_OVERFLOW_SYMBOL = "\u2039\u2039";
    private static final int FIRST_SCREEN_BIG_FONT_SIZE = 22;
    private static final int FIRST_SCREEN_MEDIUM_FONT_SIZE = 18;
    private static final int FIRST_SCREEN_SMALL_FONT_SIZE = 13;
    private static final long DELAY = 100;
    private static final String DOT_BUTTON_ID = ",";

    Parent root;
    TextField firstScreen;
    TextField secondScreen;
    Label memoryScreen;
    CalculatorFormatter formatter;

    public Parent getRootNode() {
        CalculatorView view = new CalculatorView();
        root = view.getRoot();
        firstScreen = view.firstScreen;
        secondScreen = view.secondScreen;
        memoryScreen = view.memoryScreen;
        formatter = view.getController();
        return root;
    }

    @Before
    public void initialize() {
        formatter.pressClearButton();
        formatter.pressClearMemoryButton();
        sleep(100);
    }

    private void assertDigitButton(String buttonId) {
        Button b = find(buttonId);
        if (buttonId.equals("0")) {
            assertSequence("10", "10");
        } else {
            String result = buttonId + buttonId;
            assertSequence(result, result);
        }
    }

    private void clickSequence(String sequence) {
        for (char c : sequence.toCharArray()) {
            if(c == '.') c = DOT_BUTTON_ID.charAt(0);
            click(Character.toString(c));
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

        formatter.pressClearButton();

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

        formatter.pressClearButton();

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
        assertSequence("8", "3+5=");
        assertSequence("3", "0+3=");
        assertSequence("9", "3+==");
        assertSequence("-13.5", "0.5-14=");
    }

    @Test
    public void testMinus() {
        assertSequence("2", "5-3=");
        assertSequence("-3", "0-3=");
        assertSequence("-3", "3-==");
    }

    @Test
    public void testDivide() {
        assertSequence("2.5", "5/2=");
        assertSequence("0", "0/2=");
        assertSequence("0.2", "5/==");
    }

    @Test
    public void testDivideByZero() {
        assertSequence(DIVIDE_BY_ZERO_MESSAGE, "0/0=");
        assertSequence(DIVIDE_BY_ZERO_MESSAGE, "3/0=");
    }

    @Test
    public void testMultiply() {
        assertSequence("15", "5*3=");
        assertSequence("0", "5*0=");
        assertSequence("125", "5*==");
    }

    @Test
    public void testPercent() {
        assertSequence("1", "50 + 1", "50+2%");
        assertSequence("40", "200 + 40", "200+10%%");
        assertSequence("0", "0", "20%");
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
    }

    @Test
    public void testInvert() {
        assertSequence("-5", "5" + INVERT_BUTTON_LABEL);
        assertSequence("-5", "1 +", "1+5" + INVERT_BUTTON_LABEL);
        assertSequence("0", "0" + INVERT_BUTTON_LABEL);
    }

    @Test
    public void testReverse() {
        //TODO refactor
        //1/5
        click("5");
        click("1/x");

        assertFirstScreen("0.2");
        assertSecondScreen("reciproc(5)");

        formatter.pressClearButton();

        //1+ 1/5
        click("1");
        click("+");
        click("5");
        click("1/x");

        assertFirstScreen("0.2");
        assertSecondScreen("1 + reciproc(5)");

        formatter.pressClearButton();

        //1/0
        click("0");
        click("1/x");

        assertFirstScreen(DIVIDE_BY_ZERO_MESSAGE);
        assertSecondScreen("reciproc(0)");

        formatter.pressClearButton();

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
        assertSequence("10", "sqrt(100)", "100" + SQRT_BUTTON_LABEL);
        assertSequence("3", "5 + sqrt(9)", "5+9" + SQRT_BUTTON_LABEL);
        assertSequence("3", "sqrt(sqrt(81))", "81" + SQRT_BUTTON_LABEL + SQRT_BUTTON_LABEL);
        assertSequence("3", "5 + sqrt(sqrt(81))", "5+81" + SQRT_BUTTON_LABEL + SQRT_BUTTON_LABEL);
        assertSequence("3", "", "9" + SQRT_BUTTON_LABEL + "=");
    }

    @Test
    public void testOperatorCombinations() {
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

        //3+sqrt(9)=
        assertSequence("6", "3+9" + SQRT_BUTTON_LABEL + "=");

        //3+5(+/-)=
        assertSequence("-2", "3+5" + INVERT_BUTTON_LABEL + "=");

    }

    @Test
    public void testBackSpace() {
        //one symbol
        click("1");

        assertFirstScreen("1");

        click(BACKSPACE_BUTTON_LABEL);

        assertFirstScreen("0");

        formatter.pressClearButton();

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

        formatter.pressClearButton();

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
    public void testEquals() {
        assertSequence("0", "", "==");
        assertSequence("13", "", "3+5==");
    }

    @Test
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
    public void testSecondScreenOverflow() {
        assertSequence("1.00000001e+16", SCREEN_OVERFLOW_SYMBOL + "9999999999999 + 99999999 + 1 +", "9999999999999999+99999999+1+");
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

        formatter.pressClearButton();
        formatter.pressClearMemoryButton();

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

        formatter.pressClearButton();
        formatter.pressClearMemoryButton();

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
        assertSequence("58", "55+3=");
        click("3");

        assertFirstScreen("3");
        assertSecondScreen("");

        click("=");

        assertFirstScreen("6");
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

        assertSequence("1e+16", "", "9999999999999999+1=");

        click("-");

        assertFirstScreen("1e+16");
        assertSecondScreen("1e+16 -");

        clickSequence("1=");

        assertFirstScreen("9999999999999999");
        assertSecondScreen("");

        clickSequence("*999=");

        assertFirstScreen("9.989999999999999e+18");
        assertSecondScreen("");

        formatter.pressClearButton();

        assertSequence("7.506098705197973e-6", "", "1/365==");
        assertSequence("9.999999998e+19", "", "9999999999*=");


    }

    @Test
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
        assertSequence("50", "55-5=");

        clickSequence("-7=");

        assertFirstScreen("43");
    }

    @Test
    public void testRounding() {
        assertSequence("3", "3" + SQRT_BUTTON_LABEL + "*=");
        assertSequence("7", "7" + SQRT_BUTTON_LABEL + "*=");
    }

    @Test
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
    public void testNumberOverflow() {
        assertSequence(OVERFLOW_MESSAGE, "1*0.0000000001==========*=========*=========*0.1=");
    }

    @Test
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