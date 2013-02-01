/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-01
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.themes;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;

public class TenantInsuranceTheme extends Theme {

    public enum StyleName implements IStyleName {
        TenantSureLogo, TenantSureLogoPhone, TenantSureManagementGreetingPanel, TenantSureManagementGreeting, TenantSureManagementActionsPanel;
    }

    public TenantInsuranceTheme() {
        Style style;

        style = new Style(".", StyleName.TenantSureLogo.name());
        addStyle(style);

        style = new Style(".", StyleName.TenantSureLogoPhone.name());
        style.addProperty("width", "100%");
        style.addProperty("display", "block");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureManagementGreetingPanel.name());
        addStyle(style);

        style = new Style(".", StyleName.TenantSureManagementGreetingPanel.name(), " ", ".", StyleName.TenantSureLogo.name());
        style.addProperty("display", "inline-block");
        style.addProperty("width", "auto");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureManagementGreeting.name());
        style.addProperty("width", "100%");
        style.addProperty("height", "100%");
        style.addProperty("text-align", "center");
        style.addProperty("vertical-align", "middle");
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureManagementActionsPanel.name());
        style.addProperty("margin-top", "50px");
        style.addProperty("margin-bottom", "50px");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        style.addProperty("width", "300px");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureManagementActionsPanel.name(), " ", ".Button");
        style.addProperty("display", "block");
        style.addProperty("float", "none");
        style.addProperty("margin-top", "5px");
        style.addProperty("margin-bottom", "5px");
        addStyle(style);

    }

}
