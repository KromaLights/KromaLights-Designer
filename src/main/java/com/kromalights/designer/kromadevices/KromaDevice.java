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

import com.kromalights.communications.serialusb.SerialMessenger;
import com.kromalights.designer.components.Delta;
import com.kromalights.designer.components.KromaligthsColorPicker;
import com.kromalights.designer.components.Led;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineBuilder;

import java.io.Serializable;
import java.util.*;

public abstract class KromaDevice extends AnchorPane implements Serializable {

    public enum AvailableAnchor{
        TOP, RIGHT, BOTTOM, LEFT, NONE
    }

    public static final double ANCHOR_DISTANCE = 20;
    protected Line topAnchorNotify, rightAnchorNotify, bottomAnchorNotify, leftAnchorNotify;
    private TreeMap<Integer, Led> leds;
    private int numberOfLeds;
    private Integer channel, address;
    private double[] topLeftAnchorPoint = new double[2];
    private double[] topCenterAnchorPoint = new double[2];
    private double[] topRightAnchorPoint = new double[2];
    private double[] rightTopAnchorPoint = new double[2];
    private double[] rightCenterAnchorPoint = new double[2];
    private double[] rightBottomAnchorPoint = new double[2];
    private double[] bottomLeftAnchorPoint = new double[2];
    private double[] bottomCenterAnchorPoint = new double[2];
    private double[] bottomRightAnchorPoint = new double[2];
    private double[] leftTopAnchorPoint = new double[2];
    private double[] leftCenterAnchorPoint = new double[2];
    private double[] leftBottomAnchorPoint = new double[2];
    private KromaDevice topNeighbor, rightNeighbor, bottomNeighbor, leftNeighbor;
    private Delta delta;
    private boolean anchoringAvailable = true;
    private LinkedList<TreeMap<Integer, Led>> ledFrames = new LinkedList<>();

    KromaDevice(int numberOfLeds, double width, double height, Paint paint) {
        leds = new TreeMap<>();
        this.numberOfLeds = numberOfLeds;
        setPrefWidth(width);
        setPrefHeight(height);
        setMaxSize(width, height);
        setMinSize(width, height);
        //setStyle("-fx-background-color: #FFFFFF;");


        for (int i = 0; i < numberOfLeds; i++) {
            final Led led;
            if(null == paint) {
                led = new Led(i);
            } else {
                led = new Led(i, paint);
            }

            leds.put(i, led);
        }
    }

    public TreeMap<Integer, Led> getLeds() {
        return leds;
    }

    public void turnOffLights() {
        ArrayList<Color> colors = new ArrayList<>();

        for (Map.Entry<Integer, Led> entry : this.getLeds().entrySet()) {
            colors.add(Color.BLACK);
            entry.getValue().turnOff();
        }


        if(null != channel) {
            SerialMessenger.getInstance().drawPixels(colors, channel, numberOfLeds);
        }
    }

    public void turnOnLights() {
        ArrayList<Color> colors = new ArrayList<>();

        for (Map.Entry<Integer, Led> entry : this.getLeds().entrySet()) {
            colors.add(KromaligthsColorPicker.getInstance().getValue());
            entry.getValue().turnOn();
        }

        if(null != channel) {
            SerialMessenger.getInstance().drawPixels(colors, channel, numberOfLeds);
        }
    }

    public void destroyMouseEvents() {
        for (Map.Entry<Integer, Led> entry : this.getLeds().entrySet()) {
            entry.getValue().setOnMouseClicked(null);
            entry.getValue().setOnMouseEntered(null);
        }
    }

    public int getNumberOfLeds() {
        return numberOfLeds;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public Led getLed(int led) {
        return leds.get(led);
    }

    public double[] getTopLeftAnchorPoint() {
        return topLeftAnchorPoint;
    }

    public double[] getTopCenterAnchorPoint() {
        return topCenterAnchorPoint;
    }

    public double[] getTopRightAnchorPoint() {
        return topRightAnchorPoint;
    }

    public double[] getRightTopAnchorPoint() {
        return rightTopAnchorPoint;
    }

    public double[] getRightCenterAnchorPoint() {
        return rightCenterAnchorPoint;
    }

    public double[] getRightBottomAnchorPoint() {
        return rightBottomAnchorPoint;
    }

    public double[] getBottomLeftAnchorPoint() {
        return bottomLeftAnchorPoint;
    }

    public double[] getBottomCenterAnchorPoint() {
        return bottomCenterAnchorPoint;
    }

    public double[] getBottomRightAnchorPoint() {
        return bottomRightAnchorPoint;
    }

    public double[] getLeftTopAnchorPoint() {
        return leftTopAnchorPoint;
    }

    public double[] getLeftCenterAnchorPoint() {
        return leftCenterAnchorPoint;
    }

    public double[] getLeftBottomAnchorPoint() {
        return leftBottomAnchorPoint;
    }

    public void setTopLeftAnchorPoint(double[] topLeftAnchorPoint) {
        this.topLeftAnchorPoint = topLeftAnchorPoint;
    }

    public void setTopCenterAnchorPoint(double[] topCenterAnchorPoint) {
        this.topCenterAnchorPoint = topCenterAnchorPoint;
    }

    public void setTopRightAnchorPoint(double[] topRightAnchorPoint) {
        this.topRightAnchorPoint = topRightAnchorPoint;
    }

    public void setRightTopAnchorPoint(double[] rightTopAnchorPoint) {
        this.rightTopAnchorPoint = rightTopAnchorPoint;
    }

    public void setRightCenterAnchorPoint(double[] rightCenterAnchorPoint) {
        this.rightCenterAnchorPoint = rightCenterAnchorPoint;
    }

    public void setRightBottomAnchorPoint(double[] rightBottomAnchorPoint) {
        this.rightBottomAnchorPoint = rightBottomAnchorPoint;
    }

    public void setBottomLeftAnchorPoint(double[] bottomLeftAnchorPoint) {
        this.bottomLeftAnchorPoint = bottomLeftAnchorPoint;
    }

    public void setBottomCenterAnchorPoint(double[] bottomCenterAnchorPoint) {
        this.bottomCenterAnchorPoint = bottomCenterAnchorPoint;
    }

    public void setBottomRightAnchorPoint(double[] bottomRightAnchorPoint) {
        this.bottomRightAnchorPoint = bottomRightAnchorPoint;
    }

    public void setLeftTopAnchorPoint(double[] leftTopAnchorPoint) {
        this.leftTopAnchorPoint = leftTopAnchorPoint;
    }

    public void setLeftCenterAnchorPoint(double[] leftCenterAnchorPoint) {
        this.leftCenterAnchorPoint = leftCenterAnchorPoint;
    }

    public void setLeftBottomAnchorPoint(double[] leftBottomAnchorPoint) {
        this.leftBottomAnchorPoint = leftBottomAnchorPoint;
    }

    public void setAnchors(double x, double y, double width, double height) {
        
        topLeftAnchorPoint[0] = x;
        topLeftAnchorPoint[1] = y;
        topCenterAnchorPoint[0] = x + width/2;
        topCenterAnchorPoint[1] = y;
        topRightAnchorPoint[0] = x + width;
        topRightAnchorPoint[1] = y;

        rightTopAnchorPoint[0] = x + width;
        rightTopAnchorPoint[1] = y;
        rightCenterAnchorPoint[0] = x + width;
        rightCenterAnchorPoint[1] = y + height/2;
        rightBottomAnchorPoint[0] = x + width;
        rightBottomAnchorPoint[1] = y + height;

        bottomLeftAnchorPoint[0] = x;
        bottomLeftAnchorPoint[1] = y + height;
        bottomCenterAnchorPoint[0] = x + width/2;
        bottomCenterAnchorPoint[1] = y + height;
        bottomRightAnchorPoint[0] = x + width;
        bottomRightAnchorPoint[1] = y + height;
        
        leftTopAnchorPoint[0] = x;
        leftTopAnchorPoint[1] = y;
        leftCenterAnchorPoint[0] = x;
        leftCenterAnchorPoint[1] = y + height/2;
        leftBottomAnchorPoint[0] = x;
        leftBottomAnchorPoint[1] = y +height;
    }

    public AvailableAnchor availableAnchor() {

        if (topAnchorNotify.isVisible()) {
            return AvailableAnchor.TOP;
        }
        if (rightAnchorNotify.isVisible()) {
            return AvailableAnchor.RIGHT;
        }
        if (bottomAnchorNotify.isVisible()) {
            return AvailableAnchor.BOTTOM;
        }
        if (leftAnchorNotify.isVisible()) {
            return AvailableAnchor.LEFT;
        }

        return AvailableAnchor.NONE;
    }

    public double[] getAnchors() {
        double[] allAnchorPoints = new double[8];

        allAnchorPoints[0] = topCenterAnchorPoint[0]; // X
        allAnchorPoints[1] = topCenterAnchorPoint[1]; // Y
        allAnchorPoints[2] = rightCenterAnchorPoint[0]; // X
        allAnchorPoints[3] = rightCenterAnchorPoint[1]; // Y
        allAnchorPoints[4] = bottomCenterAnchorPoint[0]; // X
        allAnchorPoints[5] = bottomCenterAnchorPoint[1]; // Y
        allAnchorPoints[6] = leftCenterAnchorPoint[0]; // X
        allAnchorPoints[7] = leftCenterAnchorPoint[1]; // Y

        return allAnchorPoints;
    }

    public void checkAnchors(double[] anchors) {

        if(!anchoringAvailable) {
            return;
        }
        if (Math.abs(anchors[4] - topCenterAnchorPoint[0]) <= ANCHOR_DISTANCE && Math.abs(anchors[5] - topCenterAnchorPoint[1]) <= ANCHOR_DISTANCE) {
            getTopAnchorNotify().setVisible(true);
            getRightAnchorNotify().setVisible(false);
            getBottomAnchorNotify().setVisible(false);
            getLeftAnchorNotify().setVisible(false);
        } else if (Math.abs(anchors[6] - rightCenterAnchorPoint[0]) <= ANCHOR_DISTANCE && Math.abs(anchors[7] - rightCenterAnchorPoint[1]) <= ANCHOR_DISTANCE) {
            getTopAnchorNotify().setVisible(false);
            getRightAnchorNotify().setVisible(true);
            getBottomAnchorNotify().setVisible(false);
            getLeftAnchorNotify().setVisible(false);
        } else if (Math.abs(anchors[0] - bottomCenterAnchorPoint[0]) <= ANCHOR_DISTANCE && Math.abs(anchors[1] - bottomCenterAnchorPoint[1]) <= ANCHOR_DISTANCE) {
            getTopAnchorNotify().setVisible(false);
            getRightAnchorNotify().setVisible(false);
            getBottomAnchorNotify().setVisible(true);
            getLeftAnchorNotify().setVisible(false);
        } else if (Math.abs(anchors[2] - leftCenterAnchorPoint[0]) <= ANCHOR_DISTANCE && Math.abs(anchors[3] - leftCenterAnchorPoint[1]) <= ANCHOR_DISTANCE) {
            getTopAnchorNotify().setVisible(false);
            getRightAnchorNotify().setVisible(false);
            getBottomAnchorNotify().setVisible(false);
            getLeftAnchorNotify().setVisible(true);
        } else {
            getTopAnchorNotify().setVisible(false);
            getRightAnchorNotify().setVisible(false);
            getBottomAnchorNotify().setVisible(false);
            getLeftAnchorNotify().setVisible(false);
        }
    }

    public boolean hasNeighbors() {
        return null != topNeighbor || null != rightNeighbor || null != bottomNeighbor || null != leftNeighbor;

    }

    protected Line getAnchorNotify(double startX, double endX, double startY, double endY) {
        return LineBuilder.create()
                .stroke(Color.YELLOW)
                .effect(new BoxBlur())
                .startX(startX)
                .endX(endX)
                .startY(startY)
                .endY(endY)
                .visible(false)
                .build();
    }

    public abstract Line getTopAnchorNotify();

    public abstract Line getRightAnchorNotify();

    public abstract Line getBottomAnchorNotify();

    public abstract Line getLeftAnchorNotify();

    public KromaDevice getTopNeighbor() {
        return topNeighbor;
    }

    public void setTopNeighbor(KromaDevice topNeighbor) {
        this.topNeighbor = topNeighbor;
    }

    public KromaDevice getRightNeighbor() {
        return rightNeighbor;
    }

    public void setRightNeighbor(KromaDevice rightNeighbor) {
        this.rightNeighbor = rightNeighbor;
    }

    public KromaDevice getBottomNeighbor() {
        return bottomNeighbor;
    }

    public void setBottomNeighbor(KromaDevice bottomNeighbor) {
        this.bottomNeighbor = bottomNeighbor;
    }

    public KromaDevice getLeftNeighbor() {
        return leftNeighbor;
    }

    public void setLeftNeighbor(KromaDevice leftNeighbor) {
        this.leftNeighbor = leftNeighbor;
    }

    public void clearAnchoNotify() {
        getTopAnchorNotify().setVisible(false);
        getRightAnchorNotify().setVisible(false);
        getBottomAnchorNotify().setVisible(false);
        getLeftAnchorNotify().setVisible(false);
    }

    public void setDelta(Delta delta) {
        this.delta = new Delta(getLayoutX() - delta.getX(), getLayoutY() - delta.getY());
        List<KromaDevice> kromaDevices = new ArrayList<>();
        kromaDevices.add(this);

            if (null != topNeighbor) {
                topNeighbor.setDelta(delta, kromaDevices);
            }
            if (null != rightNeighbor) {
                rightNeighbor.setDelta(delta, kromaDevices);
            }
            if (null != bottomNeighbor) {
                bottomNeighbor.setDelta(delta, kromaDevices);
            }
            if (null != leftNeighbor) {
                leftNeighbor.setDelta(delta, kromaDevices);
            }
    }

    private void setDelta(Delta delta, List<KromaDevice> kromaDevices) {

        if(!kromaDevices.contains(this)){
            kromaDevices.add(this);
            this.delta = new Delta(getLayoutX() - delta.getX(), getLayoutY() - delta.getY());
            if (null != topNeighbor) {
                topNeighbor.setDelta(delta, kromaDevices);
            }
            if (null != rightNeighbor) {
                rightNeighbor.setDelta(delta, kromaDevices);
            }
            if (null != bottomNeighbor) {
                bottomNeighbor.setDelta(delta, kromaDevices);
            }
            if (null != leftNeighbor) {
                leftNeighbor.setDelta(delta, kromaDevices);
            }
        }

    }

    public void drag(double mouseX, double mouseY) {
        List<KromaDevice> kromaDevices = new ArrayList<>();
        kromaDevices.add(this);

        setLayoutX(mouseX + delta.getX());
        setLayoutY(mouseY + delta.getY());
        if (null != topNeighbor) {
            topNeighbor.drag(mouseX, mouseY, kromaDevices);
        }
        if (null != rightNeighbor) {
            rightNeighbor.drag(mouseX, mouseY, kromaDevices);
        }
        if (null != bottomNeighbor) {
            bottomNeighbor.drag(mouseX, mouseY, kromaDevices);
        }
        if (null != leftNeighbor) {
            leftNeighbor.drag(mouseX, mouseY, kromaDevices);
        }
    }

    private void drag(double mouseX, double mouseY, List<KromaDevice> kromaDevices) {

        if(!kromaDevices.contains(this)){
            anchoringAvailable = false;
            kromaDevices.add(this);
            setLayoutX(mouseX + delta.getX());
            setLayoutY(mouseY + delta.getY());
            if (null != topNeighbor) {
                topNeighbor.drag(mouseX, mouseY, kromaDevices);
            }
            if (null != rightNeighbor) {
                rightNeighbor.drag(mouseX, mouseY, kromaDevices);
            }
            if (null != bottomNeighbor) {
                bottomNeighbor.drag(mouseX, mouseY, kromaDevices);
            }
            if (null != leftNeighbor) {
                leftNeighbor.drag(mouseX, mouseY, kromaDevices);
            }
        }
    }

    public void setAnchoringAvailable(boolean anchoringAvailable) {
        this.anchoringAvailable = anchoringAvailable;
    }

    private TreeMap<Integer, Led> getBlankLeds() {

        TreeMap<Integer, Led> leds = new TreeMap<>();

        for (int i = 0; i < numberOfLeds; i++) {
            final Led led = new Led(i, Color.BLACK);

            leds.put(i, led);
        }
        return leds;
    }

    public void saveFrame(int frame) {
        for(int i = ledFrames.size(); i < frame + 1; i++){
            ledFrames.addLast(getBlankLeds());
        }

        TreeMap<Integer, Led> savedLeds = new TreeMap<>();

        for(int i = 0; i < numberOfLeds; i++){
            Led led = new Led(i, getLed(i).getLedColor());
            savedLeds.put(i, led);
        }

        if(ledFrames.size() > frame) {
            ledFrames.set(frame, savedLeds);
        } else {
            ledFrames.addLast(savedLeds);
        }
    }

    public void saveFrames(TreeMap<Integer, TreeMap<Integer, Led>> frames) {
        for(Map.Entry<Integer, TreeMap<Integer, Led>> frame : frames.entrySet()) {
            ledFrames.add(frame.getKey(), frame.getValue());
        }
    }

    public void insertFrame(int frame) {
        saveFrame(frame);

        TreeMap<Integer, Led> savedLeds = new TreeMap<>();

        for(int i = 0; i < numberOfLeds; i++){
            Led led = new Led(i, Color.BLACK);
            savedLeds.put(i, led);
        }

        ledFrames.add(frame, savedLeds);
    }

    public void loadFrame(int frame) {

        ArrayList<Color> colors = new ArrayList<>();

        if(ledFrames.size() >= frame + 1 ) {
            for(int i = 0; i < numberOfLeds; i++){
                Color color = ledFrames.get(frame).get(i).getLedColor();
                leds.get(i).setLedColor(color);
                colors.add(color);
            }
        } else {
            turnOffLights();
            saveFrame(frame);
        }

        if(null != channel) {
            SerialMessenger.getInstance().drawPixels(colors, channel, numberOfLeds);
        }
    }

    public void deleteFrame(int frame) {

        for(int i = ledFrames.size(); i < frame + 1; i++){
            ledFrames.addLast(getBlankLeds());
        }

        ledFrames.remove(frame);
        if(frame >= ledFrames.size()) {
            frame = ledFrames.size() - 1;
        }

        for(int i = 0; i < numberOfLeds; i++){
            leds.get(i).setLedColor(ledFrames.get(frame).get(i).getLedColor());
        }
    }

    public LinkedList<TreeMap<Integer, Led>> getLedFrames() {
        return ledFrames;
    }
}
