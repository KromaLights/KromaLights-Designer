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

package com.kromalights.designer.components;

import com.kromalights.communications.serialusb.SerialMessenger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jssc.SerialPort;
import jssc.SerialPortList;

public class ComPortSettingsModal extends Stage {

    private Stage stage;
    private VBox root;
    private GridPane mainContent;
    private ComboBox<String> comPortsList;
    private ComboBox<Integer> baudRateList;
    private ComboBox<Integer> datBitsList;
    private ComboBox<Integer> stopBitsList;
    private ComboBox<String> parityList;
    private HBox footer;
    private Button cancelButton;
    private Button okButton;

    public ComPortSettingsModal(Stage owner) {
        super(StageStyle.TRANSPARENT);
        initOwner(owner);
        setTitle("Com Port Settings");
        setResizable(false);
        setMinWidth(200);

        stage = this;
        stage.setTitle("Com Port Settings");

        Scene scene = new Scene(getRoot(), Color.TRANSPARENT);
        scene.getStylesheets().add("/com/kromalights/designer/styles/modal-dialog.css");
        stage.setScene(scene);

    }

    private VBox getRoot() {
        if (null == root) {
            root = VBoxBuilder.create()
                    .styleClass("modal-dialog")
                    .children(getMainContent(), getFooter())
                    .build();
        }
        return root;
    }

    private GridPane getMainContent() {
        if (null == mainContent) {
            Label comPortsLabel = LabelBuilder.create()
                    .text("Com Port:")
                    .build();
            Label baudRateLabel = LabelBuilder.create()
                    .text("Baud Rate:")
                    .build();
            Label dataBitsLabel = LabelBuilder.create()
                    .text("Data Bits:")
                    .build();
            Label stopBitsLabel = LabelBuilder.create()
                    .text("Stop bits:")
                    .build();
            Label parityBitsLabel = LabelBuilder.create()
                    .text("Parity:")
                    .build();
            mainContent = GridPaneBuilder.create()
                    .padding(new Insets(5))
                    .hgap(5)
                    .vgap(5)
                    .build();

            mainContent.add(comPortsLabel, 0, 0);
            mainContent.add(baudRateLabel, 0, 1);
            mainContent.add(dataBitsLabel, 0, 2);
            mainContent.add(stopBitsLabel, 0, 3);
            mainContent.add(parityBitsLabel, 0, 4);
            mainContent.add(getComPortsList(), 1, 0);
            mainContent.add(getBaudRateList(), 1, 1);
            mainContent.add(getDatBitsList(), 1, 2);
            mainContent.add(getStopBitsList(), 1, 3);
            mainContent.add(getParityList(), 1, 4);
        }
        return mainContent;
    }

    private ComboBox getComPortsList() {

        if (null == comPortsList) {
            String[] portNames = SerialPortList.getPortNames();
            ObservableList<String> options = FXCollections.observableArrayList();

            options.addAll(portNames);

            comPortsList = new ComboBox<>(options);
            comPortsList.setValue(SerialMessenger.getInstance().getSerialPortName());
        }

        return comPortsList;
    }

    private ComboBox getBaudRateList() {
        if (null == baudRateList) {
            ObservableList<Integer> options = FXCollections.observableArrayList();

            options.addAll(SerialPort.BAUDRATE_300, SerialPort.BAUDRATE_600, SerialPort.BAUDRATE_1200,
                    SerialPort.BAUDRATE_4800, SerialPort.BAUDRATE_9600, SerialPort.BAUDRATE_14400,
                    SerialPort.BAUDRATE_19200, SerialPort.BAUDRATE_38400, SerialPort.BAUDRATE_57600,
                    SerialPort.BAUDRATE_115200, SerialPort.BAUDRATE_128000, SerialPort.BAUDRATE_256000);
            baudRateList = new ComboBox<>(options);
            baudRateList.setValue(SerialMessenger.getInstance().getBaudRate());
        }
        return baudRateList;
    }

    private ComboBox getDatBitsList() {
        if (null == datBitsList) {
            ObservableList<Integer> options = FXCollections.observableArrayList();

            options.addAll(SerialPort.DATABITS_5, SerialPort.DATABITS_6, SerialPort.DATABITS_7, SerialPort.DATABITS_8);
            datBitsList = new ComboBox<>(options);
            datBitsList.setValue(SerialMessenger.getInstance().getDataBits());
        }
        return datBitsList;
    }

    private ComboBox getStopBitsList() {
        if (null == stopBitsList) {
            ObservableList<Integer> options = FXCollections.observableArrayList();

            options.addAll(SerialPort.STOPBITS_1, SerialPort.STOPBITS_2, SerialPort.STOPBITS_1_5);
            stopBitsList = new ComboBox<>(options);
            stopBitsList.setValue(SerialMessenger.getInstance().getStopBits());
        }
        return stopBitsList;
    }

    private ComboBox getParityList() {
        if (null == parityList) {
            ObservableList<String> options = FXCollections.observableArrayList();
            options.addAll("None", "Odd", "Even", "Mark", "Space");
            parityList = new ComboBox<>(options);
            switch (SerialMessenger.getInstance().getParity()) {
                case 0:
                    parityList.setValue("None");
                    break;
                case 1:
                    parityList.setValue("Odd");
                    break;
                case 2:
                    parityList.setValue("Even");
                    break;
                case 3:
                    parityList.setValue("Mark");
                    break;
                case 4:
                    parityList.setValue("Space");
                    break;
            }
        }
        return parityList;
    }

    private HBox getFooter() {
        if (null == footer) {
            footer = HBoxBuilder.create()
                    .spacing(5)
                    .alignment(Pos.CENTER_RIGHT)
                    .build();

            footer.getChildren().addAll(getCancelButton(), getOkButton());
        }
        return footer;
    }

    private Button getCancelButton() {
        if (null == cancelButton) {
            cancelButton = ButtonBuilder.create()
                    .text("Cancel")
                    .build();

            cancelButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    stage.close();
                }
            });
        }
        return cancelButton;
    }

    private Button getOkButton() {
        if (null == okButton) {
            okButton = ButtonBuilder.create()
                    .text("OK")
                    .build();
            okButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    SerialMessenger.getInstance().setSerialPortName(comPortsList.getValue());
                    SerialMessenger.getInstance().setBaudRate(baudRateList.getValue());
                    SerialMessenger.getInstance().setDataBits(datBitsList.getValue());
                    SerialMessenger.getInstance().setStopBits(stopBitsList.getValue());
                    switch (parityList.getValue()) {
                        case "None":
                            SerialMessenger.getInstance().setParity(0);
                            break;
                        case "Odd":
                            SerialMessenger.getInstance().setParity(1);
                            break;
                        case "Even":
                            SerialMessenger.getInstance().setParity(2);
                            break;
                        case "Mark":
                            SerialMessenger.getInstance().setParity(3);
                            break;
                        case "Space":
                            SerialMessenger.getInstance().setParity(4);
                            break;
                    }
                    stage.close();
                }
            });
        }
        return okButton;
    }
}
