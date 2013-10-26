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
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.rpc.portal.web.dto.financial.BillingSummaryDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;
import com.propertyvista.portal.web.client.ui.util.decorators.FormWidgetDecoratorBuilder;
import com.propertyvista.shared.config.VistaFeatures;

public class BillingSummaryGadget extends AbstractGadget<FinancialDashboardViewImpl> {

    private static final I18n i18n = I18n.get(BillingSummaryGadget.class);

    private final BillingView view;

    BillingSummaryGadget(FinancialDashboardViewImpl viewer) {
        super(viewer, PortalImages.INSTANCE.billingIcon(), i18n.tr("My Billing Summary"), ThemeColor.contrast4, 1);

        view = new BillingView();
        view.setViewable(true);
        view.initContent();

        setContent(view);

        setActionsToolbar(new BillingToolbar());
        setNavigationBar(new NavigationBar());

    }

    protected void populate(BillingSummaryDTO value) {
        view.populate(value);
    }

    class BillingToolbar extends Toolbar {
        public BillingToolbar() {

            Button paymentButton = new Button("Make a Payment", new Command() {

                @Override
                public void execute() {
                    getGadgetView().getPresenter().makePayment();
                }
            });
            paymentButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));
            add(paymentButton);

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

    class BillingView extends CEntityForm<BillingSummaryDTO> {

        private final BasicFlexFormPanel mainPanel;

        public BillingView() {
            super(BillingSummaryDTO.class);

            mainPanel = new BasicFlexFormPanel();

        }

        @Override
        public IsWidget createContent() {

            mainPanel.setWidget(0, 0, new FormWidgetDecoratorBuilder(inject(proto().currentBalance()), 140).build());
            if (!VistaFeatures.instance().yardiIntegration()) {
                mainPanel.setWidget(1, 0, new FormWidgetDecoratorBuilder(inject(proto().dueDate()), 140).build());
            }

            return mainPanel;
        }
    }
}
