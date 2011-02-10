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
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.propertyvista.portal.client.ptapp.SiteMap;
import com.propertyvista.portal.client.ptapp.activity.ApartmentActivity;
import com.propertyvista.portal.client.ptapp.activity.CreateAccountActivity;
import com.propertyvista.portal.client.ptapp.activity.FinancialActivity;
import com.propertyvista.portal.client.ptapp.activity.InfoActivity;
import com.propertyvista.portal.client.ptapp.activity.PaymentActivity;
import com.propertyvista.portal.client.ptapp.activity.PetsActivity;
import com.propertyvista.portal.client.ptapp.activity.StaticContentActivity;
import com.propertyvista.portal.client.ptapp.activity.SummaryActivity;
import com.propertyvista.portal.client.ptapp.activity.TenantsActivity;

import com.pyx4j.site.client.place.AppPlace;

public class Center2ActivityMapper implements ActivityMapper {

    Provider<CreateAccountActivity> signInActivityProvider;

    Provider<ApartmentActivity> apartmentActivityProvider;

    Provider<TenantsActivity> tenantsActivityProvider;

    Provider<InfoActivity> infoActivityProvider;

    Provider<FinancialActivity> financialActivityProvider;

    Provider<PetsActivity> petsActivityProvider;

    Provider<PaymentActivity> paymentActivityProvider;

    Provider<SummaryActivity> summaryActivityProvider;

    Provider<StaticContentActivity> staticContentActivity;

    @Inject
    public Center2ActivityMapper(final Provider<CreateAccountActivity> signInActivityProvider,

    final Provider<ApartmentActivity> apartmentActivityProvider,

    final Provider<TenantsActivity> tenantsActivityProvider,

    final Provider<InfoActivity> infoActivityProvider,

    final Provider<FinancialActivity> financialActivityProvider,

    final Provider<PetsActivity> petsActivityProvider,

    final Provider<PaymentActivity> paymentActivityProvider,

    final Provider<SummaryActivity> summaryActivityProvider,

    final Provider<StaticContentActivity> staticContentActivity) {

        super();
        this.signInActivityProvider = signInActivityProvider;
        this.apartmentActivityProvider = apartmentActivityProvider;
        this.tenantsActivityProvider = tenantsActivityProvider;
        this.infoActivityProvider = infoActivityProvider;
        this.financialActivityProvider = financialActivityProvider;
        this.petsActivityProvider = petsActivityProvider;
        this.paymentActivityProvider = paymentActivityProvider;
        this.summaryActivityProvider = summaryActivityProvider;
        this.staticContentActivity = staticContentActivity;

    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof SiteMap.SignIn) {
            return signInActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof SiteMap.Apartment) {
            return apartmentActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof SiteMap.Tenants) {
            return tenantsActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof SiteMap.Info) {
            return infoActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof SiteMap.Financial) {
            return financialActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof SiteMap.Pets) {
            return petsActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof SiteMap.Payments) {
            return paymentActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof SiteMap.Summary) {
            return summaryActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof SiteMap.TermsAndConditions) {
            return staticContentActivity.get().withPlace((AppPlace) place);
        } else if (place instanceof SiteMap.PrivacyPolicy) {
            return staticContentActivity.get().withPlace((AppPlace) place);
        }
        //TODO what to do on other place
        return null;
    }
}
