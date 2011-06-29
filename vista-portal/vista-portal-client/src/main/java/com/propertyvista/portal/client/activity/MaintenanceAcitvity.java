/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.client.ui.residents.MaintenanceView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.domain.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class MaintenanceAcitvity extends SecurityAwareActivity implements MaintenanceView.Presenter {
    private final MaintenanceView view;

    public MaintenanceAcitvity(Place place) {
        this.view = (MaintenanceView) PortalViewFactory.instance(MaintenanceView.class);
        this.view.setPresenter(this);
        withPlace(place);
    }

    public MaintenanceAcitvity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
        //TODO Implement a service call
        MaintenanceRequestDTO problem = EntityFactory.create(MaintenanceRequestDTO.class);
        view.populate(problem);
    }

    @Override
    public void showSystemStatus() {
        // TODO Auto-generated method stub

    }

    @Override
    public void showSupportHistory() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.MaintenanceListHistory());

    }
}
