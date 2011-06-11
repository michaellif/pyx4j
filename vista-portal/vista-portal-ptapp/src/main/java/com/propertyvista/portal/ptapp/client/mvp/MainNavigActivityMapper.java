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
package com.propertyvista.portal.ptapp.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.security.client.ClientContext;

import com.propertyvista.portal.ptapp.client.activity.MainNavigActivity;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

public class MainNavigActivityMapper implements ActivityMapper {

    @Override
    public Activity getActivity(Place place) {
        if (ClientContext.isAuthenticated() && !(place instanceof PtSiteMap.Completion)) {
            RootPanel.getBodyElement().setClassName("body-nonavig");
            return new MainNavigActivity(place);
        } else {
            RootPanel.getBodyElement().setClassName("body-navig");
            return null;
        }
    }

}
