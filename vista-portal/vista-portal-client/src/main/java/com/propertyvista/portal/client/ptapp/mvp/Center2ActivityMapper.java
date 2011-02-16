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
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.propertyvista.portal.client.ptapp.activity.UserMessageActivity;

import com.pyx4j.site.client.place.AppPlace;

public class Center2ActivityMapper implements ActivityMapper {

    Provider<UserMessageActivity> userMessageActivityProvider;

    @Inject
    public Center2ActivityMapper(final Provider<UserMessageActivity> userMessageActivityProvider) {

        super();
        this.userMessageActivityProvider = userMessageActivityProvider;

    }

    @Override
    public Activity getActivity(Place place) {
        return userMessageActivityProvider.get().withPlace((AppPlace) place);
    }
}
