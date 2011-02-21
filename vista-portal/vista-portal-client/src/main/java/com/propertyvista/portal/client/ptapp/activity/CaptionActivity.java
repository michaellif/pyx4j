/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 8, 2011
 * @author Misha
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.client.ptapp.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.ui.CaptionView;

import com.pyx4j.site.client.place.AppPlaceListing;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;

public class CaptionActivity extends AbstractActivity {

    private final CaptionView view;

    private final AppPlaceListing appPlaceListing;

    @Inject
    public CaptionActivity(CaptionView view, AppPlaceListing appPlaceListing) {
        this.view = view;
        this.appPlaceListing = appPlaceListing;
    }

    public CaptionActivity withPlace(AppPlace place) {
        AppPlaceInfo info = appPlaceListing.getPlaceInfo(place);
        if (info != null) {
            view.setCaption(info.getCaption());
        }
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
    }

}