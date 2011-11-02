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
 * Created on Oct 20, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.panels;

import static com.pyx4j.forms.client.ui.panels.FormFlexPanel.StyleName.FormFlexPanel;
import static com.pyx4j.forms.client.ui.panels.FormFlexPanel.StyleName.FormFlexPanelH1;
import static com.pyx4j.forms.client.ui.panels.FormFlexPanel.StyleName.FormFlexPanelH1Label;
import static com.pyx4j.forms.client.ui.panels.FormFlexPanel.StyleName.FormFlexPanelH2;
import static com.pyx4j.forms.client.ui.panels.FormFlexPanel.StyleName.FormFlexPanelH2Label;
import static com.pyx4j.forms.client.ui.panels.FormFlexPanel.StyleName.FormFlexPanelH3;
import static com.pyx4j.forms.client.ui.panels.FormFlexPanel.StyleName.FormFlexPanelH3Label;
import static com.pyx4j.forms.client.ui.panels.FormFlexPanel.StyleName.FormFlexPanelHR;

import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;

public class DefaultFormFlexPanelTheme extends Theme {

    public DefaultFormFlexPanelTheme() {
        initStyles();
    }

    protected void initStyles() {
        Style style = new Style(".", FormFlexPanel);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", FormFlexPanelHR);
        style.addProperty("background-color", ThemeColors.object2);
        style.addProperty("height", "1px");
        style.addProperty("margin", "6px 0 4px 0");
        addStyle(style);

        style = new Style(".", FormFlexPanelH1);
        style.addProperty("background-color", ThemeColors.object2);
        style.addProperty("margin", "6px 0 4px 0");
        addStyle(style);

        style = new Style(".", FormFlexPanelH1Label);
        style.addProperty("color", ThemeColors.object2, 0.05);
        style.addShadow(ThemeColors.foreground, "1px 1px 0");
        style.addProperty("padding", "4px");
        style.addProperty("font-size", "1.3em");
        addStyle(style);

        style = new Style(".", FormFlexPanelH2);
        style.addProperty("background-color", ThemeColors.object2, 0.9);
        style.addProperty("margin", "6px 0 4px 0");
        addStyle(style);

        style = new Style(".", FormFlexPanelH2Label);
        style.addProperty("color", ThemeColors.object2, 0.05);
        style.addShadow(ThemeColors.foreground, "1px 1px 0");
        style.addProperty("padding", "3px");
        style.addProperty("font-size", "1.2em");
        style.addProperty("font-style", "italic");
        addStyle(style);

        style = new Style(".", FormFlexPanelH3);
        style.addProperty("background-color", ThemeColors.object2, 0.8);
        style.addProperty("margin", "6px 0 4px 0");
        addStyle(style);

        style = new Style(".", FormFlexPanelH3Label);
        style.addProperty("color", ThemeColors.object2, 0.05);
        style.addShadow(ThemeColors.foreground, "1px 1px 0");
        style.addProperty("padding", "2px");
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-style", "italic");
        addStyle(style);

    }
}
