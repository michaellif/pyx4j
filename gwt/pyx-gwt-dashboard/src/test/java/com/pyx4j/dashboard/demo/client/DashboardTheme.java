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
 * Created on 2011-04-07
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.dashboard.demo.client;

import com.pyx4j.commons.css.Selector;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;
import com.pyx4j.dashboard.client.DashboardPanel;

public class DashboardTheme extends Theme {

    public DashboardTheme() {

        initGeneralStyles();
        initBodyStyles();

        initDashboardDemo();
        initDashboard();
    }

    protected void initGeneralStyles() {
        Style style = new Style("html");
        style.addProperty("overflow-y", "scroll");
        addStyle(style);

        style = new Style("td");
        style.addProperty("padding", "0px");
        addStyle(style);

        style = new Style("p");
        style.addProperty("margin", "0.3em");
        addStyle(style);

        style = new Style("h1");
        style.addProperty("font-size", "2em");
        style.addProperty("line-height", "2.5em");
        style.addProperty("padding-bottom", "0.5px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h2");
        style.addProperty("font-size", "1.5em");
        style.addProperty("padding-bottom", "0.5px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h3");
        style.addProperty("font-size", "1.17em");
        style.addProperty("padding-bottom", "0.5px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h4, blockquote");
        style.addProperty("font-size", "1.12em");
        style.addProperty("padding-bottom", "0.3px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h5");
        style.addProperty("font-size", "1.08em");
        style.addProperty("padding-bottom", "0.2px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h6");
        style.addProperty("font-size", ".75em");
        style.addProperty("padding-bottom", "0.2px");
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style("h1, h2, h3, h4, h5, h6, b, strong");
        style.addProperty("font-weight", "bolder");
        addStyle(style);

        style = new Style("blockquote, ul, fieldset, form, ol, dl, dir, menu");
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style("blockquote");
        style.addProperty("margin-left", "40px");
        style.addProperty("margin-right", "40px");
        addStyle(style);
    }

    protected void initBodyStyles() {
        Style style = new Style("body");
        style.addProperty("background-color", ThemeColors.TEXT_BACKGROUND);
        style.addProperty("color", ThemeColors.TEXT);
        style.addProperty("border", "none");
        style.addProperty("font", "12px/1.5em Arial, Helvetica, sans-serif");
        addStyle(style);
    }

    protected void initDashboardDemo() {

        Style style = new Style(".Dashboard-wrapper");
        style.addProperty("border", "1px solid #aaa");
        style.addProperty("min-width", "600px");
        addStyle(style);

        style = new Style(".Dashboard-caption");
        style.addProperty("background-color", ThemeColors.OBJECT_TONE5);
        style.addProperty("color", "white");
        style.addProperty("font-weight", "bold");
        style.addProperty("text-align", "center");
        style.addProperty("line-height", "20px");
        style.addProperty("text-shadow", "0 -1px 0 #333333");
        style.addProperty("border-bottom", "1px solid #aaa");
        style.addProperty("padding-top", "1px");
        addStyle(style);

        style = new Style(".Dashboard-menu");
        style.addProperty("font", "menu");
        style.addProperty("background-color", ThemeColors.OBJECT_TONE1);
        style.addProperty("border", "1px solid #aaa");
        addStyle(style);

    }

    protected void initDashboard() {
        String prefix = DashboardPanel.BASE_NAME;

        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DashboardPanel.StyleSuffix.Column));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DashboardPanel.StyleSuffix.ColumnHeading));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE4);
        style.addProperty("font-weight", "bold");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DashboardPanel.StyleSuffix.ColumnSpacer));
        style.addProperty("height", "4em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DashboardPanel.StyleSuffix.Holder));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE2);
        style.addProperty("border", "1px solid #aaa");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DashboardPanel.StyleSuffix.HolderSetup));
        style.addProperty("background-color", ThemeColors.MANDATORY_TEXT_BACKGROUND);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DashboardPanel.StyleSuffix.HolderCaption));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE4);
        style.addProperty("font", "caption");
        style.addProperty("font-weight", "bold");
        style.addProperty("color", "#444");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DashboardPanel.StyleSuffix.HolderCaption) + ":hover");
        style.addProperty("background-color", ThemeColors.SELECTION);
        style.addProperty("color", ThemeColors.SELECTION_TEXT);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DashboardPanel.StyleSuffix.HolderHeading));
        style.addProperty("padding-top", "2px");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DashboardPanel.StyleSuffix.HolderMenu));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE1);
        style.addProperty("border", "1px solid #aaa");
        style.addProperty("font", "menu");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DashboardPanel.StyleSuffix.DndPositioner));
        style.addProperty("border", "1px dashed #aaa");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DashboardPanel.StyleSuffix.DndRowPositioner));
        style.addProperty("background-color", "#dfffff");
        style.addProperty("border", "1px dashed #aaa");
        addStyle(style);

        // overriding gwt-dnd styles:
        style = new Style(".dragdrop-handle");
        style.addProperty("cursor", "default");
        addStyle(style);
    }
}