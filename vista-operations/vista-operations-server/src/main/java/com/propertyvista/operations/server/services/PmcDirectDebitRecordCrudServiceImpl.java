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

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Validate;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecord;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecordProcessingStatus;
import com.propertyvista.operations.rpc.services.PmcDirectDebitRecordCrudService;
import com.propertyvista.shared.VistaUserVisit;

public class PmcDirectDebitRecordCrudServiceImpl extends AbstractCrudServiceImpl<DirectDebitRecord> implements PmcDirectDebitRecordCrudService {

    public PmcDirectDebitRecordCrudServiceImpl() {
        super(DirectDebitRecord.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceListRetrieved(DirectDebitRecord entity, DirectDebitRecord dto) {
        Persistence.service().retrieve(dto.pmc());
    }

    @Override
    protected void enhanceRetrieved(DirectDebitRecord bo, DirectDebitRecord to, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);
        Persistence.service().retrieve(to.pmc());
    }

    @Override
    public void markRefunded(AsyncCallback<VoidSerializable> callback, String operationNotes, DirectDebitRecord entityId) {
        DirectDebitRecord record = Persistence.service().retrieve(DirectDebitRecord.class, entityId.getPrimaryKey());
        Validate.isEquals(DirectDebitRecordProcessingStatus.Invalid, record.processingStatus().getValue(), "Can't Refund processed records");
        record.operationsNotes().setValue(
                operationNotes + "\n" + new Date() + "\nby " + Context.getUserVisit(VistaUserVisit.class).getCurrentUser().getStringView());
        record.processingStatus().setValue(DirectDebitRecordProcessingStatus.Refunded);
        Persistence.service().persist(record);
        Persistence.service().commit();
        callback.onSuccess(null);
    }
}
