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
import com.propertyvista.portal.client.ptapp.activity.CreateAccountActivity;

import com.pyx4j.site.client.place.AppPlace;

public class Center2ActivityMapper implements ActivityMapper {

    Provider<CreateAccountActivity> signInActivityProvider;

    @Inject
    public Center2ActivityMapper(final Provider<CreateAccountActivity> signInActivityProvider) {
        super();
        this.signInActivityProvider = signInActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof SiteMap.SignIn) {
            return signInActivityProvider.get().withPlace((AppPlace) place);
        }
        //TODO what to do on other place
        return null;
    }
}
