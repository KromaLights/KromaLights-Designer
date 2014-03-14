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

package com.kromalights.utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;


public class SimpleConvertImage {

    int[][] pixels = new int[256][4];

    public static void main(String[] args) throws IOException{

        SimpleConvertImage sci = new SimpleConvertImage();
        sci.handlepixels(0,0,16,16);
    }

    public SimpleConvertImage() {
        try {
            handlepixels(0,0,16,16);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void handlesinglepixel(int x, int y, int pixel, int count) {

        int alpha = (pixel >> 24) & 0xff;
        int red   = (pixel >> 16) & 0xff;
        int green = (pixel >>  8) & 0xff;
        int blue  = (pixel      ) & 0xff;

        pixels[count][0] = alpha;
        pixels[count][1] = red;
        pixels[count][2] = green;
        pixels[count][3] = blue;
    }


    public void handlepixels(int x, int y, int w, int h) throws IOException {
        InputStream is = getClass().getResourceAsStream("/com/kromalights/designer/images/Penguins.jpg");
        BufferedImage bufferedImage = ImageIO.read(is);
        BufferedImage bufferedImage2 = scale(bufferedImage, 1, 16, 16, .04, .04);
        int[] pixels = new int[w * h];
        PixelGrabber pg = new PixelGrabber(bufferedImage2, x, y, w, h, pixels, 0, w);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("interrupted waiting for pixels!");
            return;
        }
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
            System.err.println("image fetch aborted or errored");
            return;
        }

        int counter = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                handlesinglepixel(x+i, y+j, pixels[j * w + i], counter++);
            }
        }
    }

    public int[][] getImagePixels() {

        for(int[] stuff : pixels){
            //
        }
        return pixels;
    }

    /**
     * scale image
     *
     * @param sbi image to scale
     * @param imageType type of image
     * @param dWidth width of destination image
     * @param dHeight height of destination image
     * @param fWidth x-factor for transformation / scaling
     * @param fHeight y-factor for transformation / scaling
     * @return scaled image
     */
    public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
        BufferedImage dbi = null;
        if(sbi != null) {
            dbi = new BufferedImage(dWidth, dHeight, imageType);
            Graphics2D g = dbi.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
            g.drawRenderedImage(sbi, at);
        }
        return dbi;
    }
}
