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
package com.propertyvista.portal.web.client.ui.dashboard;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;

import com.propertyvista.portal.rpc.portal.dto.TenantDashboardDTO;

public class DashboardViewImpl extends FlowPanel implements DashboardView {

    @SuppressWarnings("unused")
    private static final I18n i18n = I18n.get(DashboardViewImpl.class);

    private DashboardPresenter presenter;

    private final ProfileGadget profileGadget;

    private final BillingGadget billingGadget;

    private final MaintenanceGadget maintenanceGadget;

    private final ResidentServicesGadget residentServicesGadget;

    public DashboardViewImpl() {

        profileGadget = new ProfileGadget(this);
        profileGadget.asWidget().setWidth("100%");

        billingGadget = new BillingGadget(this);
        billingGadget.getElement().getStyle().setFloat(Float.LEFT);

        maintenanceGadget = new MaintenanceGadget(this);
        maintenanceGadget.getElement().getStyle().setFloat(Float.LEFT);

        residentServicesGadget = new ResidentServicesGadget(this);
        residentServicesGadget.getElement().getStyle().setFloat(Float.LEFT);

        add(profileGadget);
        add(billingGadget);
        add(maintenanceGadget);
        add(residentServicesGadget);

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });
    }

    private void doLayout(LayoutType layoutType) {
        profileGadget.doLayout(layoutType);
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
        case tabletPortrait:
            billingGadget.asWidget().setWidth("100%");
            maintenanceGadget.asWidget().setWidth("100%");
            residentServicesGadget.asWidget().setWidth("100%");
            break;
        default:
            billingGadget.asWidget().setWidth("50%");
            maintenanceGadget.asWidget().setWidth("50%");
            residentServicesGadget.asWidget().setWidth("50%");
            break;
        }

    }

    @Override
    public void setPresenter(DashboardPresenter presenter) {
        this.presenter = presenter;
    }

    protected DashboardView.DashboardPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void populate(TenantDashboardDTO result) {
        profileGadget.populate(result.profileInfo());
        billingGadget.populate(result.billingInfo());
        maintenanceGadget.populate(result.maintenanceInfo());
        residentServicesGadget.populate(result.residentServicesInfo());
    }

}
