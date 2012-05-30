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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.payment.pad.PadBatch;
import com.propertyvista.admin.domain.payment.pad.PadDebitRecord;
import com.propertyvista.admin.domain.payment.pad.PadFile;
import com.propertyvista.admin.domain.payment.pad.PadReconciliationDebitRecord;
import com.propertyvista.admin.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.admin.domain.payment.pad.PadReconciliationSummary;
import com.propertyvista.server.jobs.TaskRunner;

public class PaymentProcessFacadeImpl implements PaymentProcessFacade {

    @Override
    public PadFile sendPadFile() {
        return new PadCaledon().sendPadFile();
    }

    @Override
    public PadFile recivePadAcknowledgementFile() {
        return new PadCaledon().recivePadAcknowledgementFile();
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

        for (PadBatch padBatch : rejectedBatch) {
            new PadProcessor().aggregatedTransferRejected(padBatch);
        }

        Persistence.service().commit();

        if (rejectedBatch.size() == 0 && rejectedRecodrs.size() == 0) {
            Integer countBatchs = TaskRunner.runInAdminNamespace(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
                    criteria.add(PropertyCriterion.eq(criteria.proto().padFile(), padFile));
                    criteria.add(PropertyCriterion.eq(criteria.proto().pmcNamespace(), namespace));
                    return Persistence.service().count(criteria);
                }
            });
            if (countBatchs > 0) {
                return "All Accepted";
            } else {
                return null;
            }
        } else {
            return "Batch Level Reject:" + rejectedBatch.size() + "; Transaction Reject:" + rejectedRecodrs.size();
        }
    }

    @Override
    public PadReconciliationFile recivePadReconciliation() {
        return new PadCaledon().recivePadReconciliation();
    }

    @Override
    public String processPadReconciliation(final PadReconciliationFile reconciliationFile) {
        final String namespace = NamespaceManager.getNamespace();

        List<PadReconciliationSummary> transactions = TaskRunner.runInAdminNamespace(new Callable<List<PadReconciliationSummary>>() {
            @Override
            public List<PadReconciliationSummary> call() throws Exception {
                EntityQueryCriteria<PadReconciliationSummary> criteria = EntityQueryCriteria.create(PadReconciliationSummary.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().reconciliationFile(), reconciliationFile));
                criteria.add(PropertyCriterion.eq(criteria.proto().merchantAccount().pmc().namespace(), namespace));
                return Persistence.service().query(criteria);
            }
        });

        if (transactions.size() == 0) {
            return null;
        }

        int processed = 0;
        int returned = 0;
        int rejected = 0;
        int duplicate = 0;

        for (PadReconciliationSummary summary : transactions) {
            new PadProcessor().aggregatedTransferReconciliation(summary);

            for (PadReconciliationDebitRecord debitRecord : summary.records()) {
                switch (debitRecord.reconciliationStatus().getValue()) {
                case PROCESSED:
                    processed++;
                    break;
                case REJECTED:
                    rejected++;
                    break;
                case RETURNED:
                    returned++;
                    break;
                case DUPLICATE:
                    duplicate++;
                    break;
                }
            }
        }
        Persistence.service().commit();

        StringBuilder message = new StringBuilder();
        if (processed != 0) {
            message.append("Processed:").append(processed);
        }
        if (returned != 0) {
            if (message.length() > 0) {
                message.append(", ");
            }
            message.append("Returned:").append(returned);
        }
        if (rejected != 0) {
            if (message.length() > 0) {
                message.append(", ");
            }
            message.append("Rejected:").append(rejected);
        }
        if (duplicate != 0) {
            if (message.length() > 0) {
                message.append(", ");
            }
            message.append("Duplicate:").append(duplicate);
        }

        return message.toString();
    }

    @Override
    public String createPreauthorisedPayments(LogicalDate dueDate) {
        // TODO Auto-generated method stub
        // Find all Bills 
        //For Due Date (trigger target date), go over all Bills that have specified DueDate - see if this bill not yet created preauthorised payments and create

        //Check this bill is latest
        // call ar facade to get current balance for dueDate

        return null;
    }

}
