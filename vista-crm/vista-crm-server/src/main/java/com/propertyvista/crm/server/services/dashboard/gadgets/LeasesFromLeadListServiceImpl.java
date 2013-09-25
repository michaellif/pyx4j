/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.AbstractListServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeasesFromLeadListService;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public class LeasesFromLeadListServiceImpl extends AbstractListServiceDtoImpl<Lead, Lead> implements LeasesFromLeadListService {

    public LeasesFromLeadListServiceImpl() {
        super(Lead.class, Lead.class);
    }

    @Override
    protected void bind() {
        bind(toProto.createDate(), boProto.createDate());
        bind(toProto.lease(), boProto.lease());
    }

    @Override
    protected void enhanceListRetrieved(Lead entity, Lead dto) {
        super.enhanceListRetrieved(entity, dto);
        Persistence.service().retrieve(dto.lease());

        Persistence.service().retrieve(dto.lease().unit());
        Persistence.service().retrieve(dto.lease().unit().building());

        if (!dto.lease().currentTerm().isNull()) {
            Persistence.service().retrieve(dto.lease().currentTerm());
            if (dto.lease().currentTerm().version().isNull()) {
                dto.lease().currentTerm().set(Persistence.secureRetrieveDraft(LeaseTerm.class, dto.lease().currentTerm().getPrimaryKey()));
            }

            Persistence.service().retrieve(dto.lease().currentTerm().version().tenants());
            Persistence.service().retrieve(dto.lease().currentTerm().version().guarantors());
        }
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new IllegalStateException("'delete' operation is not supported");
    }

}
