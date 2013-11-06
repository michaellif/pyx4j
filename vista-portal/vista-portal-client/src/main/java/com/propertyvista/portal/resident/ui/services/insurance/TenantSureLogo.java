/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.services.insurance;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.portal.resident.resources.tenantsure.TenantSureResources;
import com.propertyvista.portal.resident.themes.TenantSureTheme;

public class TenantSureLogo extends Composite {

    public TenantSureLogo() {
        FlowPanel logoPanel = new FlowPanel();
        logoPanel.sinkEvents(Event.ONCLICK);
        logoPanel.setStyleName(TenantSureTheme.StyleName.TenantSureLogo.name());

        Image tenantSureLogo = new Image(TenantSureResources.INSTANCE.logoTenantSure());
        tenantSureLogo.getElement().getStyle().setProperty("display", "block");
        tenantSureLogo.getElement().getStyle().setProperty("marginLeft", "auto");
        tenantSureLogo.getElement().getStyle().setProperty("marginRight", "auto");
        logoPanel.add(tenantSureLogo);

        Image highCourtLogo = new Image(TenantSureResources.INSTANCE.logoHighcourt());
        highCourtLogo.getElement().getStyle().setProperty("display", "block");
        highCourtLogo.getElement().getStyle().setProperty("marginLeft", "auto");
        highCourtLogo.getElement().getStyle().setProperty("marginRight", "auto");
        logoPanel.add(highCourtLogo);

        Label tenantSurePhone = new Label(TenantSureConstants.TENANTSURE_PHONE_NUMBER);
        tenantSurePhone.addStyleName(TenantSureTheme.StyleName.TenantSureLogoPhone.name());
        logoPanel.add(tenantSurePhone);
        initWidget(logoPanel);
    }

}
