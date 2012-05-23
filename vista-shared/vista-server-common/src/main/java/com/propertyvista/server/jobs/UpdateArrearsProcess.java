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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public class UpdateArrearsProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(UpdateArrearsProcess.class);

    @Override
    public boolean start() {
        log.info("Arrears Update job started");
        return true;
    }

    @Override
    public void executePmcJob() {
        updateBillingAccountsArrears();
        updateBuildingArrears();
    }

    public void updateBillingAccountsArrears() {
        log.info("Arrears Update for billing accounts started");

        EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
        criteria.add(PropertyCriterion.ne(criteria.proto().lease().version().status(), Lease.Status.Closed));

        long numOfBillingAccounts = Persistence.service().count(criteria);
        log.debug(SimpleMessageFormat.format("Found {0} billing account that belong to not closed leases", numOfBillingAccounts));

        Iterator<BillingAccount> billingAccounts = Persistence.service().query(null, criteria, AttachLevel.IdOnly);

        ARFacade facade = ServerSideFactory.create(ARFacade.class);

        long currentBillingAccount = 0L;
        long failed = 0L;

        PmcProcessContext.getRunStats().total().setValue(numOfBillingAccounts);
        PmcProcessContext.getRunStats().failed().setValue(0L);

        while (billingAccounts.hasNext()) {
            ++currentBillingAccount;

            try {
                facade.updateArrearsHistory(billingAccounts.next());
                Persistence.service().commit();
                PmcProcessContext.getRunStats().processed().setValue(currentBillingAccount);
                PmcProcessContext.setRunStats(PmcProcessContext.getRunStats());
            } catch (Throwable caught) {
                log.error("failed to update arrears history: {}", caught.getMessage());
                Persistence.service().rollback();
                PmcProcessContext.getRunStats().processed().setValue(currentBillingAccount);
                PmcProcessContext.getRunStats().failed().setValue(++failed);
                PmcProcessContext.setRunStats(PmcProcessContext.getRunStats());
            }

        }
        log.info(SimpleMessageFormat.format("Arrears Update for billing accounts finished, processed {0} billignAccounts, {1} FAILED", numOfBillingAccounts,
                failed));
    }

    private void updateBuildingArrears() {
        log.info("Arrears Update for buildings started");

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        long total = Persistence.service().count(criteria);

        Iterator<Building> buildings = Persistence.service().query(null, criteria, AttachLevel.IdOnly);

        ARFacade facade = ServerSideFactory.create(ARFacade.class);

        long current = 0L;
        long failed = 0L;

        PmcProcessContext.getRunStats().total().setValue(total);
        PmcProcessContext.getRunStats().failed().setValue(0L);

        while (buildings.hasNext()) {
            ++current;
            try {
                facade.updateArrearsHistory(buildings.next());
                Persistence.service().commit();
                PmcProcessContext.getRunStats().processed().setValue(current);
                PmcProcessContext.setRunStats(PmcProcessContext.getRunStats());
            } catch (Throwable caught) {
                log.error("failed to update arrears history: {}", caught.getMessage());
                Persistence.service().rollback();
                PmcProcessContext.getRunStats().processed().setValue(current);
                PmcProcessContext.getRunStats().failed().setValue(++failed);
                PmcProcessContext.setRunStats(PmcProcessContext.getRunStats());

            }
        }

        log.info(SimpleMessageFormat.format("Arrears Update for buildings finished, processed {0} billignAccounts, {1} FAILED", current, failed));
    }

    @Override
    public void complete() {
        log.info("Arrears Update job finished");
    }

}
