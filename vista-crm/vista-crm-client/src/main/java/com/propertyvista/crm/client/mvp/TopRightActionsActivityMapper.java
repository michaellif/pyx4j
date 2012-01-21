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

import com.propertyvista.crm.client.activity.SigningOutActivity;
import com.propertyvista.crm.client.activity.TopRightActionsActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class TopRightActionsActivityMapper implements ActivityMapper {

    public TopRightActionsActivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof CrmSiteMap.SigningOut) {
            return new SigningOutActivity(place);
        } else {
            return new TopRightActionsActivity(place);
        }
    }
}
