/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 24, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.activity.AppActivityMapper;

import com.propertyvista.portal.client.activity.StaticPageActivity;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class StaticContentActivityMapper implements AppActivityMapper {

    public StaticContentActivityMapper() {

    }

    @Override
    public void obtainActivity(Place place, AsyncCallback<Activity> callback) {
        if (place instanceof PortalSiteMap.Page || place instanceof PortalSiteMap.Landing) {
            callback.onSuccess(new StaticPageActivity(place));
        }
    }

}
