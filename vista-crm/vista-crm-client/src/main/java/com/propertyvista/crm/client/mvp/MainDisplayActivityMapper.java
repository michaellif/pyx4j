/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.propertyvista.crm.client.activity.MainDisplayActivity;
import com.propertyvista.crm.client.activity.RuntimeErrorActivity;
import com.propertyvista.crm.client.activity.crud.account.MandatoryAccountRecoveryOptionsSetupActivity;
import com.propertyvista.crm.client.activity.login.LoginActivity;
import com.propertyvista.crm.client.activity.login.LoginWithTokenActivity;
import com.propertyvista.crm.client.activity.security.PasswordResetActivity;
import com.propertyvista.crm.client.activity.security.PasswordResetRequestActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class MainDisplayActivityMapper implements ActivityMapper {

    public MainDisplayActivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof CrmSiteMap.Login) {
            return new LoginActivity(place);
        } else if (place instanceof CrmSiteMap.PasswordResetRequest) {
            return new PasswordResetRequestActivity(place);
        } else if (place instanceof CrmSiteMap.PasswordReset) {
            return new PasswordResetActivity(place);
        } else if (place instanceof CrmSiteMap.Account.AccountRecoveryOptionsRequired) {
            return new MandatoryAccountRecoveryOptionsSetupActivity();
        } else if (place instanceof CrmSiteMap.LoginWithToken) {
            return new LoginWithTokenActivity(place);
        } else if (place instanceof CrmSiteMap.RuntimeError) {
            return new RuntimeErrorActivity(place);
        } else {
            return new MainDisplayActivity(place);
        }
    }

}
