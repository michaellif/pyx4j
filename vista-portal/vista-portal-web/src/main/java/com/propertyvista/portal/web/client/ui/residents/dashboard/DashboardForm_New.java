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
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.rpc.portal.dto.TenantBillingDTO;
import com.propertyvista.portal.rpc.portal.dto.TenantDashboardDTO;
import com.propertyvista.portal.rpc.portal.dto.TenantMaintenanceDTO;
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
        profileGadget.doLayout(layoutType);
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

    class BillingGadget extends AbstractGadget<TenantBillingDTO> {

        BillingGadget() {
            super(PortalImages.INSTANCE.billingMenu().regular(), "My Billing Summary", new BillingToolbar(), ThemeColor.contrast4);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel billingBlock = new FlowPanel();
            billingBlock.setStyleName(DashboardTheme.StyleName.GadgetBlock.name());
            billingBlock
                    .add(new HTML(
                            "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."));
            return billingBlock;
        }

        @Override
        protected void setComponentsValue(TenantBillingDTO value, boolean fireEvent, boolean populate) {
        }

    }

    class BillingToolbar extends Toolbar {
        public BillingToolbar() {
            Button paymentButton = new Button("Make a Payment");
            paymentButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));
            add(paymentButton);

            Button autoPayButton = new Button("Setup Auto Pay");
            autoPayButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 0.6));
            add(autoPayButton);
        }
    }

    class MaintenanceGadget extends AbstractGadget<TenantMaintenanceDTO> {

        MaintenanceGadget() {
            super(PortalImages.INSTANCE.maintenanceMenu().regular(), "My Maintenance Requests", new MaintenanceToolbar(), ThemeColor.contrast5);
        }

        @Override
        public IsWidget createContent() {

            FlowPanel maintenanceBlock = new FlowPanel();
            maintenanceBlock.setStyleName(DashboardTheme.StyleName.GadgetBlock.name());

            maintenanceBlock
                    .add(new HTML(
                            "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."));
            return maintenanceBlock;
        }

        @Override
        protected void setComponentsValue(TenantMaintenanceDTO value, boolean fireEvent, boolean populate) {
            // TODO Auto-generated method stub

        }
    }

    class MaintenanceToolbar extends Toolbar {
        public MaintenanceToolbar() {

            Button createButton = new Button("Create New Request");
            createButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast5, 1));
            add(createButton);

        }
    }

    class ResidentServicesGadget extends AbstractGadget<TenantResidentServicesDTO> {

        ResidentServicesGadget() {
            super(PortalImages.INSTANCE.residentServicesMenu().regular(), "Resident Services", new ResidentServicesToolbar(), ThemeColor.contrast3);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel servicesBlock = new FlowPanel();
            servicesBlock.setStyleName(DashboardTheme.StyleName.GadgetBlock.name());

            servicesBlock
                    .add(new HTML(
                            "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."));
            return servicesBlock;
        }

        @Override
        protected void setComponentsValue(TenantResidentServicesDTO value, boolean fireEvent, boolean populate) {
            // TODO Auto-generated method stub

        }
    }

    class ResidentServicesToolbar extends Toolbar {
        public ResidentServicesToolbar() {

            Button purchaseButton = new Button("Purchase Insurance");
            purchaseButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast3, 1));
            add(purchaseButton);

            Button proofButton = new Button("Provide Proof of my Insurance");
            proofButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast3, 0.6));
            add(proofButton);

        }
    }
}
