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
package com.propertyvista.server.jobs;

import java.util.concurrent.Callable;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.payment.pad.PadDebitRecord;
import com.propertyvista.admin.domain.payment.pad.PadFile;
import com.propertyvista.admin.domain.scheduler.RunStats;
import com.propertyvista.biz.financial.payment.PaymentProcessFacade;

public class PadSendProcess implements PmcProcess {

    private PadFile padFile;

    @Override
    public boolean start() {
        padFile = ServerSideFactory.create(PaymentProcessFacade.class).sendPadFile();
        return (padFile != null);
    }

    @Override
    public void executePmcJob() {
        final RunStats stats = PmcProcessContext.getRunStats();
        final String namespace = NamespaceManager.getNamespace();

        Boolean updated = TaskRunner.runInAdminNamespace(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().padBatch().padFile(), padFile));
                criteria.add(PropertyCriterion.eq(criteria.proto().padBatch().pmcNamespace(), namespace));
                int records = Persistence.service().count(criteria);
                stats.total().setValue((long) records);
                stats.processed().setValue((long) records);
                return Boolean.TRUE;
            }
        });

        if (updated) {
            PmcProcessContext.setRunStats(stats);
        }
    }

    @Override
    public void complete() {
    }

}
