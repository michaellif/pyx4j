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
package com.propertyvista.portal.resident.ui.financial.dashboard;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.BillingSummaryDTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;
import com.propertyvista.portal.shared.ui.PortalFormPanel;
import com.propertyvista.shared.config.VistaFeatures;

public class BillingSummaryGadget extends AbstractGadget<FinancialDashboardViewImpl> {

    private static final I18n i18n = I18n.get(BillingSummaryGadget.class);

    private final BillingView view;

    private final Button paymentButton = new Button("Make a Payment");

    BillingSummaryGadget(FinancialDashboardViewImpl viewer) {
        super(viewer, PortalImages.INSTANCE.billingIcon(), i18n.tr("My Billing Summary"), ThemeColor.contrast4, 1);
        view = new BillingView();
        view.setViewable(true);
        view.init();

        asWidget().setWidth("100%");
        setContent(view);

        setActionsToolbar(new BillingToolbar());
        setNavigationBar(new NavigationBar());

    }

    protected void populate(BillingSummaryDTO value) {
        view.populate(value);

        paymentButton.setVisible(SecurityController.check(VistaCustomerPaymentTypeBehavior.values()));
    }

    class BillingToolbar extends GadgetToolbar {
        public BillingToolbar() {
            paymentButton.setCommand(new Command() {
                @Override
                public void execute() {
                    getGadgetView().getPresenter().makePayment();
                }
            });
            paymentButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));
            addItem(paymentButton);
        }
    }

    class NavigationBar extends FlowPanel {
        public NavigationBar() {
            if (!VistaFeatures.instance().yardiIntegration()) {

                add(new Anchor("View my Current Bill", new Command() {
                    @Override
                    public void execute() {
                        getGadgetView().getPresenter().viewCurrentBill();
                    }
                }));

                add(new Anchor("View Billing History", new Command() {
                    @Override
                    public void execute() {
                        getGadgetView().getPresenter().viewBillilngHistory();
                    }
                }));

            } else {
// Disabled in Yardi till now:
//                add(new Anchor("View Transaction History", new Command() {
//                    @Override
//                    public void execute() {
//                        getGadgetView().getPresenter().viewTransactionHistory();
//                    }
//                }));
            }
        }
    }

    class BillingView extends CForm<BillingSummaryDTO> {

        public BillingView() {
            super(BillingSummaryDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            PortalFormPanel formPanel = new PortalFormPanel(this);

            formPanel.append(Location.Left, proto().currentBalance()).decorate().componentWidth(140);
            if (!VistaFeatures.instance().yardiIntegration()) {
                formPanel.append(Location.Left, proto().dueDate()).decorate().componentWidth(140);
            }

            return formPanel;
        }
    }
}
