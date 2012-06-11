/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.billing;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.dto.billing.BillingCycleLeaseDTO;
import com.propertyvista.crm.rpc.services.billing.BillingCycleLeaseCrudService;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingCycleLeaseCrudServiceImpl extends AbstractCrudServiceDtoImpl<Lease, BillingCycleLeaseDTO> implements BillingCycleLeaseCrudService {

    public BillingCycleLeaseCrudServiceImpl() {
        super(Lease.class, BillingCycleLeaseDTO.class);
    }

    @Override
    protected void bind() {
        bind(dboClass, dtoProto.lease(), dboProto);
    }

    @Override
    protected void enhanceRetrieved(Lease entity, BillingCycleLeaseDTO dto) {
        EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount().id(), entity.billingAccount().getPrimaryKey()));
        dto.bills().addAll(Persistence.service().query(criteria));
    }

    @Override
    protected void persist(Lease entity, BillingCycleLeaseDTO dto) {
        throw new IllegalArgumentException();
    }
}
