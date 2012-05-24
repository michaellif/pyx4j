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

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.crm.rpc.services.billing.LeaseAdjustmentCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class LeaseAdjustmentCrudServiceImpl extends AbstractCrudServiceImpl<LeaseAdjustment> implements LeaseAdjustmentCrudService {

    public LeaseAdjustmentCrudServiceImpl() {
        super(LeaseAdjustment.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void persist(LeaseAdjustment dbo, LeaseAdjustment dto) {
        updateAdjustments(dbo);
        super.persist(dbo, dto);
    }

    private void updateAdjustments(LeaseAdjustment adj) {

        if (adj.created().isNull()) {
            adj.createdBy().set(CrmAppContext.getCurrentUserEmployee());
        }
    }
}
