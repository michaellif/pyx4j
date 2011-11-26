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

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;

public class TenantDashboardTheme extends Theme {

    public static enum StyleName implements IStyleName {
        TenantDashboard, TenantDashboardLeft, TenantDashboardRight, TenantDashboardTableHeader, TenantDashboardTableRow;
    }

    public TenantDashboardTheme() {
        Style style = new Style(".", StyleName.TenantDashboard.name());
        style.addProperty("width", "100%");
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(".", StyleName.TenantDashboardLeft.name());
        style.addProperty("height", "100%");
        style.addProperty("margin-bottom", "30px");
        addStyle(style);

        style = new Style(".", StyleName.TenantDashboardLeft.name(), " > div");
        style.addProperty("min-height", "500px");
        style.addProperty("border-right", "solid 1px");
        style.addProperty("border-right-color", ThemeColors.foreground, 0.4);
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(".", StyleName.TenantDashboardRight.name());
        style.addProperty("margin-bottom", "30px");
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(".", StyleName.TenantDashboardTableHeader.name());
        style.addProperty("background-color", ThemeColors.foreground, 0.1);
        style.addProperty("line-height", "35px");
        style.addProperty("color", ThemeColors.foreground, 0.7);
        addStyle(style);

        style = new Style(".", StyleName.TenantDashboardTableRow.name());
        style.addProperty("height", "45px");
        style.addProperty("border-bottom", "dotted 1px #aaa");
        addStyle(style);

        style = new Style(".", StyleName.TenantDashboardTableRow.name(), ":last-child");
        style.addProperty("border-bottom", "none");
        addStyle(style);

        style = new Style(".", StyleName.TenantDashboardTableRow.name(), ":hover");
        style.addProperty("background-color", ThemeColors.object1, 0.05);
        style.addProperty("cursor", "pointer");
        addStyle(style);

    }
}
