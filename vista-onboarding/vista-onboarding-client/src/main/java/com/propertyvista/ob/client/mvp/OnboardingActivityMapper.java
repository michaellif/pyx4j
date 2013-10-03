/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.ob.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.activity.AppActivityMapper;

import com.propertyvista.ob.client.mvp.activity.PmcAccountCreationCompleteActivity;
import com.propertyvista.ob.client.mvp.activity.PmcAccountCreationProgressActivity;
import com.propertyvista.ob.client.mvp.activity.PmcAccountCreationRequestActivity;
import com.propertyvista.ob.client.mvp.activity.PmcTermsActivity;
import com.propertyvista.ob.client.mvp.activity.RuntimeErrorActivity;
import com.propertyvista.ob.rpc.OnboardingSiteMap;

public class OnboardingActivityMapper implements AppActivityMapper {

    @Override
    public void obtainActivity(final Place place, final AsyncCallback<Activity> callback) {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                Activity activity = null;
                if (place instanceof OnboardingSiteMap.PmcAccountCreationRequest) {
                    activity = new PmcAccountCreationRequestActivity((OnboardingSiteMap.PmcAccountCreationRequest) place);
                } else if (place instanceof OnboardingSiteMap.PmcAccountCreationProgress) {
                    activity = new PmcAccountCreationProgressActivity((OnboardingSiteMap.PmcAccountCreationProgress) place);
                } else if (place instanceof OnboardingSiteMap.PmcAccountCreationComplete) {
                    activity = new PmcAccountCreationCompleteActivity((OnboardingSiteMap.PmcAccountCreationComplete) place);
                } else if (place instanceof OnboardingSiteMap.PmcAccountTerms) {
                    activity = new PmcTermsActivity();

                } else if (place instanceof OnboardingSiteMap.RuntimeError) {
                    activity = new RuntimeErrorActivity((OnboardingSiteMap.RuntimeError) place);
                }
                callback.onSuccess(activity);
            }

            @Override
            public void onFailure(Throwable reason) {
                callback.onFailure(reason);
            }
        });

    }

}
