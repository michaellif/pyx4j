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

import com.pyx4j.security.client.BehaviorChangeEvent;
import com.pyx4j.security.client.BehaviorChangeHandler;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.activity.NavigActivity;
import com.propertyvista.crm.client.activity.NavigSettingsActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.security.common.VistaBasicBehavior;

public class NavigActivityMapper implements ActivityMapper {

    private static NavigActivity navigActivity;

    private static NavigSettingsActivity navigSettingsActivity;

    public NavigActivityMapper() {
        AppSite.getEventBus().addHandler(BehaviorChangeEvent.getType(), new BehaviorChangeHandler() {
            @Override
            public void onBehaviorChange(BehaviorChangeEvent event) {
                navigActivity = null;
                navigSettingsActivity = null;
            }
        });
    }

    @Override
    public Activity getActivity(Place place) {
        if (SecurityController.checkBehavior(VistaBasicBehavior.CRM)) {
            if (place.getClass().getName().contains(CrmSiteMap.Administration.class.getName())) {
                if (navigSettingsActivity == null) {
                    navigSettingsActivity = new NavigSettingsActivity();
                }
                navigSettingsActivity.withPlace(place);
                return navigSettingsActivity;
            } else {
                if (navigActivity == null) {
                    navigActivity = new NavigActivity();
                }
                navigActivity.withPlace(place);
                return navigActivity;
            }
        } else {
            return null;
        }
    }
}
