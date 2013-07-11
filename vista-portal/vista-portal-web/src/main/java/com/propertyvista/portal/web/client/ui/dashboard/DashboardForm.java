/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.dashboard;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;

import com.propertyvista.portal.rpc.portal.dto.TenantDashboardDTO;
import com.propertyvista.portal.web.client.ui.GadgetViewer;
import com.propertyvista.portal.web.client.ui.dashboard.DashboardView.DashboardPresenter;

public class DashboardForm extends GadgetViewer<TenantDashboardDTO> {

    private static final I18n i18n = I18n.get(DashboardForm.class);

    public static final String NoRecordsFound = i18n.tr("No Records Found");

    private ProfileGadget profileGadget;

    private BillingGadget billingGadget;

    private MaintenanceGadget maintenanceGadget;

    private ResidentServicesGadget residentServicesGadget;

    private final DashboardViewImpl view;

    public DashboardForm(DashboardViewImpl view) {
        super(TenantDashboardDTO.class);
        this.view = view;

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

    @Override
    public IsWidget createContent() {

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE_BLOCK);

        profileGadget = new ProfileGadget(this);
        inject(proto().profileInfo(), profileGadget);
        profileGadget.asWidget().setWidth("100%");

        billingGadget = new BillingGadget(this);
        inject(proto().billingInfo(), billingGadget);
        billingGadget.asWidget().setWidth("50%");

        maintenanceGadget = new MaintenanceGadget(this);
        inject(proto().maintenanceInfo(), maintenanceGadget);
        maintenanceGadget.asWidget().setWidth("50%");

        residentServicesGadget = new ResidentServicesGadget(this);
        inject(proto().residentServicesInfo(), residentServicesGadget);
        residentServicesGadget.asWidget().setWidth("100%");

        contentPanel.add(profileGadget);
        contentPanel.add(billingGadget);
        contentPanel.add(maintenanceGadget);
        contentPanel.add(residentServicesGadget);

        return contentPanel;
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

    protected DashboardView.DashboardPresenter getPresenter() {
        return (DashboardPresenter) view.getPresenter();
    }
}
