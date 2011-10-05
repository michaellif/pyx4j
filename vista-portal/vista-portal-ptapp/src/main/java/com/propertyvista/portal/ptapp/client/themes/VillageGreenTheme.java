/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.themes;

import com.pyx4j.widgets.client.style.ColorFactory;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemePalette;

public class VillageGreenTheme extends VistaTheme {

    @Override
    protected void initThemeColors() {
        float hue = (float) 88 / 360;
        float saturation = (float) 0.9;
        float brightness = (float) 0.7;
        putThemeColor(ThemePalette.OBJECT_TONE1, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.08));
        putThemeColor(ThemePalette.OBJECT_TONE2, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.12));
        putThemeColor(ThemePalette.OBJECT_TONE3, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.16));
        putThemeColor(ThemePalette.OBJECT_TONE4, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.2));
        putThemeColor(ThemePalette.OBJECT_TONE5, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 1.0));
        putThemeColor(ThemePalette.BORDER, 0xf0f0f0);
        putThemeColor(ThemePalette.SELECTION, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.4));
        putThemeColor(ThemePalette.SELECTION_TEXT, 0xffffff);
        putThemeColor(ThemePalette.TEXT, 0x000000);
        putThemeColor(ThemePalette.TEXT_BACKGROUND, 0xffffff);
        putThemeColor(ThemePalette.DISABLED_TEXT_BACKGROUND, 0xfafafa);
        putThemeColor(ThemePalette.MANDATORY_TEXT_BACKGROUND, 0xe5e5e5);
        putThemeColor(ThemePalette.READ_ONLY_TEXT_BACKGROUND, 0xeeeeee);
        putThemeColor(ThemePalette.SEPARATOR, 0xeeeeee);
    }

    @Override
    protected void initBodyStyles() {
        super.initBodyStyles();
        Style style = new Style("body");
        style.addProperty("font", "80%/180% Comic Sans");
        addStyle(style);
    }

    @Override
    protected void initVistaApartmentViewStyles() {
        super.initVistaApartmentViewStyles();
    }
}
