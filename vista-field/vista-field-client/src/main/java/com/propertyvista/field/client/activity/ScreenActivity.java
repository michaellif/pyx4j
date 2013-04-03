/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.field.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.field.client.ui.ScreenViewer;
import com.propertyvista.field.client.ui.viewfactories.FieldViewFactory;
import com.propertyvista.field.rpc.ScreenMode.FullScreen;
import com.propertyvista.field.rpc.ScreenMode.HeaderListerDetails;
import com.propertyvista.field.rpc.ScreenMode.HeaderLister;
import com.propertyvista.field.rpc.ScreenMode.ScreenLayout;

public class ScreenActivity extends AbstractActivity {

    public ScreenActivity(Place place) {
        ScreenViewer view = FieldViewFactory.instance(ScreenViewer.class);
        view.setScreenLayout(getScreenLayout(place));
    }

    private ScreenLayout getScreenLayout(Place place) {

        if (place instanceof FullScreen) {
            return ScreenLayout.FullScreen;
        } else if (place instanceof HeaderLister) {
            return ScreenLayout.HeaderLister;
        } else if (place instanceof HeaderListerDetails) {
            return ScreenLayout.HeaderListerDetails;
        }

        throw new IllegalStateException("Unexpected place");
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        // TODO Auto-generated method stub
    }

}
