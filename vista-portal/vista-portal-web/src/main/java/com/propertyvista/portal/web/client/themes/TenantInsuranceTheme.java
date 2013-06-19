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
package com.propertyvista.portal.web.client.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeId;

// TODO merge with TenantSureTheme
public class TenantInsuranceTheme extends Theme {

    public enum StyleName implements IStyleName {
        //@formatter:off
        TenantSureLogo,
        TenantSureLogoPhone,
        TenantSureManagementGreetingPanel,
        TenantSureManagementGreeting,
        TenantSureManagementContentPanel,
        TenantSureManagementStatusDetailsPanel,
        TenantSureManagementActionsPanel;
        //@formatter:on
    }

    public TenantInsuranceTheme() {
        initTenantSureCommon();
        initTenantSureManagementView();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    private void initTenantSureCommon() {

        Style style;

        style = new Style(".", StyleName.TenantSureLogo.name());
        addStyle(style);

        style = new Style(".", StyleName.TenantSureLogoPhone.name());
        style.addProperty("width", "100%");
        style.addProperty("display", "block");
        style.addProperty("text-align", "center");
        addStyle(style);

    }

    private void initTenantSureManagementView() {
        Style style;

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

        style = new Style(".", StyleName.TenantSureManagementContentPanel.name());
        style.addProperty("margin-top", "50px");
        style.addProperty("margin-bottom", "50px");
        style.addProperty("width", "100%");
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureManagementStatusDetailsPanel.name());
        style.addProperty("display", "inline-block");
        style.addProperty("width", "50%");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureManagementActionsPanel.name());
        style.addProperty("display", "inline-block");
        style.addProperty("width", "50%");
        style.addProperty("vertical-align", "middle");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureManagementActionsPanel.name(), " ", ".Anchor");
        style.addProperty("display", "block");
        style.addProperty("float", "none");
        style.addProperty("margin", "0px");
        style.addProperty("padding", "0px");
        addStyle(style);

    }

}
