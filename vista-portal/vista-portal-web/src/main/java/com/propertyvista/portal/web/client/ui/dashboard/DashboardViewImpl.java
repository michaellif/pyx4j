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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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

    private final ProfileGadget profileGadget;

    private final BillingGadget billingGadget;

    private final MaintenanceGadget maintenanceGadget;

    private final ResidentServicesGadget residentServicesGadget;

    private DashboardPresenter presenter;

    public DashboardViewImpl() {

        getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE_BLOCK);

        profileGadget = new ProfileGadget(this);
        profileGadget.asWidget().setWidth("100%");

        billingGadget = new BillingGadget(this);
        billingGadget.asWidget().setWidth("50%");

        maintenanceGadget = new MaintenanceGadget(this);
        maintenanceGadget.asWidget().setWidth("50%");

        residentServicesGadget = new ResidentServicesGadget(this);
        residentServicesGadget.asWidget().setWidth("100%");

        add(profileGadget);
        add(billingGadget);
        add(maintenanceGadget);
        add(residentServicesGadget);

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                try {
                    doLayout(LayoutType.getLayoutType(Window.getClientWidth()));
                } catch (Throwable e) {
                }
            }
        });

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
            break;

        default:
            billingGadget.asWidget().setWidth("50%");
            maintenanceGadget.asWidget().setWidth("50%");
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
