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
package com.propertyvista.portal.server.portal.resident.services;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.resident.dto.LeaseContextChoiceDTO;
import com.propertyvista.portal.rpc.portal.resident.services.LeaseContextSelectionService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;
import com.propertyvista.server.common.util.AddressRetriever;

public class LeaseContextSelectionServiceImpl implements LeaseContextSelectionService {

    @Override
    public void getLeaseContextChoices(AsyncCallback<Vector<LeaseContextChoiceDTO>> callback) {

        List<Lease> activeLeases = ServerSideFactory.create(CustomerFacade.class).getActiveLeasesId(ResidentPortalContext.getCustomerUserIdStub());
        Vector<LeaseContextChoiceDTO> choices = new Vector<LeaseContextChoiceDTO>(activeLeases.size());

        for (Lease lease : activeLeases) {
            LeaseContextChoiceDTO choice = EntityFactory.create(LeaseContextChoiceDTO.class);

            choice.leaseId().set(lease.createIdentityStub());
            choice.leasedUnitAddress().setValue(AddressRetriever.getLeaseAddress(lease).getStringView());
            choice.leaseFrom().setValue(lease.leaseFrom().getValue());
            choice.leaseTo().setValue(lease.leaseTo().getValue());
            choice.status().setValue(lease.status().getValue());

            choices.add(choice);
        }

        callback.onSuccess(choices);
    }

    @Override
    public void setLeaseContext(AsyncCallback<VoidSerializable> callback, Lease leaseStub) {
        ResidentPortalContext.setLease(leaseStub);
        ServerContext.getVisit().setAclRevalidationRequired();
        callback.onSuccess(null);
    }
}
