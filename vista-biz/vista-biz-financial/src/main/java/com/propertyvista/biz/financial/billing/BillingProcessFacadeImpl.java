/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.property.asset.building.Building;

public class BillingProcessFacadeImpl implements BillingProcessFacade {

    @Override
    public void initializeFutureBillingCycles(final ExecutionMonitor executionMonitor) {
        for (final BillingType billingType : Persistence.service().query(EntityQueryCriteria.create(BillingType.class))) {
            try {
                //TODO change to TransactionScopeOption.Readonly
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() throws RuntimeException {
                        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
                        buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().billingCycles().$().billingType(), billingType));
                        ICursorIterator<Building> buildingIterator = Persistence.service().query(null, buildingCriteria, AttachLevel.IdOnly);
                        while (buildingIterator.hasNext()) {
                            Building building = buildingIterator.next();

                            // Find latest BillingCycle
                            EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
                            criteria.add(PropertyCriterion.eq(criteria.proto().billingType(), billingType));
                            criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
                            criteria.desc(criteria.proto().billingCycleStartDate());
                            BillingCycle latestBillingCycle = Persistence.service().retrieve(criteria);

                            Calendar createUntill = new GregorianCalendar();
                            createUntill.setTime(Persistence.service().getTransactionSystemTime());
                            createUntill.add(Calendar.MONTH, 1);

                            while (latestBillingCycle.billingCycleStartDate().getValue().before(createUntill.getTime())) {
                                latestBillingCycle = BillingManager.getSubsiquentBillingCycle(latestBillingCycle);
                            }

                        }
                        return null;
                    }

                });
                executionMonitor.addProcessedEvent("Billing Types");
            } catch (Throwable t) {
                executionMonitor.addErredEvent("Billing Types", t);
            }
        }
    }

    /**
     * Runs all Billing Cycles which executionTargetDate is the same as date.
     * 
     * @param date
     *            - executionTargetDate
     * @param dynamicStatisticsRecord
     */
    @Override
    public void runBilling(LogicalDate date, ExecutionMonitor executionMonitor) {
        EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().executionTargetDate(), date));
        ICursorIterator<BillingCycle> billingCycleIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        while (billingCycleIterator.hasNext()) {
            BillingManager.runBilling(billingCycleIterator.next(), executionMonitor);
        }
    }
}
