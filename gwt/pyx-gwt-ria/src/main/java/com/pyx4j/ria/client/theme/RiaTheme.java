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
 * @version $Id$
 */
package com.pyx4j.ria.client.theme;

import com.pyx4j.ria.client.HeaderPanel;
import com.pyx4j.ria.client.SectionPanel;
import com.pyx4j.widgets.client.style.Selector;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColors;
import com.pyx4j.widgets.client.style.theme.WindowsTheme;

public abstract class RiaTheme extends WindowsTheme {

    protected RiaTheme() {
    }

    @Override
    protected void initStyles() {
        super.initStyles();
        initSectionPanelStyles(SectionPanel.DEFAULT_STYLE_PREFIX);
        initHeaderPanelStyles(HeaderPanel.DEFAULT_STYLE_PREFIX);
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
    protected void initBodyStyles() {
        Style style = new Style("body");
        style.addProperty("font-size", "0.9em");
        style.addProperty("background-color", ThemeColors.OBJECT_TONE2);
        style.addProperty("color", ThemeColors.TEXT);
        addStyle(style);
    }

    private void initSectionPanelStyles(String prefix) {
        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SectionPanel.StyleSuffix.Root));
        style.addProperty("border", "1px solid {}", ThemeColors.BORDER);
        style.addProperty("margin", "0px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SectionPanel.StyleSuffix.Content));
        style.addProperty("border", "2px solid {}", ThemeColors.SELECTION);
        style.addProperty("border-top", "4px solid {}", ThemeColors.SELECTION);
        style.addProperty("background-color", "white");
        addStyle(style);

    }

    private void initHeaderPanelStyles(String prefix) {
        Style style = new Style(Selector.valueOf(prefix));
        style.addGradientBackground(ThemeColors.OBJECT_TONE5);
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
