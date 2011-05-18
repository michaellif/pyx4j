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
package com.propertyvista.crm.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.activity.AccountActivity;
import com.propertyvista.crm.client.activity.AlertActivity;
import com.propertyvista.crm.client.activity.ArrearsListerActivity;
import com.propertyvista.crm.client.activity.BuildingEditorActivity;
import com.propertyvista.crm.client.activity.BuildingListerActivity;
import com.propertyvista.crm.client.activity.BuildingViewerActivity;
import com.propertyvista.crm.client.activity.DashboardActivity;
import com.propertyvista.crm.client.activity.MessageActivity;
import com.propertyvista.crm.client.activity.ReportActivity;
import com.propertyvista.crm.client.activity.ResetPasswordActivity;
import com.propertyvista.crm.client.activity.SettingsActivity;
import com.propertyvista.crm.client.activity.UnitEditorActivity;
import com.propertyvista.crm.client.activity.UnitListerActivity;
import com.propertyvista.crm.client.activity.UnitViewerActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class MainActivityMapper implements ActivityMapper {

    //  Provider<LoginActivity> loginActivityProvider;

    //  Provider<RetrievePasswordActivity> retrievePasswordActivityProvider;

    Provider<ResetPasswordActivity> resetPasswordActivityProvider;

    Provider<BuildingListerActivity> buildingListerActivityProvider;

    Provider<BuildingViewerActivity> buildingViewerActivityProvider;

    Provider<BuildingEditorActivity> buildingEditorActivityProvider;

    Provider<UnitListerActivity> unitListerActivityProvider;

    Provider<UnitViewerActivity> unitViewerActivityProvider;

    Provider<UnitEditorActivity> unitEditorActivityProvider;

    Provider<ArrearsListerActivity> arrearsListerActivityProvider;

    Provider<DashboardActivity> dashboardActivityProvider;

    Provider<ReportActivity> reportActivityProvider;

    Provider<AccountActivity> accountActivityProvider;

    Provider<AlertActivity> alertActivityProvider;

    Provider<MessageActivity> messageActivityProvider;

    Provider<SettingsActivity> settingsActivityProvider;

    @Inject
    public MainActivityMapper(

/*
 * final Provider<LoginActivity> loginActivityProvider,
 * 
 * final Provider<RetrievePasswordActivity> retrievePasswordActivityProvider,
 */

    final Provider<ResetPasswordActivity> resetPasswordActivityProvider,

    final Provider<BuildingListerActivity> buildingListerActivityProvider,

    final Provider<BuildingViewerActivity> buildingViewerActivityProvider,

    final Provider<BuildingEditorActivity> buildingEditorActivityProvider,

    final Provider<UnitListerActivity> unitListerActivityProvider,

    final Provider<UnitViewerActivity> unitViewerActivityProvider,

    final Provider<UnitEditorActivity> unitEditorActivityProvider,

    final Provider<ArrearsListerActivity> arrearsListerActivityProvider,

    final Provider<DashboardActivity> dashboardActivityProvider,

    final Provider<ReportActivity> reportActivityProvider,

    final Provider<AccountActivity> accountActivityProvider,

    final Provider<AlertActivity> alertActivityProvider,

    final Provider<MessageActivity> messageActivityProvider,

    final Provider<SettingsActivity> settingsActivityProvider) {
        super();
//        this.loginActivityProvider = loginActivityProvider;
//        this.retrievePasswordActivityProvider = retrievePasswordActivityProvider;
        this.resetPasswordActivityProvider = resetPasswordActivityProvider;
        this.buildingListerActivityProvider = buildingListerActivityProvider;
        this.buildingViewerActivityProvider = buildingViewerActivityProvider;
        this.buildingEditorActivityProvider = buildingEditorActivityProvider;
        this.unitListerActivityProvider = unitListerActivityProvider;
        this.unitViewerActivityProvider = unitViewerActivityProvider;
        this.unitEditorActivityProvider = unitEditorActivityProvider;
        this.arrearsListerActivityProvider = arrearsListerActivityProvider;
        this.dashboardActivityProvider = dashboardActivityProvider;
        this.reportActivityProvider = reportActivityProvider;
        this.accountActivityProvider = accountActivityProvider;
        this.alertActivityProvider = alertActivityProvider;
        this.messageActivityProvider = messageActivityProvider;
        this.settingsActivityProvider = settingsActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {
/*
 * if (place instanceof CrmSiteMap.Login) {
 * return loginActivityProvider.get().withPlace((AppPlace) place);
 * } else if (place instanceof CrmSiteMap.RetrievePassword) {
 * return retrievePasswordActivityProvider.get().withPlace((AppPlace) place);
 * } else
 */     if (place instanceof CrmSiteMap.ResetPassword) {
            return resetPasswordActivityProvider.get().withPlace((AppPlace) place);
// Listers:      
        } else if (place instanceof CrmSiteMap.Properties.Buildings) {
            return buildingListerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Properties.Units) {
            return unitListerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Properties.Arrears) {
            return arrearsListerActivityProvider.get().withPlace(place);
// Viewers:      
        } else if (place instanceof CrmSiteMap.Viewers.Building) {
            return buildingViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Viewers.Unit) {
            return unitViewerActivityProvider.get().withPlace(place);
// Editors:      
        } else if (place instanceof CrmSiteMap.Editors.Building) {
            return buildingEditorActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.Unit) {
            return unitEditorActivityProvider.get().withPlace(place);
// Others:      
        } else if (place instanceof CrmSiteMap.Dashboard) {
            return dashboardActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Report) {
            return reportActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Account) {
            return accountActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Alert) {
            return alertActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Message) {
            return messageActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Settings) {
            return settingsActivityProvider.get().withPlace(place);
        }
        //TODO what to do on other place
        return null;
    }
}
