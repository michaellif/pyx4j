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
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.web.services.profile;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.portal.rpc.portal.web.dto.ResidentSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.services.profile.ResidentSummaryService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.AddressRetriever;

public class ResidentSummaryServiceImpl implements ResidentSummaryService {

    @Override
    public void retreiveProfileSummary(AsyncCallback<ResidentSummaryDTO> callback) {
        ResidentSummaryDTO profileSummary = EntityFactory.create(ResidentSummaryDTO.class);

        LeaseTermTenant tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenantInLease.leaseTermV());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit().floorplan());
        Persistence.service().retrieve(tenantInLease.leaseParticipant().customer().picture());

        profileSummary.tenantName().setValue(tenantInLease.leaseParticipant().customer().person().name().getStringView());
        profileSummary.floorplanName().set(tenantInLease.leaseTermV().holder().lease().unit().floorplan().marketingName());
        profileSummary.tenantAddress().setValue(AddressRetriever.getLeaseParticipantCurrentAddress(tenantInLease).getStringView());

        profileSummary.picture().set(tenantInLease.leaseParticipant().customer().picture());

        callback.onSuccess(profileSummary);

    }

}
