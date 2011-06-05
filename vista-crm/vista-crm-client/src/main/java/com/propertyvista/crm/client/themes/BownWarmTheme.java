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
package com.propertyvista.crm.client.themes;

import com.pyx4j.widgets.client.style.ColorFactory;
import com.pyx4j.widgets.client.style.ThemeColor;

public class BownWarmTheme extends VistaCrmTheme {

    @Override
    protected void initThemeColors() {
        float hue = (float) 30 / 360;
        float saturation = (float) 0.5;
        float brightness = (float) 0.5;

        putThemeColor(ThemeColor.OBJECT_TONE1, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.02));
        putThemeColor(ThemeColor.OBJECT_TONE2, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.12));
        putThemeColor(ThemeColor.OBJECT_TONE3, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.16));
        putThemeColor(ThemeColor.OBJECT_TONE4, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.20));
        putThemeColor(ThemeColor.OBJECT_TONE5, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.95));

        putThemeColor(ThemeColor.OBJECT_TONE10, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.02));
        putThemeColor(ThemeColor.OBJECT_TONE15, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.06));
        putThemeColor(ThemeColor.OBJECT_TONE20, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.12));
        putThemeColor(ThemeColor.OBJECT_TONE25, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.14));
        putThemeColor(ThemeColor.OBJECT_TONE30, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.16));
        putThemeColor(ThemeColor.OBJECT_TONE35, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.18));
        putThemeColor(ThemeColor.OBJECT_TONE40, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.20));
        putThemeColor(ThemeColor.OBJECT_TONE45, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.45));
        putThemeColor(ThemeColor.OBJECT_TONE50, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.50));
        putThemeColor(ThemeColor.OBJECT_TONE55, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.55));
        putThemeColor(ThemeColor.OBJECT_TONE60, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.60));
        putThemeColor(ThemeColor.OBJECT_TONE65, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.65));
        putThemeColor(ThemeColor.OBJECT_TONE70, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.70));
        putThemeColor(ThemeColor.OBJECT_TONE75, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.75));
        putThemeColor(ThemeColor.OBJECT_TONE80, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.80));
        putThemeColor(ThemeColor.OBJECT_TONE85, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.85));
        putThemeColor(ThemeColor.OBJECT_TONE90, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.90));
        putThemeColor(ThemeColor.OBJECT_TONE95, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.95));

        putThemeColor(ThemeColor.BORDER, 0xf0f0f0);
        putThemeColor(ThemeColor.SELECTION, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.4));
        putThemeColor(ThemeColor.SELECTION_TEXT, 0xffffff);
        putThemeColor(ThemeColor.TEXT, 0x000000);
        putThemeColor(ThemeColor.TEXT_BACKGROUND, 0xffffff);
        putThemeColor(ThemeColor.DISABLED_TEXT_BACKGROUND, 0xfafafa);
        putThemeColor(ThemeColor.MANDATORY_TEXT_BACKGROUND, 0xe5e5e5);
        putThemeColor(ThemeColor.READ_ONLY_TEXT_BACKGROUND, 0xeeeeee);
        putThemeColor(ThemeColor.SEPARATOR, 0xeeeeee);
    }
}
