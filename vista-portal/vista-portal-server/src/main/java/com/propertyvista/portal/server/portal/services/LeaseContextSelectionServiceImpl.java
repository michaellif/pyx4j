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
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.security.rpc.AuthenticationResponse;

import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.domain.dto.LeaseContextChoiceDTO;
import com.propertyvista.portal.rpc.portal.services.LeaseContextSelectionService;
import com.propertyvista.portal.server.portal.web.services.ResidentAuthenticationServiceImpl;
import com.propertyvista.portal.server.security.TenantAppContext;
import com.propertyvista.server.common.util.AddressRetriever;

public class LeaseContextSelectionServiceImpl implements LeaseContextSelectionService {

    @Override
    public void getLeaseContextChoices(AsyncCallback<Vector<LeaseContextChoiceDTO>> callback) {

        List<Lease> activeLeases = ServerSideFactory.create(CustomerFacade.class).getActiveLeases(TenantAppContext.getCurrentUser());
        Vector<LeaseContextChoiceDTO> choices = new Vector<LeaseContextChoiceDTO>(activeLeases.size());

        for (Lease lease : activeLeases) {
            LeaseContextChoiceDTO choice = EntityFactory.create(LeaseContextChoiceDTO.class);
            choice.leaseId().set(lease.createIdentityStub());

            AddressStructured address = AddressRetriever.getLeaseAddress(lease);
            choice.leasedUnitAddress().setValue(address.getStringView());

            choices.add(choice);
        }

        callback.onSuccess(choices);
    }

    @Override
    public void setLeaseContext(AsyncCallback<AuthenticationResponse> callback, Lease leaseStub) {
        callback.onSuccess(new ResidentAuthenticationServiceImpl().reAuthenticate(leaseStub));
    }

}
