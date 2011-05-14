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
package com.propertyvista.portal.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.propertyvista.portal.client.activity.TopRightActionsActivity;

import com.pyx4j.site.rpc.AppPlace;

public class ActionsActivityMapper implements ActivityMapper {

    Provider<TopRightActionsActivity> topRightActionActivityProvider;

    @Inject
    public ActionsActivityMapper(Provider<TopRightActionsActivity> topRightActionActivityProvider) {
        super();
        this.topRightActionActivityProvider = topRightActionActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {
        return topRightActionActivityProvider.get().withPlace((AppPlace) place);
    }

}
