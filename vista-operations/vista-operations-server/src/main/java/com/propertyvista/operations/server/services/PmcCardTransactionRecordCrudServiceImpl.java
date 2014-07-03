/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.operations.domain.eft.cards.CardTransactionRecord;
import com.propertyvista.operations.rpc.services.PmcCardTransactionRecordCrudService;

public class PmcCardTransactionRecordCrudServiceImpl extends AbstractCrudServiceImpl<CardTransactionRecord> implements PmcCardTransactionRecordCrudService {

    public PmcCardTransactionRecordCrudServiceImpl() {
        super(CardTransactionRecord.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceListRetrieved(CardTransactionRecord entity, CardTransactionRecord dto) {
        Persistence.service().retrieve(dto.pmc());
    }

    @Override
    protected void enhanceRetrieved(CardTransactionRecord bo, CardTransactionRecord to, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);
        Persistence.service().retrieve(to.pmc());
    }
}
