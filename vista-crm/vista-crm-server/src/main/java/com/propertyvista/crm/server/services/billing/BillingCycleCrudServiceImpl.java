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

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.crm.rpc.services.billing.BillingCycleCrudService;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;

public class BillingCycleCrudServiceImpl extends AbstractCrudServiceDtoImpl<BillingCycle, BillingCycleDTO> implements BillingCycleCrudService {

    public BillingCycleCrudServiceImpl() {
        super(BillingCycle.class, BillingCycleDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceListRetrieved(BillingCycle bo, BillingCycleDTO to) {
        if (bo.stats().getAttachLevel() == AttachLevel.Detached) {
            Persistence.service().retrieveMember(bo.stats());
            to.stats().set(bo.stats());
        }

        calcStatistics(bo, to);

        Persistence.service().retrieve(to.building(), AttachLevel.ToStringMembers, false);
    }

    @Override
    protected void enhanceRetrieved(BillingCycle bo, BillingCycleDTO to, RetrieveTarget retrieveTarget) {
        enhanceListRetrieved(bo, to);

        calcStatistics(bo, to);
    }

    @Override
    protected void persist(BillingCycle bo, BillingCycleDTO to) {
        throw new IllegalArgumentException();
    }

    private void calcStatistics(BillingCycle bo, BillingCycleDTO to) {
        // Total:
        EntityQueryCriteria<BillingAccount> criteriaTotal = EntityQueryCriteria.create(BillingAccount.class);
        criteriaTotal.add(PropertyCriterion.eq(criteriaTotal.proto().billingType(), bo.billingType()));
        to.total().setValue((long) Persistence.service().count(criteriaTotal));

        // not Run:
        EntityQueryCriteria<BillingAccount> criteriaNotRun = EntityQueryCriteria.create(BillingAccount.class);
        criteriaNotRun.add(PropertyCriterion.eq(criteriaNotRun.proto().billingType(), bo.billingType()));
        criteriaNotRun.add(PropertyCriterion.eq(criteriaNotRun.proto().bills().$().billingCycle(), bo));
        criteriaNotRun.add(PropertyCriterion.notExists(criteriaNotRun.proto().bills()));
        to.notRun().setValue((long) Persistence.service().count(criteriaNotRun));

        // PADs:
        EntityQueryCriteria<PaymentRecord> criteriaPADs = EntityQueryCriteria.create(PaymentRecord.class);
        criteriaPADs.eq(criteriaPADs.proto().padBillingCycle(), bo);
        to.pads().setValue((long) Persistence.service().count(criteriaPADs));
    }
}
