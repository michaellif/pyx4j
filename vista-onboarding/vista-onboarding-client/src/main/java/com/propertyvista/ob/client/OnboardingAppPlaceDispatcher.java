/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.ob.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AbstractAppPlaceDispatcher;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.security.onboarding.OnboardingApplicationBehavior;
import com.propertyvista.ob.rpc.OnboardingSiteMap;

public class OnboardingAppPlaceDispatcher extends AbstractAppPlaceDispatcher {

    @Override
    protected boolean isApplicationAuthenticated() {
        return SecurityController.checkBehavior(VistaBasicBehavior.Onboarding);
    }

    @Override
    protected void obtainDefaulPublicPlace(AsyncCallback<AppPlace> callback) {
        callback.onSuccess(new OnboardingSiteMap.PmcAccountCreationRequest());
    }

    @Override
    protected void obtainDefaultAuthenticatedPlace(AsyncCallback<AppPlace> callback) {
        if (SecurityController.checkBehavior(OnboardingApplicationBehavior.accountCreated)) {
            callback.onSuccess(new OnboardingSiteMap.PmcAccountCreationComplete());
        } else if (SecurityController.checkBehavior(OnboardingApplicationBehavior.accountCreationRequested)) {
            callback.onSuccess(new OnboardingSiteMap.PmcAccountCreationProgress());
        } else {
            callback.onSuccess(new OnboardingSiteMap.PmcAccountCreationRequest());
        }
    }

    @Override
    protected void isPlaceNavigable(AppPlace targetPlace, AsyncCallback<Boolean> callback) {
        if (targetPlace instanceof OnboardingSiteMap.PmcAccountCreationComplete) {
            callback.onSuccess(SecurityController.checkBehavior(OnboardingApplicationBehavior.accountCreated));
        } else {
            callback.onSuccess(true);
        }
    }

    @Override
    protected AppPlace specialForward(AppPlace newPlace) {
        return null;
    }

}
