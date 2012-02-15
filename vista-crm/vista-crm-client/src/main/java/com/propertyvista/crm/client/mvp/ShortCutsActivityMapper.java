/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.crm.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.pyx4j.security.client.ClientContext;

import com.propertyvista.crm.client.activity.ShortCutsActivity;

public class ShortCutsActivityMapper implements ActivityMapper {

    private final ShortCutsActivity activity = new ShortCutsActivity();

    public ShortCutsActivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {

        if (ClientContext.isAuthenticated()) {
            return activity.withPlace(place);
        } else {
            return null;
        }
    }
}
