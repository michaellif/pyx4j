/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.billing;

import com.pyx4j.entity.server.AbstractListServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.billing.BillingCycleLeaseListService;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;

public class BillingCycleLeaseListServiceImpl extends AbstractListServiceDtoImpl<Lease, LeaseDTO> implements BillingCycleLeaseListService {

    public BillingCycleLeaseListServiceImpl() {
        super(Lease.class, LeaseDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceListRetrieved(Lease in, LeaseDTO dto) {
        // load detached entities:
        Persistence.service().retrieve(dto.unit());
        Persistence.service().retrieve(dto.unit().building());

        Persistence.service().retrieve(dto.currentTerm().version().tenants());
        Persistence.service().retrieve(dto.currentTerm().version().guarantors());

        Persistence.service().retrieve(dto.billingAccount());
    }
}