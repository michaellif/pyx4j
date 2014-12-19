/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 */
package com.propertyvista.portal.server.portal.resident.services.profile;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.portal.rpc.portal.resident.dto.ResidentSummaryDTO;
import com.propertyvista.portal.rpc.portal.resident.services.profile.ResidentSummaryService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;
import com.propertyvista.server.common.util.AddressRetriever;

public class ResidentSummaryServiceImpl implements ResidentSummaryService {

    @Override
    public void retreiveProfileSummary(AsyncCallback<ResidentSummaryDTO> callback) {
        ResidentSummaryDTO profileSummary = EntityFactory.create(ResidentSummaryDTO.class);

        LeaseTermParticipant<?> leaseTermParticipant = ResidentPortalContext.getLeaseTermParticipant();
        Persistence.service().retrieve(leaseTermParticipant.leaseTermV());
        Persistence.service().retrieve(leaseTermParticipant.leaseTermV().holder().lease());
        Persistence.service().retrieve(leaseTermParticipant.leaseTermV().holder().lease().unit().floorplan());
        Persistence.service().retrieve(leaseTermParticipant.leaseParticipant().customer().picture());

        profileSummary.tenantName().setValue(leaseTermParticipant.leaseParticipant().customer().person().name().getStringView());
        profileSummary.floorplanName().set(leaseTermParticipant.leaseTermV().holder().lease().unit().floorplan().marketingName());
        profileSummary.tenantAddress().setValue(AddressRetriever.getLeaseParticipantCurrentAddress(leaseTermParticipant).getStringView());

        if (leaseTermParticipant.leaseParticipant().customer().picture().hasValues()) {
            profileSummary.picture().set(leaseTermParticipant.leaseParticipant().customer().picture());
        }

        callback.onSuccess(profileSummary);

    }

}
