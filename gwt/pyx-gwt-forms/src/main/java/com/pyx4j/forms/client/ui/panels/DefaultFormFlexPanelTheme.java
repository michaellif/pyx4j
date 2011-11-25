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

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;

public abstract class DefaultFormFlexPanelTheme extends Theme {

    public static enum StyleName implements IStyleName {
        FormFlexPanel, FormFlexPanelHR, FormFlexPanelH1, FormFlexPanelH1Label, FormFlexPanelH2, FormFlexPanelH2Label, FormFlexPanelH3, FormFlexPanelH3Label, FormFlexPanelActionWidget
    }

    public static enum StyleDependent implements IStyleDependent {

    }

    public DefaultFormFlexPanelTheme() {
        initStyles();
    }

    protected void initStyles() {
        Style style = new Style(".", StyleName.FormFlexPanel);
        style.addProperty("width", "100%");
        style.addProperty("border-spacing", "0");
        addStyle(style);

        style = new Style(".", StyleName.FormFlexPanel, " td");
        style.addProperty("padding", "0");
        addStyle(style);

        style = new Style(".", StyleName.FormFlexPanelHR);
        style.addProperty("background-color", getBackgroundColor(), 0.4);
        style.addProperty("height", "2px");
        style.addProperty("margin", "6px 0 4px 0");
        addStyle(style);

        style = new Style(".", StyleName.FormFlexPanelH1);
        style.addProperty("background-color", getBackgroundColor(), 0.4);
        style.addProperty("margin", "6px 0 4px 0");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.FormFlexPanelH1Label);
        style.addProperty("color", getBackgroundColor(), 0);
        style.addProperty("padding", "4px");
        style.addProperty("font-size", "1.3em");
        addStyle(style);

        style = new Style(".", StyleName.FormFlexPanelH2);
        style.addProperty("border-bottom", "solid 2px");
        style.addProperty("border-bottom-color", getBackgroundColor(), 1);
        style.addProperty("margin", "6px 0 4px 0");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.FormFlexPanelH2Label);
        style.addProperty("color", getBackgroundColor(), 1);
        style.addProperty("padding", "3px");
        style.addProperty("font-size", "1.2em");
        addStyle(style);

        style = new Style(".", StyleName.FormFlexPanelH3);
        style.addProperty("border-bottom", "solid 1px");
        style.addProperty("border-bottom-color", getBackgroundColor(), 0.6);
        style.addProperty("margin", "6px 0 4px 0");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.FormFlexPanelH3Label);
        style.addProperty("color", getBackgroundColor(), 1.3);
        style.addProperty("padding", "2px");
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-style", "italic");
        addStyle(style);

        style = new Style(".", StyleName.FormFlexPanelActionWidget);
        style.addProperty("float", "right");
        style.addProperty("margin-right", "20px");
        addStyle(style);

    }

    protected abstract ThemeColors getBackgroundColor();
}
