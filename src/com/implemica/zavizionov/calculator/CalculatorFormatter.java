package com.implemica.zavizionov.calculator;

import com.implemica.zavizionov.calculator.exception.DivideByZeroException;
import com.implemica.zavizionov.calculator.exception.NoOperationException;
import com.implemica.zavizionov.calculator.exception.NumberOverflowException;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CalculatorFormatter {

    private static final String DEFAULT_FIRST_SCREEN_TEXT = "0";
    private static final String DEFAULT_SECOND_SCREEN_TEXT = "";
    private static final String MEMORY_INDICATOR = "M";
    private static final String SCREEN_OVERFLOW_SYMBOL = "‹‹";
    private static final String OVERFLOW_MESSAGE = "\u041F\u0435\u0440\u0435\u043F\u043E\u043B\u043D\u0435\u043D\u0438\u0435";
    private static final String DIVIDE_BY_ZERO_MESSAGE = "\u0414\u0435\u043B\u0435\u043D\u0438\u0435 \u043D\u0430 \u043D\u043E\u043B\u044C \u043D\u0435\u0432\u043E\u0437\u043C\u043E\u0436\u043D\u043E";
    private static final int FIRST_SCREEN_BIG_FONT_SIZE = 22;
    private static final int FIRST_SCREEN_MEDIUM_FONT_SIZE = 18;
    private static final int FIRST_SCREEN_SMALL_FONT_SIZE = 13;
    private static final int FIRST_DISPLAY_SIZE = 16;
    private static final int SECOND_DISPLAY_SIZE = 30;

    private static final BigDecimal DELTA = new BigDecimal("1E-" + (FIRST_DISPLAY_SIZE - 2));
    private static final BigDecimal MAX = new BigDecimal("1E" + (FIRST_DISPLAY_SIZE));
    private static final BigDecimal MIN = new BigDecimal("1E-" + (FIRST_DISPLAY_SIZE - 2));
    private static final int SCALE = 29;
    private static final int BIG_FONT_SYMBOLS_COUNT = 12;

    private BigDecimal currentScreenValue = BigDecimal.ZERO;

    private Clipboard clipboard = Clipboard.getSystemClipboard();

    private TextField firstScreen;
    private TextField secondScreen;
    private Label memoryScreen;

    private final CalculatorController controller = CalculatorController.getInstance();

    private boolean isWeakNumber = false;
    private boolean isResult = false;
    private boolean isNext = false;
    private boolean isSqrtResult = false;

    private CalculatorFormatter(TextField firstScreen, TextField secondScreen, Label memoryScreen) {
        this.firstScreen = firstScreen;
        this.secondScreen = secondScreen;
        this.memoryScreen = memoryScreen;
        setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
        setSecondScreenText(DEFAULT_SECOND_SCREEN_TEXT);
    }

    private void ensureSize() {

        if (firstScreen.getLength() <= BIG_FONT_SYMBOLS_COUNT) {
            firstScreen.setStyle("-fx-font-size: " + FIRST_SCREEN_BIG_FONT_SIZE + ";");
        } else if (firstScreen.getLength() < 17) {
            firstScreen.setStyle("-fx-font-size: " + FIRST_SCREEN_MEDIUM_FONT_SIZE + ";");
        } else {
            firstScreen.setStyle("-fx-font-size: " + FIRST_SCREEN_SMALL_FONT_SIZE + ";");
        }
    }

    private boolean isInteger(BigDecimal value) {
        if (value.scale() <= 0) {
            return true;
        }
        BigDecimal nearestInteger = value.setScale(0, BigDecimal.ROUND_HALF_UP);
        return nearestInteger.compareTo(value) == 0;

    }

    private BigDecimal getRounded(BigDecimal value) {
        if (isInteger(value)) {
            return value;
        }
        if (value.compareTo(MIN) < 0) {
            return value;
        }
        BigDecimal nearestInteger = value.setScale(0, BigDecimal.ROUND_HALF_UP);
        if (value.subtract(nearestInteger).abs().compareTo(DELTA) < 0) {
            return nearestInteger;
        }
        return value;
    }

    private void setFirstScreenText(String text) {
        firstScreen.setText(text);
        ensureSize();
    }

    private void setFirstScreenText(BigDecimal value) {
        currentScreenValue = value;
        if (value.toPlainString().replace(".", "").length() <= FIRST_DISPLAY_SIZE) {
            setFirstScreenText(value.toPlainString());
        } else {
            setFirstScreenText(format(getRounded(value)));
        }
    }

    private String format(BigDecimal value) {
        String result;
        DecimalFormat f = new DecimalFormat("0E0");
        f.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        f.setGroupingUsed(false);
        if (value.compareTo(MAX) >= 0) {
            f.setMaximumIntegerDigits(1);
            f.setMaximumFractionDigits(FIRST_DISPLAY_SIZE - 1);
            result = f.format(value);
        } else if (value.compareTo(MIN) < 0 || value.scale() > SCALE) {
            f.setMaximumIntegerDigits(1);
            f.setMaximumFractionDigits(FIRST_DISPLAY_SIZE - 1);
            result = f.format(value);
        } else {
            f.applyPattern("0");
            int countOfIntDigits = value.toPlainString().lastIndexOf(".");
            f.setMaximumFractionDigits(FIRST_DISPLAY_SIZE - countOfIntDigits);
            result = f.format(value);
        }

        if (result.contains("E-")) {
            return result.replace("E", "e");
        } else {
            return result.replace("E", "e+");
        }
    }

    private void appendFirstScreenText(String text) {
        firstScreen.appendText(text);
        ensureSize();
    }

    private void setSecondScreenText(String text) {
        if (text.length() > SECOND_DISPLAY_SIZE) {
            text = SCREEN_OVERFLOW_SYMBOL + text.substring(text.length() - SECOND_DISPLAY_SIZE);
        }
        secondScreen.setText(text);
    }

    private void appendSecondScreenText(String text) {
        text = secondScreen.getText() + text;
        setSecondScreenText(text);
    }

    private void replaceLastSign(String sign) {
        setSecondScreenText(secondScreen.getText().substring(0, secondScreen.getText().length() - 1) + sign);
    }

    public static CalculatorFormatter getInstance(TextField firstScreen, TextField secondScreen, Label memoryScreen) {
        return new CalculatorFormatter(firstScreen, secondScreen, memoryScreen);
    }

    public void pressDigitButton(int digit) {
        if (firstScreen.getLength() == FIRST_DISPLAY_SIZE && !isWeakNumber) {
            return;
        }
        if (firstScreen.getText().equals("0") || isWeakNumber) {
            currentScreenValue = BigDecimal.ZERO;
            setFirstScreenText(Integer.toString(digit));
            isWeakNumber = false;
        } else {
            appendFirstScreenText(Integer.toString(digit));
        }
    }

    public void pressEqualButton() {
        if (isSqrtResult && !isNext) {
            controller.setOperation(Operation.NOOP);
            setSecondScreenText(DEFAULT_SECOND_SCREEN_TEXT);
            isSqrtResult = false;
            return;
        }

        try {
            if (isResult) {
                BigDecimal result;
                if (currentScreenValue.equals(BigDecimal.ZERO)) {
                    result = controller.getResultAfterEqual(new BigDecimal(firstScreen.getText()));
                } else {
                    result = controller.getResultAfterEqual(currentScreenValue);
                }
                setFirstScreenText(result);
                return;
            }

            setFirstScreenText(controller.getResult(new BigDecimal(firstScreen.getText())));
            setSecondScreenText(DEFAULT_SECOND_SCREEN_TEXT);

        } catch (DivideByZeroException e) {
            setFirstScreenText(DIVIDE_BY_ZERO_MESSAGE);
        } catch (NumberOverflowException e) {
            setFirstScreenText(OVERFLOW_MESSAGE);
        } catch (NoOperationException e) {
            //if it happens, something is done wrong
            e.printStackTrace();
        }

        isResult = true;
        isNext = false;
        isWeakNumber = true;
    }

    public void pressDotButton() {
        if (!firstScreen.getText().contains(".")) {
            appendFirstScreenText(".");
        }
        if (isWeakNumber || isResult) {
            isWeakNumber = false;
            isResult = false;
            setFirstScreenText("0.");
        }
    }

    public void pressOperationButton(Operation operation) {
        try {
            switch (operation) {
                case PLUS:
                case MINUS:
                case DIVIDE:
                case MULTIPLY:
                    if (isNext) {
                        if (isWeakNumber) {
                            replaceLastSign(operation.getSign());
                            controller.setOperation(new BigDecimal(firstScreen.getText()), operation);
                        } else {
                            appendSecondScreenText(" " + firstScreen.getText() + " " + operation.getSign());
                            setFirstScreenText(controller.getResultOnGo(new BigDecimal(firstScreen.getText())));
                            controller.setOperation(new BigDecimal(firstScreen.getText()), operation);
                        }
                    } else {
                        controller.setOperation(new BigDecimal(firstScreen.getText()), operation);
                        setSecondScreenText(firstScreen.getText() + " " + operation.getSign());
                    }
                    isNext = true;
                    isWeakNumber = true;
                    isResult = false;
                    isSqrtResult = false;
                    break;
                case INVERT:
                    setFirstScreenText(controller.getInverted(new BigDecimal(firstScreen.getText())));
                    break;
                case SQRT:
                    if (secondScreen.getText().equals("")) {
                        setSecondScreenText("sqrt(" + firstScreen.getText() + ")");
                    } else {
                        if (isSqrtResult) {
                            int start = secondScreen.getText().lastIndexOf("s");
                            String oldText = secondScreen.getText().substring(start, secondScreen.getLength());
                            String newText = "sqrt(" + oldText + ")";
                            secondScreen.replaceText(start, secondScreen.getLength(), newText);
                        } else {
                            appendSecondScreenText(" sqrt(" + firstScreen.getText() + ")");
                        }
                    }
                    setFirstScreenText(controller.getSqrt(new BigDecimal(firstScreen.getText())));
                    isSqrtResult = true;
                    isWeakNumber = true;
                    break;
                case PERCENT:
                    pressPercentButton();
                    break;
                case REVERSE:
                    pressReverseButton();
                    break;
                case MC:
                    pressClearMemoryButton();
                    break;
                case MR:
                    setFirstScreenText(controller.memoryRecall());
                    isWeakNumber = true;
                    break;
                case MS:
                    if (!firstScreen.getText().equals(DEFAULT_FIRST_SCREEN_TEXT)) {
                        memoryIndication(true);
                        controller.memoryStore(new BigDecimal(firstScreen.getText()));
                    }
                    break;
                case MPLUS:
                    if (!firstScreen.getText().equals(DEFAULT_FIRST_SCREEN_TEXT)) {
                        memoryIndication(true);
                        controller.memoryAdd(new BigDecimal(firstScreen.getText()));
                    }
                    break;
                case MMINUS:
                    if (!firstScreen.getText().equals(DEFAULT_FIRST_SCREEN_TEXT)) {
                        memoryIndication(true);
                        controller.memorySubtract(new BigDecimal(firstScreen.getText()));
                    }
                    break;
            }
        } catch (NumberOverflowException e) {
            setFirstScreenText(OVERFLOW_MESSAGE);
            isWeakNumber = true;
        } catch (DivideByZeroException e) {
            setFirstScreenText(DIVIDE_BY_ZERO_MESSAGE);
            isWeakNumber = true;
        } catch (NoOperationException e) {
            e.printStackTrace();
        }
    }

    private void pressPercentButton() {
        if (secondScreen.getText().equals("")) {
            setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
            setSecondScreenText("0");
        } else {
            BigDecimal result = controller.getPercent(new BigDecimal(firstScreen.getText()));
            setFirstScreenText(result);
            if (isResult) {
                int start = secondScreen.getText().lastIndexOf(" ");
                secondScreen.replaceText(start, secondScreen.getLength(), " " + firstScreen.getText());
            } else {
                appendSecondScreenText(" " + firstScreen.getText());
            }

        }
        isResult = true;
        isWeakNumber = true;
    }

    private void pressReverseButton() {
        controller.setOperation(new BigDecimal(firstScreen.getText()), Operation.REVERSE);
        String secondScreenText = "reciproc(" + firstScreen.getText() + ")";
        BigDecimal result = null;
        try {
            result = controller.getResult();
        } catch (DivideByZeroException e) {
            setFirstScreenText(DIVIDE_BY_ZERO_MESSAGE);
        } catch (NumberOverflowException e) {
            setFirstScreenText(OVERFLOW_MESSAGE);
        } catch (NoOperationException e) {
            e.printStackTrace();
        }

        if (secondScreen.getText().equals("")) {
            setSecondScreenText(secondScreenText);
        } else {
            if (isResult) {
                int start = secondScreen.getText().lastIndexOf(" ");
                secondScreen.replaceText(start, secondScreen.getLength(), " " + secondScreenText);
            } else {
                appendSecondScreenText(" " + secondScreenText);
            }
        }
        if (result != null) {
            setFirstScreenText(result);
        }
        isResult = true;
        isWeakNumber = true;
    }

    public void pressClearEntryButton() {
        setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
    }

    public void pressClearButton() {
        controller.clear();
        setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
        setSecondScreenText(DEFAULT_SECOND_SCREEN_TEXT);
        isResult = false;
        isWeakNumber = false;
        isSqrtResult = false;
        isNext = false;
    }

    private void memoryIndication(boolean value) {
        if (value) {
            memoryScreen.setText(MEMORY_INDICATOR);
        } else {
            memoryScreen.setText("");
        }
    }

    void pressClearMemoryButton() {
        memoryIndication(false);
        controller.memoryClear();
    }

    public void pressBackSpaceButton() {
        if (firstScreen.getLength() < 2) {
            setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
        } else {
            firstScreen.deleteText(firstScreen.getLength() - 1, firstScreen.getLength());
        }

    }

    public void setClipboard() {
        Map<DataFormat, Object> map = new HashMap<>();
        map.put(DataFormat.PLAIN_TEXT, firstScreen.getText());
        clipboard.setContent(map);
    }

    public void getClipboard() {
        String clip = clipboard.getString();
        String text = clip.contains("e") ? clip.substring(0, clip.lastIndexOf("e")) : clip;
        BigDecimal number = new BigDecimal(text);
        setFirstScreenText(number);
    }
}
