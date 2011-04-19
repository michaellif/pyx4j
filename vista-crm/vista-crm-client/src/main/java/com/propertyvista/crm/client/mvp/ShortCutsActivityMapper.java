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
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.propertyvista.crm.client.activity.ShortCutsActivity;

import com.pyx4j.security.client.ClientContext;

public class ShortCutsActivityMapper implements ActivityMapper {
    Provider<ShortCutsActivity> shortcutsActivityProvider;

    @Inject
    public ShortCutsActivityMapper(final Provider<ShortCutsActivity> shortcutsActivityProvider) {
        super();
        this.shortcutsActivityProvider = shortcutsActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {

        if (ClientContext.isAuthenticated()) {
            return shortcutsActivityProvider.get().withPlace(place);
        } else {
            return null;
        }

    }

}
