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
package com.propertyvista.portal.resident.ui.dashboard;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel.LayoutType;

import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.portal.rpc.portal.resident.dto.ResidentSummaryDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceSummaryDTO;
import com.propertyvista.portal.shared.themes.DashboardTheme;

public class MainDashboardViewImpl extends FlowPanel implements MainDashboardView {

    @SuppressWarnings("unused")
    private static final I18n i18n = I18n.get(MainDashboardViewImpl.class);

    private DashboardPresenter presenter;

    private final ProfileGadget profileGadget;

    private final BillingSummaryGadget billingGadget;

    private final MaintenanceGadget maintenanceGadget;

    private final InsuranceGadget insuranceGadget;

    private final OffersGadget offersGadget;

    public MainDashboardViewImpl() {

        setStyleName(DashboardTheme.StyleName.Dashboard.name());
        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        profileGadget = new ProfileGadget(this);
        profileGadget.asWidget().setWidth("100%");

        billingGadget = new BillingSummaryGadget(this);
        billingGadget.getElement().getStyle().setFloat(Float.LEFT);

        offersGadget = new OffersGadget(this);
        offersGadget.getElement().getStyle().setFloat(Float.LEFT);

        maintenanceGadget = new MaintenanceGadget(this);
        maintenanceGadget.getElement().getStyle().setFloat(Float.RIGHT);

        insuranceGadget = new InsuranceGadget(this);
        insuranceGadget.getElement().getStyle().setFloat(Float.RIGHT);

        add(profileGadget);
        if (SecurityController.checkBehavior(PortalResidentBehavior.Resident)) {
            add(billingGadget);
            add(maintenanceGadget);
            add(insuranceGadget);
            add(offersGadget);
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
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
        case tabletPortrait:
            billingGadget.asWidget().setWidth("100%");
            maintenanceGadget.asWidget().setWidth("100%");
            insuranceGadget.asWidget().setWidth("100%");
            offersGadget.asWidget().setWidth("100%");
            break;
        default:
            billingGadget.asWidget().setWidth("50%");
            maintenanceGadget.asWidget().setWidth("50%");
            insuranceGadget.asWidget().setWidth("50%");
            offersGadget.asWidget().setWidth("50%");
            break;
        }

    }

    @Override
    public void setPresenter(DashboardPresenter presenter) {
        this.presenter = presenter;
    }

    protected DashboardPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void populateProfileGadget(ResidentSummaryDTO profileSummary) {
        profileGadget.populate(profileSummary);
    }

    @Override
    public void populateBillingGadget(BillingSummaryDTO billingSummary) {
        billingGadget.populate(billingSummary);
    }

    @Override
    public void populateInsuranceGadget(InsuranceStatusDTO insuranceStatus) {
        insuranceGadget.populate(insuranceStatus);
    }

    @Override
    public void populateMaintenanceGadget(MaintenanceSummaryDTO maintenanceSummary) {
        maintenanceGadget.populate(maintenanceSummary);
    }
}
