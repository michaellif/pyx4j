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

import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.crm.client.activity.NavigActivity;
import com.propertyvista.crm.client.activity.NavigSettingsActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.security.common.VistaBasicBehavior;

public class NavigActivityMapper implements ActivityMapper {

    private static NavigActivity navigActivity = new NavigActivity();

    private static NavigSettingsActivity navigSettingsActivity = new NavigSettingsActivity();

    public NavigActivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {
        if (SecurityController.checkBehavior(VistaBasicBehavior.CRM)) {
            if (place.getClass().getName().contains(CrmSiteMap.Administration.class.getName())) {
                navigSettingsActivity.withPlace(place);
                return navigSettingsActivity;
            } else {
                navigActivity.withPlace(place);
                return navigActivity;
            }
        } else {
            return null;
        }
    }
}
