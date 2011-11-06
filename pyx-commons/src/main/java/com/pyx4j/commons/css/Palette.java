/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Oct 5, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.commons.css;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Palette {

    private static final Logger log = LoggerFactory.getLogger(Palette.class);

    private final Map<ThemeColors, String> themeColors;

    public Palette() {
        themeColors = new HashMap<ThemeColors, String>();
    }

    public String getThemeColor(ThemeColors themeColor, double vibrance) {
        String color = themeColors.get(themeColor);
        Integer rgb = ColorUtil.parseToRgb(color);
        if (rgb == null) {
            rgb = ColorUtil.parseToRgb(themeColor.getDefaultColor());
            if (rgb == null) {
                rgb = 0;
            }
            log.warn("No such color defined : {}", themeColor.name());
        }
        return ColorUtil.rgbToHex(ColorUtil.rgbToRgbv(rgb, (float) vibrance));
    }

    // ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.08)

    public void putThemeColor(ThemeColors color, Integer rgb) {
        themeColors.put(color, ColorUtil.rgbToHex(rgb));
    }

    public void putThemeColor(ThemeColors color, String value) {
        themeColors.put(color, value);
    }
}
