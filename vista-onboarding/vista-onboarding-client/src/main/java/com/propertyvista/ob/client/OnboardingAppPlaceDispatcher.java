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
 */
package com.propertyvista.ob.client;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AbstractAppPlaceDispatcher;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.ob.rpc.OnboardingSiteMap;
import com.propertyvista.ob.rpc.dto.OnboardingUserVisit;

public class OnboardingAppPlaceDispatcher extends AbstractAppPlaceDispatcher {

    @Override
    protected AppPlace obtainDefaultPlace() {
        if (ClientContext.isAuthenticated()) {
            if (ClientContext.getUserVisit() instanceof OnboardingUserVisit) {
                OnboardingUserVisit visit = ClientContext.visit(OnboardingUserVisit.class);
                switch (visit.status) {
                case accountCreated:
                    return new OnboardingSiteMap.PmcAccountCreationComplete();
                case accountCreation:
                    return new OnboardingSiteMap.PmcAccountCreationProgress().placeArg("id", visit.accountCreationDeferredCorrelationId);
                default:
                    return new OnboardingSiteMap.PmcAccountCreationRequest();
                }

            } else {
                return new OnboardingSiteMap.PmcAccountCreationRequest();
            }
        } else {
            return new OnboardingSiteMap.PmcAccountCreationRequest();
        }
    }

    @Override
    protected boolean isPlaceNavigable(AppPlace targetPlace) {
        return true;
    }

    @Override
    protected AppPlace mandatoryActionForward(AppPlace newPlace) {
        return newPlace;
    }

}
