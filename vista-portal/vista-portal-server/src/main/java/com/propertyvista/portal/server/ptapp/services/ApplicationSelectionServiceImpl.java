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
package com.propertyvista.portal.server.ptapp.services;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.security.rpc.AuthenticationResponse;

import com.propertyvista.biz.tenant.OnlineApplicationFacade;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
import com.propertyvista.portal.rpc.ptapp.dto.OnlineApplicationContextDTO;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationSelectionService;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class ApplicationSelectionServiceImpl implements ApplicationSelectionService {

    @Override
    public void setApplicationContext(AsyncCallback<AuthenticationResponse> callback, OnlineApplication applicationStub) {
        callback.onSuccess(new PtAuthenticationServiceImpl().reAuthenticate(applicationStub));
    }

    @Override
    public void getApplications(AsyncCallback<Vector<OnlineApplicationContextDTO>> callback) {
        OnlineApplicationFacade appFacade = ServerSideFactory.create(OnlineApplicationFacade.class);

        List<OnlineApplication> applications = appFacade.getOnlineApplications(PtAppContext.getCurrentUser());
        Vector<OnlineApplicationContextDTO> contextOptions = new Vector<OnlineApplicationContextDTO>();

        for (OnlineApplication application : applications) {
            contextOptions.add(onlineApplicationContext(application, appFacade));
        }

        callback.onSuccess(contextOptions);
    }

    private OnlineApplicationContextDTO onlineApplicationContext(OnlineApplication application, OnlineApplicationFacade appFacade) {
        OnlineApplicationContextDTO view = EntityFactory.create(OnlineApplicationContextDTO.class);

        view.onlineApplicationIdStub().set(application.createIdentityStub());
        view.role().setValue(appFacade.getOnlineApplicationBehavior(application));
        Persistence.service().retrieveMember(application.masterOnlineApplication());
        AptUnit unit = Persistence.service().retrieve(AptUnit.class, application.masterOnlineApplication().leaseApplication().lease().unit().getPrimaryKey());
        view.unit().set(unit.duplicate());
        view.unit().setAttachLevel(AttachLevel.ToStringMembers);

        Building building = Persistence.service().retrieve(Building.class, unit.building().getPrimaryKey());
        view.address().set(building.info().address().detach());
        return view;
    }
}
