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
import java.util.Collections;
import java.util.Locale;

/**
 * Class responds for CalculatorView behavior logic
 * and representation of results on its screens.
 *
 * @author Zavizionov Andrii
 */
public class CalculatorFormatter {

    /**
     * Default first screen text,
     * displays on start and after clear operation is performed.
     */
    private static final String DEFAULT_FIRST_SCREEN_TEXT = "0";

    /**
     * Default second screen text,
     * displays on start and after clear operation is performed.
     */
    private static final String DEFAULT_SECOND_SCREEN_TEXT = "";

    /**
     * Text of memory indicator.
     */
    private static final String MEMORY_INDICATOR = "M";

    /**
     * Symbol that tells if second screen text can't
     * fit the screen size and is trimmed.
     */
    private static final String SCREEN_OVERFLOW_SYMBOL = "‹‹";

    /**
     * Message to be shown when resulting number overflows
     * max number scale.
     */
    private static final String OVERFLOW_MESSAGE = "Переполнение";

    /**
     * Message to be shown when divide by zero operation is performed.
     */
    private static final String DIVIDE_BY_ZERO_MESSAGE = "Деление на ноль невозможно";

    /**
     * First screen big font size.
     */
    private static final int FIRST_SCREEN_BIG_FONT_SIZE = 22;

    /**
     * First screen medium font size.
     */
    private static final int FIRST_SCREEN_MEDIUM_FONT_SIZE = 18;

    /**
     * First screen small font size.
     */
    private static final int FIRST_SCREEN_SMALL_FONT_SIZE = 12;

    /**
     * Count of digits, that first screen can fit.
     */
    private static final int FIRST_DISPLAY_SIZE = 16;

    /**
     * Count of symbols, that second screen can fit.
     */
    private static final int SECOND_DISPLAY_SIZE = 30;

    /**
     * Count of symbols of big size that first screen can fit.
     */
    private static final int BIG_FONT_SYMBOLS_COUNT = 12;

    /**
     * Count of symbols of medium size that first screen can fit.
     */
    private static final int MEDIUM_FONT_SYMBOLS_COUNT = 17;

    /**
     * Min difference with nearest integer for number not to be rounded.
     */
    private static final BigDecimal DELTA = new BigDecimal("1E-" + (FIRST_DISPLAY_SIZE - 2));

    /**
     * Max number, that first screen can fit with plain representation.
     * It will be scientific otherwise.
     * Plain is representation like 56.568 or 5862.  Scientific is like 5e+13.
     */
    private static final BigDecimal MAX = new BigDecimal("1E" + (FIRST_DISPLAY_SIZE));

    /**
     * Min absolute number value, that first screen can fit with plain representation.
     * It will be scientific otherwise.
     * Plain is representation like 56.568 or 5862. Scientific is like 5e+13.
     */
    private static final BigDecimal MIN = new BigDecimal("1E-" + (FIRST_DISPLAY_SIZE - 2));

    /**
     * Max integer digits for scientific representation of number.
     */
    private static final int MAXIMUM_INTEGER_DIGITS_FOR_SCIENCE_NUM = 1;

    /**
     * Max fraction digits, that display can fit.
     */
    private static final int MAXIMUM_FRACTION_DIGITS = FIRST_DISPLAY_SIZE - 1;

    /**
     * Pattern for scientific number representation formatting.
     * Scientific is representation like 5e+13.
     */
    private static final String SCIENTIFIC_DECIMAL_PATTERN = "0E0";

    private static final String PLAIN_DECIMAL_PATTERN = "0";

    /**
     * Formats numbers to scientific representation.
     */
    private static final DecimalFormat SCIENTIFIC_FORMATTER = new DecimalFormat(SCIENTIFIC_DECIMAL_PATTERN);

    /**
     * Formats numbers to plain representation.
     */
    private static final DecimalFormat PLAIN_FORMATTER = new DecimalFormat(PLAIN_DECIMAL_PATTERN);


    /**
     * Initializing of scientific decimal formatter.
     */
    static {
        SCIENTIFIC_FORMATTER.setMaximumIntegerDigits(MAXIMUM_INTEGER_DIGITS_FOR_SCIENCE_NUM);
        SCIENTIFIC_FORMATTER.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
        SCIENTIFIC_FORMATTER.setGroupingUsed(false);
        SCIENTIFIC_FORMATTER.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        PLAIN_FORMATTER.setGroupingUsed(false);
        PLAIN_FORMATTER.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
    }

    /**
     * Scale for switching between scientific and plain number representations.
     * If number absolute scale is bigger then this value, scientific representation will
     * be used. Otherwise, valu will be represented with plain number.
     * Plain is representation like 56.568 or 5862. Scientific is like 5e+13.
     */
    private static final int MAX_SCALE = 29;


    /**
     * Exponent sign
     */
    private static final String EXPONENT_SIGN = "e";

    /**
     * Big negative exponent sign E-.
     */
    private static final String BIG_NEGATIVE_EXPONENT = "E-";

    /**
     * Big exponent sign E.
     */
    private static final String BIG_EXPONENT = "E";

    /**
     * Positive exponent sign.
     */
    private static final String POSITIVE_EXPONENT = EXPONENT_SIGN + "+";


    /**
     * Instance of calculator controller.
     */
    private final CalculatorController controller = CalculatorController.getInstance();

    /**
     * BigDecimal that stores value, which representation is
     * currently shown on the screen.
     */
    private BigDecimal currentScreenValue = BigDecimal.ZERO;

    /**
     * Clipboard instance.
     */
    private final Clipboard clipboard = Clipboard.getSystemClipboard();

    /**
     * First screen reference.
     */
    private final TextField firstScreen;

    /**
     * Second screen reference.
     */
    private final TextField secondScreen;

    /**
     * Memory screen reference.
     */
    private final Label memoryScreen;

    /**
     * Tells if the number currently shown on the first screen
     * must be replaced with any performed input instead of appending.
     */
    private boolean isWeakNumber = false;

    /**
     * Tells if the number currently shown on the first screen
     * is a result of some operation. In this case calculator
     * behaviour should be slightly different.
     */
    private boolean isResult = false;

    /**
     * Tells if some uncompleted sequence of operations
     * is happening.
     */
    private boolean isSequence = false;

    /**
     * Tells if the current result of calculators work
     * is a result of square root operation or reverse operation.
     * In this case calculator behaviour should be slightly different.
     */
    private boolean isSqrtOrReverseResult = false;

    /**
     * Tells if the current result of calculators work
     * is a result of percent operation. In this case calculator
     * behaviour should be slightly different.
     */
    private boolean isPercentResult = false;

    /**
     * Shows if calculators screens are currently locked.
     * Screens can be locked after some error.
     * It can be unlocked only after clear operation is performed.
     */
    private boolean isLocked = false;


    /**
     * Constructor. Class instances can't be created directly.
     *
     * @param firstScreen   - reference to first calculator screen from view.
     * @param secondScreen- reference to second calculator screen from view.
     * @param memoryScreen- reference to calculator memory screen from view.
     */
    private CalculatorFormatter(TextField firstScreen, TextField secondScreen, Label memoryScreen) {
        this.firstScreen = firstScreen;
        this.secondScreen = secondScreen;
        this.memoryScreen = memoryScreen;
        setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
        setSecondScreenText(DEFAULT_SECOND_SCREEN_TEXT);
    }

    /**
     * Creates an instance of CalculatorFormatter with given parameters.
     *
     * @param firstScreen   - reference to first calculator screen from view.
     * @param secondScreen- reference to second calculator screen from view.
     * @param memoryScreen- reference to calculator memory screen from view.
     * @return instance of CalculatorController
     */
    public static CalculatorFormatter getInstance(TextField firstScreen, TextField secondScreen, Label memoryScreen) {
        return new CalculatorFormatter(firstScreen, secondScreen, memoryScreen);
    }

    /**
     * Returns a current screen value.
     *
     * @return number, currently shown on the screen
     */
    private BigDecimal getCurrentScreenValue() {
        if (currentScreenValue.equals(BigDecimal.ZERO)) {
            return new BigDecimal(firstScreen.getText());
        } else {
            return currentScreenValue;
        }
    }

    /**
     * Ensures that text on the first screen will fit it.
     * Switches size of text if needed.
     */
    private void ensureSize() {

        if (firstScreen.getLength() <= BIG_FONT_SYMBOLS_COUNT) {
            firstScreen.setStyle("-fx-font-size: " + FIRST_SCREEN_BIG_FONT_SIZE + ";");
        } else if (firstScreen.getLength() < MEDIUM_FONT_SYMBOLS_COUNT) {
            firstScreen.setStyle("-fx-font-size: " + FIRST_SCREEN_MEDIUM_FONT_SIZE + ";");
        } else {
            firstScreen.setStyle("-fx-font-size: " + FIRST_SCREEN_SMALL_FONT_SIZE + ";");
        }
    }

    /**
     * Check if given number is integer.
     *
     * @param value - given number
     * @return true if number is integer, false instead.
     */
    private static boolean isInteger(BigDecimal value) {
        if (value.scale() <= 0) {
            return true;
        }
        BigDecimal nearestInteger = value.setScale(0, BigDecimal.ROUND_HALF_UP);
        return nearestInteger.compareTo(value) == 0;

    }

    /**
     * Rounds given number to integer if it's needed.
     *
     * @param value - number to be rounded
     * @return rounded variant of number if rounding is needed, given number instead.
     */
    private static BigDecimal getRounded(BigDecimal value) {
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

    /**
     * Sets first screen text.
     *
     * @param text - text to set.
     */
    private void setFirstScreenText(String text) {
        if (isLocked) {
            return;
        }
        firstScreen.setText(text);
        ensureSize();
    }

    /**
     * Sets given number as first screen text.
     *
     * @param value - give number.
     */
    private void setFirstScreenText(BigDecimal value) {
        currentScreenValue = value;
        if (value.toPlainString().replace(".", "").length() <= FIRST_DISPLAY_SIZE) {
            setFirstScreenText(value.toPlainString());
        } else {
            setFirstScreenText(format(getRounded(value)));
        }
    }

    /**
     * Formats given number to fit the requirements. Returns
     * its string representation in scientific or plain
     * representation depending on the number.
     *
     * @param value - given number.
     * @return string representation of number.
     */
    private static String format(BigDecimal value) {
        String result;

        if (value.compareTo(MAX) >= 0 || value.compareTo(MIN) < 0 || value.scale() > MAX_SCALE) {
            result = SCIENTIFIC_FORMATTER.format(value);
        } else {
            int countOfIntDigits = value.toPlainString().lastIndexOf(".");
            PLAIN_FORMATTER.setMaximumFractionDigits(FIRST_DISPLAY_SIZE - countOfIntDigits);
            result = PLAIN_FORMATTER.format(value);
        }

        //switching to lowercase e;
        if (result.contains(BIG_NEGATIVE_EXPONENT)) {
            return result.replace(BIG_EXPONENT, EXPONENT_SIGN);
        } else {
            return result.replace(BIG_EXPONENT, POSITIVE_EXPONENT);
        }
    }

    /**
     * Appends given text to first screen text.
     *
     * @param text - text to append.
     */
    private void appendFirstScreenText(String text) {
        if (isLocked) {
            return;
        }
        firstScreen.appendText(text);
        ensureSize();
    }

    /**
     * Sets second screen text
     *
     * @param text - text to set.
     */
    private void setSecondScreenText(String text) {
        if (text.length() > SECOND_DISPLAY_SIZE) {
            text = SCREEN_OVERFLOW_SYMBOL + text.substring(text.length() - SECOND_DISPLAY_SIZE);
        }
        secondScreen.setText(text);
    }

    /**
     * Appends text to second screen text.
     *
     * @param text - text to append.
     */
    private void appendSecondScreenText(String text) {
        text = secondScreen.getText() + text;
        setSecondScreenText(text);
    }

    /**
     * Replaces last element on the second string with
     * given element string.
     *
     * @param newString - string of element to place.
     */
    private void replaceLast(String newString) {
        setSecondScreenText(secondScreen.getText().substring(0, secondScreen.getText().lastIndexOf(" ")) + newString);
    }

    /**
     * Replaces last sign on the second string
     * with given sign string.
     *
     * @param sign - sign to set.
     */
    private void replaceLastSign(String sign) {
        replaceLast(" " + sign);
    }

    /**
     * Describes behavior of calculator after
     * pressing digit buttons.
     *
     * @param digit - digit of pressed button.
     */
    public void pressDigitButton(int digit) {
        if (firstScreen.getLength() == FIRST_DISPLAY_SIZE && !isWeakNumber) {
            return;
        }

        if (firstScreen.getText().equals(DEFAULT_FIRST_SCREEN_TEXT) || isWeakNumber) {
            if (isSqrtOrReverseResult || isPercentResult) {
                replaceLast("");
            }
            currentScreenValue = BigDecimal.ZERO;
            setFirstScreenText(Integer.toString(digit));
            isWeakNumber = false;
        } else {
            appendFirstScreenText(Integer.toString(digit));
        }
    }


    /**
     * Describes behavior of calculator after
     * pressing equal button.
     */
    public void pressEqualButton() {
        if (isLocked) {
            return;
        }
        //for single sqrt result
        if (isSqrtOrReverseResult && !isSequence) {
            controller.clear();
            setSecondScreenText(DEFAULT_SECOND_SCREEN_TEXT);
            isSqrtOrReverseResult = false;
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

    /**
     * Describes behavior of calculator after
     * pressing dot (decimal) button.
     */
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

    /**
     * Describes behavior of calculator after
     * pressing operation button.
     *
     * @param operation - operation of pressed button.
     */
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
                    pressMemoryClearButton();
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

    /**
     * Describes behavior of calculator after
     * pressing invert button.
     */
    private void pressInvertButton() {
        setFirstScreenText(controller.getInverted(getCurrentScreenValue()));
    }

    /**
     * Describes behavior of calculator after
     * pressing memory minus button.
     */
    private void pressMemoryMinusButton() {
        if (!firstScreen.getText().equals(DEFAULT_FIRST_SCREEN_TEXT)) {
            memoryIndication(true);
            controller.memorySubtract(getCurrentScreenValue());
        }
    }

    /**
     * Describes behavior of calculator after
     * pressing memory plus button.
     */
    private void pressMemoryPlusButton() {
        if (!firstScreen.getText().equals(DEFAULT_FIRST_SCREEN_TEXT)) {
            memoryIndication(true);
            controller.memoryAdd(getCurrentScreenValue());
        }
    }

    /**
     * Describes behavior of calculator after
     * pressing memory store button.
     */
    private void pressMemoryStoreButton() {
        if (!firstScreen.getText().equals(DEFAULT_FIRST_SCREEN_TEXT)) {
            memoryIndication(true);
            controller.memoryStore(getCurrentScreenValue());
        }
    }

    /**
     * Describes behavior of calculator after
     * pressing memory recall button.
     */
    private void pressMemoryRecallButton() {
        setFirstScreenText(controller.memoryRecall());
        isWeakNumber = true;
    }

    /**
     * Describes behavior of calculator after
     * pressing square root operation button.
     */
    private void pressSqrtButton() {
        if (secondScreen.getText().isEmpty()) {
            setSecondScreenText("sqrt(" + firstScreen.getText() + ")");
        } else {

            if (isSqrtOrReverseResult) {
                int start = secondScreen.getText().lastIndexOf("sqrt");
                String oldText = secondScreen.getText().substring(start, secondScreen.getLength());
                String newText = "sqrt(" + oldText + ")";
                secondScreen.replaceText(start, secondScreen.getLength(), newText);
            } else {
                appendSecondScreenText(" sqrt(" + firstScreen.getText() + ")");
            }

        }
        setFirstScreenText(controller.getSqrt(getCurrentScreenValue()));

        isSqrtOrReverseResult = true;
        isWeakNumber = true;
    }

    /**
     * Describes behavior of calculator after
     * pressing operation button, which operation uses two operands.
     *
     * @param operation - operation of pressed button.
     */
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

            if (isSqrtOrReverseResult) {
                appendSecondScreenText(" " + operation.getSign());
            } else {
                setSecondScreenText(firstScreen.getText() + " " + operation.getSign());
            }

        }
        isSequence = true;
        isWeakNumber = true;
        isResult = false;
        isSqrtOrReverseResult = false;
    }

    /**
     * Describes behavior of calculator after
     * pressing percent operation button.
     */
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

    /**
     * Describes behavior of calculator after
     * pressing reverse operation button.
     */
    private void pressReverseButton() throws NumberOverflowException, DivideByZeroException, NoOperationException {
        controller.setOperation(getCurrentScreenValue(), Operation.REVERSE);

        String secondScreenText = "reciproc(" + firstScreen.getText() + ")";


        if (secondScreen.getText().isEmpty()) {
            setSecondScreenText(secondScreenText);
        } else {
            if (isSqrtOrReverseResult) {
                int start = secondScreen.getText().lastIndexOf("reciproc");
                String oldText = secondScreen.getText().substring(start, secondScreen.getLength());
                String newText = "reciproc(" + oldText + ")";
                secondScreen.replaceText(start, secondScreen.getLength(), newText);
            } else {
                appendSecondScreenText(" " + secondScreenText);
            }
        }
        setFirstScreenText(controller.getResult());

        isResult = true;
        isWeakNumber = true;
        isSqrtOrReverseResult = true;
    }

    /**
     * Describes behavior of calculator after
     * pressing clear entry button.
     */
    public void pressClearEntryButton() {
        setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
    }

    /**
     * Describes behavior of calculator after
     * pressing clear button.
     */
    public void pressClearButton() {
        isResult = false;
        isWeakNumber = false;
        isSqrtOrReverseResult = false;
        isSequence = false;
        isPercentResult = false;
        isLocked = false;

        controller.clear();
        setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
        setSecondScreenText(DEFAULT_SECOND_SCREEN_TEXT);

    }

    /**
     * Switches on and off memory indication.
     *
     * @param value - if true indication is switching on, off otherwise
     */
    private void memoryIndication(boolean value) {
        if (value) {
            memoryScreen.setText(MEMORY_INDICATOR);
        } else {
            memoryScreen.setText("");
        }
    }

    /**
     * Describes behavior of calculator after
     * pressing memory clear button.
     */
    private void pressMemoryClearButton() {
        memoryIndication(false);
        controller.memoryClear();
    }

    /**
     * Describes behavior of calculator after
     * pressing backspace button.
     */
    public void pressBackSpaceButton() {
        if (firstScreen.getLength() < 2) {
            setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
        } else {
            firstScreen.deleteText(firstScreen.getLength() - 1, firstScreen.getLength());
        }

    }

    /**
     * Describes behavior of calculator after
     * pressing copy-to-clipboard combination.
     */
    public void setClipboard() {
        clipboard.setContent(Collections.singletonMap(DataFormat.PLAIN_TEXT, firstScreen.getText()));
    }

    /**
     * Describes behavior of calculator after
     * pressing past-from-clipboard combination.
     */
    public void getClipboard() {
        String clip = clipboard.getString();
        String text = clip.contains(EXPONENT_SIGN) ? clip.substring(0, clip.lastIndexOf(EXPONENT_SIGN)) : clip;
        BigDecimal number = new BigDecimal(text);
        setFirstScreenText(number);
    }
}
