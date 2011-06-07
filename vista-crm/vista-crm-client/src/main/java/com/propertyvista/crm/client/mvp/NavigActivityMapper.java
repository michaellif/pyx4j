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

import com.pyx4j.security.client.ClientContext;

import com.propertyvista.crm.client.activity.NavigActivity;
import com.propertyvista.crm.client.activity.NavigSettingsActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class NavigActivityMapper implements ActivityMapper {

    Provider<NavigActivity> navigActivityProvider;

    Provider<NavigSettingsActivity> navigSettingsActivityProvider;

    @Inject
    public NavigActivityMapper(final Provider<NavigActivity> navigActivityProvider, final Provider<NavigSettingsActivity> navigSettingsActivityProvider) {
        this.navigActivityProvider = navigActivityProvider;
        this.navigSettingsActivityProvider = navigSettingsActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {
        if (ClientContext.isAuthenticated()) {
            if (place.getClass().getName().contains(CrmSiteMap.Settings.class.getName())) {
                return navigSettingsActivityProvider.get().withPlace(place);
            } else {
                return navigActivityProvider.get().withPlace(place);
            }
        } else {
            return null;
        }
    }
}
