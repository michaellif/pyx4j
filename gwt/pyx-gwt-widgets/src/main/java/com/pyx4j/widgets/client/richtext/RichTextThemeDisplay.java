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
 * Created on Mar 22, 2016
 * @author vlads
 */
package com.pyx4j.widgets.client.richtext;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.richtext.RichTextTheme.StyleName;

public class RichTextThemeDisplay extends Theme {

    public RichTextThemeDisplay() {
        initStyles();
    }

    public static ThemeId themeId() {
        return new ClassBasedThemeId(RichTextThemeDisplay.class);
    }

    @Override
    public final ThemeId getId() {
        return themeId();
    }

    protected void initStyles() {
        Style style = new Style(".", StyleName.ReachTextDisplay);
        style.addProperty("line-height", "normal");
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.ReachTextDisplay, " p");
        style.addProperty("margin", "0");
        addStyle(style);
    }
}
