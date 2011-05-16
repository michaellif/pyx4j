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
package com.propertyvista.portal.ptapp.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.propertyvista.portal.ptapp.client.activity.ApartmentActivity;
import com.propertyvista.portal.ptapp.client.activity.ChangePasswordActivity;
import com.propertyvista.portal.ptapp.client.activity.ChargesActivity;
import com.propertyvista.portal.ptapp.client.activity.CompletionActivity;
import com.propertyvista.portal.ptapp.client.activity.CreateAccountActivity;
import com.propertyvista.portal.ptapp.client.activity.FinancialActivity;
import com.propertyvista.portal.ptapp.client.activity.GenericMessageActivity;
import com.propertyvista.portal.ptapp.client.activity.InfoActivity;
import com.propertyvista.portal.ptapp.client.activity.LoginActivity;
import com.propertyvista.portal.ptapp.client.activity.PaymentActivity;
import com.propertyvista.portal.ptapp.client.activity.PetsActivity;
import com.propertyvista.portal.ptapp.client.activity.ResetPasswordActivity;
import com.propertyvista.portal.ptapp.client.activity.RetrievePasswordActivity;
import com.propertyvista.portal.ptapp.client.activity.StaticContentActivity;
import com.propertyvista.portal.ptapp.client.activity.SummaryActivity;
import com.propertyvista.portal.ptapp.client.activity.TenantsActivity;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

import com.pyx4j.site.rpc.AppPlace;

public class ContentActivityMapper implements ActivityMapper {

    Provider<CreateAccountActivity> createAccountActivityProvider;

    Provider<LoginActivity> loginActivityProvider;

    Provider<RetrievePasswordActivity> retrievePasswordActivityProvider;

    Provider<ResetPasswordActivity> resetPasswordActivityProvider;

    Provider<ChangePasswordActivity> changePasswordActivityProvider;

    Provider<ApartmentActivity> apartmentActivityProvider;

    Provider<TenantsActivity> tenantsActivityProvider;

    Provider<InfoActivity> infoActivityProvider;

    Provider<FinancialActivity> financialActivityProvider;

    Provider<PetsActivity> petsActivityProvider;

    Provider<PaymentActivity> paymentActivityProvider;

    Provider<ChargesActivity> chargesActivityProvider;

    Provider<SummaryActivity> summaryActivityProvider;

    Provider<CompletionActivity> completionActivityProvider;

    Provider<StaticContentActivity> staticContentActivity;

    Provider<GenericMessageActivity> genericMessageActivity;

    @Inject
    public ContentActivityMapper(final Provider<CreateAccountActivity> createAccountActivityProvider,

    final Provider<LoginActivity> loginActivityProvider,

    final Provider<RetrievePasswordActivity> retrievePasswordActivityProvider,

    final Provider<ResetPasswordActivity> resetPasswordActivityProvider,

    final Provider<ChangePasswordActivity> changePasswordActivityProvider,

    final Provider<ApartmentActivity> apartmentActivityProvider,

    final Provider<TenantsActivity> tenantsActivityProvider,

    final Provider<InfoActivity> infoActivityProvider,

    final Provider<FinancialActivity> financialActivityProvider,

    final Provider<PetsActivity> petsActivityProvider,

    final Provider<PaymentActivity> paymentActivityProvider,

    final Provider<ChargesActivity> chargesActivityProvider,

    final Provider<SummaryActivity> summaryActivityProvider,

    final Provider<CompletionActivity> completionActivityProvider,

    final Provider<StaticContentActivity> staticContentActivity,

    final Provider<GenericMessageActivity> genericMessageActivity) {

        super();
        this.createAccountActivityProvider = createAccountActivityProvider;
        this.loginActivityProvider = loginActivityProvider;
        this.retrievePasswordActivityProvider = retrievePasswordActivityProvider;
        this.resetPasswordActivityProvider = resetPasswordActivityProvider;
        this.changePasswordActivityProvider = changePasswordActivityProvider;
        this.apartmentActivityProvider = apartmentActivityProvider;
        this.tenantsActivityProvider = tenantsActivityProvider;
        this.infoActivityProvider = infoActivityProvider;
        this.financialActivityProvider = financialActivityProvider;
        this.petsActivityProvider = petsActivityProvider;
        this.paymentActivityProvider = paymentActivityProvider;
        this.chargesActivityProvider = chargesActivityProvider;
        this.summaryActivityProvider = summaryActivityProvider;
        this.completionActivityProvider = completionActivityProvider;
        this.staticContentActivity = staticContentActivity;
        this.genericMessageActivity = genericMessageActivity;
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof PtSiteMap.CreateAccount) {
            return createAccountActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof PtSiteMap.Login) {
            return loginActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof PtSiteMap.RetrievePassword) {
            return retrievePasswordActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof PtSiteMap.ChangePassword) {
            return changePasswordActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof PtSiteMap.ResetPassword) {
            return resetPasswordActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof PtSiteMap.Apartment) {
            return apartmentActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof PtSiteMap.Tenants) {
            return tenantsActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof PtSiteMap.Info) {
            return infoActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof PtSiteMap.Financial) {
            return financialActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof PtSiteMap.Pets) {
            return petsActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof PtSiteMap.Charges) {
            return chargesActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof PtSiteMap.Payment) {
            return paymentActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof PtSiteMap.Summary) {
            return summaryActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof PtSiteMap.Completion) {
            return completionActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof PtSiteMap.GenericMessage) {
            return genericMessageActivity.get().withPlace((AppPlace) place);
        }
        //TODO what to do on other place
        return null;
    }
}
