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
package com.pyx4j.site.client.theme;

import com.pyx4j.widgets.client.style.ColorFactory;
import com.pyx4j.widgets.client.style.Palette;
import com.pyx4j.widgets.client.style.ThemeColors;

public class AppSitePalette extends Palette {

    public AppSitePalette() {
        float hue = (float) 213 / 360;
        float saturation = (float) 0.9;
        float brightness = (float) 0.7;
        putThemeColor(ThemeColors.OBJECT_TONE1, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.08));
        putThemeColor(ThemeColors.OBJECT_TONE2, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.12));
        putThemeColor(ThemeColors.OBJECT_TONE3, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.16));
        putThemeColor(ThemeColors.OBJECT_TONE4, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.2));
        putThemeColor(ThemeColors.OBJECT_TONE5, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.99));
        putThemeColor(ThemeColors.BORDER, 0xf0f0f0);
        putThemeColor(ThemeColors.SELECTION, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.4));
        putThemeColor(ThemeColors.SELECTION_TEXT, 0xffffff);
        putThemeColor(ThemeColors.TEXT, 0x000000);
        putThemeColor(ThemeColors.TEXT_BACKGROUND, 0xffffff);
        putThemeColor(ThemeColors.DISABLED_TEXT_BACKGROUND, 0xfafafa);
        putThemeColor(ThemeColors.MANDATORY_TEXT_BACKGROUND, 0xfcba84);
        putThemeColor(ThemeColors.READ_ONLY_TEXT_BACKGROUND, 0xeeeeee);
        putThemeColor(ThemeColors.SEPARATOR, 0xeeeeee);

    }
}
