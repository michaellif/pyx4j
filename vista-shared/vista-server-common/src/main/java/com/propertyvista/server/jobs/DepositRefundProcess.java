/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 22, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.domain.tenant.lease.Lease;

public class DepositRefundProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(DepositRefundProcess.class);

    @Override
    public boolean start(PmcProcessContext context) {
        log.info("Deposit Refund batch job started");
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.add(PropertyCriterion.in(criteria.proto().status(), Lease.Status.current()));

        long total = 0;
        long failed = 0;

        DepositFacade depositFacade = ServerSideFactory.create(DepositFacade.class);
        for (Lease lease : Persistence.service().query(criteria)) {
            ++total;

            try {
                depositFacade.issueDepositRefunds(lease);
                Persistence.service().commit();

                StatisticsUtils.addProcessed(context.getRunStats(), 1);
            } catch (Throwable error) {
                log.error("failed to issue refunds for lease id = {}:  {}", lease.getPrimaryKey(), error.getMessage());
                Persistence.service().rollback();
                ++failed;
                StatisticsUtils.addFailed(context.getRunStats(), 1);
            }
        }
        log.info("{} out of {} leases were processed successfully", total - failed, total);
    }

    @Override
    public void complete(PmcProcessContext context) {
        log.info("Deposit Refund batch job finished");
    }

}
