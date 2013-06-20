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

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.field.client.activity.RuntimeErrorActivity;
import com.propertyvista.field.client.activity.appselection.ApplicationSelectionActivity;
import com.propertyvista.field.client.activity.crud.building.BuildingEditorActivity;
import com.propertyvista.field.client.activity.crud.building.BuildingListerActivity;
import com.propertyvista.field.client.activity.crud.building.BuildingViewerActivity;
import com.propertyvista.field.client.activity.login.LoginActivity;
import com.propertyvista.field.client.activity.login.LoginWithTokenActivity;
import com.propertyvista.field.client.activity.security.PasswordResetActivity;
import com.propertyvista.field.client.activity.security.PasswordResetRequestActivity;
import com.propertyvista.field.rpc.FieldSiteMap;

public class ContentActivityMapper implements ActivityMapper {

    public ContentActivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {

        Activity activity = null;

        if (place instanceof FieldSiteMap.Login) {
            activity = new LoginActivity(place);
        } else if (place instanceof FieldSiteMap.PasswordResetRequest) {
            activity = new PasswordResetRequestActivity(place);
        } else if (place instanceof FieldSiteMap.PasswordReset) {
            activity = new PasswordResetActivity(place);
        } else if (place instanceof FieldSiteMap.LoginWithToken) {
            activity = new LoginWithTokenActivity(place);
        } else if (place instanceof FieldSiteMap.RuntimeError) {
            activity = new RuntimeErrorActivity(place);
        } else if (place instanceof FieldSiteMap.ApplicationSelection) {
            activity = new ApplicationSelectionActivity();
        } else if (place instanceof CrudAppPlace) {
            CrudAppPlace crudPlace = (CrudAppPlace) place;
            if (crudPlace instanceof FieldSiteMap.Properties.Building) {
                switch (crudPlace.getType()) {
                case editor:
                    activity = new BuildingEditorActivity(crudPlace);
                    break;
                case viewer:
                    activity = new BuildingViewerActivity(crudPlace);
                    break;
                case lister:
                    activity = new BuildingListerActivity(crudPlace);
                    break;

                }
            }

        }

        return activity;
    }

}
