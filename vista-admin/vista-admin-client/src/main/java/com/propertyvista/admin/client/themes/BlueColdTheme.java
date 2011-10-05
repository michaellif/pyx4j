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
package com.propertyvista.admin.client.themes;

import com.pyx4j.widgets.client.style.ColorFactory;
import com.pyx4j.widgets.client.style.ThemePalette;

public class BlueColdTheme extends VistaAdminTheme {

    @Override
    protected void initThemeColors() {
        super.initThemeColors();

        float hue = (float) 218 / 360;
        float saturation = (float) 0.5;
        float brightness = (float) 0.5;

        putThemeColor(ThemePalette.OBJECT_TONE1, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.02));
        putThemeColor(ThemePalette.OBJECT_TONE2, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.12));
        putThemeColor(ThemePalette.OBJECT_TONE3, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.16));
        putThemeColor(ThemePalette.OBJECT_TONE4, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.20));
        putThemeColor(ThemePalette.OBJECT_TONE5, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.95));

        putThemeColor(ThemePalette.OBJECT_TONE10, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.02));
        putThemeColor(ThemePalette.OBJECT_TONE15, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.06));
        putThemeColor(ThemePalette.OBJECT_TONE20, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.12));
        putThemeColor(ThemePalette.OBJECT_TONE25, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.14));
        putThemeColor(ThemePalette.OBJECT_TONE30, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.16));
        putThemeColor(ThemePalette.OBJECT_TONE35, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.18));
        putThemeColor(ThemePalette.OBJECT_TONE40, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.20));
        putThemeColor(ThemePalette.OBJECT_TONE45, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.45));
        putThemeColor(ThemePalette.OBJECT_TONE50, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.50));
        putThemeColor(ThemePalette.OBJECT_TONE55, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.55));
        putThemeColor(ThemePalette.OBJECT_TONE60, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.60));
        putThemeColor(ThemePalette.OBJECT_TONE65, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.65));
        putThemeColor(ThemePalette.OBJECT_TONE70, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.70));
        putThemeColor(ThemePalette.OBJECT_TONE75, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.75));
        putThemeColor(ThemePalette.OBJECT_TONE80, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.80));
        putThemeColor(ThemePalette.OBJECT_TONE85, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.85));
        putThemeColor(ThemePalette.OBJECT_TONE90, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.90));
        putThemeColor(ThemePalette.OBJECT_TONE95, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.95));

        putThemeColor(ThemePalette.SELECTION, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.4));
    }
}
