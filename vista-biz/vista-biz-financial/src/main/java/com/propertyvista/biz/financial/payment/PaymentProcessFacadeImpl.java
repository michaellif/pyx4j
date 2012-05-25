/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.List;
import java.util.concurrent.Callable;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.payment.pad.PadBatch;
import com.propertyvista.admin.domain.payment.pad.PadDebitRecord;
import com.propertyvista.admin.domain.payment.pad.PadFile;
import com.propertyvista.server.jobs.TaskRunner;

public class PaymentProcessFacadeImpl implements PaymentProcessFacade {

    @Override
    public PadFile sendPadFile() {
        return new PadCaledon().sendPadFile();
    }

    @Override
    public PadFile recivePadAcknowledgementFiles() {
        return new PadCaledon().recivePadAcknowledgementFiles();
    }

    @Override
    public String processAcknowledgement(final PadFile padFile) {
        final String namespace = NamespaceManager.getNamespace();

        List<PadDebitRecord> rejectedRecodrs = TaskRunner.runInAdminNamespace(new Callable<List<PadDebitRecord>>() {
            @Override
            public List<PadDebitRecord> call() throws Exception {
                EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().padBatch().padFile(), padFile));
                criteria.add(PropertyCriterion.eq(criteria.proto().padBatch().pmcNamespace(), namespace));
                criteria.add(PropertyCriterion.isNotNull(criteria.proto().acknowledgmentStatusCode()));
                return Persistence.service().query(criteria);
            }
        });

        for (PadDebitRecord debitRecord : rejectedRecodrs) {
            new PadProcessor().acknowledgmentReject(debitRecord);
        }

        List<PadBatch> rejectedBatch = TaskRunner.runInAdminNamespace(new Callable<List<PadBatch>>() {
            @Override
            public List<PadBatch> call() throws Exception {
                EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().padFile(), padFile));
                criteria.add(PropertyCriterion.eq(criteria.proto().pmcNamespace(), namespace));
                criteria.add(PropertyCriterion.isNotNull(criteria.proto().acknowledgmentStatusCode()));
                return Persistence.service().query(criteria);
            }
        });

        Persistence.service().commit();

        if (rejectedBatch.size() == 0 && rejectedRecodrs.size() == 0) {
            return "All Accepted";
        } else {
            return "Batch Level Reject:" + rejectedBatch.size() + "; Transaction Reject:" + rejectedRecodrs.size();
        }
    }
}
