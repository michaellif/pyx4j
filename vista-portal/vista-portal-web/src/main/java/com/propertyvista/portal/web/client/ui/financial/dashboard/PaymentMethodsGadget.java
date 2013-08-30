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
package com.propertyvista.portal.web.client.ui.financial.dashboard;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.rpc.portal.web.dto.ResidentServicesDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;

public class PaymentMethodsGadget extends AbstractGadget<FinancialDashboardViewImpl> {

    private static final I18n i18n = I18n.get(PaymentMethodsGadget.class);

    PaymentMethodsGadget(FinancialDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.billingIcon(), i18n.tr("Payment Methods"), ThemeColor.contrast4);
        setActionsToolbar(new PaymentMethodsToolbar());

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.add(new HTML("Payment Methods"));

        setContent(contentPanel);
    }

    protected void populate(ResidentServicesDTO value) {

    }

    class PaymentMethodsToolbar extends Toolbar {
        public PaymentMethodsToolbar() {
            Button autoPayButton = new Button("Add Payment Method", new Command() {

                @Override
                public void execute() {
                    getGadgetViewer().getPresenter().addPaymentMethod();
                }
            });
            autoPayButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));
            add(autoPayButton);
        }
    }
}
