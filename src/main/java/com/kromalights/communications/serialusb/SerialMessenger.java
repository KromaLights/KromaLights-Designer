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

package com.kromalights.communications.serialusb;


import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import jssc.SerialPort;
import jssc.SerialPortException;

public class SerialMessenger {
    private static SerialMessenger kromaligthsColorPicker = new SerialMessenger();
    private String serialPortName;
    private Integer baudRate = SerialPort.BAUDRATE_9600;
    private Integer dataBits = SerialPort.DATABITS_8;
    private Integer stopBits = SerialPort.STOPBITS_1;
    private Integer parity = SerialPort.PARITY_NONE;
    private final int BRIGHTNESS_FACTOR = 32; //0-256


    List<Integer> fa = new ArrayList<>();

    private SerialMessenger() {
        fa.add(250);
        fa.add(0);
        fa.add(0);
    }

    /* Static 'instance' method */
    public static SerialMessenger getInstance() {
        return kromaligthsColorPicker;
    }

    public String sendInt(List<Integer> bytesToSend) {
        if (null == serialPortName) return "Serial port not selected";
        
        int[] intsToSend = new int[bytesToSend.size()];

        try {
            SerialPort serialPort = new SerialPort(serialPortName);
            if (!serialPort.isOpened()) {
                serialPort.openPort();
            }
            serialPort.setParams(baudRate, dataBits, stopBits, parity);
            
            for(int i = 0; i < bytesToSend.size(); i++) {
            	intsToSend[i] = bytesToSend.get(i);
            }
            
            serialPort.writeIntArray(intsToSend);
            serialPort.closePort();
        } catch (SerialPortException ex) {
            return "Serial send failed: " + ex;
        }

        return "Send Success!";
    }

    public String getSerialPortName() {
        return serialPortName;
    }

    public void setSerialPortName(String serialPortName) {
        this.serialPortName = serialPortName;
    }

    public Integer getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(Integer baudRate) {
        this.baudRate = baudRate;
    }

    public Integer getDataBits() {
        return dataBits;
    }

    public void setDataBits(Integer dataBits) {
        this.dataBits = dataBits;
    }

    public Integer getStopBits() {
        return stopBits;
    }

    public void setStopBits(Integer stopBits) {
        this.stopBits = stopBits;
    }

    public Integer getParity() {
        return parity;
    }

    public void setParity(Integer parity) {
        this.parity = parity;
    }

    public synchronized void drawPixels(ArrayList<Color> colors, Integer channel, Integer ledAddress){

        if(null == channel || null == ledAddress) {
            return;
        }

        List<Integer> bytesToSend = new ArrayList<>();

        //Channel Select
        bytesToSend.add(4);
        bytesToSend.add(0);
        bytesToSend.add(channel);

        //Start address in channel
        bytesToSend.add(6);
        bytesToSend.add(0);
        bytesToSend.add(0);

        for(int i = 0; i < ledAddress; i ++) {

            Color color = colors.get(i);
            int green = (int) (color.getGreen()*BRIGHTNESS_FACTOR);
            int red = (int) (color.getRed()*BRIGHTNESS_FACTOR);
            int blue = (int) (color.getBlue()*BRIGHTNESS_FACTOR);

            //Data, Alpha + Green
            bytesToSend.add(0x08);
            bytesToSend.add(0x00);
            bytesToSend.add(green);

            //Data, Red + Blue
            bytesToSend.add(0x08);
            bytesToSend.add(red);
            bytesToSend.add(blue);
        }

        //Pixels to write count
        bytesToSend.add(0x0A + channel*4);
        bytesToSend.add(ledAddress/256);
        bytesToSend.add(ledAddress%256);

        //Address in memory for channel incrementx2 0C = Channel 0, 0E = Channel 1...
        bytesToSend.add(0x0C + channel*4);
        bytesToSend.add(0x00);
        bytesToSend.add(0x00);

        //Go frame
//        bytesToSend.add(0xFA);
//        bytesToSend.add(0x00);
//        bytesToSend.add(0x00);

        int[] newInt = new int[bytesToSend.size()];

        for(int i = 0; i < bytesToSend.size(); i ++) {
            newInt[i] = bytesToSend.get(i);
        }

        String result = SerialMessenger.getInstance().sendInt(bytesToSend);
        //TODO: Handle our results. Maybe use a try catch.
    }

    public synchronized void sendFA() {
        String result = SerialMessenger.getInstance().sendInt(fa);
    }
}
