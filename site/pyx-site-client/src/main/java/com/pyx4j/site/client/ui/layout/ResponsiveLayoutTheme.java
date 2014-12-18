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
 */
package com.pyx4j.site.client.ui.layout;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class ResponsiveLayoutTheme extends Theme {

    public static enum StyleName implements IStyleName {
        ResponsiveLayoutOverlayActions, ResponsiveLayoutOverlayActionsTabbar, ResponsiveLayoutOverlayActionsTabItem, ResponsiveLayoutOverlayActionsTabDeck,

        ResponsiveLayoutOverlayActionsTabPanel, ResponsiveLayoutOverlayActionsCloseButton
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, extra1, extra2, extra3
    }

    public ResponsiveLayoutTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {

        Style style = new Style(".", StyleName.ResponsiveLayoutOverlayActions);
        style.addProperty("position", "absolute");
        style.addProperty("bottom", "0");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutOverlayActionsTabbar);
        style.addProperty("float", "left");
        style.addProperty("margin", "-30px 0 0 10px");
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutOverlayActionsTabItem, ".", WidgetsTheme.StyleName.Button);
        style.addProperty("margin", "0 4px 0 0");
        style.addProperty("line-height", "30px");
        style.addProperty("padding", "0 10px");
        style.addProperty("height", "30px");
        style.addProperty("color", ThemeColor.foreground, 0.05);
        style.addProperty("cursor", "pointer");
        style.addProperty("background", "transparent");
        style.addProperty("background-color", ThemeColor.foreground, 0.8);
        style.addProperty("white-space", "nowrap");
        style.addProperty("outline", "none");
        style.addProperty("border", "0px");
        style.addProperty("border-radius", "0px");
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutOverlayActionsCloseButton, ".", WidgetsTheme.StyleName.Button);
        style.addProperty("float", "right");
        style.addProperty("margin", "7px 10px 0 0");
        style.addProperty("height", "28px");
        style.addProperty("cursor", "pointer");
        style.addProperty("background", "transparent");
        style.addProperty("background-color", ThemeColor.foreground, 0.8);
        style.addProperty("outline", "none");
        style.addProperty("margin-top", "-28px");
        style.addProperty("border", "0px");
        style.addProperty("border-radius", "0px");
        style.addProperty("padding", "0 3px");
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutOverlayActionsCloseButton, " img");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutOverlayActionsTabDeck);
        style.addProperty("border-top", "4px solid");
        style.addProperty("border-color", ThemeColor.foreground, 1.2);
        style.addProperty("background-color", ThemeColor.foreground, 0.1);
        style.addProperty("height", "200px");
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutOverlayActionsTabItem, " .", WidgetsTheme.StyleName.ButtonText);
        style.addProperty("line-height", "normal");
        addStyle(style);

        style = new Style(".", WidgetsTheme.StyleName.ToolbarItem, "-", StyleDependent.selected, " .", StyleName.ResponsiveLayoutOverlayActionsTabItem, ".",
                WidgetsTheme.StyleName.Button);
        style.addProperty("background-color", ThemeColor.foreground, 1.2);
        addStyle(style);

    }

}
