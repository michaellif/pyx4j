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
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.activity.AppActivityMapper;

import com.propertyvista.portal.client.activity.ApartmentDetailsActivity;
import com.propertyvista.portal.client.activity.FloorplanDetailsActivity;
import com.propertyvista.portal.client.activity.LoginActivity;
import com.propertyvista.portal.client.activity.LoginInvitationActivity;
import com.propertyvista.portal.client.activity.MaintenanceAcitvity;
import com.propertyvista.portal.client.activity.PaymentActivity;
import com.propertyvista.portal.client.activity.PropertyMapActivity;
import com.propertyvista.portal.client.activity.SearchApartmentActivity;
import com.propertyvista.portal.client.activity.TenantProfileActivity;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class ContentActivityMapper implements AppActivityMapper {

    public ContentActivityMapper() {

    }

    @Override
    public void obtainActivity(final Place place, final AsyncCallback<Activity> callback) {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                Activity activity = null;
                if (place instanceof PortalSiteMap.FindApartment) {
                    activity = new SearchApartmentActivity(place);
                } else if (place instanceof PortalSiteMap.Residents && !ClientContext.isAuthenticated()
                        || place instanceof PortalSiteMap.Residents.LoginInvitation) {
                    activity = new LoginInvitationActivity(place);
                } else if (place instanceof PortalSiteMap.FindApartment.PropertyMap) {
                    activity = new PropertyMapActivity(place);
                } else if (place instanceof PortalSiteMap.FindApartment.ApartmentDetails) {
                    activity = new ApartmentDetailsActivity(place);
                } else if (place instanceof PortalSiteMap.FindApartment.FloorplanDetails) {
                    activity = new FloorplanDetailsActivity(place);
                } else if (place instanceof PortalSiteMap.Residents.Navigator.TenantProfile) {
                    activity = new TenantProfileActivity(place);
                } else if (place instanceof PortalSiteMap.Residents.Navigator.Maintenance) {
                    activity = new MaintenanceAcitvity(place);
                } else if (place instanceof PortalSiteMap.Residents.Navigator.Payment) {
                    activity = new PaymentActivity(place);
                } else if (place instanceof PortalSiteMap.FindApartment.FloorplanDetails) {
                    activity = new FloorplanDetailsActivity(place);
                } else if (place instanceof PortalSiteMap.Residents.Login) {
                    activity = new LoginActivity(place);
                }

                callback.onSuccess(activity);
            }

            @Override
            public void onFailure(Throwable reason) {
                callback.onFailure(reason);
            }
        });

    }
}
