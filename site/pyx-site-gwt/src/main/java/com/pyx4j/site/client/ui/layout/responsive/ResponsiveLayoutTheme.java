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
        ResponsiveLayoutMainHolder, ResponsiveLayoutStickyHeaderHolder, ResponsiveLayoutFooterHolder, ResponsiveLayoutContentHolder;
    }

    public ResponsiveLayoutTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {
        Style style = new Style(".", StyleName.ResponsiveLayoutMainHolder);
        style.addProperty("background-color", ThemeColor.background);
        style.addProperty("min-width", "320px");
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutStickyHeaderHolder);
        style.addGradient(ThemeColor.foreground, 1, ThemeColor.foreground, 0.95);
        style.addProperty("min-width", "320px");
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutFooterHolder);
        style.addGradient(ThemeColor.foreground, 0.6, ThemeColor.foreground, 0.55);
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutContentHolder);
        style.addProperty("min-height", "400px");
        addStyle(style);

    }

}
