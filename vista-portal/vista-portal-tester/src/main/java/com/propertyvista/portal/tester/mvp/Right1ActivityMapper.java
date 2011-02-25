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
package com.propertyvista.portal.tester.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.propertyvista.portal.tester.activity.SignInActivity;

import com.pyx4j.site.rpc.AppPlace;

public class Right1ActivityMapper implements ActivityMapper {

    Provider<SignInActivity> signInActivityProvider;

    @Inject
    public Right1ActivityMapper(final Provider<SignInActivity> signInActivityProvider) {
        super();
        this.signInActivityProvider = signInActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {
        return signInActivityProvider.get().withPlace((AppPlace) place);
    }

}
