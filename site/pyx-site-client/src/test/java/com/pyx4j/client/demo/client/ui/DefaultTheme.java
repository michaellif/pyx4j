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
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.pyx4j.client.demo.client.ui;

import com.pyx4j.client.demo.client.theme.SiteTheme;
import com.pyx4j.widgets.client.style.Selector;
import com.pyx4j.widgets.client.style.Style;

public class DefaultTheme extends SiteTheme {

    @Override
    protected void initBodyStyles() {
        super.initBodyStyles();
        Style style = new Style("body");
        style.addProperty("background-color", "#eee");
        addStyle(style);
    }

    @Override
    protected void initSiteViewStyles() {
        String prefix = SiteView.DEFAULT_STYLE_PREFIX;

        int minWidth = 760;
        int maxWidth = 960;
        int leftColumnWidth = 230;
        int rightColumnWidth = 200;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("background-color", "#fff");
        style.addProperty("width", "95%");
        style.addProperty("min-width", minWidth + "px");
        style.addProperty("max-width", maxWidth + "px");
        style.addProperty("margin", "0 auto");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Header));
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.MainNavig));
        style.addProperty("width", "100%");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Center));
        style.addProperty("width", "100%");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Main));
        style.addProperty("height", "100%");
        style.addProperty("margin", "0 " + rightColumnWidth + "px 0 " + leftColumnWidth + "px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Left));
        style.addProperty("float", "left");
        style.addProperty("width", leftColumnWidth + "px");
        style.addProperty("margin-left", "-100%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Right));
        style.addProperty("float", "left");
        style.addProperty("width", rightColumnWidth + "px");
        style.addProperty("margin-left", "-" + rightColumnWidth + "px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Footer));
        style.addProperty("clear", "left");
        addStyle(style);

    }

}
