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
 * Created on Oct 6, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.tester.client.theme;

import com.pyx4j.commons.css.ColorUtil;
import com.pyx4j.commons.css.Palette;
import com.pyx4j.commons.css.ThemeColors;

public class TesterPalette extends Palette {

    public TesterPalette() {
        float hue = (float) 218 / 360;
        float saturation = (float) 0.5;
        float brightness = (float) 0.5;

        putThemeColor(ThemeColors.BORDER, 0xe7e7e7);
        putThemeColor(ThemeColors.SELECTION, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.4));
        putThemeColor(ThemeColors.SELECTION_TEXT, 0xffffff);
        putThemeColor(ThemeColors.TEXT, 0x333333);
        putThemeColor(ThemeColors.TEXT_BACKGROUND, 0xffffff);
        putThemeColor(ThemeColors.DISABLED_TEXT_BACKGROUND, 0xfafafa);
        putThemeColor(ThemeColors.MANDATORY_TEXT_BACKGROUND, 0xe5e5e5);
        putThemeColor(ThemeColors.READ_ONLY_TEXT_BACKGROUND, 0xeeeeee);
        putThemeColor(ThemeColors.SEPARATOR, 0xeeeeee);

        putThemeColor(ThemeColors.OBJECT_TONE1, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.02));
        putThemeColor(ThemeColors.OBJECT_TONE2, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.12));
        putThemeColor(ThemeColors.OBJECT_TONE3, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.16));
        putThemeColor(ThemeColors.OBJECT_TONE4, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.20));
        putThemeColor(ThemeColors.OBJECT_TONE5, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.95));

        putThemeColor(ThemeColors.OBJECT_TONE10, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.02));
        putThemeColor(ThemeColors.OBJECT_TONE15, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.06));
        putThemeColor(ThemeColors.OBJECT_TONE20, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.12));
        putThemeColor(ThemeColors.OBJECT_TONE25, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.14));
        putThemeColor(ThemeColors.OBJECT_TONE30, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.16));
        putThemeColor(ThemeColors.OBJECT_TONE35, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.18));
        putThemeColor(ThemeColors.OBJECT_TONE40, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.20));
        putThemeColor(ThemeColors.OBJECT_TONE45, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.45));
        putThemeColor(ThemeColors.OBJECT_TONE50, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.50));
        putThemeColor(ThemeColors.OBJECT_TONE55, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.55));
        putThemeColor(ThemeColors.OBJECT_TONE60, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.60));
        putThemeColor(ThemeColors.OBJECT_TONE65, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.65));
        putThemeColor(ThemeColors.OBJECT_TONE70, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.70));
        putThemeColor(ThemeColors.OBJECT_TONE75, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.75));
        putThemeColor(ThemeColors.OBJECT_TONE80, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.80));
        putThemeColor(ThemeColors.OBJECT_TONE85, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.85));
        putThemeColor(ThemeColors.OBJECT_TONE90, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.90));
        putThemeColor(ThemeColors.OBJECT_TONE95, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.95));

        putThemeColor(ThemeColors.SELECTION, ColorUtil.hsbvToRgb(hue, saturation, brightness, (float) 0.4));
    }
}
