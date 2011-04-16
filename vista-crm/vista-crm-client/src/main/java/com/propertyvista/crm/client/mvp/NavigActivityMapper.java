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
package com.propertyvista.crm.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.propertyvista.crm.client.activity.NavigActivity;

import com.pyx4j.security.client.ClientContext;

public class NavigActivityMapper implements ActivityMapper {

    Provider<NavigActivity> navigActivityProvider;

    @Inject
    public NavigActivityMapper(final Provider<NavigActivity> navigActivityProvider) {
        super();
        this.navigActivityProvider = navigActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {
        if (ClientContext.isAuthenticated()) {
            return navigActivityProvider.get().withPlace(place);
        } else {
            return null;
        }
    }

}
