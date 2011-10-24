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
import static com.pyx4j.forms.client.ui.panels.FormFlexPanel.StyleName.FormFlexPanelHeader;
import static com.pyx4j.forms.client.ui.panels.FormFlexPanel.StyleName.FormFlexPanelHeaderLabel;

import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;

public class DefaultFormFlexPanelTheme extends Theme {

    public DefaultFormFlexPanelTheme() {
        initStyles();
    }

    protected void initStyles() {
        Style style = new Style(".", FormFlexPanel);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", FormFlexPanelHeader);
        style.addProperty("background-color", "#ddd");
        style.addProperty("margin", "6px 0 4px 0");
        addStyle(style);

        style = new Style(".", FormFlexPanelHeaderLabel);
        style.addProperty("color", "#666");
        style.addProperty("padding", "4px");
        style.addProperty("font-size", "1.3em");
        addStyle(style);

    }
}
