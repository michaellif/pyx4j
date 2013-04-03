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

import com.propertyvista.field.client.activity.ScreenActivity;
import com.propertyvista.field.rpc.ScreenMode.FullScreen;
import com.propertyvista.field.rpc.ScreenMode.HeaderListerDetails;
import com.propertyvista.field.rpc.ScreenMode.HeaderLister;

public class ScreenViewerActivityMapper implements ActivityMapper {

    public ScreenViewerActivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {

        if (isScreenViewerPlace(place)) {
            return new ScreenActivity(place);
        }

        return null;
    }

    private boolean isScreenViewerPlace(Place place) {
        return (place instanceof FullScreen) || (place instanceof HeaderLister) || (place instanceof HeaderListerDetails);
    }
}
