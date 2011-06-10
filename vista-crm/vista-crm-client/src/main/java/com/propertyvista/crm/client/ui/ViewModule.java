/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.crm.client.ui;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

import com.pyx4j.widgets.client.style.Theme;

import com.propertyvista.crm.client.themes.BlueColdTheme;
import com.propertyvista.crm.client.ui.crud.building.BoilerEditorView;
import com.propertyvista.crm.client.ui.crud.building.BoilerEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.BoilerViewerView;
import com.propertyvista.crm.client.ui.crud.building.BoilerViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.BuildingEditorView;
import com.propertyvista.crm.client.ui.crud.building.BuildingEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.BuildingListerView;
import com.propertyvista.crm.client.ui.crud.building.BuildingListerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.BuildingViewerView;
import com.propertyvista.crm.client.ui.crud.building.BuildingViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.ElevatorEditorView;
import com.propertyvista.crm.client.ui.crud.building.ElevatorEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.ElevatorViewerView;
import com.propertyvista.crm.client.ui.crud.building.ElevatorViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.LockerAreaEditorView;
import com.propertyvista.crm.client.ui.crud.building.LockerAreaEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.LockerAreaViewerView;
import com.propertyvista.crm.client.ui.crud.building.LockerAreaViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.LockerEditorView;
import com.propertyvista.crm.client.ui.crud.building.LockerEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.LockerListerView;
import com.propertyvista.crm.client.ui.crud.building.LockerListerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.LockerViewerView;
import com.propertyvista.crm.client.ui.crud.building.LockerViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.ParkingEditorView;
import com.propertyvista.crm.client.ui.crud.building.ParkingEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.ParkingSpotEditorView;
import com.propertyvista.crm.client.ui.crud.building.ParkingSpotEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.ParkingSpotListerView;
import com.propertyvista.crm.client.ui.crud.building.ParkingSpotListerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.ParkingSpotViewerView;
import com.propertyvista.crm.client.ui.crud.building.ParkingSpotViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.ParkingViewerView;
import com.propertyvista.crm.client.ui.crud.building.ParkingViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.RoofEditorView;
import com.propertyvista.crm.client.ui.crud.building.RoofEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.RoofViewerView;
import com.propertyvista.crm.client.ui.crud.building.RoofViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.marketing.ConcessionEditorView;
import com.propertyvista.crm.client.ui.crud.marketing.ConcessionEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.marketing.ConcessionViewerView;
import com.propertyvista.crm.client.ui.crud.marketing.ConcessionViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.ApplicationEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.ApplicationEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.ApplicationListerView;
import com.propertyvista.crm.client.ui.crud.tenant.ApplicationListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.ApplicationViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.ApplicationViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.InquiryEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.InquiryEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.InquiryListerView;
import com.propertyvista.crm.client.ui.crud.tenant.InquiryListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.InquiryViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.InquiryViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.LeaseEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.LeaseEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.LeaseListerView;
import com.propertyvista.crm.client.ui.crud.tenant.LeaseListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.LeaseViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.LeaseViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.TenantEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.TenantEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.TenantListerView;
import com.propertyvista.crm.client.ui.crud.tenant.TenantListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.TenantViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.TenantViewerViewImpl;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.client.ui.dashboard.DashboardViewImpl;
import com.propertyvista.crm.client.ui.login.LoginView;
import com.propertyvista.crm.client.ui.login.LoginViewImpl;
import com.propertyvista.crm.client.ui.login.NewPasswordView;
import com.propertyvista.crm.client.ui.login.NewPasswordViewImpl;
import com.propertyvista.crm.client.ui.login.RetrievePasswordView;
import com.propertyvista.crm.client.ui.login.RetrievePasswordViewImpl;
import com.propertyvista.crm.client.ui.report.ReportView;
import com.propertyvista.crm.client.ui.report.ReportViewImpl;

public class ViewModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(CrmView.class);

        bind(LogoView.class).to(LogoViewImpl.class).in(Singleton.class);
        bind(FooterView.class).to(FooterViewImpl.class).in(Singleton.class);
        bind(NavigView.class).to(NavigViewImpl.class).in(Singleton.class);

        bind(TopRightActionsView.class).to(TopRightActionsViewImpl.class).in(Singleton.class);

        bind(LoginView.class).to(LoginViewImpl.class).in(Singleton.class);
        bind(RetrievePasswordView.class).to(RetrievePasswordViewImpl.class).in(Singleton.class);
        bind(NewPasswordView.class).to(NewPasswordViewImpl.class).in(Singleton.class);

// - Building-related:
        bind(BuildingListerView.class).to(BuildingListerViewImpl.class).in(Singleton.class);
        bind(BuildingViewerView.class).to(BuildingViewerViewImpl.class).in(Singleton.class);
        bind(BuildingEditorView.class).to(BuildingEditorViewImpl.class).in(Singleton.class);

        bind(ElevatorViewerView.class).to(ElevatorViewerViewImpl.class).in(Singleton.class);
        bind(ElevatorEditorView.class).to(ElevatorEditorViewImpl.class).in(Singleton.class);

        bind(BoilerViewerView.class).to(BoilerViewerViewImpl.class).in(Singleton.class);
        bind(BoilerEditorView.class).to(BoilerEditorViewImpl.class).in(Singleton.class);

        bind(RoofViewerView.class).to(RoofViewerViewImpl.class).in(Singleton.class);
        bind(RoofEditorView.class).to(RoofEditorViewImpl.class).in(Singleton.class);

        bind(ParkingViewerView.class).to(ParkingViewerViewImpl.class).in(Singleton.class);
        bind(ParkingEditorView.class).to(ParkingEditorViewImpl.class).in(Singleton.class);

        bind(ParkingSpotListerView.class).to(ParkingSpotListerViewImpl.class).in(Singleton.class);
        bind(ParkingSpotViewerView.class).to(ParkingSpotViewerViewImpl.class).in(Singleton.class);
        bind(ParkingSpotEditorView.class).to(ParkingSpotEditorViewImpl.class).in(Singleton.class);

        bind(LockerAreaViewerView.class).to(LockerAreaViewerViewImpl.class).in(Singleton.class);
        bind(LockerAreaEditorView.class).to(LockerAreaEditorViewImpl.class).in(Singleton.class);

        bind(LockerListerView.class).to(LockerListerViewImpl.class).in(Singleton.class);
        bind(LockerViewerView.class).to(LockerViewerViewImpl.class).in(Singleton.class);
        bind(LockerEditorView.class).to(LockerEditorViewImpl.class).in(Singleton.class);

// - Unit-related:
        bind(ConcessionViewerView.class).to(ConcessionViewerViewImpl.class).in(Singleton.class);
        bind(ConcessionEditorView.class).to(ConcessionEditorViewImpl.class).in(Singleton.class);

// - Tenant-related:
        bind(TenantListerView.class).to(TenantListerViewImpl.class).in(Singleton.class);
        bind(TenantViewerView.class).to(TenantViewerViewImpl.class).in(Singleton.class);
        bind(TenantEditorView.class).to(TenantEditorViewImpl.class).in(Singleton.class);

        bind(LeaseListerView.class).to(LeaseListerViewImpl.class).in(Singleton.class);
        bind(LeaseViewerView.class).to(LeaseViewerViewImpl.class).in(Singleton.class);
        bind(LeaseEditorView.class).to(LeaseEditorViewImpl.class).in(Singleton.class);

        bind(ApplicationListerView.class).to(ApplicationListerViewImpl.class).in(Singleton.class);
        bind(ApplicationViewerView.class).to(ApplicationViewerViewImpl.class).in(Singleton.class);
        bind(ApplicationEditorView.class).to(ApplicationEditorViewImpl.class).in(Singleton.class);

        bind(InquiryListerView.class).to(InquiryListerViewImpl.class).in(Singleton.class);
        bind(InquiryViewerView.class).to(InquiryViewerViewImpl.class).in(Singleton.class);
        bind(InquiryEditorView.class).to(InquiryEditorViewImpl.class).in(Singleton.class);

// - Other:
        bind(ReportView.class).to(ReportViewImpl.class).in(Singleton.class);
        bind(DashboardView.class).to(DashboardViewImpl.class).in(Singleton.class);

// - Themes:
        //        bind(Theme.class).to(GainsboroTheme.class).in(Singleton.class);
        bind(Theme.class).to(BlueColdTheme.class).in(Singleton.class);
        //        bind(Theme.class).to(BownWarmTheme.class).in(Singleton.class);

        bind(ShortCutsView.class).to(ShortCutsViewImpl.class).in(Singleton.class);

        bind(AccountView.class).to(AccountViewImpl.class).in(Singleton.class);
        bind(AlertView.class).to(AlertViewImpl.class).in(Singleton.class);
        bind(MessageView.class).to(MessageViewImpl.class).in(Singleton.class);
    }
}
