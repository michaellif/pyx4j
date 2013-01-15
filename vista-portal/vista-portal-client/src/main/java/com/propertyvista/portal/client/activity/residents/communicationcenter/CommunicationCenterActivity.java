/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-15
 * @author matheszabi
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.residents.communicationcenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.communicationcenter.CommunicationCenterView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.services.resident.CommunicationCenterService;

public class CommunicationCenterActivity extends SecurityAwareActivity implements CommunicationCenterView.Presenter {

    private final CommunicationCenterView view;

    private final CommunicationCenterService srv;

    public CommunicationCenterActivity(Place place) {
        this.view = PortalViewFactory.instance(CommunicationCenterView.class);
        this.view.setPresenter(this);
        srv = GWT.create(CommunicationCenterService.class);
    }

    @Override
    public void createNewRequest() {
        // TODO Auto-generated method stub

    }

    @Override
    public void editRequest(MaintenanceRequestDTO requests) {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancelRequest(MaintenanceRequestDTO request) {
        // TODO Auto-generated method stub

    }

    @Override
    public void rateRequest(MaintenanceRequestDTO request, Integer rate) {
        // TODO Auto-generated method stub

    }

}
