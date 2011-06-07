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
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.client.ui.dashboard.DashboardViewImpl;
import com.propertyvista.crm.client.ui.editors.AccountView;
import com.propertyvista.crm.client.ui.editors.AccountViewImpl;
import com.propertyvista.crm.client.ui.editors.ApplicationEditorView;
import com.propertyvista.crm.client.ui.editors.ApplicationEditorViewImpl;
import com.propertyvista.crm.client.ui.editors.BoilerEditorView;
import com.propertyvista.crm.client.ui.editors.BoilerEditorViewImpl;
import com.propertyvista.crm.client.ui.editors.BuildingEditorView;
import com.propertyvista.crm.client.ui.editors.BuildingEditorViewImpl;
import com.propertyvista.crm.client.ui.editors.ConcessionEditorView;
import com.propertyvista.crm.client.ui.editors.ConcessionEditorViewImpl;
import com.propertyvista.crm.client.ui.editors.ElevatorEditorView;
import com.propertyvista.crm.client.ui.editors.ElevatorEditorViewImpl;
import com.propertyvista.crm.client.ui.editors.InquiryEditorView;
import com.propertyvista.crm.client.ui.editors.InquiryEditorViewImpl;
import com.propertyvista.crm.client.ui.editors.LeaseEditorView;
import com.propertyvista.crm.client.ui.editors.LeaseEditorViewImpl;
import com.propertyvista.crm.client.ui.editors.LockerAreaEditorView;
import com.propertyvista.crm.client.ui.editors.LockerAreaEditorViewImpl;
import com.propertyvista.crm.client.ui.editors.LockerEditorView;
import com.propertyvista.crm.client.ui.editors.LockerEditorViewImpl;
import com.propertyvista.crm.client.ui.editors.ParkingEditorView;
import com.propertyvista.crm.client.ui.editors.ParkingEditorViewImpl;
import com.propertyvista.crm.client.ui.editors.ParkingSpotEditorView;
import com.propertyvista.crm.client.ui.editors.ParkingSpotEditorViewImpl;
import com.propertyvista.crm.client.ui.editors.RoofEditorView;
import com.propertyvista.crm.client.ui.editors.RoofEditorViewImpl;
import com.propertyvista.crm.client.ui.editors.TenantEditorView;
import com.propertyvista.crm.client.ui.editors.TenantEditorViewImpl;
import com.propertyvista.crm.client.ui.editors.UnitEditorView;
import com.propertyvista.crm.client.ui.editors.UnitEditorViewImpl;
import com.propertyvista.crm.client.ui.editors.UnitItemEditorView;
import com.propertyvista.crm.client.ui.editors.UnitItemEditorViewImpl;
import com.propertyvista.crm.client.ui.editors.UnitOccupancyEditorView;
import com.propertyvista.crm.client.ui.editors.UnitOccupancyEditorViewImpl;
import com.propertyvista.crm.client.ui.listers.ApplicationListerView;
import com.propertyvista.crm.client.ui.listers.ApplicationListerViewImpl;
import com.propertyvista.crm.client.ui.listers.ArrearsListerView;
import com.propertyvista.crm.client.ui.listers.ArrearsListerViewImpl;
import com.propertyvista.crm.client.ui.listers.BuildingListerView;
import com.propertyvista.crm.client.ui.listers.BuildingListerViewImpl;
import com.propertyvista.crm.client.ui.listers.InquiryListerView;
import com.propertyvista.crm.client.ui.listers.InquiryListerViewImpl;
import com.propertyvista.crm.client.ui.listers.LeaseListerView;
import com.propertyvista.crm.client.ui.listers.LeaseListerViewImpl;
import com.propertyvista.crm.client.ui.listers.LockerListerView;
import com.propertyvista.crm.client.ui.listers.LockerListerViewImpl;
import com.propertyvista.crm.client.ui.listers.ParkingSpotListerView;
import com.propertyvista.crm.client.ui.listers.ParkingSpotListerViewImpl;
import com.propertyvista.crm.client.ui.listers.TenantListerView;
import com.propertyvista.crm.client.ui.listers.TenantListerViewImpl;
import com.propertyvista.crm.client.ui.listers.UnitListerView;
import com.propertyvista.crm.client.ui.listers.UnitListerViewImpl;
import com.propertyvista.crm.client.ui.login.LoginView;
import com.propertyvista.crm.client.ui.login.LoginViewImpl;
import com.propertyvista.crm.client.ui.login.NewPasswordView;
import com.propertyvista.crm.client.ui.login.NewPasswordViewImpl;
import com.propertyvista.crm.client.ui.login.RetrievePasswordView;
import com.propertyvista.crm.client.ui.login.RetrievePasswordViewImpl;
import com.propertyvista.crm.client.ui.report.ReportView;
import com.propertyvista.crm.client.ui.report.ReportViewImpl;
import com.propertyvista.crm.client.ui.settings.ContentView;
import com.propertyvista.crm.client.ui.settings.ContentViewImpl;
import com.propertyvista.crm.client.ui.vewers.ApplicationViewerView;
import com.propertyvista.crm.client.ui.vewers.ApplicationViewerViewImpl;
import com.propertyvista.crm.client.ui.vewers.BoilerViewerView;
import com.propertyvista.crm.client.ui.vewers.BoilerViewerViewImpl;
import com.propertyvista.crm.client.ui.vewers.BuildingViewerView;
import com.propertyvista.crm.client.ui.vewers.BuildingViewerViewImpl;
import com.propertyvista.crm.client.ui.vewers.ConcessionViewerView;
import com.propertyvista.crm.client.ui.vewers.ConcessionViewerViewImpl;
import com.propertyvista.crm.client.ui.vewers.ElevatorViewerView;
import com.propertyvista.crm.client.ui.vewers.ElevatorViewerViewImpl;
import com.propertyvista.crm.client.ui.vewers.InquiryViewerView;
import com.propertyvista.crm.client.ui.vewers.InquiryViewerViewImpl;
import com.propertyvista.crm.client.ui.vewers.LeaseViewerView;
import com.propertyvista.crm.client.ui.vewers.LeaseViewerViewImpl;
import com.propertyvista.crm.client.ui.vewers.LockerAreaViewerView;
import com.propertyvista.crm.client.ui.vewers.LockerAreaViewerViewImpl;
import com.propertyvista.crm.client.ui.vewers.LockerViewerView;
import com.propertyvista.crm.client.ui.vewers.LockerViewerViewImpl;
import com.propertyvista.crm.client.ui.vewers.ParkingSpotViewerView;
import com.propertyvista.crm.client.ui.vewers.ParkingSpotViewerViewImpl;
import com.propertyvista.crm.client.ui.vewers.ParkingViewerView;
import com.propertyvista.crm.client.ui.vewers.ParkingViewerViewImpl;
import com.propertyvista.crm.client.ui.vewers.RoofViewerView;
import com.propertyvista.crm.client.ui.vewers.RoofViewerViewImpl;
import com.propertyvista.crm.client.ui.vewers.TenantViewerView;
import com.propertyvista.crm.client.ui.vewers.TenantViewerViewImpl;
import com.propertyvista.crm.client.ui.vewers.UnitItemViewerView;
import com.propertyvista.crm.client.ui.vewers.UnitItemViewerViewImpl;
import com.propertyvista.crm.client.ui.vewers.UnitOccupancyViewerView;
import com.propertyvista.crm.client.ui.vewers.UnitOccupancyViewerViewImpl;
import com.propertyvista.crm.client.ui.vewers.UnitViewerView;
import com.propertyvista.crm.client.ui.vewers.UnitViewerViewImpl;

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
        bind(UnitListerView.class).to(UnitListerViewImpl.class).in(Singleton.class);
        bind(UnitViewerView.class).to(UnitViewerViewImpl.class).in(Singleton.class);
        bind(UnitEditorView.class).to(UnitEditorViewImpl.class).in(Singleton.class);

        bind(UnitItemViewerView.class).to(UnitItemViewerViewImpl.class).in(Singleton.class);
        bind(UnitItemEditorView.class).to(UnitItemEditorViewImpl.class).in(Singleton.class);

        bind(UnitOccupancyViewerView.class).to(UnitOccupancyViewerViewImpl.class).in(Singleton.class);
        bind(UnitOccupancyEditorView.class).to(UnitOccupancyEditorViewImpl.class).in(Singleton.class);

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
        bind(ArrearsListerView.class).to(ArrearsListerViewImpl.class).in(Singleton.class);

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

// - Settings:
        bind(ContentView.class).to(ContentViewImpl.class).in(Singleton.class);
    }
}
