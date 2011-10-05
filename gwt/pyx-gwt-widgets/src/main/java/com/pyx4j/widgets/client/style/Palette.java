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
package com.pyx4j.widgets.client.style;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Palette {

    private static final Logger log = LoggerFactory.getLogger(Palette.class);

    private final Map<ThemeColors, Integer> themeColors;

    public Palette() {
        themeColors = new HashMap<ThemeColors, Integer>();
    }

    public Integer getThemeColor(ThemeColors color) {
        if (themeColors.get(color) == null) {
            log.warn("Theme color {} is not set", color.name());
            return 0xffffff;
        }
        return themeColors.get(color);
    }

    public String getThemeColorString(ThemeColors color) {
        String colorString = Integer.toHexString(getThemeColor(color));
        int appendZeros = 6 - colorString.length();
        for (int i = 0; i < appendZeros; i++) {
            colorString = "0" + colorString;
        }

        return "#" + colorString;
    }

    public void putThemeColor(ThemeColors color, Integer value) {
        themeColors.put(color, value);
    }
}
