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
import com.propertyvista.crm.client.activity.DashboardActivity;
import com.propertyvista.crm.client.activity.LoginActivity;
import com.propertyvista.crm.client.activity.ReportActivity;
import com.propertyvista.crm.client.activity.ResetPasswordActivity;
import com.propertyvista.crm.client.activity.RetrievePasswordActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;

import com.pyx4j.site.rpc.AppPlace;

public class MainActivityMapper implements ActivityMapper {

    Provider<LoginActivity> loginActivityProvider;

    Provider<RetrievePasswordActivity> retrievePasswordActivityProvider;

    Provider<ResetPasswordActivity> resetPasswordActivityProvider;

    Provider<DashboardActivity> dashboardActivityProvider;

    Provider<ReportActivity> reportActivityProvider;

    @Inject
    public MainActivityMapper(

    final Provider<LoginActivity> loginActivityProvider,

    final Provider<RetrievePasswordActivity> retrievePasswordActivityProvider,

    final Provider<ResetPasswordActivity> resetPasswordActivityProvider,

    final Provider<DashboardActivity> dashboardActivityProvider,

    final Provider<ReportActivity> reportActivityProvider) {
        super();
        this.loginActivityProvider = loginActivityProvider;
        this.retrievePasswordActivityProvider = retrievePasswordActivityProvider;
        this.resetPasswordActivityProvider = resetPasswordActivityProvider;
        this.dashboardActivityProvider = dashboardActivityProvider;
        this.reportActivityProvider = reportActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof CrmSiteMap.Login) {
            return loginActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof CrmSiteMap.RetrievePassword) {
            return retrievePasswordActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof CrmSiteMap.ResetPassword) {
            return resetPasswordActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof CrmSiteMap.Dashboard) {
            return dashboardActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Report) {
            return reportActivityProvider.get().withPlace(place);
        }
        //TODO what to do on other place
        return null;
    }
}
