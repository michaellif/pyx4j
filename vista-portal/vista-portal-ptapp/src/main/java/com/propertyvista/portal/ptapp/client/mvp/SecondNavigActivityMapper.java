/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-12
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.propertyvista.portal.ptapp.client.activity.SecondNavigActivity;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

import com.pyx4j.security.client.ClientContext;

public class SecondNavigActivityMapper implements ActivityMapper {

    Provider<SecondNavigActivity> secondNavigActivityProvider;

    @Inject
    public SecondNavigActivityMapper(final Provider<SecondNavigActivity> secondNavigActivityProvider) {
        super();
        this.secondNavigActivityProvider = secondNavigActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {
        if (ClientContext.isAuthenticated() && !(place instanceof PtSiteMap.Completion)) {
            return secondNavigActivityProvider.get().withPlace(place);
        } else {
            return null;
        }

    }

}