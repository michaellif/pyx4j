/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;

import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureLogo;

public class TenantSureManagementViewImpl extends ResizeComposite implements TenantSureManagementlView {

    public TenantSureManagementViewImpl() {
        LayoutPanel viewPanel = new LayoutPanel();

        final int PADDING = 20;
        TenantSureLogo tenantSureLogo = new TenantSureLogo();
        viewPanel.add(tenantSureLogo);
        viewPanel.setWidgetLeftWidth(tenantSureLogo, PADDING, Unit.PX, 10, Unit.EM);
        viewPanel.setWidgetTopHeight(tenantSureLogo, PADDING, Unit.PX, 8, Unit.EM);

        Label message = new Label(//@formatter:off
                "TenantSure is a Licensed Broker. Below please find your TenantSure insurance details. "
                + "If you have any claims, you can reach TenanSure's claim department at 1-888-1234-444"
        );//@formatter:on

        message.getElement().getStyle().setHeight(8, Unit.EM);
        message.getElement().getStyle().setProperty("display", "table-cell");
        message.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        SimplePanel messageHolder = new SimplePanel();
        messageHolder.setWidget(message);

        viewPanel.add(messageHolder);
        viewPanel.setWidgetLeftRight(messageHolder, 10.5, Unit.EM, PADDING, Unit.PX);
        viewPanel.setWidgetTopHeight(messageHolder, PADDING, Unit.PX, 8, Unit.EM);

        initWidget(viewPanel);
    }
}
