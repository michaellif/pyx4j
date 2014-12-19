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
package com.pyx4j.site.client.ui.sidemenu;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

public class SideMenuTheme extends Theme {

    public static enum StyleName implements IStyleName {
        SideMenu, SideMenuList, SideMenuItem, SideMenuLabel, SideMenuIcon, SideMenuItemPanel, SideMenuExpantionHandler;
    }

    public static enum StyleDependent implements IStyleDependent {
        active, slideOutMenu, collapsedMenu, l1, l2, l3,
    }

    public SideMenuTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {
        Style style = new Style(".", StyleName.SideMenu);
        style.addProperty("width", "14em");
        style.addProperty("margin", "10px 0 0 10px");
        style.addProperty("padding-left", "10px");
        addStyle(style);

        style = new Style(".", StyleName.SideMenu, "-", StyleDependent.collapsedMenu);
        style.addProperty("width", "auto");
        style.addProperty("height", "auto");
        addStyle(style);

        style = new Style(".", StyleName.SideMenu, "-", StyleDependent.collapsedMenu, " .", StyleName.SideMenuLabel);
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style(".", StyleName.SideMenuList);
        style.addProperty("padding", "0");
        addStyle(style);

        style = new Style(".", StyleName.SideMenuItem);
        style.addProperty("white-space", "nowrap");
        style.addProperty("list-style", "none");
        addStyle(style);

        style = new Style(".", StyleName.SideMenuItem, "-", StyleDependent.active);
        style.addProperty("color", ThemeColor.foreground, 0.01);
        addStyle(style);

        style = new Style(".", StyleName.SideMenuLabel);
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "middle");
        style.addProperty("padding-left", "5px");
        addStyle(style);

        style = new Style(".", StyleName.SideMenuIcon);
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "middle");
        style.addProperty("padding-left", "5px");
        addStyle(style);

        style = new Style(".", StyleName.SideMenuItemPanel);
        style.addProperty("padding", "4px 0");
        style.addProperty("transition", "all 0.3s ease-in-out 0s");
        addStyle(style);

        style = new Style(".", StyleName.SideMenuExpantionHandler);
        style.addProperty("position", "absolute");
        style.addProperty("left", "0");
        addStyle(style);

        style = new Style(".", StyleName.SideMenuItem, "-", StyleDependent.l1, " >.", StyleName.SideMenuList);
        style.addProperty("padding-bottom", "15px");
        addStyle(style);

        style = new Style(".", StyleName.SideMenuItem, "-", StyleDependent.l1, ">.", StyleName.SideMenuItemPanel);
        style.addProperty("line-height", "2.2em");
        style.addProperty("font-size", "1.2em");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding", "5px 0px 3px 0");
        style.addGradient(ThemeColor.foreground, 0.2, ThemeColor.foreground, 0.15);
        addStyle(style);

        style = new Style(".", StyleName.SideMenuItem, "-", StyleDependent.l1, ">.", StyleName.SideMenuItemPanel, "-", StyleDependent.active);
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(".", StyleName.SideMenuItem, "-", StyleDependent.l1, ">.", StyleName.SideMenuItemPanel, " .", StyleName.SideMenuExpantionHandler);
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style(".", StyleName.SideMenuItem, "-", StyleDependent.l2, ">.", StyleName.SideMenuItemPanel);
        style.addProperty("padding-left", "20px");
        addStyle(style);

        style = new Style(".", StyleName.SideMenuItem, "-", StyleDependent.l2, ">.", StyleName.SideMenuItemPanel, "-", StyleDependent.active);
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", StyleName.SideMenuItem, "-", StyleDependent.l2, ">.", StyleName.SideMenuItemPanel, ":hover");
        style.addProperty("background", ThemeColor.foreground, 0.05);
        addStyle(style);

        style = new Style(".", StyleName.SideMenuItem, "-", StyleDependent.l3, ">.", StyleName.SideMenuItemPanel);
        style.addProperty("padding-left", "40px");
        addStyle(style);

        style = new Style(".", StyleName.SideMenuItem, "-", StyleDependent.l3, ">.", StyleName.SideMenuItemPanel, "-", StyleDependent.active);
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", StyleName.SideMenuItem, "-", StyleDependent.l3, ">.", StyleName.SideMenuItemPanel, ":hover");
        style.addProperty("background", ThemeColor.foreground, 0.05);
        addStyle(style);

        style = new Style(".", StyleName.SideMenu, "-", StyleDependent.collapsedMenu, " .", StyleName.SideMenuItem, ":hover .", StyleName.SideMenuLabel);
        style.addProperty("display", "inline");
        style.addProperty("position", "absolute");
        style.addProperty("margin-top", "-6px");
        style.addProperty("padding", "5px 5px 5px 10px");
        style.addProperty("background", ThemeColor.foreground, 0.1);
        addStyle(style);

        style = new Style(".", StyleName.SideMenu, "-", StyleDependent.slideOutMenu);
        style.addProperty("width", "100%");
        style.addProperty("height", "100%");
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        style.addProperty("background", ThemeColor.foreground, 0.8);
        addStyle(style);

    }
}
