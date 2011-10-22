/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Nov 17, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.widgets.client.style;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorUtil {

    private static final Logger log = LoggerFactory.getLogger(ColorUtil.class);

    public static int hsbToRgb(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) java.lang.Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
            case 0:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (t * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 1:
                r = (int) (q * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 2:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (t * 255.0f + 0.5f);
                break;
            case 3:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (q * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 4:
                r = (int) (t * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 5:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (q * 255.0f + 0.5f);
                break;
            }
        }
        return (r << 16) | (g << 8) | (b << 0);
    }

    public static float[] rgbToHsb(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb >> 0) & 0xFF;
        float hue, saturation, brightness;
        float[] hsbvals = new float[3];
        int cmax = (r > g) ? r : g;
        if (b > cmax)
            cmax = b;
        int cmin = (r < g) ? r : g;
        if (b < cmin)
            cmin = b;

        brightness = (cmax) / 255.0f;
        if (cmax != 0)
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        else
            saturation = 0;
        if (saturation == 0)
            hue = 0;
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0f + redc - bluec;
            else
                hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0)
                hue = hue + 1.0f;
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }

    public static int hsbvToRgb(float hue, float saturation, float brightness, float vibrance) {
        float ns = saturation * vibrance;
        float nb = 1 - (1 - brightness) * vibrance;
        return hsbToRgb(hue, ns, nb);
    }

    public static int rgbToRgbv(int rgb, float vibrance) {
        float[] hsbvals = rgbToHsb(rgb);
        return hsbvToRgb(hsbvals[0], hsbvals[1], hsbvals[2], vibrance);
    }

    public static String rgbToHex(int rgb) {
        String colorString = Integer.toHexString(rgb);
        for (int i = 0; i < (6 - colorString.length()); i++) {
            colorString = "0" + colorString;
        }
        return "#" + colorString;
    }

    public static Integer parseToRgb(String color) {
        Integer rgb = null;
        if (color.startsWith("#")) {
            String hex = color.substring(1, color.length());
            if (hex.length() == 3) {
                String shortHex = hex;
                hex = "";
                for (int i = 0; i < 6; i++) {
                    hex += shortHex.charAt(i / 2);
                }
            }
            if (hex.length() == 6) {
                try {
                    rgb = Integer.parseInt(hex, 16);
                } catch (Exception e) {
                    return null;
                }

            }
        } else {
            rgb = ColorName.getHexRGB(color);
        }
        return rgb;
    }

}
