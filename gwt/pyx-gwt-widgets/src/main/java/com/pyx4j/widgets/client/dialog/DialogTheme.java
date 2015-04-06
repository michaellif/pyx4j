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
 */
package com.pyx4j.widgets.client.dialog;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

public class DialogTheme extends Theme {

    public static enum StyleName implements IStyleName {
        Dialog, DialogCaption, DialogContent,

        DialogButtonsPanel, DialogDefaultButtonsToolbar, DialogCustomButtonsToolbar
    }

    public static enum StyleDependent implements IStyleDependent {

    }

    public DialogTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {

        Style style = new Style(".", StyleName.Dialog);
        style.addProperty("box-shadow", "10px 10px 5px rgba(0, 0, 0, 0.3)");
        style.addProperty("border", "5px solid");
        style.addProperty("border-color", ThemeColor.object2, 1);
        addStyle(style);

        style = new Style(".", StyleName.DialogCaption);
        style.addProperty("background", ThemeColor.object2, 0.9);
        style.addProperty("filter", "alpha(opacity=95)");
        style.addProperty("opacity", "0.95");
        style.addProperty("color", ThemeColor.object1, 0.1);
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-left", "10px");
        addStyle(style);

        style = new Style(".", StyleName.DialogContent);
        style.addProperty("background-color", ThemeColor.formBackground, 0.1);
        style.addProperty("width", "100%");
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(".", StyleName.DialogButtonsPanel);
        style.addProperty("border-top", "solid 1px");
        style.addProperty("border-top-color", ThemeColor.foreground, 0.3);
        style.addProperty("display", "inline-block");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.DialogDefaultButtonsToolbar);
        style.addProperty("margin", "10px");
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style(".", StyleName.DialogCustomButtonsToolbar);
        style.addProperty("margin", "10px");
        style.addProperty("float", "right");
        addStyle(style);
    }

}
