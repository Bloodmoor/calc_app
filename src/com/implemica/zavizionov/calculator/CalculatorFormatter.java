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
    private static final int FIRST_SCREEN_SMALL_FONT_SIZE = 12;
    private static final int FIRST_DISPLAY_SIZE = 16;
    private static final int SECOND_DISPLAY_SIZE = 30;
    private static final int BIG_FONT_SYMBOLS_COUNT = 12;
    private static final int MEDIUM_FONT_SYMBOLS_COUNT = 17;

    private static final BigDecimal DELTA = new BigDecimal("1E-" + (FIRST_DISPLAY_SIZE - 2));
    private static final BigDecimal MAX = new BigDecimal("1E" + (FIRST_DISPLAY_SIZE));
    private static final BigDecimal MIN = new BigDecimal("1E-" + (FIRST_DISPLAY_SIZE - 2));
    private static final int SCALE = 29;

    private static final String SCIENTIFIC_DECIMAL_PATTERN = "0E0";
    private static final String EXPONENT_SIGN = "e";
    private final CalculatorController controller = CalculatorController.getInstance();
    private BigDecimal currentScreenValue = BigDecimal.ZERO;
    private Clipboard clipboard = Clipboard.getSystemClipboard();
    private TextField firstScreen;
    private TextField secondScreen;
    private Label memoryScreen;
    private boolean isWeakNumber = false;
    private boolean isResult = false;
    private boolean isSequence = false;
    private boolean isSqrtResult = false;
    private boolean isPercentResult = false;
    /**
     * Screen can be locked after some error.
     * It can be unlocked only after clear operation is performed.
     */
    private boolean isLocked = false;

    private CalculatorFormatter(TextField firstScreen, TextField secondScreen, Label memoryScreen) {
        this.firstScreen = firstScreen;
        this.secondScreen = secondScreen;
        this.memoryScreen = memoryScreen;
        setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
        setSecondScreenText(DEFAULT_SECOND_SCREEN_TEXT);
    }

    public static CalculatorFormatter getInstance(TextField firstScreen, TextField secondScreen, Label memoryScreen) {
        return new CalculatorFormatter(firstScreen, secondScreen, memoryScreen);
    }

    private BigDecimal getCurrentScreenValue() {
        if (currentScreenValue.equals(BigDecimal.ZERO)) {
            return new BigDecimal(firstScreen.getText());
        } else {
            return currentScreenValue;
        }
    }

    private void ensureSize() {

        if (firstScreen.getLength() <= BIG_FONT_SYMBOLS_COUNT) {
            firstScreen.setStyle("-fx-font-size: " + FIRST_SCREEN_BIG_FONT_SIZE + ";");
        } else if (firstScreen.getLength() < MEDIUM_FONT_SYMBOLS_COUNT) {
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
        if (isLocked) {
            return;
        }
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
        DecimalFormat f = new DecimalFormat(SCIENTIFIC_DECIMAL_PATTERN);
        f.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        f.setGroupingUsed(false);

        if (value.compareTo(MAX) >= 0) {
            f.setMaximumIntegerDigits(1);
            f.setMaximumFractionDigits(FIRST_DISPLAY_SIZE - 1);
        } else if (value.compareTo(MIN) < 0 || value.scale() > SCALE) {
            f.setMaximumIntegerDigits(1);
            f.setMaximumFractionDigits(FIRST_DISPLAY_SIZE - 1);
        } else {
            f.applyPattern("0");
            int countOfIntDigits = value.toPlainString().lastIndexOf(".");
            f.setMaximumFractionDigits(FIRST_DISPLAY_SIZE - countOfIntDigits);
        }

        result = f.format(value);

        //switching to lowercase e;
        if (result.contains("E-")) {
            return result.replace("E", EXPONENT_SIGN);
        } else {
            return result.replace("E", "e+");
        }
    }

    private void appendFirstScreenText(String text) {
        if (isLocked) {
            return;
        }
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

    public void pressDigitButton(int digit) {
        if (firstScreen.getLength() == FIRST_DISPLAY_SIZE && !isWeakNumber) {
            return;
        }
        if (firstScreen.getText().equals("0") || isWeakNumber) {
            if (isSqrtResult || isPercentResult) {
                replaceLast("");
            }
            currentScreenValue = BigDecimal.ZERO;
            setFirstScreenText(Integer.toString(digit));
            isWeakNumber = false;
        } else {
            appendFirstScreenText(Integer.toString(digit));
        }
    }

    private void replaceLast(String newString) {
        setSecondScreenText(secondScreen.getText().substring(0, secondScreen.getText().lastIndexOf(" ")) + newString);
    }

    private void replaceLastSign(String sign) {
        replaceLast(" " + sign);
    }

    public void pressEqualButton() {
        if (isLocked) {
            return;
        }
        //for single sqrt result
        if (isSqrtResult && !isSequence) {
            controller.clear();
            setSecondScreenText(DEFAULT_SECOND_SCREEN_TEXT);
            isSqrtResult = false;
            //isPercentResult = false;
            return;
        }

        if (controller.getOperation().equals(Operation.NOOP)) {
            return;
        }

        try {

            if (isResult) {
                setFirstScreenText(controller.getResultAfterEqual(getCurrentScreenValue()));
            } else {
                setFirstScreenText(controller.getResult(getCurrentScreenValue()));
                setSecondScreenText(DEFAULT_SECOND_SCREEN_TEXT);
            }

            isResult = true;
            isSequence = false;
            isWeakNumber = true;

        } catch (DivideByZeroException e) {
            setFirstScreenText(DIVIDE_BY_ZERO_MESSAGE);
            isLocked = true;
        } catch (NumberOverflowException e) {
            setFirstScreenText(OVERFLOW_MESSAGE);
            isLocked = true;
        } catch (NoOperationException e) {
            //if it happens, something is done wrong
            e.printStackTrace();
        }


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
        if (isLocked) {
            return;
        }
        try {
            switch (operation) {
                case PLUS:
                case MINUS:
                case DIVIDE:
                case MULTIPLY:
                    pressTwoOperandOperationButton(operation);
                    break;
                case INVERT:
                    pressInvertButton();
                    break;
                case SQRT:
                    pressSqrtButton();
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
                    pressMemoryRecallButton();
                    break;
                case MS:
                    pressMemoryStoreButton();
                    break;
                case MPLUS:
                    pressMemoryPlusButton();
                    break;
                case MMINUS:
                    pressMemoryMinusButton();
                    break;
            }
        } catch (DivideByZeroException e) {
            setFirstScreenText(DIVIDE_BY_ZERO_MESSAGE);
        } catch (NumberOverflowException e) {
            setFirstScreenText(OVERFLOW_MESSAGE);
        } catch (NoOperationException e) {
            //if it happens, smthng is done wrong
            e.printStackTrace();
        }
    }

    private void pressInvertButton() {
        setFirstScreenText(controller.getInverted(getCurrentScreenValue()));
    }

    private void pressMemoryMinusButton() {
        if (!firstScreen.getText().equals(DEFAULT_FIRST_SCREEN_TEXT)) {
            memoryIndication(true);
            controller.memorySubtract(getCurrentScreenValue());
        }
    }

    private void pressMemoryPlusButton() {
        if (!firstScreen.getText().equals(DEFAULT_FIRST_SCREEN_TEXT)) {
            memoryIndication(true);
            controller.memoryAdd(getCurrentScreenValue());
        }
    }

    private void pressMemoryStoreButton() {
        if (!firstScreen.getText().equals(DEFAULT_FIRST_SCREEN_TEXT)) {
            memoryIndication(true);
            controller.memoryStore(getCurrentScreenValue());
        }
    }

    private void pressMemoryRecallButton() {
        setFirstScreenText(controller.memoryRecall());
        isWeakNumber = true;
    }

    private void pressSqrtButton() {
        if (secondScreen.getText().isEmpty()) {
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
        setFirstScreenText(controller.getSqrt(getCurrentScreenValue()));

        isSqrtResult = true;
        isWeakNumber = true;
    }

    private void pressTwoOperandOperationButton(Operation operation) throws NumberOverflowException, DivideByZeroException, NoOperationException {
        if (isSequence) {
            if (isWeakNumber) {
                replaceLastSign(operation.getSign());
                controller.setOperation(getCurrentScreenValue(), operation);
            } else {
                appendSecondScreenText(" " + firstScreen.getText() + " " + operation.getSign());
                setFirstScreenText(controller.getResultOnGo(getCurrentScreenValue()));
                controller.setOperation(getCurrentScreenValue(), operation);
            }
        } else {
            controller.setOperation(getCurrentScreenValue(), operation);

            if (isSqrtResult) {
                appendSecondScreenText(" " + operation.getSign());
            } else {
                setSecondScreenText(firstScreen.getText() + " " + operation.getSign());
            }

        }
        isSequence = true;
        isWeakNumber = true;
        isResult = false;
        isSqrtResult = false;
    }

    private void pressPercentButton() {
        if (!isSequence) {
            setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
            setSecondScreenText("0");
        } else {
            setFirstScreenText(controller.getPercent(getCurrentScreenValue()));

            if (isWeakNumber) {
                int start = secondScreen.getText().lastIndexOf(" ");
                secondScreen.replaceText(start, secondScreen.getLength(), " " + firstScreen.getText());
            } else {
                appendSecondScreenText(" " + firstScreen.getText());
            }

        }
        isWeakNumber = true;
        isPercentResult = true;
    }

    private void pressReverseButton() throws NumberOverflowException, DivideByZeroException, NoOperationException {
        controller.setOperation(getCurrentScreenValue(), Operation.REVERSE);

        String secondScreenText = "reciproc(" + firstScreen.getText() + ")";


        if (!isSequence) {
            setSecondScreenText(secondScreenText);
        } else {
            if (isResult) {
                int start = secondScreen.getText().lastIndexOf(" ");
                secondScreen.replaceText(start, secondScreen.getLength(), " " + secondScreenText);
            } else {
                appendSecondScreenText(" " + secondScreenText);
            }
        }
        setFirstScreenText(controller.getResult());

        isResult = true;
        isWeakNumber = true;
    }

    public void pressClearEntryButton() {
        setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
    }

    public void pressClearButton() {
        isResult = false;
        isWeakNumber = false;
        isSqrtResult = false;
        isSequence = false;
        isPercentResult = false;
        isLocked = false;

        controller.clear();
        setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
        setSecondScreenText(DEFAULT_SECOND_SCREEN_TEXT);

    }

    private void memoryIndication(boolean value) {
        if (value) {
            memoryScreen.setText(MEMORY_INDICATOR);
        } else {
            memoryScreen.setText("");
        }
    }

    private void pressClearMemoryButton() {
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
        String text = clip.contains(EXPONENT_SIGN) ? clip.substring(0, clip.lastIndexOf(EXPONENT_SIGN)) : clip;
        BigDecimal number = new BigDecimal(text);
        setFirstScreenText(number);
    }
}
