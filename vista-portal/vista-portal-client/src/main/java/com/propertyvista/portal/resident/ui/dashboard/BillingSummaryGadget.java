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
package com.propertyvista.portal.resident.ui.dashboard;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.BillingSummaryDTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;
import com.propertyvista.shared.config.VistaFeatures;

public class BillingSummaryGadget extends AbstractGadget<MainDashboardViewImpl> {

    private static final I18n i18n = I18n.get(BillingSummaryGadget.class);

    private final BillingViewer billingViewer;

    private final Button paymentButton = new Button("Make a Payment");

    private final Button autoPayButton = new Button("Setup Auto Pay");

    BillingSummaryGadget(MainDashboardViewImpl viewer) {
        super(viewer, PortalImages.INSTANCE.billingIcon(), i18n.tr("My Billing Summary"), ThemeColor.contrast4, 1);
        setActionsToolbar(new BillingToolbar());

        billingViewer = new BillingViewer();
        billingViewer.setViewable(true);
        billingViewer.initContent();

        setContent(billingViewer);
        setNavigationBar(new NavigationBar());
    }

    protected void populate(BillingSummaryDTO value) {
        billingViewer.populate(value);

        autoPayButton.setVisible(!value.leaseStatus().getValue().isNoAutoPay()
                && SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.forAutoPay()));
        paymentButton.setVisible(SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.values()));
    }

    class BillingToolbar extends Toolbar {
        public BillingToolbar() {
            paymentButton.setCommand(new Command() {
                @Override
                public void execute() {
                    getGadgetView().getPresenter().payNow();
                }
            });
            paymentButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));
            addItem(paymentButton);

            autoPayButton.setCommand(new Command() {
                @Override
                public void execute() {
                    getGadgetView().getPresenter().setAutopay();
                }
            });
            autoPayButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 0.8));
            addItem(autoPayButton);
        }
    }

    class NavigationBar extends FlowPanel {
        public NavigationBar() {
            if (!VistaFeatures.instance().yardiIntegration()) {

                Anchor viewBillAnchor = new Anchor("View my Current Bill", new Command() {

                    @Override
                    public void execute() {
                        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial.BillingHistory.BillView());
                    }
                });
                add(viewBillAnchor);
            }
        }
    }

    class BillingViewer extends CEntityForm<BillingSummaryDTO> {

        public BillingViewer() {
            super(BillingSummaryDTO.class);
        }

        @Override
        public IsWidget createContent() {

            BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
            mainPanel.setWidget(0, 0, new FormWidgetDecoratorBuilder(inject(proto().currentBalance()), "140px", "100px", "120px").build());
            if (!VistaFeatures.instance().yardiIntegration()) {
                mainPanel.setWidget(1, 0, new FormWidgetDecoratorBuilder(inject(proto().dueDate()), "140px", "100px", "120px").build());
            }

            return mainPanel;
        }
    }

}
