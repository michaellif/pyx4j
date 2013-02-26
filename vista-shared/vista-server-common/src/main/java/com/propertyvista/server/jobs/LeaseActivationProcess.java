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
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseActivationProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(LeaseActivationProcess.class);

    private static final String EXECUTION_MONITOR_SECTION_NAME = "LeaseActivated";

    @Override
    public boolean start(PmcProcessContext context) {
        log.info("Activate Lease batch job started");
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        if (!VistaFeatures.instance().yardiIntegration()) {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().status(), Lease.Status.Approved));
            criteria.add(PropertyCriterion.le(criteria.proto().currentTerm().termFrom(), context.getForDate()));

            long total = 0;
            long failed = 0;

            ICursorIterator<Lease> i = Persistence.service().query(null, criteria, AttachLevel.IdOnly);
            LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);
            try {
                while (i.hasNext()) {
                    ++total;

                    Lease lease = i.next();
                    try {
                        leaseFacade.activate(lease);
                        Persistence.service().commit();

                        context.getExecutionMonitor().addProcessedEvent(EXECUTION_MONITOR_SECTION_NAME);
                    } catch (Throwable t) {
                        log.error("failed to activate lease id = {}:  {}", lease.getPrimaryKey(), t.getMessage());
                        Persistence.service().rollback();
                        ++failed;
                        context.getExecutionMonitor().addFailedEvent(EXECUTION_MONITOR_SECTION_NAME, t);
                    }
                }
            } finally {
                i.completeRetrieval();
            }
            log.info("{} out of {} leases were activated successfully", total - failed, total);
        }
    }

    @Override
    public void complete(PmcProcessContext context) {
        log.info("Activate Lease batch job finished");
    }

}
