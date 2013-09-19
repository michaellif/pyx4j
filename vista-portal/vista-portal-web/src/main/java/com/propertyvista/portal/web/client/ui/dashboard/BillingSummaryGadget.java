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
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.rpc.portal.web.dto.BillingSummaryDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;
import com.propertyvista.shared.config.VistaFeatures;

public class BillingSummaryGadget extends AbstractGadget<MainDashboardViewImpl> {

    private static final I18n i18n = I18n.get(BillingSummaryGadget.class);

    private final BillingViewer billingViewer;

    BillingSummaryGadget(MainDashboardViewImpl viewer) {
        super(viewer, PortalImages.INSTANCE.billingIcon(), i18n.tr("My Billing Summary"), ThemeColor.contrast4);
        setActionsToolbar(new BillingToolbar());

        billingViewer = new BillingViewer();
        billingViewer.setViewable(true);
        billingViewer.initContent();

        setContent(billingViewer);
        setNavigationBar(new NavigationBar());

    }

    protected void populate(BillingSummaryDTO value) {
        billingViewer.populate(value);
    }

    class BillingToolbar extends Toolbar {
        public BillingToolbar() {
            Button paymentButton = new Button("Make a Payment", new Command() {

                @Override
                public void execute() {
                    getGadgetView().getPresenter().payNow();
                }
            });
            paymentButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));
            add(paymentButton);

            Button autoPayButton = new Button("Setup Auto Pay", new Command() {

                @Override
                public void execute() {
                    getGadgetView().getPresenter().setAutopay();
                }
            });
            autoPayButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 0.8));
            add(autoPayButton);
        }
    }

    class NavigationBar extends FlowPanel {
        public NavigationBar() {
            if (!VistaFeatures.instance().yardiIntegration()) {

                Anchor viewBillAnchor = new Anchor("View my Current Bill", new Command() {

                    @Override
                    public void execute() {
                        getGadgetView().getPresenter().viewCurrentBill();
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
            mainPanel.setWidget(0, 0, new FormDecoratorBuilder(inject(proto().currentBalance()), "140px", "100px", "120px").build());
            mainPanel.setWidget(1, 0, new FormDecoratorBuilder(inject(proto().dueDate()), "140px", "100px", "120px").build());

            return mainPanel;
        }
    }

}
