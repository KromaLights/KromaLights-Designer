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

import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;

public class ColorPaletteButton extends ToggleButton {

    private int buttonId;
    private Rectangle colorOverlay;

    public ColorPaletteButton(int buttonId, Color color) {
        this.buttonId = buttonId;

        colorOverlay = RectangleBuilder
                .create()
                .width(18)
                .height(18)
                .arcHeight(8)
                .arcWidth(8)
                .fill(color)
                .stroke(color.invert())
                .strokeWidth(.3)
                .build();

        this.setStyle("-fx-padding: 2px;");
        this.setText(String.valueOf(buttonId));

        this.setId(String.valueOf(buttonId));

        this.setGraphic(colorOverlay);

    }

    public Color getColor() {
        return (Color) colorOverlay.getFill();
    }

    public void setColor(Color color) {
        colorOverlay.setFill(color);
        colorOverlay.setStroke(color.invert());
    }

    public int getButtonId() {
        return buttonId;
    }
}
