/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.dashboard;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.rpc.portal.dto.TenantResidentServicesDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;

public class ResidentServicesGadget extends AbstractGadget<TenantResidentServicesDTO, DashboardForm> {

    ResidentServicesGadget(DashboardForm form) {
        super(form, PortalImages.INSTANCE.residentServicesIcon(), "Resident Services", ThemeColor.contrast3);
        setActionsToolbar(new ResidentServicesToolbar());
    }

    @Override
    public IsWidget createContent() {
        FlowPanel contentPanel = new FlowPanel();
        contentPanel
                .add(new HTML(
                        "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."));
        return contentPanel;
    }

    @Override
    protected void setComponentsValue(TenantResidentServicesDTO value, boolean fireEvent, boolean populate) {
        // TODO Auto-generated method stub

    }

    class ResidentServicesToolbar extends Toolbar {
        public ResidentServicesToolbar() {

            Button purchaseButton = new Button("Purchase Insurance");
            purchaseButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast3, 1));
            add(purchaseButton);

            Button proofButton = new Button("Provide Proof of my Insurance");
            proofButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast3, 0.6));
            add(proofButton);

        }
    }

}
