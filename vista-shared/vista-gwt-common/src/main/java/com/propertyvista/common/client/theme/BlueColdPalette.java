/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-02
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.theme;

import com.pyx4j.widgets.client.style.ColorFactory;
import com.pyx4j.widgets.client.style.ThemeColors;

public class BlueColdPalette extends VistaPalette {

    public BlueColdPalette() {

        float hue = (float) 218 / 360;
        float saturation = (float) 0.5;
        float brightness = (float) 0.5;

        putThemeColor(ThemeColors.OBJECT_TONE1, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.02));
        putThemeColor(ThemeColors.OBJECT_TONE2, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.12));
        putThemeColor(ThemeColors.OBJECT_TONE3, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.16));
        putThemeColor(ThemeColors.OBJECT_TONE4, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.20));
        putThemeColor(ThemeColors.OBJECT_TONE5, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.95));

        putThemeColor(ThemeColors.OBJECT_TONE10, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.02));
        putThemeColor(ThemeColors.OBJECT_TONE15, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.06));
        putThemeColor(ThemeColors.OBJECT_TONE20, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.12));
        putThemeColor(ThemeColors.OBJECT_TONE25, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.14));
        putThemeColor(ThemeColors.OBJECT_TONE30, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.16));
        putThemeColor(ThemeColors.OBJECT_TONE35, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.18));
        putThemeColor(ThemeColors.OBJECT_TONE40, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.20));
        putThemeColor(ThemeColors.OBJECT_TONE45, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.45));
        putThemeColor(ThemeColors.OBJECT_TONE50, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.50));
        putThemeColor(ThemeColors.OBJECT_TONE55, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.55));
        putThemeColor(ThemeColors.OBJECT_TONE60, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.60));
        putThemeColor(ThemeColors.OBJECT_TONE65, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.65));
        putThemeColor(ThemeColors.OBJECT_TONE70, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.70));
        putThemeColor(ThemeColors.OBJECT_TONE75, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.75));
        putThemeColor(ThemeColors.OBJECT_TONE80, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.80));
        putThemeColor(ThemeColors.OBJECT_TONE85, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.85));
        putThemeColor(ThemeColors.OBJECT_TONE90, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.90));
        putThemeColor(ThemeColors.OBJECT_TONE95, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.95));

        putThemeColor(ThemeColors.SELECTION, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.4));
    }
}
