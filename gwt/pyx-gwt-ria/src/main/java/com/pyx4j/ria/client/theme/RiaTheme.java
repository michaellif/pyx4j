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
 * Created on Jan 18, 2010
 * @author michaellif
 * @version $Id: SiteTheme.java 7522 2010-11-17 11:36:20Z michaellif $
 */
package com.pyx4j.ria.client.theme;

import com.pyx4j.ria.client.HeaderPanel;
import com.pyx4j.ria.client.SectionPanel;
import com.pyx4j.widgets.client.style.ColorFactory;
import com.pyx4j.widgets.client.style.Selector;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColor;
import com.pyx4j.widgets.client.style.theme.WindowsTheme;

public abstract class RiaTheme extends WindowsTheme {

    private final float hue;

    private final float saturation;

    private final float brightness;

    protected RiaTheme(float hue, float saturation, float brightness) {
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
        initThemeColors();
    }

    @Override
    protected void initThemeColors() {
        putThemeColor(ThemeColor.OBJECT_TONE1, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.04));
        putThemeColor(ThemeColor.OBJECT_TONE2, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.12));
        putThemeColor(ThemeColor.OBJECT_TONE3, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.16));
        putThemeColor(ThemeColor.OBJECT_TONE4, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.2));
        putThemeColor(ThemeColor.OBJECT_TONE5, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.99));
        putThemeColor(ThemeColor.BORDER, 0x666666);
        putThemeColor(ThemeColor.SELECTION, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.4));
        putThemeColor(ThemeColor.SELECTION_TEXT, 0xffffff);
        putThemeColor(ThemeColor.TEXT, 0x000000);
        putThemeColor(ThemeColor.TEXT_BACKGROUND, 0xffffff);
        putThemeColor(ThemeColor.DISABLED_TEXT_BACKGROUND, 0xfafafa);
        putThemeColor(ThemeColor.MANDATORY_TEXT_BACKGROUND, 0xfcba84);
        putThemeColor(ThemeColor.READ_ONLY_TEXT_BACKGROUND, 0xeeeeee);
        putThemeColor(ThemeColor.SEPARATOR, 0xeeeeee);
    }

    @Override
    protected void initGeneralStyles() {
        super.initGeneralStyles();

        Style style = new Style(".gwt-SplitLayoutPanel");
        addStyle(style);

        style = new Style(".gwt-SplitLayoutPanel-HDragger");
        style.addProperty("cursor", "col-resize");
        addStyle(style);

        style = new Style(".gwt-SplitLayoutPanel-VDragger");
        style.addProperty("cursor", "row-resize");
        addStyle(style);

    }

    @Override
    protected void initStyles() {
        super.initStyles();
        initSectionPanelStyles(SectionPanel.DEFAULT_STYLE_PREFIX);
        initHeaderPanelStyles(HeaderPanel.DEFAULT_STYLE_PREFIX);
    }

    private void initSectionPanelStyles(String prefix) {
        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SectionPanel.StyleSuffix.Root));
        style.addProperty("border", "1px solid {}", ThemeColor.BORDER);
        style.addProperty("margin", "0px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SectionPanel.StyleSuffix.Content));
        style.addProperty("border", "2px solid {}", ThemeColor.SELECTION);
        style.addProperty("border-top", "4px solid {}", ThemeColor.SELECTION);
        style.addProperty("background-color", "white");
        addStyle(style);

    }

    private void initHeaderPanelStyles(String prefix) {
        Style style = new Style(Selector.valueOf(prefix));
        style.addGradientBackground(ThemeColor.OBJECT_TONE5);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, HeaderPanel.StyleSuffix.Label));
        style.addProperty("color", "white");
        style.addProperty("fontFamily", "Arial");
        style.addProperty("fontWeight", "bold");
        style.addProperty("margin-left", "5px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, HeaderPanel.StyleSuffix.Logo));
        style.addProperty("margin-left", "5px");
        addStyle(style);

    }

}
