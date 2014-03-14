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

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import com.kromalights.communications.serialusb.SerialMessenger;
import com.kromalights.designer.kromadevices.KromaDevice;

public class Led extends Circle {
    private boolean clickEnabled = true;
    private boolean ledOn = true;

    public Led(int ledAddress) {
        this(ledAddress, getRandomColor());
    }

    public Led(final int ledAddress, final Paint paint) {

        if (!paint.equals(Color.BLACK)) {
            setFill(paint);

            //setEffect(getBloom());
        } else {
            turnOff();
        }

        setRadius(1.5);

        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    if (isClickEnabled()) {
                        if (ledOn) {
                            turnOff();

                            KromaDevice kromaDevice = (KromaDevice) getParent().getParent();
                            ArrayList<Color> colors = new ArrayList<>();

                            for(Map.Entry<Integer, Led> led : kromaDevice.getLeds().entrySet()) {
                                colors.add(led.getValue().getLedColor());
                            }

                            if(null != kromaDevice.getChannel()) {
                                SerialMessenger.getInstance().drawPixels(colors, kromaDevice.getChannel(), ledAddress + 1);
                                SerialMessenger.getInstance().sendFA();
                            }
                        } else {
                            turnOn();

                            KromaDevice kromaDevice = (KromaDevice) getParent().getParent();
                            ArrayList<Color> colors = new ArrayList<>();

                            for(Map.Entry<Integer, Led> led : kromaDevice.getLeds().entrySet()) {
                                colors.add(led.getValue().getLedColor());
                            }

                            if(null != kromaDevice.getChannel()) {
                                SerialMessenger.getInstance().drawPixels(colors, kromaDevice.getChannel(), ledAddress + 1);
                                SerialMessenger.getInstance().sendFA();
                            }
                        }
                    } else {
                        setClickEnabled(true);
                    }
                }
            }
        });

        setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setClickEnabled(false);
            }
        });
        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isShiftDown()) {
                    turnOn();
                    
                	KromaDevice kromaDevice = (KromaDevice) getParent().getParent();
                	ArrayList<Color> colors = new ArrayList<>();

                    for(Map.Entry<Integer, Led> led : kromaDevice.getLeds().entrySet()) {
                        colors.add(led.getValue().getLedColor());
                    }

                    if(null != kromaDevice.getChannel()) {
                        SerialMessenger.getInstance().drawPixels(colors, kromaDevice.getChannel(), ledAddress + 1);
                        SerialMessenger.getInstance().sendFA();
                    }
                    
                } else if (event.isAltDown()) {
                    turnOff();
                    
                	KromaDevice kromaDevice = (KromaDevice) getParent().getParent();
                	ArrayList<Color> colors = new ArrayList<>();

                    for(Map.Entry<Integer, Led> led : kromaDevice.getLeds().entrySet()) {
                        colors.add(led.getValue().getLedColor());
                    }

                    if(null != kromaDevice.getChannel()) {
                        SerialMessenger.getInstance().drawPixels(colors, kromaDevice.getChannel(), ledAddress + 1);
                        SerialMessenger.getInstance().sendFA();
                    }
                }
            }
        });
    }

    private static Color getRandomColor() {
        Random r = new Random();

        return Color.color(r.nextDouble(), r.nextDouble(), r.nextDouble());
    }

    public void turnOn() {
        setFill(KromaligthsColorPicker.getInstance().getValue());
        //setEffect(bloom);
        ledOn = true;
    }

    public void turnOff() {
        setFill(Color.BLACK);
        setEffect(null);
        ledOn = false;
    }

    public boolean isClickEnabled() {
        return clickEnabled;
    }

    public void setClickEnabled(boolean clickEnabled) {
        this.clickEnabled = clickEnabled;
    }

    public Color getLedColor() {
        return (Color) getFill();
    }

    public void setLedColor(int red, int green, int blue) {
        setFill(Color.rgb(red, green, blue));
    }

    public void setLedColor(Paint paint) {
        setFill(paint);
    }
}
