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
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseRenewalProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(LeaseRenewalProcess.class);

    @Override
    public boolean start(PmcProcessContext context) {
        log.info("Renew Lease batch job started");
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        if (!VistaFeatures.instance().yardiIntegration()) {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().status(), Lease.Status.Active));
            criteria.add(PropertyCriterion.isNotNull(criteria.proto().nextTerm()));
            criteria.add(PropertyCriterion.le(criteria.proto().nextTerm().termFrom(), context.getForDate()));

            long total = 0;
            long failed = 0;

            Iterator<Lease> i = Persistence.service().query(null, criteria, AttachLevel.IdOnly);
            LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);
            while (i.hasNext()) {
                ++total;

                Lease lease = i.next();
                try {
                    leaseFacade.renew(lease);
                    Persistence.service().commit();

                    StatisticsUtils.addProcessed(context.getRunStats(), 1);
                } catch (Throwable error) {
                    log.error("failed to renew lease id = {}:  {}", lease.getPrimaryKey(), error.getMessage());
                    Persistence.service().rollback();
                    ++failed;
                    StatisticsUtils.addFailed(context.getRunStats(), 1);
                }
            }
            log.info("{} out of {} leases were renewed successfully", total - failed, total);
        }
    }

    @Override
    public void complete(PmcProcessContext context) {
        log.info("Renew Lease batch job finished");
    }

}
