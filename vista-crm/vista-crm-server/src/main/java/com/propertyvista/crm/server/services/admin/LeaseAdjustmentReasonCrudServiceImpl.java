/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.admin;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.crm.rpc.services.admin.LeaseAdjustmentReasonCrudService;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class LeaseAdjustmentReasonCrudServiceImpl extends AbstractCrudServiceImpl<LeaseAdjustmentReason> implements LeaseAdjustmentReasonCrudService {

    public LeaseAdjustmentReasonCrudServiceImpl() {
        super(LeaseAdjustmentReason.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceRetrieved(LeaseAdjustmentReason entity, LeaseAdjustmentReason dto, RetrieveTraget retrieveTraget) {
        super.enhanceRetrieved(entity, dto, retrieveTraget);

        // TODO fill taxes from policy here, but which policy scope to use? 
    }

    @Override
    protected void enhanceListRetrieved(LeaseAdjustmentReason entity, LeaseAdjustmentReason dto) {
        super.enhanceListRetrieved(entity, dto);
    }
}
