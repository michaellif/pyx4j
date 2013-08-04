/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jun 21, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.layout.responsive;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

public class ResponsiveLayoutTheme extends Theme {

    public static enum StyleName implements IStyleName {
        ResponsiveLayoutMainHolder, ResponsiveLayoutStickyToolbarHolder, ResponsiveLayoutInlineToolbarHolder, ResponsiveLayoutStickyMessageHolder,

        ResponsiveLayoutFooterHolder, ResponsiveLayoutContentHolder, ResponsiveLayoutContentBackground;
    }

    public ResponsiveLayoutTheme() {
        initStyles();
        initScrollStyles();
    }

    private void initScrollStyles() {

        Style style = new Style(".customScrollPanel");
        style.addProperty("background-color", "#295d98");
        addStyle(style);

        style = new Style(".customScrollPanelCorner");
        style.addProperty("background-color", "#295d98");
        addStyle(style);

        style = new Style(".nativeVerticalScrollbar");
        style.addProperty("opacity", "0.6");
        style.addProperty("-webkit-transition", "opacity 350ms");
        style.addProperty("-moz-transition", "opacity 350ms");
        style.addProperty("-o-transition", "opacity 350ms");
        style.addProperty("transition", "opacity 350ms");
        addStyle(style);

        style = new Style(".nativeVerticalScrollbar:hover");
        style.addProperty("opacity", "0.6");
        addStyle(style);

    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {
        Style style = new Style(".", StyleName.ResponsiveLayoutMainHolder);
        style.addProperty("min-width", "320px");
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutInlineToolbarHolder);
        style.addGradient(ThemeColor.foreground, 1, ThemeColor.foreground, 0.95);
        style.addProperty("min-width", "320px");
        style.addProperty("height", "60px");
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutStickyToolbarHolder);
        style.addGradient(ThemeColor.foreground, 1, ThemeColor.foreground, 0.95);
        style.addProperty("min-width", "320px");
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutFooterHolder);
        style.addGradient(ThemeColor.foreground, 0.7, ThemeColor.foreground, 0.65);
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutContentHolder);
        style.addProperty("min-height", "450px");
        addStyle(style);

    }

}
