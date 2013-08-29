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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.rpc.portal.dto.TenantBillingSummaryDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;
import com.propertyvista.shared.config.VistaFeatures;

public class BillingSummaryGadget extends AbstractGadget<FinancialDashboardViewImpl> {

    private static final I18n i18n = I18n.get(BillingSummaryGadget.class);

    private final BillingViewer billingViewer;

    BillingSummaryGadget(FinancialDashboardViewImpl viewer) {
        super(viewer, PortalImages.INSTANCE.billingIcon(), i18n.tr("My Billing Summary"), ThemeColor.contrast4);
        setActionsToolbar(new BillingToolbar());

        billingViewer = new BillingViewer();
        billingViewer.setViewable(true);
        billingViewer.initContent();

        SimplePanel contentPanel = new SimplePanel(billingViewer.asWidget());
        contentPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

        setContent(contentPanel);

    }

    protected void populate(TenantBillingSummaryDTO value) {
        billingViewer.populate(value);
    }

    class BillingToolbar extends Toolbar {
        public BillingToolbar() {
            Button paymentButton = new Button("Make a Payment", new Command() {

                @Override
                public void execute() {
                    getGadgetViewer().getPresenter().payNow();
                }
            });
            paymentButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));
            add(paymentButton);
        }
    }

    class BillingViewer extends CEntityForm<TenantBillingSummaryDTO> {

        public BillingViewer() {
            super(TenantBillingSummaryDTO.class);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel contentPanel = new FlowPanel();

            BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
            mainPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            mainPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            mainPanel.setWidget(0, 0, new FormDecoratorBuilder(inject(proto().currentBalance()), "140px", "100px", "120px").build());
            mainPanel.setWidget(1, 0, new FormDecoratorBuilder(inject(proto().dueDate()), "140px", "100px", "120px").build());
            contentPanel.add(mainPanel);

            FlowPanel actionsPanel = new FlowPanel();
            actionsPanel.getElement().getStyle().setMarginTop(10, Unit.PX);
            actionsPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            actionsPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            contentPanel.add(actionsPanel);

            if (!VistaFeatures.instance().yardiIntegration()) {
                Anchor viewBillAnchor = new Anchor("View my Current Bill", new Command() {

                    @Override
                    public void execute() {
                        getGadgetViewer().getPresenter().viewCurrentBill();
                    }
                });
                actionsPanel.add(viewBillAnchor);
            }

            Anchor viewBillAnchor = new Anchor("View Billing History", new Command() {

                @Override
                public void execute() {
                    getGadgetViewer().getPresenter().viewBillingHistory();
                }
            });
            actionsPanel.add(viewBillAnchor);

            return contentPanel;
        }
    }
}
