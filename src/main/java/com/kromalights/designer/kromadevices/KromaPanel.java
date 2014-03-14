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

package com.kromalights.designer.kromadevices;


import com.kromalights.designer.components.Led;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Map;

public class KromaPanel extends KromaDevice {

    private AnchorPane board;
    private AnchorPane ledHolder;
    private BorderPane details;
    private VBox settings;
    private Label titleLabel, channelLabel, addressLabel;

    KromaPanel(int numberOfLeds, double width, double height, Paint paint) {

        super(numberOfLeds, width, height, paint);

        AnchorPane.setTopAnchor(getBoard(), 0.0);
        AnchorPane.setRightAnchor(getBoard(), 0.0);
        AnchorPane.setBottomAnchor(getBoard(), 0.0);
        AnchorPane.setLeftAnchor(getBoard(), 0.0);

        getChildren().addAll(getBoard(), getLedHolder());
        getChildren().addAll(getTopAnchorNotify(), getRightAnchorNotify(), getBottomAnchorNotify(), getLeftAnchorNotify());

        showLeds();
        //showDetails();

    }

    //TODO: Need to utilize this
    public boolean verifyPanel() {

        int sqrtOfNumLeds = (int) Math.sqrt(this.getNumberOfLeds());

        return sqrtOfNumLeds * sqrtOfNumLeds == this.getNumberOfLeds();
    }

    public void showLeds() {
        getLedHolder().setVisible(true);
        getDetails().setVisible(false);
        getBoard().setStyle("-fx-background-color: #3A6629; -fx-border-color: #3A6629; -fx-border-insets: .2; -fx-border-width: .2");
    }

    public void showDetails() {
        getLedHolder().setVisible(false);
        getDetails().setVisible(true);
        getBoard().setStyle("-fx-background-color: #3A6629; -fx-border-color: #000000; -fx-border-insets: .2; -fx-border-width: .2");
    }

    public void updateDetails() {
        getChannelLabel().setText("Channel: " + (null == this.getChannel() ? "" : this.getChannel()));
        getAddressLabel().setText("ID: " + (null == this.getAddress() ? "" : this.getAddress()));
    }

    private AnchorPane getBoard() {
        if (null == board) {
            board = AnchorPaneBuilder.create()
                    .build();
            board.getChildren().addAll(getDetails());

        }

        return board;
    }

    private AnchorPane getLedHolder() {
        if (null == ledHolder) {
            ledHolder = AnchorPaneBuilder.create()
                    .prefWidth(getWidth())
                    .prefHeight(getHeight())
                    .build();

            double xPos = 1.8;
            double yPos = 1.8;
            int ledRowCount = (int) Math.sqrt(this.getNumberOfLeds());
            for (final Map.Entry<Integer, Led> entry : this.getLeds().entrySet()) {
                entry.getValue().setLayoutX(xPos);
                entry.getValue().setLayoutY(yPos);

                yPos += 3.5;
                if ((entry.getKey() + 1) % ledRowCount == 0) {
                    yPos = 1.8;
                    xPos += 3.5;
                }
                ledHolder.getChildren().add(entry.getValue());
            }
        }

        return ledHolder;
    }

    private BorderPane getDetails() {
        if (null == details) {

            details = BorderPaneBuilder.create()
                    .top(getTitleLabel())
                    .center(getSettings())
                    .padding(new Insets(2, 2, 0, 2))
                    .build();
        }
        return details;
    }

    private VBox getSettings() {
        if (null == settings) {
            settings = VBoxBuilder.create()
                    .children(getChannelLabel(), getAddressLabel())
                    .padding(new Insets(6, 0, 0, 0))
                    .build();
        }
        return settings;
    }

    private Label getTitleLabel() {
        if (null == titleLabel) {
            titleLabel = LabelBuilder.create()
                    .text("Kroma" + (int) Math.sqrt(getNumberOfLeds()))
                            //.alignment(Pos.CENTER)
                    .font(Font.font("System", FontWeight.BOLD, 5.5))
                    .build();
        }

        return titleLabel;
    }

    private Label getChannelLabel() {
        if (null == channelLabel) {
            channelLabel = LabelBuilder.create()
                    .text("Channel: " + (null == this.getChannel() ? "" : this.getChannel()))
                    .font(Font.font("System", 4.3))
                    .build();
        }

        return channelLabel;
    }

    private Label getAddressLabel() {
        if (null == addressLabel) {
            addressLabel = LabelBuilder.create()
                    .text("ID: " + (null == this.getAddress() ? "" : this.getAddress()))
                    .font(Font.font("System", 4.3))
                    .build();
        }

        return addressLabel;
    }

    @Override
    public Line getTopAnchorNotify() {
        if (null == topAnchorNotify) {
            topAnchorNotify = getAnchorNotify(1, getPrefWidth() - 2, -1, -1);
            topAnchorNotify.setLayoutX(1);
            topAnchorNotify.setLayoutY(1);
        }
        return topAnchorNotify;

    }

    @Override
    public Line getRightAnchorNotify() {
        if (null == rightAnchorNotify) {
            rightAnchorNotify = getAnchorNotify(getPrefWidth() - 2, getPrefWidth() - 2, 1, getPrefHeight() - 2);
            rightAnchorNotify.setLayoutX(getWidth() - 1);
            rightAnchorNotify.setLayoutY(1);
        }
        return rightAnchorNotify;
    }

    @Override
    public Line getBottomAnchorNotify() {
        if (null == bottomAnchorNotify) {
            bottomAnchorNotify = getAnchorNotify(1, getPrefWidth() - 2, getPrefHeight() - 2, getPrefHeight() - 2);
            bottomAnchorNotify.setLayoutX(1);
            bottomAnchorNotify.setLayoutY(getHeight() - 1);
        }
        return bottomAnchorNotify;
    }

    @Override
    public Line getLeftAnchorNotify() {
        if (null == leftAnchorNotify) {
            leftAnchorNotify = getAnchorNotify(1, 1, 1, getPrefHeight() - 2);
            leftAnchorNotify.setLayoutX(1);
            leftAnchorNotify.setLayoutY(1);
        }
        return leftAnchorNotify;
    }
}
