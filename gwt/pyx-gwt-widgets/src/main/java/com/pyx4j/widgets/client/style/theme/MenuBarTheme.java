/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Mar 25, 2016
 * @author vlads
 */
package com.pyx4j.widgets.client.style.theme;

import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;

public class MenuBarTheme extends Theme {

    public MenuBarTheme() {
        initStyles();
    }

    protected void initStyles() {
        Style style = new Style(".gwt-MenuBar");
        style.addProperty("background-color", ThemeColor.foreground, 0.1);
        style.addProperty("color", ThemeColor.foreground, 0.9);
        addStyle(style);

        style = new Style(".gwt-MenuItem");
        style.addProperty("color", "#666666");
        addStyle(style);

        style = new Style(".gwt-MenuItem-selected");
        style.addGradient(ThemeColor.foreground, 0.15, ThemeColor.foreground, 0.35);
        style.addProperty("color", "#333333");
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(".gwt-MenuItem-disabled");
        style.addProperty("color", ThemeColor.foreground, 0.6);
        style.addProperty("cursor", "default");
        addStyle(style);

        style = new Style(".gwt-MenuBar-vertical");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.6);

        addStyle(style);

        style = new Style(".gwt-MenuBar-vertical .gwt-MenuItem");
        style.addProperty("padding", "4px 8px 6px 4px");
        addStyle(style);

        style = new Style(".gwt-MenuBar-horizontal");
        addStyle(style);

        style = new Style(".gwt-MenuBar-horizontal .gwt-MenuItem");
        style.addProperty("vertical-align", "bottom");
        addStyle(style);
    }

}
