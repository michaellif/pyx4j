/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;

import com.propertyvista.portal.client.ui.residents.MaintenanceListerView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.domain.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.services.MaintenanceRequestCrudService;

public class MaintenanceListerActivity extends ListerActivityBase<MaintenanceRequestDTO> {

    @SuppressWarnings("unchecked")
    public MaintenanceListerActivity(Place place) {
        super((MaintenanceListerView) PortalViewFactory.instance(MaintenanceListerView.class), (AbstractCrudService<MaintenanceRequestDTO>) GWT
                .create(MaintenanceRequestCrudService.class), MaintenanceRequestDTO.class);
        setPlace(place);
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, EventBus eventBus) {
        super.start(containerWidget, eventBus);
        eventBus.addHandler(SecurityControllerEvent.getType(), new SecurityControllerHandler() {
            @Override
            public void onSecurityContextChange(SecurityControllerEvent event) {
                if (!ClientContext.isAuthenticated()) {
                    containerWidget.setWidget(null);
                }

            }
        });

    }

}
