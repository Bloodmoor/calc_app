package com.implemica.zavizionov.calculator;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CalculatorController {

    private static final String DEFAULT_FIRST_SCREEN_TEXT = "0";
    private static final String DEFAULT_SECOND_SCREEN_TEXT = "";
    private static final String MEMORY_INDICATOR = "M";
    private static final String DIVIDE_BY_ZERO_MESSAGE = "\u0414\u0435\u043B\u0435\u043D\u0438\u0435 \u043D\u0430 \u043D\u043E\u043B\u044C \u043D\u0435\u0432\u043E\u0437\u043C\u043E\u0436\u043D\u043E";
    private static final int FIRST_SCREEN_BIG_FONT_SIZE = 22;
    private static final int FIRST_SCREEN_MEDIUM_FONT_SIZE = 18;
    private static final int FIRST_SCREEN_SMALL_FONT_SIZE = 15;
    private static final int DISPLAY_SIZE = 16;
    private static final String DECIMAL_FORMAT = "0.#####E0";

    private TextField firstScreen;
    private TextField secondScreen;
    private Label memoryScreen;

    private final Calculator calculator = new Calculator();

    private boolean isWeakNumber = false;
    private boolean isResult = false;
    private boolean isNext = false;
    private boolean isSqrtResult = false;

    private CalculatorController(TextField firstScreen, TextField secondScreen, Label memoryScreen) {
        this.firstScreen = firstScreen;
        this.secondScreen = secondScreen;
        this.memoryScreen = memoryScreen;
        setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
        setSecondScreenText(DEFAULT_SECOND_SCREEN_TEXT);
    }

    private void ensureSize(){
        if (firstScreen.getLength() < 13) {
            firstScreen.setStyle("-fx-font-size: " + FIRST_SCREEN_BIG_FONT_SIZE + ";");
        } else if(firstScreen.getLength() < 18) {
            firstScreen.setStyle("-fx-font-size: " + FIRST_SCREEN_MEDIUM_FONT_SIZE + ";");
        }else{
            firstScreen.setStyle("-fx-font-size: " + FIRST_SCREEN_SMALL_FONT_SIZE + ";");
        }
    }


    private void setFirstScreenText(String text){
        firstScreen.setText(text);
        ensureSize();
    }

    private void setFirstScreenText(BigDecimal value){
        if(value.toPlainString().length()>16){
            DecimalFormat format = new DecimalFormat(DECIMAL_FORMAT);
            format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance());
            firstScreen.setText(format.format(value));
        }else{
            firstScreen.setText(value.stripTrailingZeros().toPlainString());
        }
        ensureSize();
        return;
    }

    private void appendFirstScreenText(String text){
        firstScreen.appendText(text);
        ensureSize();
    }

    private void appendFirstScreenText(BigDecimal value){
        return;
    }
    
    private void setSecondScreenText(String text){
        secondScreen.setText(text);
    }
    
    private void appendSecondScreenText(String text){
        secondScreen.appendText(text);
    }



    public static CalculatorController getInstance(TextField firstScreen, TextField secondScreen, Label memoryScreen) {
        return new CalculatorController(firstScreen, secondScreen, memoryScreen);
    }

    public void pressDigitButton(int digit) {
        if (firstScreen.getLength() == DISPLAY_SIZE && !isWeakNumber) {
            return;
        }
        if (firstScreen.getText().equals("0") || isWeakNumber) {
            setFirstScreenText(Integer.toString(digit));
            isWeakNumber = false;
        } else {
            appendFirstScreenText(Integer.toString(digit));
        }
    }

    public void pressEqualButton() {
        if (calculator.getOperation().equals(Operation.NOOP)){
            return;
        }
        if(isSqrtResult){
            calculator.setOperation(Operation.NOOP);
            setSecondScreenText(DEFAULT_SECOND_SCREEN_TEXT);
            isSqrtResult = false;
            return;
        }
        if(isResult){
            setFirstScreenText(calculator.getResultAfterEqual(new BigDecimal(firstScreen.getText())));
            return;
        }
        try {
           setFirstScreenText(calculator.getResult(new BigDecimal(firstScreen.getText())));
           setSecondScreenText(DEFAULT_SECOND_SCREEN_TEXT);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("divide by zero")) {
                setFirstScreenText(DIVIDE_BY_ZERO_MESSAGE);
            } else {
                setFirstScreenText("Error");
            }
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
        switch (operation) {
            case PLUS:
                if (isNext){
                    appendSecondScreenText(" " + firstScreen.getText() + " +");
                    setFirstScreenText(calculator.getResultOnGo(new BigDecimal(firstScreen.getText())));
                    calculator.setOperation(new BigDecimal(firstScreen.getText()), operation);
                }else{
                    calculator.setOperation(new BigDecimal(firstScreen.getText()), operation);
                    setSecondScreenText(firstScreen.getText() + " +");
                }
                isNext = true;
                isWeakNumber = true;
                break;
            case MINUS:
                if (isNext){
                    appendSecondScreenText(" " + firstScreen.getText() + " -");
                    setFirstScreenText(calculator.getResultOnGo(new BigDecimal(firstScreen.getText())));
                    calculator.setOperation(new BigDecimal(firstScreen.getText()), operation);
                }else{
                    calculator.setOperation(new BigDecimal(firstScreen.getText()), operation);
                    setSecondScreenText(firstScreen.getText() + " -");
                }
                isNext = true;
                isWeakNumber = true;
                break;
            case DIVIDE:
                if (isNext){
                    appendSecondScreenText(" " + firstScreen.getText() + " /");
                    setFirstScreenText(calculator.getResultOnGo(new BigDecimal(firstScreen.getText())));
                    calculator.setOperation(new BigDecimal(firstScreen.getText()), operation);
                }else{
                    calculator.setOperation(new BigDecimal(firstScreen.getText()), operation);
                    setSecondScreenText(firstScreen.getText() + " /");
                }
                isNext = true;
                isWeakNumber = true;
                break;
            case MULTIPLY:
                if (isNext){
                    appendSecondScreenText(" " + firstScreen.getText() + " *");
                    setFirstScreenText(calculator.getResultOnGo(new BigDecimal(firstScreen.getText())));
                    calculator.setOperation(new BigDecimal(firstScreen.getText()), operation);
                }else{
                    calculator.setOperation(new BigDecimal(firstScreen.getText()), operation);
                    setSecondScreenText(firstScreen.getText() + " *");
                }
                isNext = true;
                isWeakNumber = true;
                break;
            case INVERT:
                calculator.setOperation(new BigDecimal(firstScreen.getText()), operation);
                setFirstScreenText(calculator.getResult());
                break;
            case SQRT:
                calculator.setOperation(new BigDecimal(firstScreen.getText()), operation);
                if(secondScreen.getText().equals("")){
                    setSecondScreenText("sqrt(" + firstScreen.getText() + ")");
                }else{
                    if(isSqrtResult){
                        int start = secondScreen.getText().lastIndexOf("s");
                        String oldText = secondScreen.getText().substring(start, secondScreen.getLength());
                        String newText = "sqrt(" + oldText + ")";
                        secondScreen.replaceText(start, secondScreen.getLength(), newText);
                    }else{
                        appendSecondScreenText(" sqrt(" + firstScreen.getText() + ")");
                    }
                }
                setFirstScreenText(calculator.getResult());
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
                setFirstScreenText(calculator.memoryRecall());
                isWeakNumber = true;
                break;
            case MS:
                if (!firstScreen.getText().equals(DEFAULT_FIRST_SCREEN_TEXT)) {
                    memoryIndication(true);
                    calculator.memoryStore(new BigDecimal(firstScreen.getText()));
                }
                break;
            case MPLUS:
                if (!firstScreen.getText().equals(DEFAULT_FIRST_SCREEN_TEXT)) {
                    memoryIndication(true);
                    calculator.memoryAdd(new BigDecimal(firstScreen.getText()));
                }
                break;
            case MMINUS:
                if (!firstScreen.getText().equals(DEFAULT_FIRST_SCREEN_TEXT)) {
                    memoryIndication(true);
                    calculator.memorySubtract(new BigDecimal(firstScreen.getText()));
                }
                break;
        }
    }

    private void pressPercentButton() {
        if (secondScreen.getText().equals("")) {
            setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
            setSecondScreenText("0");
        } else {
            BigDecimal result = calculator.getPercent(new BigDecimal(firstScreen.getText()));
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
        calculator.setOperation(new BigDecimal(firstScreen.getText()), Operation.REVERSE);
        String secondScreenText = "reciproc(" + firstScreen.getText() + ")";
        BigDecimal result = null;
        try{
            result = calculator.getResult();
        }catch (IllegalArgumentException e){
            if (e.getMessage().contains("divide by zero")){
                setFirstScreenText(DIVIDE_BY_ZERO_MESSAGE);
            }else{
                setFirstScreenText("Error");
            }
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
        if(result != null){
            setFirstScreenText(result);
        }
        isResult = true;
        isWeakNumber = true;
    }

    public void pressClearEntryButton() {
        setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
    }

    public void pressClearButton() {
        calculator.clear();
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
        calculator.memoryClear();
    }

    public void pressBackSpaceButton() {
        if (firstScreen.getLength() < 2) {
            setFirstScreenText(DEFAULT_FIRST_SCREEN_TEXT);
        } else {
            firstScreen.deleteText(firstScreen.getLength() - 1, firstScreen.getLength());
        }

    }
}
