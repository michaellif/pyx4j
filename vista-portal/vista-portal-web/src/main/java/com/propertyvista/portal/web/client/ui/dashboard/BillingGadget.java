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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.rpc.portal.dto.TenantBillingDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;

public class BillingGadget extends AbstractGadget<TenantBillingDTO> {

    BillingGadget(DashboardForm_New form) {
        super(form, PortalImages.INSTANCE.billingIcon(), "My Billing Summary", ThemeColor.contrast4);
        setActionsToolbar(new BillingToolbar());
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
    protected void setComponentsValue(TenantBillingDTO value, boolean fireEvent, boolean populate) {
    }

    class BillingToolbar extends Toolbar {
        public BillingToolbar() {
            Button paymentButton = new Button("Make a Payment", new Command() {

                @Override
                public void execute() {
                    getForm().getPresenter().payNow();
                }
            });
            paymentButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));
            add(paymentButton);

            Button autoPayButton = new Button("Setup Auto Pay", new Command() {

                @Override
                public void execute() {
                    getForm().getPresenter().setAutopay();
                }
            });
            autoPayButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 0.6));
            add(autoPayButton);
        }
    }
}
