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

import com.kromalights.designer.kromadevices.KromaDevice;

public class KromaDevicePOJO {
    private String id;
    private KromaDevice kromaDevice;
    private String topNeighber;
    private String rightNeighber;
    private String bottomNeighber;
    private String leftNeighber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public KromaDevice getKromaDevice() {
        return kromaDevice;
    }

    public void setKromaDevice(KromaDevice kromaDevice) {
        this.kromaDevice = kromaDevice;
    }

    public String getTopNeighber() {
        return topNeighber;
    }

    public void setTopNeighber(String topNeighber) {
        this.topNeighber = topNeighber;
    }

    public String getRightNeighber() {
        return rightNeighber;
    }

    public void setRightNeighber(String rightNeighber) {
        this.rightNeighber = rightNeighber;
    }

    public String getBottomNeighber() {
        return bottomNeighber;
    }

    public void setBottomNeighber(String bottomNeighber) {
        this.bottomNeighber = bottomNeighber;
    }

    public String getLeftNeighber() {
        return leftNeighber;
    }

    public void setLeftNeighber(String leftNeighber) {
        this.leftNeighber = leftNeighber;
    }
}
