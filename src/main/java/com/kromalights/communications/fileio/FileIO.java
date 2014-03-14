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

package com.kromalights.communications.fileio;

import com.kromalights.designer.components.ColorPaletteButton;
import com.kromalights.designer.components.ColorPaletteButtons;
import com.kromalights.designer.components.Led;
import com.kromalights.designer.kromadevices.Kroma16;
import com.kromalights.designer.kromadevices.Kroma8;
import com.kromalights.designer.kromadevices.KromaDevice;
import com.kromalights.utils.Converters;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Dialogs;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooserBuilder;
import javafx.stage.Stage;

import javax.xml.stream.*;
import java.io.*;
import java.util.*;

public class FileIO {
    private static FileIO fileIO = new FileIO();
    private static final String APPLICATION_VERSION = "0.1";

    private FileIO() {
    }

    //Get instance
    public static FileIO getInstance() {
        return fileIO;
    }

    private File saveLocation = null;

    public boolean saveDesign(Stage stage, ObservableList<Node> kromaDevices, int frameCount) {
        return saveDesign(stage, kromaDevices, saveLocation, frameCount);
    }

    public boolean saveDesignAs(Stage stage, ObservableList<Node> kromaDevices, int frameCount) {
        return saveDesign(stage, kromaDevices, null, frameCount);
    }

    private boolean saveDesign(Stage stage, ObservableList<Node> kromaDevices, File file, int frameCount) {

        try {
            if (null == kromaDevices || kromaDevices.isEmpty()) {
                Dialogs.showWarningDialog(stage, "Nothing to save!");
                return false;
            }

            if (null == file) {
                FileChooser fileChooser = FileChooserBuilder.create()
                        .title("Save As...")
                        .build();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Kromalights Designer File", "*.kldesigner"));
                file = fileChooser.showSaveDialog(stage);
            }

            if (null == file) {
                return false;
            }

            saveLocation = file;

            PrintWriter writerXml = new PrintWriter(new OutputStreamWriter(new FileOutputStream(saveLocation)));

            XMLOutputFactory xof = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlsw = xof.createXMLStreamWriter(writerXml);

            xmlsw.writeStartDocument("UTF-8", "1.0");
            xmlsw.writeStartElement("design");
            writeElement(xmlsw, "version", APPLICATION_VERSION);
            writeElement(xmlsw, "frame-count", Integer.toString(frameCount));
            for(Map.Entry<Integer, ColorPaletteButton> colorPaletteButton : ColorPaletteButtons.getInstance().entrySet()) {
                writeElement(xmlsw, "color-palette" + colorPaletteButton.getKey(), colorPaletteButton.getValue().getColor().toString());
            }
            xmlsw.writeStartElement("kromadevices");

            for (Node node : kromaDevices) {
                if (!(node instanceof KromaDevice))
                    continue;
                KromaDevice kromaDevice = (KromaDevice) node;
                xmlsw.writeStartElement("kromadevice");

                writeElement(xmlsw, "id", String.valueOf(kromaDevice));
                writeElement(xmlsw, "type", kromaDevice.getClass().getSimpleName());
                writeElement(xmlsw, "channel", kromaDevice.getChannel() == null ? "" : Integer.toString(kromaDevice.getChannel())); //TODO: Not null safe
                writeElement(xmlsw, "address", kromaDevice.getAddress() == null ? "" : Integer.toString(kromaDevice.getAddress())); //TODO: Not null safe
                writeElement(xmlsw, "top-neighbor", String.valueOf(kromaDevice.getTopNeighbor()));
                writeElement(xmlsw, "right-neighbor", String.valueOf(kromaDevice.getRightNeighbor()));
                writeElement(xmlsw, "bottom-neighbor", String.valueOf(kromaDevice.getBottomNeighbor()));
                writeElement(xmlsw, "left-neighbor", String.valueOf(kromaDevice.getLeftNeighbor()));
                writeElement(xmlsw, "layout-x", Double.toString(kromaDevice.getLayoutX()));
                writeElement(xmlsw, "layout-y", Double.toString(kromaDevice.getLayoutY()));
                writeElement(xmlsw, "scale-x", Double.toString(kromaDevice.getScaleX()));
                writeElement(xmlsw, "scale-y", Double.toString(kromaDevice.getScaleY()));
                writeElement(xmlsw, "top-left-anchor-point", Arrays.toString(kromaDevice.getTopLeftAnchorPoint()));
                writeElement(xmlsw, "top-center-anchor-point", Arrays.toString(kromaDevice.getTopCenterAnchorPoint()));
                writeElement(xmlsw, "top-right-anchor-point", Arrays.toString(kromaDevice.getTopRightAnchorPoint()));
                writeElement(xmlsw, "right-top-anchor-point", Arrays.toString(kromaDevice.getRightTopAnchorPoint()));
                writeElement(xmlsw, "right-center-anchor-point", Arrays.toString(kromaDevice.getRightCenterAnchorPoint()));
                writeElement(xmlsw, "right-bottom-anchor-point", Arrays.toString(kromaDevice.getRightBottomAnchorPoint()));
                writeElement(xmlsw, "bottom-left-anchor-point", Arrays.toString(kromaDevice.getBottomLeftAnchorPoint()));
                writeElement(xmlsw, "bottom-center-anchor-point", Arrays.toString(kromaDevice.getBottomCenterAnchorPoint()));
                writeElement(xmlsw, "bottom-right-anchor-point", Arrays.toString(kromaDevice.getBottomRightAnchorPoint()));
                writeElement(xmlsw, "left-top-anchor-point", Arrays.toString(kromaDevice.getLeftTopAnchorPoint()));
                writeElement(xmlsw, "left-center-anchor-point", Arrays.toString(kromaDevice.getLeftCenterAnchorPoint()));
                writeElement(xmlsw, "left-bottom-anchor-point", Arrays.toString(kromaDevice.getLeftBottomAnchorPoint()));

                xmlsw.writeStartElement("frames");

                ListIterator iterator = kromaDevice.getLedFrames().listIterator();
                while (iterator.hasNext()) {
                    xmlsw.writeStartElement("frame" + iterator.nextIndex());
                    TreeMap<Integer, Led> map = (TreeMap<Integer, Led>) iterator.next();
                    for (Map.Entry<Integer, Led> ledArray : map.entrySet()) {
                        writeElement(xmlsw, "led" + ledArray.getKey(), ledArray.getValue().getLedColor().toString());
                    }
                    xmlsw.writeEndElement();
                }

                xmlsw.writeEndElement();

                xmlsw.writeEndElement();
            }
            // end <kromadevices> node
            xmlsw.writeEndElement();
            // end <application-settings> node
            xmlsw.writeEndElement();
            // end XML document
            xmlsw.writeEndDocument();
            xmlsw.flush();
            xmlsw.close();

            Dialogs.showInformationDialog(stage, "Your file was saved successfully", "Saved");

            //xmlStr = writerXml.getBuffer().toString();
            //writerXml.close();

        } catch (XMLStreamException | IOException e) {
            Dialogs.showErrorDialog(stage, "Error saving file", "Error", "Error", e);
            return false;
        }

        return true;
    }

    public List<KromaDevice> openDesign(Stage stage, ObservableList<Node> kromaDevices, int frameCount) {

        boolean canOpen = true;

        if (kromaDevices.size() > 0) {
            Dialogs.DialogResponse response = Dialogs.showConfirmDialog(stage, "Save?", "Would you like to save?", "Open");
            switch (response) {
                case YES:
                    canOpen = saveDesign(stage, kromaDevices, frameCount);
                    break;
                case NO:
                    canOpen = true;
                    break;
                case CANCEL:
                default:
                    return null;
            }
        }

        if(!canOpen) {
            return null;
        }

        Map<String, KromaDevicePOJO> kromaDeviceMap = new HashMap<>();
        List<KromaDevice> kromaDevicesOut = new ArrayList<>();

        FileChooser fileChooser = FileChooserBuilder.create()
                .title("Save As...")
                .build();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Kromalights Designer File", "*.kldesigner"));
        File file = fileChooser.showOpenDialog(stage);

        if(null == file) {
            return null;
        }

        saveLocation = file;

        final XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();

        try {
            final XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileReader(file));

            while (xmlStreamReader.hasNext()) {
                xmlStreamReader.next();
                if (xmlStreamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
                    switch (xmlStreamReader.getLocalName()) {
                        case "design":
                            kromaDeviceMap = parseDesignElement(xmlStreamReader);
                            break;
                    }
                }
            }
            for (Map.Entry<String, KromaDevicePOJO> kromaDevicePOJOEntry : kromaDeviceMap.entrySet()) {
                if (null != kromaDevicePOJOEntry.getValue().getTopNeighber()) {
                    kromaDevicePOJOEntry.getValue().getKromaDevice().setTopNeighbor(kromaDeviceMap.get(kromaDevicePOJOEntry.getValue().getTopNeighber()).getKromaDevice());
                }
                if (null != kromaDevicePOJOEntry.getValue().getRightNeighber()) {
                    kromaDevicePOJOEntry.getValue().getKromaDevice().setRightNeighbor(kromaDeviceMap.get(kromaDevicePOJOEntry.getValue().getRightNeighber()).getKromaDevice());
                }
                if (null != kromaDevicePOJOEntry.getValue().getBottomNeighber()) {
                    kromaDevicePOJOEntry.getValue().getKromaDevice().setBottomNeighbor(kromaDeviceMap.get(kromaDevicePOJOEntry.getValue().getBottomNeighber()).getKromaDevice());
                }
                if (null != kromaDevicePOJOEntry.getValue().getLeftNeighber()) {
                    kromaDevicePOJOEntry.getValue().getKromaDevice().setLeftNeighbor(kromaDeviceMap.get(kromaDevicePOJOEntry.getValue().getLeftNeighber()).getKromaDevice());
                }
                kromaDevicesOut.add(kromaDevicePOJOEntry.getValue().getKromaDevice());
            }
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }

        return kromaDevicesOut;
    }

    public boolean closeDesign(Stage stage, ObservableList<Node> kromaDevices, int frameCount) {
        boolean closeable = false;

        if (kromaDevices.size() > 0) {
            Dialogs.DialogResponse response = Dialogs.showConfirmDialog(stage, "Save?", "Would you like to save before you exit?", "Exit");
            switch (response) {
                case YES:
                    closeable = saveDesign(stage, kromaDevices, frameCount);
                    break;
                case NO:
                    closeable = true;
                    break;
                case CANCEL:
                default:
                    closeable = false;
            }
        }

        return closeable;
    }

    private static void writeElement(XMLStreamWriter xmlStreamWriter, String key, String value) {

        try {
            // add <type> node
            xmlStreamWriter.writeStartElement(key);
            xmlStreamWriter.writeCharacters(value);
            // end <type> node
            xmlStreamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }


    //Element parsers
    private Map<String, KromaDevicePOJO> parseDesignElement(XMLStreamReader xmlStreamReader) throws XMLStreamException {
        Map<String, KromaDevicePOJO> kromaDeviceMap = new HashMap<>();

        while (xmlStreamReader.hasNext()) {
            xmlStreamReader.next();

            if (xmlStreamReader.getEventType() == XMLStreamReader.END_ELEMENT) {
                switch (xmlStreamReader.getLocalName()) {
                    case "design":
                        return kromaDeviceMap;
                }
            } else if (xmlStreamReader.getEventType() == XMLStreamReader.START_ELEMENT) {

                switch (xmlStreamReader.getLocalName()) {
                    case "version":
                        if (!APPLICATION_VERSION.equals(getCharacters(xmlStreamReader))) {
                            throw new XMLStreamException("Incompatible file version");
                        }
                        break;
                    case "frame-count":
                        break;
                    case "kromadevice":
                        KromaDevicePOJO kromaDevicePOJO = parseKromaDevice(xmlStreamReader);
                        kromaDeviceMap.put(kromaDevicePOJO.getId(), kromaDevicePOJO);
                        break;
                    case "color-palette0":
                    case "color-palette1":
                    case "color-palette2":
                    case "color-palette3":
                    case "color-palette4":
                    case "color-palette5":
                    case "color-palette6":
                    case "color-palette7":
                    case "color-palette8":
                    case "color-palette9":
                        int colorPaletteButtonId = Integer.parseInt(xmlStreamReader.getLocalName().substring(13));
                        ColorPaletteButtons.getInstance().get(colorPaletteButtonId).setColor(Color.web(getCharacters(xmlStreamReader)));
                        break;
                }
            }

        }
        return kromaDeviceMap;
    }

    private KromaDevicePOJO parseKromaDevice(XMLStreamReader xmlStreamReader) throws XMLStreamException {
        KromaDevicePOJO kromaDevicePOJO = new KromaDevicePOJO();

        while (xmlStreamReader.hasNext()) {
            xmlStreamReader.next();

            if (xmlStreamReader.getEventType() == XMLStreamReader.END_ELEMENT) {
                switch (xmlStreamReader.getLocalName()) {
                    case "kromadevice":
                        return kromaDevicePOJO;
                }
            } else if (xmlStreamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
                switch (xmlStreamReader.getLocalName()) {
                    case "id":
                        kromaDevicePOJO.setId(getCharacters(xmlStreamReader));
                        break;
                    case "type":
                        switch (getCharacters(xmlStreamReader)) {
                            case "Kroma8":
                                kromaDevicePOJO.setKromaDevice(new Kroma8(Color.BLACK));
                                break;
                            case "Kroma16":
                                kromaDevicePOJO.setKromaDevice(new Kroma16(Color.BLACK));
                        }
                        break;
                    case "channel":
                        String channel = getCharacters(xmlStreamReader);
                        kromaDevicePOJO.getKromaDevice().setChannel(null == channel ? null : Integer.parseInt(channel));
                        break;
                    case "address":
                        String address = getCharacters(xmlStreamReader);
                        kromaDevicePOJO.getKromaDevice().setAddress(null == address ? null : Integer.parseInt(address));
                        break;
                    case "top-neighbor":
                        kromaDevicePOJO.setTopNeighber(getCharacters(xmlStreamReader));
                        break;
                    case "right-neighbor":
                        kromaDevicePOJO.setRightNeighber(getCharacters(xmlStreamReader));
                        break;
                    case "bottom-neighbor":
                        kromaDevicePOJO.setBottomNeighber(getCharacters(xmlStreamReader));
                        break;
                    case "left-neighbor":
                        kromaDevicePOJO.setLeftNeighber(getCharacters(xmlStreamReader));
                        break;
                    case "layout-x":
                        kromaDevicePOJO.getKromaDevice().setLayoutX(Double.parseDouble(getCharacters(xmlStreamReader)));
                        break;
                    case "layout-y":
                        kromaDevicePOJO.getKromaDevice().setLayoutY(Double.parseDouble(getCharacters(xmlStreamReader)));
                        break;
                    case "scale-x":
                        kromaDevicePOJO.getKromaDevice().setScaleX(Double.parseDouble(getCharacters(xmlStreamReader)));
                        break;
                    case "scale-y":
                        kromaDevicePOJO.getKromaDevice().setScaleY(Double.parseDouble(getCharacters(xmlStreamReader)));
                        break;
                    case "top-left-anchor-point":
                        kromaDevicePOJO.getKromaDevice().setTopLeftAnchorPoint(Converters.doubleArrayFromString(getCharacters(xmlStreamReader)));
                        break;
                    case "top-center-anchor-point":
                        kromaDevicePOJO.getKromaDevice().setTopCenterAnchorPoint(Converters.doubleArrayFromString(getCharacters(xmlStreamReader)));
                        break;
                    case "top-right-anchor-point":
                        kromaDevicePOJO.getKromaDevice().setTopRightAnchorPoint(Converters.doubleArrayFromString(getCharacters(xmlStreamReader)));
                        break;
                    case "right-top-anchor-point":
                        kromaDevicePOJO.getKromaDevice().setRightTopAnchorPoint(Converters.doubleArrayFromString(getCharacters(xmlStreamReader)));
                        break;
                    case "right-center-anchor-point":
                        kromaDevicePOJO.getKromaDevice().setRightCenterAnchorPoint(Converters.doubleArrayFromString(getCharacters(xmlStreamReader)));
                        break;
                    case "right-bottom-anchor-point":
                        kromaDevicePOJO.getKromaDevice().setRightBottomAnchorPoint(Converters.doubleArrayFromString(getCharacters(xmlStreamReader)));
                        break;
                    case "bottom-left-anchor-point":
                        kromaDevicePOJO.getKromaDevice().setBottomLeftAnchorPoint(Converters.doubleArrayFromString(getCharacters(xmlStreamReader)));
                        break;
                    case "bottom-center-anchor-point":
                        kromaDevicePOJO.getKromaDevice().setBottomCenterAnchorPoint(Converters.doubleArrayFromString(getCharacters(xmlStreamReader)));
                        break;
                    case "bottom-right-anchor-point":
                        kromaDevicePOJO.getKromaDevice().setBottomRightAnchorPoint(Converters.doubleArrayFromString(getCharacters(xmlStreamReader)));
                        break;
                    case "left-top-anchor-point":
                        kromaDevicePOJO.getKromaDevice().setLeftTopAnchorPoint(Converters.doubleArrayFromString(getCharacters(xmlStreamReader)));
                        break;
                    case "left-center-anchor-point":
                        kromaDevicePOJO.getKromaDevice().setLeftCenterAnchorPoint(Converters.doubleArrayFromString(getCharacters(xmlStreamReader)));
                        break;
                    case "left-bottom-anchor-point":
                        kromaDevicePOJO.getKromaDevice().setLeftBottomAnchorPoint(Converters.doubleArrayFromString(getCharacters(xmlStreamReader)));
                        break;
                    case "frames":
                        kromaDevicePOJO.getKromaDevice().saveFrames(parseDeviceFrames(xmlStreamReader));
                        break;
                }
            }

        }
        return kromaDevicePOJO;
    }


    private TreeMap<Integer, TreeMap<Integer, Led>> parseDeviceFrames(XMLStreamReader xmlStreamReader) throws XMLStreamException {
        TreeMap<Integer, TreeMap<Integer, Led>> ledFrames = new TreeMap<>();
        while (xmlStreamReader.hasNext()) {
            xmlStreamReader.next();
            if (xmlStreamReader.getEventType() == XMLStreamReader.END_ELEMENT) {
                switch (xmlStreamReader.getLocalName()) {
                    case "frames":
                        return ledFrames;
                }
            } else if (xmlStreamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
                if (xmlStreamReader.getLocalName().contains("frame")) {
                    ledFrames.put(Integer.parseInt(xmlStreamReader.getLocalName().substring(5)), parseDeviceFrame(xmlStreamReader));
                }
            }
        }
        return ledFrames;
    }

    private TreeMap<Integer, Led> parseDeviceFrame(XMLStreamReader xmlStreamReader) throws XMLStreamException {
        TreeMap<Integer, Led> leds = new TreeMap<>();
        while (xmlStreamReader.hasNext()) {
            xmlStreamReader.next();
            if (xmlStreamReader.getEventType() == XMLStreamReader.END_ELEMENT) {
                if (xmlStreamReader.getLocalName().contains("frame")) {
                    return leds;
                }
            } else if (xmlStreamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
                if (xmlStreamReader.getLocalName().contains("led")) {
                    leds.put(Integer.parseInt(xmlStreamReader.getLocalName().substring(3)), new Led(Integer.parseInt(xmlStreamReader.getLocalName().substring(3)), Color.web(getCharacters(xmlStreamReader))));
                }
            }
        }

        return leds;
    }

    private String getCharacters(XMLStreamReader xmlStreamReader) throws XMLStreamException {
        while (xmlStreamReader.hasNext()) {
            xmlStreamReader.next();
            if (xmlStreamReader.getEventType() == XMLStreamReader.CHARACTERS) {
                return "null".equals(xmlStreamReader.getText().trim()) ? null : xmlStreamReader.getText().trim();
            } else if (xmlStreamReader.getEventType() == XMLStreamReader.END_ELEMENT) {
                return null;
            }
        }
        return null;
    }
}
