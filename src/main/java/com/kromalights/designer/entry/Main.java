/*

Copyright 2014 KromaLights

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package com.kromalights.designer.entry;

import com.kromalights.communications.fileio.FileIO;
import com.kromalights.communications.serialusb.SerialMessenger;
import com.kromalights.designer.components.*;
import com.kromalights.designer.kromadevices.Kroma16;
import com.kromalights.designer.kromadevices.Kroma8;
import com.kromalights.designer.kromadevices.KromaDevice;
import com.kromalights.designer.kromadevices.KromaPanel;
import com.kromalights.utils.SimpleConvertImage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Main extends Application {

    private final boolean LAUNCH_FULL_SCREEN = false;
    private final int FRAME_RATE = 30;
    //UI Components
    private Stage primaryStage;
    private VBox header;
    private MenuItem pasteMenuItem;
    private MenuItem detailToggleMenuItem;
    private MenuBar menuBar;
    private ToolBar colorBar;
    private final ToggleGroup colorPaletteGroup = new ToggleGroup();
    private AnchorPane root;
    private AnchorPane mainLeftPane;
    private Button lightOnButton;
    private Button lightOffButton;
    private Button frameBack;
    private Button insertBlankFrame;
    private Button insertCopyFrame;
    private Button deleteFrame;
    private Button frameForward;
    private Button framePlay;
    private Button drawImage;
    private ScrollPane mainContentScrollPane;
    private Pane mainContentPane;
    private Group mainContentGroup;
    private Kroma8 kroma8;
    private GridPane kroma8LabelPane;
    private Label kroma8Label;
    private Kroma16 kroma16;
    private GridPane kroma16LabelPane;
    private Label kroma16Label;
    private AnchorPane mainFooterPane;
    private Label frameLabel;

    //TODO: Limit max number of lights/Channels
    // 16 Channels / 1024 LEDs per channel

    //TODO: Undo functionality
    //TODO: Copy/Paste functionality
    private double currentZoomLevel = 3;
    private int currentFrame = 0, maxFrame = 0;
    private Timeline timeline;
    private Integer copiedFrame = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;

        //Anchor left panel which holds all th thumbnails for the KromaDevices to drag to the design pane
        AnchorPane.setTopAnchor(getMainLeftPane(), 57.0);
        AnchorPane.setBottomAnchor(getMainLeftPane(), 0.0);

        //Anchor toolbar
        AnchorPane.setLeftAnchor(getHeader(), 0.0);
        AnchorPane.setRightAnchor(getHeader(), 0.0);
        AnchorPane.setTopAnchor(getHeader(), 0.0);

        //Anchor design panel
        AnchorPane.setBottomAnchor(getMainContentScrollPane(), 100.0);
        AnchorPane.setLeftAnchor(getMainContentScrollPane(), 100.0);
        AnchorPane.setRightAnchor(getMainContentScrollPane(), 0.0);
        AnchorPane.setTopAnchor(getMainContentScrollPane(), 57.0);

        AnchorPane.setBottomAnchor(getMainFooterPane(), 0.0);
        AnchorPane.setRightAnchor(getMainFooterPane(), 0.0);
        AnchorPane.setLeftAnchor(getMainFooterPane(), 100.0);

        this.primaryStage.setTitle("Kromalights Designer");
        //Root panel, width, height
        Scene scene = new Scene(getRoot(), 640, 480);

        scene.getStylesheets().add("/com/kromalights/designer/styles/mainStyles.css");
        scene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                Integer digitPressed = null;
                switch(keyEvent.getCode()) {
                    case DIGIT0:
                        digitPressed = 0;
                        break;
                    case DIGIT1:
                        digitPressed = 1;
                        break;
                    case DIGIT2:
                        digitPressed = 2;
                        break;
                    case DIGIT3:
                        digitPressed = 3;
                        break;
                    case DIGIT4:
                        digitPressed = 4;
                        break;
                    case DIGIT5:
                        digitPressed = 5;
                        break;
                    case DIGIT6:
                        digitPressed = 6;
                        break;
                    case DIGIT7:
                        digitPressed = 7;
                        break;
                    case DIGIT8:
                        digitPressed = 8;
                        break;
                    case DIGIT9:
                        digitPressed = 9;
                        break;
                }

                if(keyEvent.isControlDown() && null != digitPressed) {
                    System.out.println("Ctrl+" + digitPressed);
                    ColorPaletteButtons.getInstance().get(digitPressed).setColor(KromaligthsColorPicker.getInstance().getValue());
                } else if(null != digitPressed){
                    KromaligthsColorPicker.getInstance().setValue(ColorPaletteButtons.getInstance().get(digitPressed).getColor());
                    //Bug fix to make color picker display selection
                    KromaligthsColorPicker.getInstance().fireEvent(new ActionEvent(keyEvent, KromaligthsColorPicker.getInstance()));

                    //Select the right button
                    ColorPaletteButtons.getInstance().get(digitPressed).fire();
                }
            }
        });

        //Handle the close event to allow saving file on exit
        Platform.setImplicitExit(false);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                applicationExit();
                event.consume();
            }
        });

        this.primaryStage.setScene(scene);

        this.primaryStage.setMinWidth(640);
        this.primaryStage.setMinHeight(480);

        if (LAUNCH_FULL_SCREEN) {
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            this.primaryStage.setX(bounds.getMinX());
            this.primaryStage.setY(bounds.getMinY());
            this.primaryStage.setWidth(bounds.getWidth());
            this.primaryStage.setHeight(bounds.getHeight());
        }

        this.primaryStage.show();

        getMainContentScrollPane().setVvalue(0.5);
        getMainContentScrollPane().setHvalue(0.5);
    }

    private VBox getHeader() {
        if (null == header) {
            header = VBoxBuilder.create()
                    .build();

            header.getChildren().addAll(getMenuBar(), getColorBar());
        }

        return header;
    }

    private ToolBar getColorBar() {
        if (null == colorBar) {
            colorBar = ToolBarBuilder.create()
                    .id("mainToolbar")
                    .build();

            colorPaletteGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> observableValue, final Toggle oldValue, final Toggle newValue) {
                    if ((newValue == null)) {
                        Platform.runLater(new Runnable() {

                            public void run() {
                                colorPaletteGroup.selectToggle(oldValue);
                            }
                        });
                    }
                }
            });
            TreeMap<Integer, Color> colorPaletteButtonsDefaultColors = new TreeMap<>();
            colorPaletteButtonsDefaultColors.put(0, Color.WHITE);
            colorPaletteButtonsDefaultColors.put(1, Color.RED);
            colorPaletteButtonsDefaultColors.put(2, Color.GREEN);
            colorPaletteButtonsDefaultColors.put(3, Color.BLUE);
            colorPaletteButtonsDefaultColors.put(4, Color.YELLOW);
            colorPaletteButtonsDefaultColors.put(5, Color.PURPLE);
            colorPaletteButtonsDefaultColors.put(6, Color.ORANGE);
            colorPaletteButtonsDefaultColors.put(7, Color.BROWN);
            colorPaletteButtonsDefaultColors.put(8, Color.CHARTREUSE);
            colorPaletteButtonsDefaultColors.put(9, Color.CORNFLOWERBLUE);

            for(int i = 1; i < 10; i++) {
                ColorPaletteButton colorPaletteButton = new ColorPaletteButton(i, colorPaletteButtonsDefaultColors.get(i));
                colorPaletteButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        KromaligthsColorPicker.getInstance().setValue(((ColorPaletteButton) actionEvent.getSource()).getColor());
                        //Bug fix to make color picker display selection
                        KromaligthsColorPicker.getInstance().fireEvent(new ActionEvent(actionEvent, KromaligthsColorPicker.getInstance()));
                    }
                });

                colorPaletteButton.setToggleGroup(colorPaletteGroup);
                colorBar.getItems().add(colorPaletteButton);
                ColorPaletteButtons.getInstance().put(i, colorPaletteButton);
            }
            ColorPaletteButton colorButton0 = new ColorPaletteButton(0, colorPaletteButtonsDefaultColors.get(0));
            colorButton0.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    KromaligthsColorPicker.getInstance().setValue(((ColorPaletteButton) actionEvent.getSource()).getColor());
                    //Bug fix to make color picker display selection
                    KromaligthsColorPicker.getInstance().fireEvent(new ActionEvent(actionEvent, KromaligthsColorPicker.getInstance()));
                }
            });
            ColorPaletteButtons.getInstance().put(0, colorButton0);
            colorButton0.setToggleGroup(colorPaletteGroup);
            ColorPaletteButtons.getInstance().get(0).fire();
            colorBar.getItems().addAll(colorButton0, KromaligthsColorPicker.getInstance(), getLightOnButton(),
                    getLightOffButton(),getDrawImage());
        }
        return colorBar;
    }

    private AnchorPane getRoot() {

        if (null == root) {
            root = AnchorPaneBuilder.create()
                    .id("rootPanel")
                    .build();
            root.getChildren().addAll(getMainContentScrollPane(), getMainLeftPane(), getHeader(), getMainFooterPane(),
                    getKroma8(), getKroma8LabelPane(), getKroma16(), getKroma16LabelPane());


        }
        return root;
    }

    private MenuItem getPasteMenuItem() {
        if(null == pasteMenuItem) {
            pasteMenuItem = new MenuItem("Paste Frame");
            pasteMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+V"));
            pasteMenuItem.setDisable(true);
            pasteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    for(Node node : getMainContentGroup().getChildren()) {
                        if(node instanceof KromaDevice) {
                            ((KromaDevice) node).loadFrame(copiedFrame);
                        }
                    }
                }
            });
        }

        return pasteMenuItem;
    }

    private MenuItem getDetailToggleMenuItem() {
        if(null == detailToggleMenuItem) {
            detailToggleMenuItem = new MenuItem("Show LEDs");
            detailToggleMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                    MenuItem menuItem = (MenuItem) actionEvent.getSource();
                    if ("Show Details".equals(menuItem.getText())) {
                        for (Node node : getMainContentGroup().getChildren()) {
                            if (node instanceof KromaPanel) {
                                KromaPanel kromaPanel = (KromaPanel) node;
                                kromaPanel.showDetails();
                            }
                        }
                        menuItem.setText("Show LEDs");
                    } else {
                        for (Node node : getMainContentGroup().getChildren()) {
                            if (node instanceof KromaPanel) {
                                KromaPanel kromaPanel = (KromaPanel) node;
                                kromaPanel.showLeds();
                            }
                        }
                        menuItem.setText("Show Details");
                    }
                }
            });
        }

        return detailToggleMenuItem;
    }

    private MenuBar getMenuBar() {
        if (null == menuBar) {

            menuBar = MenuBarBuilder.create()
                    .useSystemMenuBar(false)
                    .build();

            //File Menu
            MenuItem newMenuItem = new MenuItem("New");
            newMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
            newMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    newFile();
                }
            });
            MenuItem openMenuItem = new MenuItem("Open");
            openMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
            openMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    openFile();
                }
            });
            MenuItem saveMenuItem = new MenuItem("Save");
            saveMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
            saveMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    saveFile();
                }
            });
            MenuItem saveAsMenuItem = new MenuItem("Save As");
            saveAsMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+S"));
            saveAsMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    saveFileAs();
                }
            });
            MenuItem exitMenuItem = new MenuItem("Exit");
            exitMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
            exitMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    applicationExit();
                }
            });
            Menu menuFile = new Menu("File");
            menuFile.getItems().addAll(newMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem, new SeparatorMenuItem(), exitMenuItem);

            MenuItem cutMenuItem = new MenuItem("Cut Frame");
            cutMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
            cutMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    getPasteMenuItem().setDisable(false);
                    copiedFrame = currentFrame;
                    for(Node node : getMainContentGroup().getChildren()) {
                        if(node instanceof KromaDevice) {
                            ((KromaDevice) node).turnOffLights();
                        }
                    }
                }
            });
            MenuItem copyMenuItem = new MenuItem("Copy Frame");
            copyMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
            copyMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    getPasteMenuItem().setDisable(false);
                    copiedFrame = currentFrame;
                }
            });
            Menu menuEdit = new Menu("Edit");
            menuEdit.getItems().addAll(cutMenuItem, copyMenuItem, getPasteMenuItem());


            //Settings Menu
            MenuItem settingsMenuItem = new MenuItem("COM Settings");
            settingsMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    final ComPortSettingsModal comPortSettingsModal = new ComPortSettingsModal(primaryStage);
                    comPortSettingsModal.setOnHidden(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent windowEvent) {
                            primaryStage.getScene().getRoot().setEffect(null);
                        }
                    });
                    comPortSettingsModal.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent windowEvent) {
                            primaryStage.getScene().getRoot().setEffect(null);
                        }
                    });
                    comPortSettingsModal.initModality(Modality.WINDOW_MODAL);
                    comPortSettingsModal.initOwner(primaryStage);

                    primaryStage.getScene().getRoot().setEffect(new BoxBlur());
                    comPortSettingsModal.show();

                    //Center modal in application window
                    comPortSettingsModal.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - comPortSettingsModal.getWidth() / 2);
                    comPortSettingsModal.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - comPortSettingsModal.getHeight() / 2);
                }
            });
            Menu menuSettings = new Menu("Settings");
            menuSettings.getItems().addAll(getDetailToggleMenuItem(), settingsMenuItem);

            //Help Menu
            Menu menuHelp = new Menu("Help");

            menuBar.getMenus().addAll(menuFile, menuEdit, menuSettings, menuHelp);
        }

        return menuBar;
    }

    private AnchorPane getMainLeftPane() {
        if (null == mainLeftPane) {
            mainLeftPane = AnchorPaneBuilder.create()
                    .id("mainLeftPane")
                    .prefWidth(100)
                    .build();
        }
        return mainLeftPane;
    }

    private Button getLightOnButton() {
        if (null == lightOnButton) {
            lightOnButton = ButtonBuilder.create()
                    .text("Lights On")
                    .build();

            lightOnButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    for (Node node : getMainContentGroup().getChildren()) {
                        if (node instanceof KromaDevice) {
                            KromaDevice kromaDevice = (KromaDevice) node;
                            kromaDevice.turnOnLights();
                        }
                    }
                    SerialMessenger.getInstance().sendFA();
                }
            });
        }
        return lightOnButton;
    }

    private Button getLightOffButton() {
        if (null == lightOffButton) {
            lightOffButton = ButtonBuilder.create()
                    .text("Lights Off")
                    .build();

            lightOffButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    for (Node node : getMainContentGroup().getChildren()) {
                        if (node instanceof KromaDevice) {
                            ((KromaDevice) node).turnOffLights();
                        }
                    }
                    Button button = (Button) actionEvent.getSource();
                    SerialMessenger.getInstance().sendFA();
                }
            });
        }

        return lightOffButton;
    }

    private Button getDrawImage() {
        if (null == drawImage) {
            drawImage = ButtonBuilder.create()
                    .text("Image")
                    .build();
            drawImage.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    SimpleConvertImage sci = new SimpleConvertImage();
                    int[][] pixels = sci.getImagePixels();
                    int i = 0;
                    for (Node node : getMainContentGroup().getChildren()) {
                        if (node instanceof KromaDevice) {
                            for (Map.Entry<Integer, Led> entry : ((KromaDevice) node).getLeds().entrySet()) {
                                //colors.add(KromaligthsColorPicker.getInstance().getValue());
                                entry.getValue().setLedColor(pixels[i][1], pixels[i][2], pixels[i++][3]);
                            }
                        }
                    }
                }
            });
        }
        return drawImage;
    }

    public Button getFrameBack() {
        if (null == frameBack) {
            frameBack = ButtonBuilder.create()
                    .text("<")
                    .disable(true)
                    .build();
            frameBack.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    for (Node node : getMainContentGroup().getChildren()) {
                        if (node instanceof KromaDevice) {
                            ((KromaDevice) node).saveFrame(currentFrame);
                            ((KromaDevice) node).loadFrame(currentFrame - 1);
                        }
                    }
                    if (--currentFrame == 0) {
                        frameBack.setDisable(true);
                    }
                    getFrameForward().setDisable(false);
                    updateFrameLabel();
                    SerialMessenger.getInstance().sendFA();
                }
            });
        }
        return frameBack;
    }

    public Button getInsertBlankFrame() {
        if (null == insertBlankFrame) {
            insertBlankFrame = ButtonBuilder.create()
                    .text("Insert Blank")
                    .build();
            insertBlankFrame.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    for (Node node : getMainContentGroup().getChildren()) {
                        if (node instanceof KromaDevice) {
                            ((KromaDevice) node).insertFrame(currentFrame);
                            ((KromaDevice) node).loadFrame(currentFrame);
                        }
                    }

                    if(null != copiedFrame && copiedFrame > currentFrame) {
                        copiedFrame++;
                    }

                    maxFrame++;

                    getFrameForward().setDisable(false);
                    getDeleteFrame().setDisable(false);
                    getFramePlay().setDisable(false);
                    updateFrameLabel();

                    SerialMessenger.getInstance().sendFA();
                }
            });
        }
        return insertBlankFrame;
    }

    public Button getInsertCopyFrame() {
        if (null == insertCopyFrame) {
            insertCopyFrame = ButtonBuilder.create()
                    .text("Insert Copy")
                    .build();
            insertCopyFrame.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    for (Node node : getMainContentGroup().getChildren()) {
                        if (node instanceof KromaDevice) {
                            ((KromaDevice) node).insertFrame(currentFrame);
                            ((KromaDevice) node).loadFrame(currentFrame + 1);
                        }
                    }

                    if(null != copiedFrame && copiedFrame > currentFrame) {
                        copiedFrame++;
                    }

                    maxFrame++;

                    getFrameForward().setDisable(false);
                    getDeleteFrame().setDisable(false);
                    getFramePlay().setDisable(false);
                    updateFrameLabel();

                    SerialMessenger.getInstance().sendFA();
                }
            });
        }
        return insertCopyFrame;
    }

    public Button getDeleteFrame() {
        if (null == deleteFrame) {
            deleteFrame = ButtonBuilder.create()
                    .text("Delete Frame")
                    .disable(true)
                    .build();
            deleteFrame.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {

                    if(null != copiedFrame && copiedFrame == currentFrame) {
                        getPasteMenuItem().setDisable(true);
                        copiedFrame = null;
                    } else if(null != copiedFrame && copiedFrame > currentFrame) {
                        copiedFrame--;
                    }

                    for (Node node : getMainContentGroup().getChildren()) {
                        if (node instanceof KromaDevice) {
                            ((KromaDevice) node).deleteFrame(currentFrame);
                        }
                    }
                    if (currentFrame == maxFrame) {
                        currentFrame--;
                    }
                    if (--maxFrame < 1) {
                        getFrameBack().setDisable(true);
                        deleteFrame.setDisable(true);
                        getFrameForward().setDisable(true);
                        getFramePlay().setDisable(true);
                    }
                    if (currentFrame == maxFrame) {
                        getFrameForward().setDisable(true);
                    }
                    updateFrameLabel();
                    SerialMessenger.getInstance().sendFA();
                }
            });
        }
        return deleteFrame;
    }

    public Button getFrameForward() {
        if (null == frameForward) {
            frameForward = ButtonBuilder.create()
                    .text(">")
                    .disable(true)
                    .build();
            frameForward.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    for (Node node : getMainContentGroup().getChildren()) {
                        if (node instanceof KromaDevice) {
                            ((KromaDevice) node).saveFrame(currentFrame);
                            ((KromaDevice) node).loadFrame(currentFrame + 1);
                        }
                    }
                    if (++currentFrame == maxFrame) {
                        frameForward.setDisable(true);
                    }
                    getFrameBack().setDisable(false);
                    updateFrameLabel();
                    SerialMessenger.getInstance().sendFA();
                }
            });
        }
        return frameForward;
    }

    public Button getFramePlay() {
        if (null == framePlay) {
            framePlay = ButtonBuilder.create()
                    .text("Play")
                    .disable(true)
                    .build();

            framePlay.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {

                    if ("Play".equals(framePlay.getText())) {
                        framePlay.setText("Stop");
                        getFrameBack().setDisable(true);
                        getInsertBlankFrame().setDisable(true);
                        getDeleteFrame().setDisable(true);
                        getFrameForward().setDisable(true);

                        getTimeline().play();
                    } else {
                        framePlay.setText("Play");
                        getTimeline().stop();
                        getInsertBlankFrame().setDisable(false);
                        if (maxFrame > 0) {
                            getDeleteFrame().setDisable(false);
                            if (currentFrame > 0) {
                                getFrameBack().setDisable(false);
                            }
                            if (currentFrame < maxFrame) {
                                getFrameForward().setDisable(false);
                            }
                        }
                    }
                }
            });
        }

        return framePlay;
    }

    private Timeline getTimeline() {
        if (null == timeline) {
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.setAutoReverse(true);

            EventHandler onFinished = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    if (currentFrame == maxFrame) {
                        for (Node node : getMainContentGroup().getChildren()) {
                            if (node instanceof KromaDevice) {
                                ((KromaDevice) node).saveFrame(currentFrame);
                                ((KromaDevice) node).loadFrame(0);
                            }
                        }
                        currentFrame = 0;
                    } else {
                        getFrameForward().fire();
                        getFrameForward().setDisable(true);
                        getFrameBack().setDisable(true);
                    }
                    SerialMessenger.getInstance().sendFA();
                }
            };

            KeyFrame keyFrame = new KeyFrame(Duration.millis(1000 / FRAME_RATE), onFinished);
            timeline.getKeyFrames().add(keyFrame);
        }
        return timeline;
    }

    private ScrollPane getMainContentScrollPane() {
        if (null == mainContentScrollPane) {
            mainContentScrollPane = ScrollPaneBuilder.create()
                    .content(getMainContentPane())
                    .build();
        }
        return mainContentScrollPane;
    }

    private Pane getMainContentPane() {
        if (null == mainContentPane) {

            mainContentPane = new Pane();
            mainContentPane.setId("mainContentPane");
            mainContentPane.setPrefSize(8000, 8000);

            mainContentPane.getChildren().add(getMainContentGroup());


            mainContentPane.setOnDragOver(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                /* data is dragged over the target */

                /* accept it only if it is  not dragged from the same node
                 * and if it has a string data */
                    if (event.getGestureSource() != mainContentPane &&
                            event.getDragboard().hasString()) {
                    /* allow for both copying and moving, whatever user chooses */
                        event.acceptTransferModes(TransferMode.COPY);
                    }

                    event.consume();
                }
            });

            mainContentPane.setOnDragDropped(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                /* data dropped */
                /* if there is a string data on dragboard, read it and use it */
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasString()) {
                        final KromaDevice kromaDevice;
                        switch (db.getString()) {
                            case "kroma8":
                                kromaDevice = new Kroma8(Color.BLACK);
                                break;
                            case "kroma16":
                                kromaDevice = new Kroma16(Color.BLACK);
                                break;
                            default:
                                kromaDevice = null;
                                break;
                        }

                        if (null != kromaDevice) {
                            kromaDevice.setScaleX(currentZoomLevel);
                            kromaDevice.setScaleY(currentZoomLevel);
                            kromaDevice.setLayoutX(event.getX() - kromaDevice.getWidth() / 2);
                            kromaDevice.setLayoutY(event.getY() - kromaDevice.getHeight() / 2);

                            if ("Show Details".equals(getDetailToggleMenuItem().getText())) {
                                ((KromaPanel) kromaDevice).showLeds();
                            } else {
                                ((KromaPanel) kromaDevice).showDetails();
                            }

                            getMainContentGroup().getChildren().add(kromaDevice);

                            success = true;

                            // allow the kromadevice to be dragged around.
                            kromaDevice.setOnMousePressed(kromaDeviceMousePress);
                            kromaDevice.setOnMouseDragged(kromaDeviceMouseDragged);
                            kromaDevice.setOnMouseReleased(kromaDeviceMouseReleased);
                        }
                    }
                /* let the source know whether the string was successfully
                 * transferred and used */
                    event.setDropCompleted(success);

                    event.consume();
                }
            });
        }
        return mainContentPane;
    }

    private Group getMainContentGroup() {
        if (null == mainContentGroup) {
            mainContentGroup = new Group();
        }
        return mainContentGroup;
    }

    private Kroma8 getKroma8() {
        if (null == kroma8) {
            kroma8 = new Kroma8();
            kroma8.setLayoutX(50 - kroma8.getBoundsInParent().getWidth() / 2);
            kroma8.setLayoutY(90);
            kroma8.setCursor(Cursor.OPEN_HAND);
            kroma8.destroyMouseEvents();

            kroma8.setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                /* drag was detected, start drag-and-drop gesture*/

                /* allow any transfer mode */
                    Dragboard db = kroma8.startDragAndDrop(TransferMode.ANY);

                /* put a string on dragboard */
                    ClipboardContent content = new ClipboardContent();
                    content.putString("kroma8");
                    db.setContent(content);

                    event.consume();
                }
            });
        }
        return kroma8;
    }

    private GridPane getKroma8LabelPane() {
        if (null == kroma8LabelPane) {
            kroma8LabelPane = GridPaneBuilder.create()
                    .prefWidth(44)
                    .prefHeight(12)
                    .layoutX(28)
                    .layoutY(120)
                    .alignment(Pos.CENTER)
                    .build();
            kroma8LabelPane.getChildren().add(getKroma8Label());
        }
        return kroma8LabelPane;
    }

    private Label getKroma8Label() {
        if (null == kroma8Label) {
            kroma8Label = LabelBuilder.create()
                    .text("Kroma8")
                    .font(Font.font("System", FontPosture.REGULAR, 10.0))
                    .textFill(Color.WHITE)
                    .build();
        }
        return kroma8Label;
    }

    private Kroma16 getKroma16() {
        if (null == kroma16) {
            kroma16 = new Kroma16();
            kroma16.setLayoutX(50 - kroma16.getBoundsInParent().getWidth() / 2);
            kroma16.setLayoutY(148);
            kroma16.setCursor(Cursor.OPEN_HAND);
            kroma16.destroyMouseEvents();

            kroma16.setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                /* drag was detected, start drag-and-drop gesture*/

                /* allow any transfer mode */
                    Dragboard db = kroma16.startDragAndDrop(TransferMode.ANY);

                /* put a string on dragboard */
                    ClipboardContent content = new ClipboardContent();
                    content.putString("kroma16");
                    db.setContent(content);

                    event.consume();
                }
            });
        }
        return kroma16;
    }

    private GridPane getKroma16LabelPane() {
        if (null == kroma16LabelPane) {
            kroma16LabelPane = GridPaneBuilder.create()
                    .prefWidth(47)
                    .prefHeight(12)
                    .layoutX(27)
                    .layoutY(205)
                    .alignment(Pos.CENTER)
                    .build();
            kroma16LabelPane.getChildren().add(getKroma16Label());
        }
        return kroma16LabelPane;
    }

    private Label getKroma16Label() {
        if (null == kroma16Label) {
            kroma16Label = LabelBuilder.create()
                    .text("Kroma16")
                    .font(Font.font("System", FontPosture.REGULAR, 10.0))
                    .textFill(Color.WHITE)
                    .alignment(Pos.CENTER)
                    .build();
        }
        return kroma16Label;
    }

    private AnchorPane getMainFooterPane() {
        if (null == mainFooterPane) {
            mainFooterPane = AnchorPaneBuilder.create()
                    .id("mainFooterPane")
                    .prefHeight(100)
                    .build();
            HBox buttonsHbox = HBoxBuilder.create()
                    .alignment(Pos.BOTTOM_CENTER)
                    .padding(new Insets(5, 5, 5, 5))
                    .spacing(12)
                    .build();
            HBox labelHbox = HBoxBuilder.create()
                    .alignment(Pos.TOP_CENTER)
                    .padding(new Insets(5, 5, 5, 5))
                    .build();

            AnchorPane.setLeftAnchor(buttonsHbox, 0.0);
            AnchorPane.setRightAnchor(buttonsHbox, 0.0);
            AnchorPane.setBottomAnchor(buttonsHbox, 50.0);
            AnchorPane.setTopAnchor(buttonsHbox, 0.0);

            AnchorPane.setLeftAnchor(labelHbox, 0.0);
            AnchorPane.setRightAnchor(labelHbox, 0.0);
            AnchorPane.setBottomAnchor(labelHbox, 0.0);
            AnchorPane.setTopAnchor(labelHbox, 50.0);

            buttonsHbox.getChildren().addAll(getFrameBack(), getInsertBlankFrame(), getInsertCopyFrame(), getDeleteFrame(), getFrameForward(), getFramePlay());
            labelHbox.getChildren().add(getFrameLabel());

            mainFooterPane.getChildren().addAll(buttonsHbox, labelHbox);
        }

        return mainFooterPane;
    }

    private Label getFrameLabel() {
        if (null == frameLabel) {
            frameLabel = LabelBuilder.create()
                    .text("Frame " + (currentFrame + 1) + " of " + (maxFrame + 1))
                    .textFill(Color.WHITE)
                    .build();
        }

        return frameLabel;
    }

    private void updateFrameLabel() {
        getFrameLabel().setText("Frame " + (currentFrame + 1) + " of " + (maxFrame + 1));
    }

    private void clearDesigner() {
        currentZoomLevel = 3;
        for (final Node node : getMainContentGroup().getChildren()) {
            if (node instanceof KromaDevice) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        getMainContentGroup().getChildren().remove(node);
                    }
                });
            }
        }
        currentFrame = 0;
        maxFrame = 0;
        updateFrameLabel();

        getFrameBack().setDisable(true);
        getDeleteFrame().setDisable(true);
        getFrameForward().setDisable(true);
        getFramePlay().setDisable(true);
    }

    private void saveFrame() {
        for (Node node : getMainContentGroup().getChildren()) {
            if (node instanceof KromaDevice) {
                ((KromaDevice) node).saveFrame(currentFrame);
            }
        }
    }

    private void newFile() {
        if (FileIO.getInstance().closeDesign(primaryStage, getMainContentGroup().getChildren(), maxFrame)) {
            currentZoomLevel = 3;
            for (final Node node : getMainContentGroup().getChildren()) {
                if (node instanceof KromaDevice) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            getMainContentGroup().getChildren().remove(node);
                        }
                    });
                }
            }
            currentFrame = 0;
            maxFrame = 0;
            updateFrameLabel();

            getFrameBack().setDisable(true);
            getDeleteFrame().setDisable(true);
            getFrameForward().setDisable(true);
            getFramePlay().setDisable(true);

            getMainContentScrollPane().setHvalue(0.5);
            getMainContentScrollPane().setVvalue(0.5);
        }

        ColorPaletteButtons.getInstance().get(0).setColor(Color.WHITE);
        ColorPaletteButtons.getInstance().get(1).setColor(Color.RED);
        ColorPaletteButtons.getInstance().get(2).setColor(Color.GREEN);
        ColorPaletteButtons.getInstance().get(3).setColor(Color.BLUE);
        ColorPaletteButtons.getInstance().get(4).setColor(Color.YELLOW);
        ColorPaletteButtons.getInstance().get(5).setColor(Color.PURPLE);
        ColorPaletteButtons.getInstance().get(6).setColor(Color.ORANGE);
        ColorPaletteButtons.getInstance().get(7).setColor(Color.BROWN);
        ColorPaletteButtons.getInstance().get(8).setColor(Color.CHARTREUSE);
        ColorPaletteButtons.getInstance().get(9).setColor(Color.CORNFLOWERBLUE);
    }

    private void openFile() {
        saveFrame();
        List<KromaDevice> kromaDevices = FileIO.getInstance().openDesign(primaryStage, getMainContentGroup().getChildren(), maxFrame);

        if (null == kromaDevices) {
            return;
        }

        clearDesigner();

        for (KromaDevice kromaDevice : kromaDevices) {

            // allow the kromadevice to be dragged around.
            kromaDevice.setOnMousePressed(kromaDeviceMousePress);
            kromaDevice.setOnMouseDragged(kromaDeviceMouseDragged);
            kromaDevice.setOnMouseReleased(kromaDeviceMouseReleased);

            int frames = kromaDevice.getLedFrames().size() - 1;
            maxFrame = frames > maxFrame ? frames : maxFrame;

            kromaDevice.loadFrame(0);

            if (kromaDevice instanceof KromaPanel) {
                ((KromaPanel) kromaDevice).showDetails();
            }

            getDetailToggleMenuItem().setText("Show LEDs");
        }

        if (maxFrame > 0) {
            getFrameForward().setDisable(false);
            getDeleteFrame().setDisable(false);
            getFramePlay().setDisable(false);
        }
        getMainContentGroup().getChildren().addAll(kromaDevices);

        updateFrameLabel();

        getMainContentScrollPane().setHvalue(0.5);
        getMainContentScrollPane().setVvalue(0.5);
    }

    private void saveFile() {
        saveFrame();
        FileIO.getInstance().saveDesign(primaryStage, getMainContentGroup().getChildren(), maxFrame);
    }

    private void saveFileAs() {
        saveFrame();
        FileIO.getInstance().saveDesignAs(primaryStage, getMainContentGroup().getChildren(), maxFrame);
    }

    public void applicationExit() {
        if (getMainContentGroup().getChildren().size() > 0) {
            Dialogs.DialogResponse response = Dialogs.showConfirmDialog(primaryStage, "Save?", "Would you like to save before you exit?", "Exit");
            switch (response) {
                case YES:
                    saveFile();
                    break;
                case CANCEL:
                    return;
            }
        }

        Platform.exit();
    }

    //Check to see if two nodes overlap
    private boolean checkOverlap(KromaDevice droppedKromaDevice, KromaDevice parentKromaDevice) {

        double droppedLeftX = droppedKromaDevice.getTopLeftAnchorPoint()[0];
        double droppedTopY = droppedKromaDevice.getTopLeftAnchorPoint()[1];
        double droppedRightX = droppedKromaDevice.getTopRightAnchorPoint()[0];
        double droppedBottomY = droppedKromaDevice.getBottomLeftAnchorPoint()[1];
        double parentLeftX = parentKromaDevice.getTopLeftAnchorPoint()[0];
        double parentTopY = parentKromaDevice.getTopLeftAnchorPoint()[1];
        double parentRightX = parentKromaDevice.getTopRightAnchorPoint()[0];
        double parentBottomY = parentKromaDevice.getBottomLeftAnchorPoint()[1];

        boolean droppedLeftOverlap = droppedLeftX >= parentLeftX && droppedLeftX <= parentRightX;
        boolean droppedRightOverlap = droppedRightX >= parentLeftX && droppedRightX <= parentRightX;
        boolean droppedTopOverlap = droppedTopY >= parentTopY && droppedTopY <= parentBottomY;
        boolean droppedBottomOverlap = droppedBottomY >= parentTopY && droppedBottomY <= parentBottomY;

        return (droppedLeftOverlap || droppedRightOverlap) && (droppedTopOverlap || droppedBottomOverlap);
    }


    //Event Hanlders
    EventHandler<MouseEvent> kromaDeviceMousePress = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            // record a delta distance for the drag and drop operation.
            final Delta dragDelta = new Delta(mouseEvent.getScreenX(), mouseEvent.getScreenY());

            if (!(mouseEvent.getSource() instanceof KromaDevice)) {
                return;
            }

            KromaDevice kromaDevice = (KromaDevice) mouseEvent.getSource();
            kromaDevice.setDelta(dragDelta);
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                final PanelSettingModal panelSettingModal = new PanelSettingModal(primaryStage, (KromaDevice) mouseEvent.getSource());
                panelSettingModal.setOnHidden(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent windowEvent) {
                        primaryStage.getScene().getRoot().setEffect(null);
                    }
                });
                panelSettingModal.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent windowEvent) {
                        primaryStage.getScene().getRoot().setEffect(null);
                    }
                });
                panelSettingModal.initModality(Modality.WINDOW_MODAL);
                panelSettingModal.initOwner(primaryStage);

                primaryStage.getScene().getRoot().setEffect(new BoxBlur());
                panelSettingModal.show();

                //Center modal in application window
                panelSettingModal.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - panelSettingModal.getWidth() / 2);
                panelSettingModal.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - panelSettingModal.getHeight() / 2);
            }


            for (Node node : getMainContentGroup().getChildren()) {
                if (node instanceof KromaDevice) {

                    double kromaDeviceWidth = node.getBoundsInParent().getWidth();
                    double kromaDeviceHeight = node.getBoundsInParent().getHeight();
                    //This x,y represents the top left corner of the node
                    double kromaDeviceX = node.localToParent(0.0, 0.0).getX();
                    double kromaDeviceY = node.localToParent(0.0, 0.0).getY();

                    ((KromaDevice) node).setAnchors(kromaDeviceX, kromaDeviceY, kromaDeviceWidth, kromaDeviceHeight);
                }
            }
        }
    };

    EventHandler<MouseEvent> kromaDeviceMouseDragged = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {

            if (!(mouseEvent.getSource() instanceof KromaDevice)) {
                return;
            }

            KromaDevice kromaDevice = (KromaDevice) mouseEvent.getSource();

            kromaDevice.drag(mouseEvent.getScreenX(), mouseEvent.getScreenY());

            //Need to get the size and position relative to the parent

            double kromaDeviceWidth = kromaDevice.getBoundsInParent().getWidth();
            double kromaDeviceHeight = kromaDevice.getBoundsInParent().getHeight();
            //This x,y represents the top left corner of the node
            double kromaDeviceX = kromaDevice.localToParent(0.0, 0.0).getX();
            double kromaDeviceY = kromaDevice.localToParent(0.0, 0.0).getY();

            kromaDevice.setAnchors(kromaDeviceX, kromaDeviceY, kromaDeviceWidth, kromaDeviceHeight);

            boolean overlappingNodes = false;

            //Check for overlapping nodes
            for (Node node : getMainContentGroup().getChildren()) {
                if (node instanceof KromaDevice && !mouseEvent.getSource().equals(node)) {
                    if (checkOverlap((KromaDevice) mouseEvent.getSource(), (KromaDevice) node)) {
                        overlappingNodes = true;
                        break;
                    }
                }
            }

            for (Node node : getMainContentGroup().getChildren()) {
                if (node instanceof KromaDevice && !kromaDevice.equals(node)) {
                    if (!kromaDevice.hasNeighbors()) {
                        if (overlappingNodes) {
                            ((KromaDevice) node).clearAnchoNotify();
                        } else {
                            ((KromaDevice) node).checkAnchors(kromaDevice.getAnchors());
                        }
                    }
                }
            }


            mouseEvent.consume();
        }
    };

    //Drop kromapanels
    EventHandler<MouseEvent> kromaDeviceMouseReleased = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {

            if (!(mouseEvent.getSource() instanceof KromaDevice)) {
                return;
            }

            boolean overlappingNodes = false;

            for (Node node : getMainContentGroup().getChildren()) {
                if (node instanceof KromaDevice && !mouseEvent.getSource().equals(node)) {
                    if (checkOverlap((KromaDevice) mouseEvent.getSource(), (KromaDevice) node)) {
                        overlappingNodes = true;
                        break;
                    }
                }
            }

            if (!overlappingNodes) {
                snapCheck:
                for (Node node : getMainContentGroup().getChildren()) {
                    if (node instanceof KromaDevice && !mouseEvent.getSource().equals(node)) {
                        KromaDevice droppedKromaDevice = (KromaDevice) mouseEvent.getSource();
                        KromaDevice parentKromaDevice = (KromaDevice) node;

                        parentKromaDevice.setAnchoringAvailable(true);

                        switch (parentKromaDevice.availableAnchor()) {
                            case TOP:
                                droppedKromaDevice.setLayoutX(droppedKromaDevice.getLayoutX() - droppedKromaDevice.getBottomCenterAnchorPoint()[0] + parentKromaDevice.getTopCenterAnchorPoint()[0]);
                                droppedKromaDevice.setLayoutY(droppedKromaDevice.getLayoutY() - droppedKromaDevice.getBottomCenterAnchorPoint()[1] + parentKromaDevice.getTopCenterAnchorPoint()[1]);

                                droppedKromaDevice.setBottomNeighbor(parentKromaDevice);
                                parentKromaDevice.setTopNeighbor(droppedKromaDevice);
                                break snapCheck;
                            case RIGHT:
                                droppedKromaDevice.setLayoutX(droppedKromaDevice.getLayoutX() - droppedKromaDevice.getLeftCenterAnchorPoint()[0] + parentKromaDevice.getRightCenterAnchorPoint()[0]);
                                droppedKromaDevice.setLayoutY(droppedKromaDevice.getLayoutY() - droppedKromaDevice.getLeftCenterAnchorPoint()[1] + parentKromaDevice.getRightCenterAnchorPoint()[1]);

                                droppedKromaDevice.setLeftNeighbor(parentKromaDevice);
                                parentKromaDevice.setRightNeighbor(droppedKromaDevice);
                                break snapCheck;
                            case BOTTOM:
                                droppedKromaDevice.setLayoutX(droppedKromaDevice.getLayoutX() - droppedKromaDevice.getTopCenterAnchorPoint()[0] + parentKromaDevice.getBottomCenterAnchorPoint()[0]);
                                droppedKromaDevice.setLayoutY(droppedKromaDevice.getLayoutY() - droppedKromaDevice.getTopCenterAnchorPoint()[1] + parentKromaDevice.getBottomCenterAnchorPoint()[1]);

                                droppedKromaDevice.setTopNeighbor(parentKromaDevice);
                                parentKromaDevice.setBottomNeighbor(droppedKromaDevice);
                                break snapCheck;
                            case LEFT:
                                droppedKromaDevice.setLayoutX(droppedKromaDevice.getLayoutX() - droppedKromaDevice.getRightCenterAnchorPoint()[0] + parentKromaDevice.getLeftCenterAnchorPoint()[0]);
                                droppedKromaDevice.setLayoutY(droppedKromaDevice.getLayoutY() - droppedKromaDevice.getRightCenterAnchorPoint()[1] + parentKromaDevice.getLeftCenterAnchorPoint()[1]);

                                droppedKromaDevice.setRightNeighbor(parentKromaDevice);
                                parentKromaDevice.setLeftNeighbor(droppedKromaDevice);
                                break snapCheck;
                            default:
                                break;
                        }

                    }
                }
            }
            for (Node node : getMainContentGroup().getChildren()) {
                if (node instanceof KromaDevice) {
                    ((KromaDevice) node).clearAnchoNotify();
                }
            }
            mouseEvent.consume();
        }
    };

}
