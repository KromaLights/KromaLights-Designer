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

import com.kromalights.designer.kromadevices.KromaDevice;

import com.kromalights.designer.kromadevices.KromaPanel;
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

public class PanelSettingModal extends Stage {
    private Stage stage;
    private VBox root;
    private GridPane mainContent;
    private HBox footer;
    private Button cancelButton;
    private Button okButton;
    private ComboBox<Integer> channelList;
    private ComboBox<Integer> addressList;
    private KromaDevice kromaDevice;

    public PanelSettingModal(Stage owner, KromaDevice kromaDevice) {
        super(StageStyle.TRANSPARENT);

        this.kromaDevice = kromaDevice;
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
            Label ChannelLabel = LabelBuilder.create()
                    .text("Channel:")
                    .build();
            Label idLabel = LabelBuilder.create()
                    .text("ID:")
                    .build();
            mainContent = GridPaneBuilder.create()
                    .padding(new Insets(5))
                    .hgap(5)
                    .vgap(5)
                    .build();

            mainContent.add(ChannelLabel, 0, 0);
            mainContent.add(idLabel, 0, 1);
            mainContent.add(getChannelList(), 1, 0);
            mainContent.add(getAddressList(), 1, 1);
        }
        return mainContent;
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

    public Button getCancelButton() {
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

    public Button getOkButton() {
        if (null == okButton) {
            okButton = ButtonBuilder.create()
                    .text("OK")
                    .build();
            okButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    kromaDevice.setChannel(channelList.getValue());
                    kromaDevice.setAddress(addressList.getValue());

                    //TODO: Need to go through all panels and update as necessary. Needed if we are over-ridding an existing channel and address
                    if(kromaDevice instanceof KromaPanel) {
                        ((KromaPanel) kromaDevice).updateDetails();
                    }
                    
                    stage.close();
                }
            });
        }
        return okButton;
    }

    private ComboBox getChannelList() {
        if (null == channelList) {
            ObservableList<Integer> options = FXCollections.observableArrayList();

            for( int i = 0; i < 32; i++) {
                options.add(i);
            }

            channelList = new ComboBox<>(options);
            channelList.setValue(kromaDevice.getChannel());
        }

        return channelList;
    }

    private ComboBox getAddressList() {
        if (null == addressList) {
            ObservableList<Integer> options = FXCollections.observableArrayList();

            //TODO: This needs to be dynamic
            for(int i = 0; i < 16; i ++) {
                options.add(i);
            }

            addressList = new ComboBox<>(options);
            addressList.setValue((kromaDevice.getAddress()));

        }
        return addressList;
    }
}
