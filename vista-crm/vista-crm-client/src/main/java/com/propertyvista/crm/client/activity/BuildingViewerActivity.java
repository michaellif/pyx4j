/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.vewers.BuildingViewerView;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class BuildingViewerActivity extends AbstractActivity {

    private final BuildingViewerView view;

    @Inject
    public BuildingViewerActivity(BuildingViewerView view) {
        this.view = view;
    }

    public BuildingViewerActivity withPlace(Place place) {
        String stepArg = ((AppPlace) place).getArgs().get(CrmSiteMap.ARG_NAME_ITEM_ID);
        if (stepArg != null) {
            view.setViewingEntityId(Long.valueOf(stepArg));
        }

        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }
}
