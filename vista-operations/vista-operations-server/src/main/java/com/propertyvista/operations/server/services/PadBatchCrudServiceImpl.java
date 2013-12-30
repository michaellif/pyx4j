/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.operations.domain.payment.pad.FundsTransferBatch;
import com.propertyvista.operations.rpc.dto.FundsTransferBatchDTO;
import com.propertyvista.operations.rpc.services.PadBatchCrudService;

public class PadBatchCrudServiceImpl extends AbstractCrudServiceDtoImpl<FundsTransferBatch, FundsTransferBatchDTO> implements PadBatchCrudService {

    public PadBatchCrudServiceImpl() {
        super(FundsTransferBatch.class, FundsTransferBatchDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(FundsTransferBatch bo, FundsTransferBatchDTO to, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieve(to.pmc());
    }

    @Override
    protected void enhanceListRetrieved(FundsTransferBatch entity, FundsTransferBatchDTO dto) {
        Persistence.service().retrieve(dto.pmc());
    }

}
