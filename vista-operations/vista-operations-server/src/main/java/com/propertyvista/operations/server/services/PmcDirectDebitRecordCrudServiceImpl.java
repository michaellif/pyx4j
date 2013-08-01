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

import com.propertyvista.operations.domain.payment.dbp.DirectDebitRecord;
import com.propertyvista.operations.rpc.services.PmcDirectDebitRecordCrudService;

public class PmcDirectDebitRecordCrudServiceImpl extends AbstractCrudServiceImpl<DirectDebitRecord> implements PmcDirectDebitRecordCrudService {

    public PmcDirectDebitRecordCrudServiceImpl() {
        super(DirectDebitRecord.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceListRetrieved(DirectDebitRecord entity, DirectDebitRecord dto) {
        Persistence.service().retrieve(dto.pmc());
    }

}
