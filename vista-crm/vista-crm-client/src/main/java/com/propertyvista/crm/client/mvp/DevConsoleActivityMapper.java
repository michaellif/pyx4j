/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 30, 2014
 * @author vlads
 */
package com.propertyvista.crm.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.pyx4j.config.shared.ApplicationMode;

import com.propertyvista.crm.client.activity.DevConsoleActivity;

public class DevConsoleActivityMapper implements ActivityMapper {

    public DevConsoleActivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {
        if (ApplicationMode.isDevelopment() || ApplicationMode.isDemo()) {
            return DevConsoleActivity.instance().withPlace(place);
        } else {
            return null;
        }
    }

}
