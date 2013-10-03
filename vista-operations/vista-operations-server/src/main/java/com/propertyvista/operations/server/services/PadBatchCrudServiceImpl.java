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

import com.propertyvista.operations.domain.payment.pad.PadBatch;
import com.propertyvista.operations.rpc.dto.PadBatchDTO;
import com.propertyvista.operations.rpc.services.PadBatchCrudService;

public class PadBatchCrudServiceImpl extends AbstractCrudServiceDtoImpl<PadBatch, PadBatchDTO> implements PadBatchCrudService {

    public PadBatchCrudServiceImpl() {
        super(PadBatch.class, PadBatchDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(PadBatch bo, PadBatchDTO to, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieve(to.pmc());
    }

    @Override
    protected void enhanceListRetrieved(PadBatch entity, PadBatchDTO dto) {
        Persistence.service().retrieve(dto.pmc());
    }

}
