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

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.crm.rpc.services.billing.BillingCycleCrudService;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.BillingCycle;

public class BillingCycleCrudServiceImpl extends AbstractCrudServiceDtoImpl<BillingCycle, BillingCycleDTO> implements BillingCycleCrudService {

    public BillingCycleCrudServiceImpl() {
        super(BillingCycle.class, BillingCycleDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(BillingCycle entity, BillingCycleDTO dto, RetrieveTraget retrieveTraget) {
        enhanceListRetrieved(entity, dto);
        Persistence.service().retrieve(dto.building(), AttachLevel.ToStringMembers);

        // calculate statistics on leases:
        // Total:
        EntityQueryCriteria<BillingAccount> criteriaTotal = EntityQueryCriteria.create(BillingAccount.class);
        criteriaTotal.add(PropertyCriterion.eq(criteriaTotal.proto().billingType(), entity.billingType()));
        dto.total().setValue((long) Persistence.service().query(criteriaTotal).size());

        // not Run:
        EntityQueryCriteria<BillingAccount> criteriaNotRun = EntityQueryCriteria.create(BillingAccount.class);
        criteriaNotRun.add(PropertyCriterion.eq(criteriaNotRun.proto().billingType(), entity.billingType()));
        criteriaNotRun.add(PropertyCriterion.eq(criteriaNotRun.proto().bills().$().billingCycle(), entity));
        criteriaNotRun.add(PropertyCriterion.notExists(criteriaNotRun.proto().bills()));

        dto.notRun().setValue((long) Persistence.service().query(criteriaNotRun).size());
    }

    @Override
    protected void enhanceListRetrieved(BillingCycle entity, BillingCycleDTO dto) {

    }

    @Override
    protected void persist(BillingCycle entity, BillingCycleDTO dto) {
        throw new IllegalArgumentException();
    }
}
