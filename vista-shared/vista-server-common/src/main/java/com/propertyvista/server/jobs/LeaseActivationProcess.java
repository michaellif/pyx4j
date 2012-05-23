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

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeaseActivationProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(LeaseActivationProcess.class);

    @Override
    public boolean start() {
        log.info("Activate Lease batch job started");
        return true;
    }

    @Override
    public void executePmcJob() {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().version().status(), Lease.Status.Approved));
        criteria.add(PropertyCriterion.le(criteria.proto().leaseFrom(), Persistence.service().getTransactionSystemTime()));

        final long total = Persistence.service().count(criteria);
        long current = 0;
        long failed = 0;
        PmcProcessContext.getRunStats().total().setValue(total);
        PmcProcessContext.getRunStats().failed().setValue(0L);

        Iterator<Lease> i = Persistence.service().query(null, criteria, AttachLevel.IdOnly);
        LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);
        while (i.hasNext()) {
            ++current;

            Lease lease = i.next();
            try {
                leaseFacade.activate(lease.getPrimaryKey());
                Persistence.service().commit();

                PmcProcessContext.getRunStats().processed().setValue(current);
                PmcProcessContext.setRunStats(PmcProcessContext.getRunStats());
            } catch (Throwable error) {
                log.error("failed to activate lease id = {}:  {}", lease.getPrimaryKey(), error.getMessage());

                Persistence.service().rollback();

                ++failed;
                PmcProcessContext.getRunStats().processed().setValue(current);
                PmcProcessContext.getRunStats().failed().setValue(failed);

                PmcProcessContext.setRunStats(PmcProcessContext.getRunStats());
            }
        }
        log.info("{} out of {} leases were activated successfully", current - failed, total);
    }

    @Override
    public void complete() {
        log.info("Activate Lease batch job finished");
    }

}
