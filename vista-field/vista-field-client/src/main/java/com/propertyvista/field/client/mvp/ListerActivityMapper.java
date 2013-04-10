/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 19, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.field.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.propertyvista.field.client.activity.building.BuildingListerActivity;
import com.propertyvista.field.rpc.FieldSiteMap;

public class ListerActivityMapper implements ActivityMapper {

    public ListerActivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {

        if (isBuidingListerPlace(place)) {
            return new BuildingListerActivity();
        }

        return null;
    }

    private boolean isBuidingListerPlace(Place place) {
        return (place instanceof FieldSiteMap.BuildingLister) || (place instanceof FieldSiteMap.BuildingListerDetails);
    }
}
