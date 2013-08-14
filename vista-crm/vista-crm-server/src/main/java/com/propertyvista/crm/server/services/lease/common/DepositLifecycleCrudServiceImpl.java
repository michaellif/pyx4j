/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease.common;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.crm.rpc.services.lease.common.DepositLifecycleCrudService;
import com.propertyvista.domain.tenant.lease.DepositLifecycle;
import com.propertyvista.dto.DepositLifecycleDTO;

public class DepositLifecycleCrudServiceImpl extends AbstractCrudServiceDtoImpl<DepositLifecycle, DepositLifecycleDTO> implements DepositLifecycleCrudService {

    public DepositLifecycleCrudServiceImpl() {
        super(DepositLifecycle.class, DepositLifecycleDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceRetrieved(DepositLifecycle entity, DepositLifecycleDTO dto, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(entity, dto, retrieveTarget);
        enhanceListRetrieved(entity, dto);

        // load detached:
        Persistence.service().retrieve(dto.interestAdjustments());
    }

    @Override
    protected void enhanceListRetrieved(DepositLifecycle entity, DepositLifecycleDTO dto) {
        super.enhanceListRetrieved(entity, dto);

        // find corresponding deposit:
        dto.deposit().set(ServerSideFactory.create(DepositFacade.class).getDeposit(entity));

        // load detached:
        Persistence.service().retrieve(dto.deposit());
        Persistence.service().retrieve(dto.deposit().billableItem());
    }
}
