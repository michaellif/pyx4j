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
package com.pyx4j.site.client.ui.layout;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

public class ResponsiveLayoutTheme extends Theme {

    public static enum StyleName implements IStyleName {
        ResponsiveLayoutOverlayActions, ResponsiveLayoutOverlayActionsTab, ResponsiveLayoutOverlayActionsTabPanel, ResponsiveLayoutOverlayActionsCloseButton
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

        style = new Style(".", StyleName.ResponsiveLayoutOverlayActions, " .gwt-TabPanelBottom");
        style.addProperty("background-color", ThemeColor.foreground, 1.0);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutOverlayActions, " .gwt-TabBarItem");
        style.addProperty("outline", "none");
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutOverlayActionsTab);
        style.addProperty("margin", "0 10px");
        style.addProperty("line-height", "34px");
        style.addProperty("padding", "0 10px");
        style.addProperty("height", "34px");
        style.addProperty("color", ThemeColor.background, 1.0);
        style.addProperty("cursor", "pointer");
        style.addProperty("background-color", ThemeColor.foreground, 1.0);
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutOverlayActionsCloseButton);
        style.addProperty("float", "right");
        style.addProperty("margin", "0 10px");
        style.addProperty("padding", "5px");
        style.addProperty("cursor", "pointer");
        style.addProperty("background-color", ThemeColor.foreground, 1.0);
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutOverlayActionsCloseButton, " img");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".", StyleName.ResponsiveLayoutOverlayActionsTabPanel);
        style.addProperty("border", "4px solid");
        style.addProperty("padding", "10px");
        style.addProperty("background-color", ThemeColor.foreground, 0.9);
        addStyle(style);

    }

}
