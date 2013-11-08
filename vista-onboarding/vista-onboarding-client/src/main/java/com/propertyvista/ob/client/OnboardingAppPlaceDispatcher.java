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

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AbstractAppPlaceDispatcher;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.NotificationAppPlace;
import com.pyx4j.site.shared.domain.Notification;

import com.propertyvista.ob.rpc.OnboardingSiteMap;
import com.propertyvista.ob.rpc.dto.OnboardingUserVisit;

public class OnboardingAppPlaceDispatcher extends AbstractAppPlaceDispatcher {

    @Override
    public NotificationAppPlace getNotificationPlace(Notification notification) {
        NotificationAppPlace place = new OnboardingSiteMap.RuntimeError();
        place.setNotification(notification);
        return place;
    }

    @Override
    protected void obtainDefaulPublicPlace(AsyncCallback<AppPlace> callback) {
        callback.onSuccess(new OnboardingSiteMap.PmcAccountCreationRequest());
    }

    @Override
    protected void obtainDefaultAuthenticatedPlace(AsyncCallback<AppPlace> callback) {
        if (ClientContext.getUserVisit() instanceof OnboardingUserVisit) {
            OnboardingUserVisit visit = (OnboardingUserVisit) ClientContext.getUserVisit();
            switch (visit.status) {
            case accountCreated:
                callback.onSuccess(new OnboardingSiteMap.PmcAccountCreationComplete());
                break;
            case accountCreation:
                callback.onSuccess(new OnboardingSiteMap.PmcAccountCreationProgress().placeArg("id", visit.accountCreationDeferredCorrelationId));
                break;
            default:
                callback.onSuccess(new OnboardingSiteMap.PmcAccountCreationRequest());
                break;
            }

        } else {
            callback.onSuccess(new OnboardingSiteMap.PmcAccountCreationRequest());
        }
    }

    @Override
    protected void isPlaceNavigable(AppPlace targetPlace, AsyncCallback<Boolean> callback) {
        callback.onSuccess(true);
    }

    @Override
    protected AppPlace specialForward(AppPlace newPlace) {
        return null;
    }

}
