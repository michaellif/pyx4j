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
package com.propertyvista.portal.web.client.ui.services.dashboard;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.InsuranceStatusDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;

public class InsuranceSummaryGadget extends AbstractGadget<ServicesDashboardViewImpl> {

    private static final I18n i18n = I18n.get(InsuranceSummaryGadget.class);

    InsuranceSummaryGadget(ServicesDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Insurance"), ThemeColor.contrast3);
        setActionsToolbar(new PaymentMethodsToolbar());

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.add(new HTML("Insurance"));

        setContent(contentPanel);
    }

    protected void populate(InsuranceStatusDTO value) {

    }

    class PaymentMethodsToolbar extends Toolbar {
        public PaymentMethodsToolbar() {
            Button tenantSureButton = new Button("Purchase Insurance", new Command() {

                @Override
                public void execute() {
                    getGadgetViewer().getPresenter().getTenantSure();
                }
            });
            tenantSureButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast3, 1));
            add(tenantSureButton);

            Button thirdPartyButton = new Button("Provide Proof of my Insurance", new Command() {

                @Override
                public void execute() {
                }
            });
            thirdPartyButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast3, 1));
            add(thirdPartyButton);
        }
    }
}
