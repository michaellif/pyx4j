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
package com.propertyvista.portal.tester.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.propertyvista.portal.tester.SiteMap.SignUp;
import com.propertyvista.portal.tester.SiteMap.SignUpResult;
import com.propertyvista.portal.tester.SiteMap.TopRightActions;
import com.propertyvista.portal.tester.activity.MainContentActivity;
import com.propertyvista.portal.tester.activity.SignUpActivity;
import com.propertyvista.portal.tester.activity.SignUpResultActivity;
import com.propertyvista.portal.tester.activity.TopRightActionsActivity;

import com.pyx4j.site.rpc.AppPlace;

public class Center1ActivityMapper implements ActivityMapper {

    Provider<MainContentActivity> mainContentActivityProvider;

    Provider<SignUpActivity> signUpActivityProvider;

    Provider<SignUpResultActivity> signUpResultActivityProvider;

    Provider<TopRightActionsActivity> topRightActionsActivityProvider;

    @Inject
    public Center1ActivityMapper(final Provider<MainContentActivity> mainContentActivityProvider, final Provider<SignUpActivity> signUpActivityProvider,
            final Provider<SignUpResultActivity> signUpResultActivityProvider, final Provider<TopRightActionsActivity> topRightActionsActivityProvider) {
        super();
        this.mainContentActivityProvider = mainContentActivityProvider;
        this.signUpActivityProvider = signUpActivityProvider;
        this.signUpResultActivityProvider = signUpResultActivityProvider;
        this.topRightActionsActivityProvider = topRightActionsActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof TopRightActions) {
            return topRightActionsActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof SignUp) {
            return signUpActivityProvider.get().withPlace((AppPlace) place);
        } else if (place instanceof SignUpResult) {
            return signUpResultActivityProvider.get().withPlace((AppPlace) place);
        }
        return mainContentActivityProvider.get().withPlace((AppPlace) place);
    }

}
