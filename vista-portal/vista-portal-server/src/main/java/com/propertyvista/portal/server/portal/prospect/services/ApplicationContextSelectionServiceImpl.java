/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 24, 2012
 * @author ArtyomB
 */
package com.propertyvista.portal.server.portal.prospect.services;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.biz.tenant.OnlineApplicationFacade;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.prospect.ProspectUserVisit;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationContextChoiceDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationContextSelectionService;
import com.propertyvista.portal.server.portal.prospect.ProspectPortalContext;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;
import com.propertyvista.server.common.util.AddressRetriever;

public class ApplicationContextSelectionServiceImpl implements ApplicationContextSelectionService {

    @Override
    //TODO Implement CustomerFacade.getActiveOnlineApplications and AddressRetriever.getOnlineApplicationAddress
    public void getApplicationContextChoices(AsyncCallback<Vector<OnlineApplicationContextChoiceDTO>> callback) {
        List<OnlineApplication> activeApplications = ServerSideFactory.create(OnlineApplicationFacade.class).getOnlineApplications(
                ResidentPortalContext.getCustomerUserIdStub());
        Vector<OnlineApplicationContextChoiceDTO> choices = new Vector<OnlineApplicationContextChoiceDTO>(activeApplications.size());

        for (OnlineApplication onlineApplication : activeApplications) {
            OnlineApplicationContextChoiceDTO choice = EntityFactory.create(OnlineApplicationContextChoiceDTO.class);
            choice.onlineApplication().set(onlineApplication.createIdentityStub());
            choice.leaseApplicationUnitAddress().setValue(AddressRetriever.getOnlineApplicationAddress(onlineApplication).getStringView());
            choices.add(choice);
        }

        callback.onSuccess(choices);
    }

    @Override
    public void setApplicationContext(AsyncCallback<VoidSerializable> callback, OnlineApplication applicationStub) {
        ProspectPortalContext.setOnlineApplication(ServerContext.visit(ProspectUserVisit.class), applicationStub);
        ServerContext.getVisit().setAclRevalidationRequired();
        callback.onSuccess(null);
    }

}
