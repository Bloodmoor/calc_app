package com.implemica.zavizionov.calculator;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CalculatorView extends Application {

    private static final String MENU_FILE_TEXT = "\u0424\u0430\u0439\u043B";
    private static final String MENU_FILE_EXIT_TEXT = "\u0412\u044B\u0445\u043E\u0434";
    private static final String TITLE_TEXT = "\u041A\u0430\u043B\u044C\u043A\u0443\u043B\u044F\u0442\u043E\u0440";
    private static final double BUTTON_GRID_VGAP = 5;
    private static final double BUTTON_GRID_HGAP = 5;
    private static final double BUTTON_HEIGHT = 27;
    private static final double BUTTON_WIDTH = 34;
    private static final Insets BUTTON_GRID_PADDING = new Insets(10, 10, 10, 10);
    private static final Insets SCREENS_PADDING = new Insets(5, 10, 0, 10);
    private static final Insets MEMORY_INDICATOR_PADDING = new Insets(0, 0, 0, 5);
    private static final double FIRST_SCREEN_HEIGHT = 35;
    private static final double SECOND_SCREEN_HEIGHT = 12;
    private static final double SCREENS_WIDTH = 189;
    private static final double WINDOW_HEIGHT = 318;
    private static final double WINDOW_WIDTH = 215;
    private static final double MEMORY_INDICATOR_SIZE = 25;
    private static final Insets MENU_BAR_PADDING = new Insets(0, 0, 2, 0);



    private TextField secondScreen;
    private TextField firstScreen;
    private Label memoryScreen;

    private CalculatorFormatter formatter;

    private final Enum[][] grid = {
            {ButtonEnum.MC, ButtonEnum.MR, ButtonEnum.MS, ButtonEnum.MPLUS, ButtonEnum.MMINUS},
            {ButtonEnum.BACKSPACE, ButtonEnum.CE, ButtonEnum.C, ButtonEnum.INVERT, ButtonEnum.SQRT},
            {ButtonEnum.SEVEN, ButtonEnum.EIGHT, ButtonEnum.NINE, ButtonEnum.DIVIDE, ButtonEnum.PERCENT},
            {ButtonEnum.FOUR, ButtonEnum.FIVE, ButtonEnum.SIX, ButtonEnum.MULTIPLY, ButtonEnum.REVERSE},
            {ButtonEnum.ONE, ButtonEnum.TWO, ButtonEnum.THREE, ButtonEnum.MINUS, ButtonEnum.EQUAL},
            {ButtonEnum.ZERO, null, ButtonEnum.DOT, ButtonEnum.PLUS, null}
    };

    Parent getRoot() {
        return createRoot();
    }

    CalculatorFormatter getFormatter() {
        return formatter;
    }

    public TextField getFirstScreen() {
        return firstScreen;
    }

    public TextField getSecondScreen() {
        return secondScreen;
    }

    public Label getMemoryScreen() {
        return memoryScreen;
    }

    /**
     * Holds calculator buttons.
     */
    enum ButtonEnum {
        ONE("1", KeyCode.NUMPAD1), TWO("2", KeyCode.NUMPAD2), THREE("3", KeyCode.NUMPAD3),
        FOUR("4", KeyCode.NUMPAD4), FIVE("5", KeyCode.NUMPAD5), SIX("6", KeyCode.NUMPAD6),
        SEVEN("7", KeyCode.NUMPAD7), EIGHT("8" ,KeyCode.NUMPAD8), NINE("9", KeyCode.NUMPAD9),
        ZERO("0", "0", KeyCode.NUMPAD0), EQUAL("=", "equal", KeyCode.ENTER), DOT(",", KeyCode.DECIMAL),
        BACKSPACE("\u2190", "backspace", KeyCode.BACK_SPACE), PLUS("+", KeyCode.ADD), MINUS("-", KeyCode.SUBTRACT),
        DIVIDE("/", KeyCode.DIVIDE), MULTIPLY("*", KeyCode.MULTIPLY),
        INVERT("\u00B1"), SQRT("\u221A"), PERCENT("%"), REVERSE("1/x", "reverse"),
        MC("MC"), MR("MR"), MS("MS"), MPLUS("M+"), MMINUS("M-"), CE("CE"), C("C", KeyCode.ESCAPE);

        /**
         * Text of the button.
         */
        private final String text;

        /**
         * Id of the button.
         */
        private String id;

        /**
         * Key code of the button.
         */
        private KeyCode keyCode;

        /**
         * Text field getter.
         * @return text of the button.
         */
        public String getText() {
            return text;
        }

        /**
         * Id field getter.
         * @return id of the button.
         */
        public String getId(){
            return id;
        }

        /**
         * Key code field getter.
         * @return key code of the button.
         * @throws Exception - if button has no key code.
         */
        public KeyCode getKeyCode() throws Exception{
            if(keyCode == null){
                throw new Exception("No key for this button");
            }
            return keyCode;
        }

        /**
         * Creates button with given text and id same as text.
         * @param text - text of the button.
         */
        ButtonEnum(String text) {
            this.text = text;
            this.id = text;
        }

        /**
         * Creates button with given text and key code.
         * @param text - text of the button.
         * @param keyCode - key code of the button.
         */
        ButtonEnum(String text, KeyCode keyCode){
            this.text = text;
            this.keyCode = keyCode;
        }

        /**
         * Creates button with given text, id and key code.
         * @param text - text of the button.
         * @param id - id of the button.
         * @param keyCode - key code of the button.
         */
        ButtonEnum(String text, String id, KeyCode keyCode){
            this.text = text;
            this.id = id;
            this.keyCode = keyCode;
        }
        ButtonEnum(String text, String id){
            this.text = text;
            this.id = id;
        }
    }

    /**
     * Application entry point.
     * @param args - command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts JavaFX application.
     * @param primaryStage - primary stage.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        primaryStage.setTitle(TITLE_TEXT);
        primaryStage.setScene(createScene());
        primaryStage.setMaxHeight(WINDOW_HEIGHT);
        primaryStage.setMaxWidth(WINDOW_WIDTH);
        primaryStage.getIcons().addAll(new Image("icon.png"));
        primaryStage.show();
    }

    /**
     * Creates scene with calculator.
     * @return scene.
     */
    private Scene createScene() {
        Scene scene = new Scene(createRoot());
        scene.getStylesheets().add("style.css");
        scene.getRoot().requestFocus();
        return scene;
    }

    /**
     * Add key events to the given parent node.
     * @param root - given parent node
     */
    private void addKeyEvents(Parent root){
        root.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case NUMPAD0:
                    formatter.pressDigitButton(0);
                    break;
                case NUMPAD1:
                    formatter.pressDigitButton(1);
                    break;
                case NUMPAD2:
                    formatter.pressDigitButton(2);
                    break;
                case NUMPAD3:
                    formatter.pressDigitButton(3);
                    break;
                case NUMPAD4:
                    formatter.pressDigitButton(4);
                    break;
                case NUMPAD5:
                    formatter.pressDigitButton(5);
                    break;
                case NUMPAD6:
                    formatter.pressDigitButton(6);
                    break;
                case NUMPAD7:
                    formatter.pressDigitButton(7);
                    break;
                case NUMPAD8:
                    formatter.pressDigitButton(8);
                    break;
                case NUMPAD9:
                    formatter.pressDigitButton(9);
                    break;
                case MULTIPLY:
                    formatter.pressOperationButton(Operation.MULTIPLY);
                    break;
                case ADD:
                    formatter.pressOperationButton(Operation.PLUS);
                    break;
                case SUBTRACT:
                    formatter.pressOperationButton(Operation.MINUS);
                    break;
                case DECIMAL:
                    formatter.pressDotButton();
                    break;
                case DIVIDE:
                    formatter.pressOperationButton(Operation.DIVIDE);
                    break;
                case BACK_SPACE:
                    formatter.pressBackSpaceButton();
                    break;
                case ENTER:
                    formatter.pressEqualButton();
                    break;
                case ESCAPE:
                    formatter.pressClearButton();
                    break;
                case C:
                    if(event.isControlDown()){
                        formatter.setClipboard();
                    }
                    break;
                case V:
                    if(event.isControlDown()){
                        formatter.getClipboard();
                    }
                    break;
            }
        });
    }

    /**
     * Creates node with calculator screens.
     * @return calculator screens in VBox.
     */
    private VBox createScreens(){
        firstScreen = new TextField();
        secondScreen = new TextField();
        memoryScreen = new Label();

        firstScreen.setEditable(false);
        firstScreen.setDisable(true);
        firstScreen.setId("firstScreen");
        firstScreen.setAlignment(Pos.BASELINE_RIGHT);
        firstScreen.setPrefWidth(SCREENS_WIDTH);
        firstScreen.setMaxWidth(SCREENS_WIDTH);
        secondScreen.setPrefWidth(SCREENS_WIDTH);
        secondScreen.setMaxWidth(SCREENS_WIDTH);
        firstScreen.setPrefHeight(FIRST_SCREEN_HEIGHT);
        secondScreen.setEditable(false);
        secondScreen.setDisable(true);
        secondScreen.setId("secondScreen");
        secondScreen.setAlignment(Pos.BOTTOM_RIGHT);
        secondScreen.setPrefHeight(SECOND_SCREEN_HEIGHT);
        memoryScreen.setId("memoryScreen");
        memoryScreen.setMaxSize(MEMORY_INDICATOR_SIZE, MEMORY_INDICATOR_SIZE);
        memoryScreen.setPadding(MEMORY_INDICATOR_PADDING);
        StackPane firstScreenWithMemory = new StackPane();
        firstScreenWithMemory.getChildren().add(firstScreen);
        firstScreenWithMemory.getChildren().add(memoryScreen);
        firstScreenWithMemory.setAlignment(Pos.BOTTOM_LEFT);
        VBox screens = new VBox();
        screens.getChildren().addAll(secondScreen, firstScreenWithMemory);
        screens.setId("screens");
        screens.setPadding(SCREENS_PADDING);
        return screens;
    }

    /**
     * Creates menu bar for application.
     * @return menu bar.
     */
    private MenuBar createMenu(){
        MenuBar menuBar = new MenuBar();
        menuBar.setPadding(MENU_BAR_PADDING);

        Menu menuFile = new Menu(MENU_FILE_TEXT);
        MenuItem exit = new MenuItem(MENU_FILE_EXIT_TEXT);
        exit.setOnAction(e -> System.exit(0));

        menuFile.getItems().addAll(exit);
        menuBar.getMenus().addAll(menuFile);

        return menuBar;
    }

    /**
     * Creates root node for application scene.
     * @return root node.
     */
    private BorderPane createRoot() {
        BorderPane root = new BorderPane();
        root.setId("root");
        addKeyEvents(root);

        VBox top = new VBox();
        top.setId("top");
        top.getChildren().addAll(createMenu(), createScreens());

        root.setTop(top);
        root.setCenter(createButtons());

        formatter = CalculatorFormatter.getInstance(firstScreen, secondScreen, memoryScreen);

        return root;
    }

    /**
     * Creates buttons for calculator.
     * @return grid pane with buttons.
     */
    private GridPane createButtons() {
        GridPane buttons = new GridPane();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == null) continue;
                ButtonEnum be = ((ButtonEnum) grid[i][j]);
                Button b = createButton(be);
                if (be.equals(ButtonEnum.ZERO)) {
                    GridPane.setColumnSpan(b, 2);
                    b.setPrefSize(BUTTON_WIDTH*2 + 5, BUTTON_HEIGHT);
                }
                if (be.equals(ButtonEnum.EQUAL)) {
                    GridPane.setRowSpan(b, 2);
                    b.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT * 2 + 5);
                }
                buttons.add(b, j, i);
            }
            buttons.setVgap(BUTTON_GRID_VGAP);
            buttons.setHgap(BUTTON_GRID_HGAP);
            buttons.setPadding(BUTTON_GRID_PADDING);
        }

        return buttons;
    }

    /**
     * Creates a single button based on information from buttons enum.
     * @param buttonEnum - information about button.
     * @return new button
     */
    private Button createButton(ButtonEnum buttonEnum) {
        Button b = null;
        switch (buttonEnum) {
            case ONE:
                b = createDigitButton(1);
                break;
            case TWO:
                b = createDigitButton(2);
                break;
            case THREE:
                b = createDigitButton(3);
                break;
            case FOUR:
                b = createDigitButton(4);
                break;
            case FIVE:
                b = createDigitButton(5);
                break;
            case SIX:
                b = createDigitButton(6);
                break;
            case SEVEN:
                b = createDigitButton(7);
                break;
            case EIGHT:
                b = createDigitButton(8);
                break;
            case NINE:
                b = createDigitButton(9);
                break;
            case ZERO:
                b = createDigitButton(0);
                break;
            case DOT:
                b = createDotButton();
                break;
            case BACKSPACE:
                b = createBackSpaceButton();
                break;
            case EQUAL:
                b = createEqualButton();
                break;
            case PLUS:
                b = createOperationButton(Operation.PLUS);
                break;
            case MINUS:
                b = createOperationButton(Operation.MINUS);
                break;
            case DIVIDE:
                b = createOperationButton(Operation.DIVIDE);
                break;
            case MULTIPLY:
                b = createOperationButton(Operation.MULTIPLY);
                break;
            case INVERT:
                b = createOperationButton(Operation.INVERT);
                break;
            case SQRT:
                b = createOperationButton(Operation.SQRT);
                break;
            case PERCENT:
                b = createOperationButton(Operation.PERCENT);
                break;
            case REVERSE:
                b = createOperationButton(Operation.REVERSE);
                break;
            case MC:
                b = createOperationButton(Operation.MC);
                break;
            case MR:
                b = createOperationButton(Operation.MR);
                break;
            case MS:
                b = createOperationButton(Operation.MS);
                break;
            case MPLUS:
                b = createOperationButton(Operation.MPLUS);
                break;
            case MMINUS:
                b = createOperationButton(Operation.MMINUS);
                break;
            case CE:
                b = createClearEntryButton();
                break;
            case C:
                b = createClearButton();
                break;
        }
        if (b == null) {
            b = new Button(buttonEnum.getText());
        }
        b.setId(buttonEnum.getId());
        b.setText(buttonEnum.getText());
        b.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        return b;
    }

    /**
     * Creates clear button (C).
     * @return clear button.
     */
    private Button createClearButton() {
        Button button = new Button();
        button.setOnAction(e -> formatter.pressClearButton());
        return button;
    }

    /**
     * Creates clear entry button (CE).
     * @return clear entry button.
     */
    private Button createClearEntryButton() {
        Button button = new Button("=");
        button.setOnAction(e -> formatter.pressClearEntryButton());
        return button;
    }

    /**
     * Creates operation button, based on information from operation enum.
     * @param operation - information about operation.
     * @return operation button.
     */
    private Button createOperationButton(final Operation operation) {
        Button button = new Button("");
        button.setOnAction(e -> formatter.pressOperationButton(operation));
        return button;
    }

    /**
     * Creates backspace button.
     * @return backspace button.
     */
    private Button createBackSpaceButton() {
        Button button = new Button("=");

        button.setOnAction(e -> formatter.pressBackSpaceButton());

        return button;
    }

    /**
     * Creates dot (decimal) button.
     * @return dot button.
     */
    private Button createDotButton() {
        Button button = new Button("=");
        button.setOnAction(e -> formatter.pressDotButton());
        return button;
    }

    /**
     * Creates equal button.
     * @return equal button.
     */
    private Button createEqualButton() {
        Button button = new Button("=");
        button.setOnAction(e -> formatter.pressEqualButton());
        button.getStyleClass().add("equalButton");
        return button;
    }

    /**
     * Creates digit button with given digit.
     * @param digit - digit of button
     * @return digit button
     */
    private Button createDigitButton(final int digit) {
        Button button = new Button();
        button.setOnAction(e -> formatter.pressDigitButton(digit));
        button.getStyleClass().add("digitButton");
        return button;
    }

}
