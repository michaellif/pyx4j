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
package com.propertyvista.portal.web.client.ui.residents.dashboard;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.ui.CEntityViewer;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.rpc.portal.dto.TenantBillingDTO;
import com.propertyvista.portal.rpc.portal.dto.TenantDashboardDTO;
import com.propertyvista.portal.rpc.portal.dto.TenantMaintenanceDTO;
import com.propertyvista.portal.rpc.portal.dto.TenantProfileDTO;
import com.propertyvista.portal.rpc.portal.dto.TenantResidentServicesDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.themes.DashboardTheme;

public class DashboardForm_New extends CEntityDecoratableForm<TenantDashboardDTO> {

    private static final I18n i18n = I18n.get(DashboardForm_New.class);

    public static final String NoRecordsFound = i18n.tr("No Records Found");

    private DashboardView.Presenter presenter;

    private ProfileGadget profileGadget;

    private BillingGadget billingGadget;

    private MaintenanceGadget maintenanceGadget;

    private ResidentServicesGadget residentServicesGadget;

    public DashboardForm_New() {
        super(TenantDashboardDTO.class, new VistaViewersComponentFactory());

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
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
        case tabletPortrait:
            billingGadget.setWidth("100%");
            maintenanceGadget.setWidth("100%");
            break;

        default:
            billingGadget.setWidth("50%");
            maintenanceGadget.setWidth("50%");
            break;
        }

    }

    public void setPresenter(DashboardView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE_BLOCK);

        profileGadget = new ProfileGadget();
        inject(proto().profileInfo(), profileGadget);
        profileGadget.setWidth("100%");

        billingGadget = new BillingGadget();
        inject(proto().billingInfo(), billingGadget);
        billingGadget.setWidth("50%");

        maintenanceGadget = new MaintenanceGadget();
        inject(proto().maintenanceInfo(), maintenanceGadget);
        maintenanceGadget.setWidth("50%");

        residentServicesGadget = new ResidentServicesGadget();
        inject(proto().residentServicesInfo(), residentServicesGadget);
        residentServicesGadget.setWidth("100%");

        contentPanel.add(profileGadget);
        contentPanel.add(billingGadget);
        contentPanel.add(maintenanceGadget);
        contentPanel.add(residentServicesGadget);

        return contentPanel;
    }

    class ProfileGadget extends CEntityViewer<TenantProfileDTO> {

        ProfileGadget() {
            asWidget().setStyleName(DashboardTheme.StyleName.Gadget.name());
        }

        @Override
        public IsWidget createContent(TenantProfileDTO value) {

            FlowPanel content = new FlowPanel();
            content.setStyleName(DashboardTheme.StyleName.GadgetContent.name());

            content.add(new HTML("Welcome " + value.tenantName().getValue()));
            content.add(new HTML(value.floorplanName().getValue()));
            content.add(new HTML(value.tenantAddress().getValue()));

            SimplePanel container = new SimplePanel(content);
            container.setStyleName(DashboardTheme.StyleName.GadgetContainer.name());
            return container;
        }
    }

    class BillingGadget extends CEntityViewer<TenantBillingDTO> {

        BillingGadget() {
            asWidget().setStyleName(DashboardTheme.StyleName.Gadget.name());
        }

        @Override
        public IsWidget createContent(TenantBillingDTO value) {

            FlowPanel content = new FlowPanel();
            content.setStyleName(DashboardTheme.StyleName.GadgetContent.name());

            content.add(new Image(PortalImages.INSTANCE.billingMenu().regular()));
            content.add(new HTML("My Billing Summary"));
            content.add(new HTML(
                    "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."));

            SimplePanel container = new SimplePanel(content);
            container.setStyleName(DashboardTheme.StyleName.GadgetContainer.name());
            return container;
        }
    }

    class MaintenanceGadget extends CEntityViewer<TenantMaintenanceDTO> {

        MaintenanceGadget() {
            asWidget().setStyleName(DashboardTheme.StyleName.Gadget.name());
        }

        @Override
        public IsWidget createContent(TenantMaintenanceDTO value) {

            FlowPanel content = new FlowPanel();
            content.setStyleName(DashboardTheme.StyleName.GadgetContent.name());

            content.add(new Image(PortalImages.INSTANCE.maintenanceMenu().regular()));
            content.add(new HTML("My Maintenance Requests"));
            content.add(new HTML(
                    "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."));

            SimplePanel container = new SimplePanel(content);
            container.setStyleName(DashboardTheme.StyleName.GadgetContainer.name());
            return container;
        }
    }

    class ResidentServicesGadget extends CEntityViewer<TenantResidentServicesDTO> {

        ResidentServicesGadget() {
            asWidget().setStyleName(DashboardTheme.StyleName.Gadget.name());
        }

        @Override
        public IsWidget createContent(TenantResidentServicesDTO value) {
            FlowPanel content = new FlowPanel();
            content.setStyleName(DashboardTheme.StyleName.GadgetContent.name());

            content.add(new Image(PortalImages.INSTANCE.residentServicesMenu().regular()));
            content.add(new HTML("Resident Services"));
            content.add(new HTML(
                    "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."));

            SimplePanel container = new SimplePanel(content);
            container.setStyleName(DashboardTheme.StyleName.GadgetContainer.name());
            return container;
        }
    }
}
