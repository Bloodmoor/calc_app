package com.implemica.zavizionov.calculator;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CalculatorView extends Application {

    private static final String DEFAULT_TEXT = "0";
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



    BorderPane root = new BorderPane();
    TextField secondScreen = new TextField();
    TextField screen = new TextField();
    Label memoryScreen = new Label();


    private CalculatorController controller;

    private Enum[][] grid = {
            {ButtonEnum.MC, ButtonEnum.MR, ButtonEnum.MS, ButtonEnum.MPLUS, ButtonEnum.MMINUS},
            {ButtonEnum.BACKSPACE, ButtonEnum.CE, ButtonEnum.C, ButtonEnum.INVERT, ButtonEnum.SQRT},
            {ButtonEnum.SEVEN, ButtonEnum.EIGHT, ButtonEnum.NINE, ButtonEnum.DIVIDE, ButtonEnum.PERCENT},
            {ButtonEnum.FOUR, ButtonEnum.FIVE, ButtonEnum.SIX, ButtonEnum.MULTIPLY, ButtonEnum.REVERSE},
            {ButtonEnum.ONE, ButtonEnum.TWO, ButtonEnum.THREE, ButtonEnum.MINUS, ButtonEnum.EQUAL},
            {ButtonEnum.ZERO, null, ButtonEnum.DOT, ButtonEnum.PLUS, null}
    };

    Parent getCalculatorRoot() {
        fillRoot();
        return root;
    }

    CalculatorController getController() {
        return controller;
    }

    enum ButtonEnum {
        ONE("1"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"),
        SEVEN("7"), EIGHT("8"), NINE("9"), ZERO("0"), EQUAL("="), DOT("."),
        BACKSPACE("<-"), PLUS("+"), MINUS("-"), DIVIDE("/"), MULTIPLY("*"),
        INVERT("\u00B1"), SQRT("\u221A"), PERCENT("%"), REVERSE("1/x"),
        MC("MC"), MR("MR"), MS("MS"), MPLUS("M+"), MMINUS("M-"), CE("CE"), C("C");

        private String text;

        public String getText() {
            return text;
        }

        ButtonEnum(String text) {
            this.text = text;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        primaryStage.setTitle(TITLE_TEXT);
        primaryStage.setScene(createScene());
        primaryStage.setMaxHeight(318);
        primaryStage.setMaxWidth(215);
        primaryStage.getIcons().addAll(new Image("icon.png"));
        primaryStage.show();
    }

    private Scene createScene() {
        fillRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().addAll("style.css");
        return scene;
    }

    private void fillRoot() {
        screen.setEditable(false);
        screen.setDisable(true);
        screen.setId("firstScreen");
        screen.setAlignment(Pos.CENTER_RIGHT);
        screen.setPrefHeight(FIRST_SCREEN_HEIGHT);
        secondScreen.setEditable(false);
        secondScreen.setDisable(true);
        secondScreen.setId("secondScreen");
        secondScreen.setAlignment(Pos.CENTER_RIGHT);
        secondScreen.setPrefHeight(SECOND_SCREEN_HEIGHT);
        memoryScreen.setId("memoryScreen");
        memoryScreen.setMaxSize(25, 25);
        memoryScreen.setPadding(MEMORY_INDICATOR_PADDING);
        StackPane firstScreen = new StackPane();
        VBox screens = new VBox();
//        screens.setStyle("-fx-background-color: linear-gradient(#E5EEFB, #ffffff)");
        screens.getChildren().addAll(secondScreen, firstScreen);
        screens.setId("screens");
        screens.setPadding(SCREENS_PADDING);
        VBox top = new VBox();
        MenuBar menuBar = new MenuBar();
        menuBar.setPadding(new Insets(0, 0 ,2 ,0));
        menuBar.setStyle("-fx-border-color: #D4D0C8");
        top.setStyle("-fx-background-color: #D9E4F1");
        Menu menuFile = new Menu(MENU_FILE_TEXT);
        MenuItem exit = new MenuItem(MENU_FILE_EXIT_TEXT);
        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.exit(0);
            }
        });
        menuFile.getItems().addAll(exit);
        menuBar.getMenus().addAll(menuFile);
        firstScreen.getChildren().add(screen);
        firstScreen.getChildren().add(memoryScreen);
        firstScreen.setAlignment(Pos.BOTTOM_LEFT);
        top.getChildren().addAll(menuBar, screens);
        controller = CalculatorController.getInstance(screen, secondScreen, memoryScreen);
        root.setTop(top);
        root.setCenter(createButtons());
        root.setStyle("-fx-background-color: #D9E4F1");
    }

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
                b.getStyleClass().add("reverseButton");
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
        if(buttonEnum.equals(ButtonEnum.EQUAL)){
            b.setId("equal");
        }else{
            b.setId(buttonEnum.getText());
        }
        b.setText(buttonEnum.getText());
        b.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        return b;
    }

    private Button createClearButton() {
        Button button = new Button();
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                controller.pressClearButton();
            }
        });
        return button;
    }

    private Button createClearEntryButton() {
        Button button = new Button("=");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                controller.pressClearEntryButton();
            }
        });
        return button;
    }

    private Button createOperationButton(final Operation operation) {
        Button button = new Button("");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                controller.pressOperationButton(operation);
            }
        });
        return button;
    }

    private Button createBackSpaceButton() {
        Button button = new Button("=");

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                controller.pressBackSpaceButton();
            }
        });

        return button;
    }

    private Button createDotButton() {
        Button button = new Button("=");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                controller.pressDotButton();
            }
        });
        return button;
    }

    private Button createEqualButton() {
        Button button = new Button("=");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                controller.pressEqualButton();
            }
        });
        button.getStyleClass().add("equalButton");
        return button;
    }

    private Button createDigitButton(final int digit) {
        Button button = new Button();
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                controller.pressDigitButton(digit);
            }
        });
        button.getStyleClass().add("digitButton");
        return button;
    }

}
