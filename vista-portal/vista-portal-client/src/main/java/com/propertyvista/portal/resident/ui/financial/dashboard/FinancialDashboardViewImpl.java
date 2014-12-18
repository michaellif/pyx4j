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
 */
package com.propertyvista.portal.resident.ui.financial.dashboard;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.gwt.commons.layout.LayoutChangeEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeHandler;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.AutoPaySummaryDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.LatestActivitiesDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentMethodSummaryDTO;
import com.propertyvista.portal.shared.themes.DashboardTheme;

public class FinancialDashboardViewImpl extends FlowPanel implements FinancialDashboardView {

    private FinancialDashboardPresenter presenter;

    private BillingSummaryGadget billingSummarygGadget;

    private LatestActivitiesGadget latestActivitiesGadget;

    private AutoPayAgreementsGadget autoPayAgreementsGadget;

    private PaymentMethodsGadget paymentMethodsGadget;

    public FinancialDashboardViewImpl() {

        setStyleName(DashboardTheme.StyleName.Dashboard.name());

        if (SecurityController.check(PortalResidentBehavior.Resident)) {
            add(billingSummarygGadget = new BillingSummaryGadget(this));
            add(autoPayAgreementsGadget = new AutoPayAgreementsGadget(this));
            add(latestActivitiesGadget = new LatestActivitiesGadget(this));
            add(paymentMethodsGadget = new PaymentMethodsGadget(this));
        } else if (SecurityController.check(PortalResidentBehavior.Guarantor)) {
            add(new ComingSoonGadget(this));
        }

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
