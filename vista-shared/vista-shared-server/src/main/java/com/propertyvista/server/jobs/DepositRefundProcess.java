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
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.domain.tenant.lease.Lease;

public class DepositRefundProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(DepositRefundProcess.class);

    @Override
    public boolean start(PmcProcessContext context) {
        log.info("Deposit Refund batch job started");
        return true;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return !features.yardiIntegration().getValue(false);
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.add(PropertyCriterion.in(criteria.proto().status(), Lease.Status.current()));

        final DepositFacade depositFacade = ServerSideFactory.create(DepositFacade.class);
        for (final Lease lease : Persistence.service().query(criteria)) {
            try {
                new UnitOfWork().execute(new Executable<Void, RuntimeException>() {
                    @Override
                    public Void execute() {
                        depositFacade.issueDepositRefunds(lease);
                        return null;
                    }
                });

                context.getExecutionMonitor().addProcessedEvent("Deposit");
            } catch (Throwable t) {
                context.getExecutionMonitor().addFailedEvent("Deposit", t);
            }
        }
        log.info(context.getExecutionMonitor().toString());
    }

    @Override
    public void complete(PmcProcessContext context) {
        log.info("Deposit Refund batch job finished");
    }

}
