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
 * Created on Nov 13, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud.lister;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;

public class DefaultListerTheme extends Theme {

    public static enum StyleSuffix implements IStyleName {
        Lister, ListerActionsPanel, ListerFiltersPanel, ListerListPanel, ListerButton
    }

    public DefaultListerTheme() {
        initStyles();
    }

    protected void initStyles() {

        Style style = new Style(".", StyleSuffix.Lister);
        addStyle(style);

        style = new Style(".", StyleSuffix.ListerActionsPanel);
        style.addProperty("background-color", ThemeColors.foreground, 0.20);
        style.addProperty("color", ThemeColors.foreground);
        style.addProperty("height", "3em");
        addStyle(style);

        style = new Style(".", StyleSuffix.ListerFiltersPanel);
        style.addProperty("background-color", ThemeColors.foreground, 0.05);
        style.addProperty("padding-top", "0.5em");
        addStyle(style);

        style = new Style(".", StyleSuffix.ListerListPanel);
        addStyle(style);

        style = new Style(".", StyleSuffix.ListerButton);
        style.addProperty("color", ThemeColors.foreground, 0.15);
        style.addProperty("background-color", ThemeColors.foreground, 1.1);
        addStyle(style);

        style = new Style(".", StyleSuffix.ListerButton, ":hover");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.foreground, 0.3);
        addStyle(style);

    }
}
