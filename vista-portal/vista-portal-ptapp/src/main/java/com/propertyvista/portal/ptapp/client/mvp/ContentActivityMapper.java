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

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.ptapp.client.activity.ChangePasswordActivity;
import com.propertyvista.portal.ptapp.client.activity.CreateAccountActivity;
import com.propertyvista.portal.ptapp.client.activity.GenericMessageActivity;
import com.propertyvista.portal.ptapp.client.activity.LoginActivity;
import com.propertyvista.portal.ptapp.client.activity.ResetPasswordActivity;
import com.propertyvista.portal.ptapp.client.activity.RetrievePasswordActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.ApartmentActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.ChargesActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.CompletionActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.FinancialActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.InfoActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.PaymentActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.PetsActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.SummaryActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.TenantsActivity;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

public class ContentActivityMapper implements ActivityMapper {

    @Override
    public Activity getActivity(Place place) {

        if (place instanceof PtSiteMap.CreateAccount) {
            return new CreateAccountActivity(place);
        } else if (place instanceof PtSiteMap.Login) {
            return new LoginActivity(place);
        } else if (place instanceof PtSiteMap.RetrievePassword) {
            return new RetrievePasswordActivity(place);
        } else if (place instanceof PtSiteMap.ChangePassword) {
            return new ChangePasswordActivity(place);
        } else if (place instanceof PtSiteMap.ResetPassword) {
            return new ResetPasswordActivity(place);
        } else if (place instanceof PtSiteMap.GenericMessage) {
            return new GenericMessageActivity(place);
// WizardSteps:
        } else if (place instanceof PtSiteMap.Apartment) {
            return new ApartmentActivity((AppPlace) place);
        } else if (place instanceof PtSiteMap.Tenants) {
            return new TenantsActivity((AppPlace) place);
        } else if (place instanceof PtSiteMap.Info) {
            return new InfoActivity((AppPlace) place);
        } else if (place instanceof PtSiteMap.Financial) {
            return new FinancialActivity((AppPlace) place);
        } else if (place instanceof PtSiteMap.Pets) {
            return new PetsActivity((AppPlace) place);
        } else if (place instanceof PtSiteMap.Charges) {
            return new ChargesActivity((AppPlace) place);
        } else if (place instanceof PtSiteMap.Summary) {
            return new SummaryActivity((AppPlace) place);
        } else if (place instanceof PtSiteMap.Payment) {
            return new PaymentActivity((AppPlace) place);
        } else if (place instanceof PtSiteMap.Completion) {
            return new CompletionActivity((AppPlace) place);
        }
        //TODO what to do on other place
        return null;
    }
}
