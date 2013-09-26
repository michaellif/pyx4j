/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.client.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

public class TenantDashboardTheme extends Theme {

    public static enum StyleName implements IStyleName {
        TenantDashboard, TenantDashboardLeft, TenantDashboardRight, TenantDashboardTableHeader, TenantDashboardTableRow, TenantDashboardSection;
    }

    public TenantDashboardTheme() {
        Style style = new Style(".", StyleName.TenantDashboard);
        style.addProperty("width", "100%");
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(".", StyleName.TenantDashboardLeft);
        style.addProperty("height", "100%");
        style.addProperty("margin-bottom", "30px");
        addStyle(style);

        style = new Style(".", StyleName.TenantDashboardLeft, " > div");
        style.addProperty("min-height", "500px");
        style.addProperty("border-right", "solid 1px");
        style.addProperty("border-right-color", ThemeColor.foreground, 0.4);
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(".", StyleName.TenantDashboardRight);
        style.addProperty("margin-bottom", "30px");
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(".", StyleName.TenantDashboardTableHeader);
        style.addProperty("background-color", ThemeColor.foreground, 0.1);
        style.addProperty("height", "20px");
        style.addProperty("color", ThemeColor.foreground, 0.7);
        addStyle(style);

        style = new Style(".", StyleName.TenantDashboardTableHeader, ">td");
        style.addProperty("padding", "2px 6px");
        addStyle(style);

        style = new Style(".", StyleName.TenantDashboardTableRow);
        style.addProperty("height", "45px");
        style.addProperty("border-bottom", "dotted 1px");
        style.addProperty("border-bottom-color", ThemeColor.foreground, 0.7);
        addStyle(style);

        style = new Style(".", StyleName.TenantDashboardTableRow, ":last-child");
        style.addProperty("border-bottom", "none");
        addStyle(style);

        style = new Style(".", StyleName.TenantDashboardTableRow, ">td");
        style.addProperty("padding", "2px 6px");
        addStyle(style);

//        style = new Style(".", StyleName.TenantDashboardTableRow, ":hover");
//        style.addProperty("background-color", ThemeColors.object1, 0.05);
//        style.addProperty("cursor", "pointer");
//        addStyle(style);

        style = new Style(".", StyleName.TenantDashboardSection);
        style.addProperty("height", "12em");
        style.addProperty("padding-left", "0.5em");
        style.addProperty("padding-right", "0.5em");
        addStyle(style);
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }
}
