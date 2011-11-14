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
 * Created on Nov 14, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;

public class DefaultCCOmponentsTheme extends Theme {

    public static enum StyleName implements IStyleName {

    }

    public static enum StyleDependent implements IStyleDependent {
        disabled, readOnly, invalid
    }

    public DefaultCCOmponentsTheme() {
        initStyles();
    }

    protected void initStyles() {
        initTextBoxStyle();
        initListBoxStyle();
    }

    protected void initTextBoxStyle() {

        Style style = new Style(".", DefaultWidgetsTheme.StyleName.TextBox, "-", StyleDependent.disabled);
        style.addProperty("background-color", ThemeColors.foreground, 0.3);
        addStyle(style);

        style = new Style(".", DefaultWidgetsTheme.StyleName.TextBox, "-", StyleDependent.readOnly);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "none");
        style.addProperty("background-color", ThemeColors.foreground, 0.3);
        addStyle(style);

        style = new Style(".", DefaultWidgetsTheme.StyleName.TextBox, "-", StyleDependent.invalid);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", "#f79494");
        style.addProperty("background-color", "#f8d8d8");
        addStyle(style);

    }

    protected void initListBoxStyle() {
        Style style = new Style(".", DefaultWidgetsTheme.StyleName.ListBox, "-", StyleDependent.disabled);
        style.addProperty("background-color", ThemeColors.foreground, 0.3);
        addStyle(style);

        style = new Style(".", DefaultWidgetsTheme.StyleName.ListBox, "-", StyleDependent.readOnly);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "none");
        style.addProperty("background-color", ThemeColors.foreground, 0.3);
        addStyle(style);

        style = new Style(".", DefaultWidgetsTheme.StyleName.ListBox, "-", StyleDependent.invalid);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", "#f79494");
        style.addProperty("background-color", "#f8d8d8");
        addStyle(style);
    }

}
