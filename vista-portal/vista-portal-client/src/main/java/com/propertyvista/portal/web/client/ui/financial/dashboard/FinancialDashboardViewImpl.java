/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.financial.dashboard;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;

import com.propertyvista.portal.rpc.portal.web.dto.AutoPaySummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.LatestActivitiesDTO;
import com.propertyvista.portal.rpc.portal.web.dto.PaymentMethodSummaryDTO;
import com.propertyvista.portal.web.client.themes.DashboardTheme;

public class FinancialDashboardViewImpl extends FlowPanel implements FinancialDashboardView {

    @SuppressWarnings("unused")
    private static final I18n i18n = I18n.get(FinancialDashboardViewImpl.class);

    private FinancialDashboardPresenter presenter;

    private final BillingSummaryGadget billingSummarygGadget;

    private final LatestActivitiesGadget latestActivitiesGadget;

    private final AutoPayAgreementsGadget autoPayAgreementsGadget;

    private final PaymentMethodsGadget paymentMethodsGadget;

    public FinancialDashboardViewImpl() {

        setStyleName(DashboardTheme.StyleName.Dashboard.name());

        billingSummarygGadget = new BillingSummaryGadget(this);
        billingSummarygGadget.asWidget().setWidth("100%");

        autoPayAgreementsGadget = new AutoPayAgreementsGadget(this);
        autoPayAgreementsGadget.asWidget().setWidth("100%");

        latestActivitiesGadget = new LatestActivitiesGadget(this);
        latestActivitiesGadget.asWidget().setWidth("100%");

        paymentMethodsGadget = new PaymentMethodsGadget(this);
        paymentMethodsGadget.asWidget().setWidth("100%");

        add(billingSummarygGadget);
        add(autoPayAgreementsGadget);
        add(latestActivitiesGadget);
        add(paymentMethodsGadget);

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));
        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {
            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }
        });
    }

    private void doLayout(LayoutType layoutType) {
    }

    @Override
    public void setPresenter(FinancialDashboardPresenter presenter) {
        this.presenter = presenter;
    }

    protected FinancialDashboardPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void populate(BillingSummaryDTO value) {
        billingSummarygGadget.populate(value);
    }

    @Override
    public void populate(AutoPaySummaryDTO value) {
        autoPayAgreementsGadget.populate(value);
    }

    @Override
    public void populate(PaymentMethodSummaryDTO value) {
        paymentMethodsGadget.populate(value);
    }

    @Override
    public void populate(LatestActivitiesDTO value) {
        latestActivitiesGadget.populate(value);
    }
}
