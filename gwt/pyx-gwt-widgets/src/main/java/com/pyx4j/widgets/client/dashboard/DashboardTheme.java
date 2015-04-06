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
 * Created on Nov 8, 2011
 * @author michaellif
 */
package com.pyx4j.widgets.client.dashboard;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

public class DashboardTheme extends Theme {

    public static enum StyleName implements IStyleName {
        Dashboard, DashboardColumn, DashboardColumnSpacer, DashboardGadgetHolder, DashboardGadgetHolderSetup, DashboardGadgetHolderCaption,

        DashboardGadgetHolderHeading, DashboardGadgetHolderMenu,

        DashboardDndPositioner, DashboardDndReportPositioner;
    }

    public static enum StyleDependent implements IStyleDependent {
        disabled, selected, hover, maximized, readonly
    }

    public DashboardTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {
        initDashboardStyle();
    }

    protected void initDashboardStyle() {

        Style style = new Style(".", StyleName.Dashboard);
        addStyle(style);

        style = new Style(".", StyleName.DashboardColumn);
        addStyle(style);

        style = new Style(".", StyleName.DashboardColumnSpacer);
        style.addProperty("height", "4em");
        addStyle(style);

        style = new Style(".", StyleName.DashboardGadgetHolder);
        style.addProperty("background-color", ThemeColor.formBackground);
        style.addProperty("border", "1px solid #ccc");
        style.addProperty("margin", "5px");
        addStyle(style);

        style = new Style(".", StyleName.DashboardGadgetHolder, "-", StyleDependent.maximized);
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style(".", StyleName.DashboardGadgetHolderSetup);
        style.addProperty("background-color", ThemeColor.contrast1, 0.1);
        addStyle(style);

        style = new Style(".", StyleName.DashboardGadgetHolderCaption);
        style.addProperty("background-color", ThemeColor.foreground);
        style.addProperty("color", ThemeColor.foreground, 0.1);
        style.addProperty("font", "caption");
        style.addProperty("font-weight", "bold");
        style.addProperty("height", "20px");
        style.addProperty("padding-left", "1em");
        style.addProperty("cursor", "move");
        addStyle(style);

        style = new Style(".", StyleName.DashboardGadgetHolderCaption, "-", StyleDependent.readonly);
        style.addProperty("cursor", "default");
        addStyle(style);

        style = new Style(".", StyleName.DashboardGadgetHolderCaption, ":hover");
        addStyle(style);

        style = new Style(".", StyleName.DashboardGadgetHolderHeading);
        style.addProperty("padding", "2px 6px");
        addStyle(style);

        style = new Style(".", StyleName.DashboardGadgetHolderMenu);
        style.addProperty("font", "menu");
        addStyle(style);

        style = new Style(".", StyleName.DashboardDndPositioner);
        style.addProperty("border", "1px dotted #555");
        style.addProperty("margin", "5px");
        addStyle(style);

        style = new Style(".", StyleName.DashboardDndReportPositioner);
        style.addProperty("border", "1px dotted #555");
        style.addProperty("margin", "5px");
        addStyle(style);

        // overriding gwt-dnd styles:
        style = new Style(".dragdrop-handle");
        addStyle(style);
    }
}
