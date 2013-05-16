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

import com.propertyvista.field.client.activity.crud.building.BuildingEditorActivity;
import com.propertyvista.field.client.activity.crud.building.BuildingViewerActivity;
import com.propertyvista.field.rpc.FieldSiteMap;

public class DetailsActivityMapper implements ActivityMapper {

    public DetailsActivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {

        Activity activity = null;
        if (place instanceof CrudAppPlace) {
            CrudAppPlace crudPlace = (CrudAppPlace) place;

            if (crudPlace instanceof FieldSiteMap.Properties.Building) {
                switch (crudPlace.getType()) {
                case editor:
                    activity = new BuildingEditorActivity(crudPlace);
                    break;
                case viewer:
                    activity = new BuildingViewerActivity(crudPlace);
                    break;
                }
            }

        }

        return activity;
    }
}
