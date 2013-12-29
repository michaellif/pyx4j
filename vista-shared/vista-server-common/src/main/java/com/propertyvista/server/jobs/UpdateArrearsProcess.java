/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.domain.tenant.lease.Lease;

public class UpdateArrearsProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(UpdateArrearsProcess.class);

    private static final String EXECUTION_MONITOR_SECTION_NAME = "ArrearsUpdated";

    @Override
    public boolean start(PmcProcessContext context) {
        log.info("Arrears Update job started");
        return true;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        updateBillingAccountsArrears(context);
        updateBuildingArrears(context);
    }

    public void updateBillingAccountsArrears(PmcProcessContext context) {
        log.info("Arrears Update for billing accounts started");

        EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
        criteria.add(PropertyCriterion.ne(criteria.proto().lease().status(), Lease.Status.Closed));
        final Iterator<BillingAccount> billingAccounts = Persistence.service().query(null, criteria, AttachLevel.IdOnly);

        long currentBillingAccount = 0L;
        long failed = 0L;

        while (billingAccounts.hasNext()) {
            ++currentBillingAccount;

            try {
                new UnitOfWork().execute(new Executable<Void, RuntimeException>() {
                    @Override
                    public Void execute() {
                        ServerSideFactory.create(ARFacade.class).updateArrearsHistory(billingAccounts.next());
                        return null;
                    }
                });

                context.getExecutionMonitor().addProcessedEvent(EXECUTION_MONITOR_SECTION_NAME);
            } catch (Throwable t) {
                log.error("failed to update arrears history", t);
                failed++;
                context.getExecutionMonitor().addFailedEvent(EXECUTION_MONITOR_SECTION_NAME, t);
            }

        }
        log.info(SimpleMessageFormat.format("Arrears Update for billing accounts finished, processed {0} billing accounts, {1} FAILED", currentBillingAccount,
                failed));

    }

    public void updateBuildingArrears(PmcProcessContext context) {
        log.info("Arrears Update for buildings started");
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        final Iterator<Building> buildings = Persistence.service().query(null, criteria, AttachLevel.IdOnly);

        long current = 0L;
        long failed = 0L;
        while (buildings.hasNext()) {
            ++current;
            try {
                new UnitOfWork().execute(new Executable<Void, RuntimeException>() {
                    @Override
                    public Void execute() {
                        ServerSideFactory.create(ARFacade.class).updateArrearsHistory(buildings.next());
                        return null;
                    }
                });

                context.getExecutionMonitor().addProcessedEvent(EXECUTION_MONITOR_SECTION_NAME);
            } catch (Throwable t) {
                log.error("failed to update arrears history: {}", t.getMessage());
                Persistence.service().rollback();
                failed++;
                context.getExecutionMonitor().addFailedEvent(EXECUTION_MONITOR_SECTION_NAME, t);
            }
        }

        log.info(SimpleMessageFormat.format("Arrears Update for buildings finished, processed {0} buildings, {1} FAILED", current, failed));
    }

    @Override
    public void complete(PmcProcessContext context) {
        log.info("Arrears Update job finished");
    }

}
