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
import com.pyx4j.widgets.client.style.ThemePalette;

public class GainsboroTheme extends VistaTheme {

    @Override
    protected void initThemeColors() {
        float hue = (float) 0 / 360;
        float saturation = (float) 0.0;
        float brightness = (float) 0.25;
        putThemeColor(ThemePalette.OBJECT_TONE1, 0xF7F7F7);
        putThemeColor(ThemePalette.OBJECT_TONE2, 0xE1E1E1);
        putThemeColor(ThemePalette.OBJECT_TONE3, 0xC8C8C8);
        putThemeColor(ThemePalette.OBJECT_TONE4, 0xB5B5B5);
        putThemeColor(ThemePalette.OBJECT_TONE5, 0xE6E6E6);
        putThemeColor(ThemePalette.BORDER, 0xe7e7e7);
        putThemeColor(ThemePalette.SELECTION, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.4));
        putThemeColor(ThemePalette.SELECTION_TEXT, 0xffffff);
        putThemeColor(ThemePalette.TEXT, 0x333333);
        putThemeColor(ThemePalette.TEXT_BACKGROUND, 0xffffff);
        putThemeColor(ThemePalette.DISABLED_TEXT_BACKGROUND, 0xfafafa);
        putThemeColor(ThemePalette.MANDATORY_TEXT_BACKGROUND, 0xe5e5e5);
        putThemeColor(ThemePalette.READ_ONLY_TEXT_BACKGROUND, 0xeeeeee);
        putThemeColor(ThemePalette.SEPARATOR, 0xeeeeee);
    }

}
