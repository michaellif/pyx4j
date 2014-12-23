/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2014
 * @author stanp
 */
package com.propertyvista.biz.legal.eviction;

import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.eviction.EvictionCase;
import com.propertyvista.domain.eviction.EvictionDocument;
import com.propertyvista.domain.eviction.EvictionStatus;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep;
import com.propertyvista.domain.tenant.lease.Lease;

public class EvictionCaseFacadeImpl implements EvictionCaseFacade {

    @Override
    public EvictionStatus getCurrentEvictionStatus(Lease leaseId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EvictionCase getCurrentEvictionCase(Lease leaseId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EvictionCase> getEvictionHistory(Lease leaseId) {
        EntityQueryCriteria<EvictionCase> crit = EntityQueryCriteria.create(EvictionCase.class);
        crit.eq(crit.proto().lease(), leaseId);
        return Persistence.service().query(crit);
    }

    @Override
    public void addEvictionStatusDetails(Lease leaseId, EvictionFlowStep evictionStep, String note, List<EvictionDocument> attachments) {
        // TODO Auto-generated method stub

    }

}
