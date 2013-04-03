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
package com.propertyvista.field.client.activity.building;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.field.client.ui.building.BuildingListerView;
import com.propertyvista.field.client.ui.viewfactories.BuildingViewFactory;

public class BuildingListerActivity extends AbstractActivity implements BuildingListerView.Presenter {

    private final BuildingListerView view;

    public BuildingListerActivity() {
        view = BuildingViewFactory.instance(BuildingListerView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        panel.setWidget(view);
    }

}
