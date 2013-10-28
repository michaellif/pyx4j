/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 30, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import com.pyx4j.commons.Filter;
import com.pyx4j.commons.FilterIterator;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingProcessManager {

    private BillingProcessManager() {
    }

    private static class SingletonHolder {
        public static final BillingProcessManager INSTANCE = new BillingProcessManager();
    }

    static BillingProcessManager instance() {
        return SingletonHolder.INSTANCE;
    }

    void initializeFutureBillingCycles(final ExecutionMonitor executionMonitor) {
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
                            createUntill.setTime(SystemDateManager.getDate());
                            createUntill.add(Calendar.MONTH, 1);

                            while (latestBillingCycle.billingCycleStartDate().getValue().before(createUntill.getTime())) {
                                latestBillingCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(latestBillingCycle);
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
    void runBilling(LogicalDate date, ExecutionMonitor executionMonitor) {
        EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().targetBillExecutionDate(), date));
        ICursorIterator<BillingCycle> billingCycleIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        while (billingCycleIterator.hasNext()) {
            runBilling(billingCycleIterator.next(), executionMonitor);
        }
    }

    void runBilling(final BillingCycle billingCycle, ExecutionMonitor executionMonitor) {
        EntityQueryCriteria<Lease> leaseCriteria = EntityQueryCriteria.create(Lease.class);
        leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().unit().building(), billingCycle.building()));
        leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().billingAccount().billingType(), billingCycle.billingType()));
        leaseCriteria.add(PropertyCriterion.in(leaseCriteria.proto().status(), Lease.Status.Active));

        ICursorIterator<Lease> leaseIterator = Persistence.service().query(null, leaseCriteria, AttachLevel.Attached);
        FilterIterator<Lease> filteredLeaseIterator = new FilterIterator<Lease>(leaseIterator, new Filter<Lease>() {
            @Override
            public boolean accept(Lease lease) {
                //Don't run bill on cycle that out of boundaries of lease end
                return billingCycle.billingCycleStartDate().compareTo(lease.leaseTo()) <= 0;
            }
        });
        runBilling(billingCycle, filteredLeaseIterator, executionMonitor);
    }

    private void runBilling(final BillingCycle billingCycle, final Iterator<Lease> leasesIterator, final ExecutionMonitor executionMonitor) {
        while (leasesIterator.hasNext()) {
            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() throws RuntimeException {
                        Bill bill = ServerSideFactory.create(BillingFacade.class).runBilling(leasesIterator.next(), billingCycle);
                        if (bill.billStatus().getValue() == Bill.BillStatus.Finished) {
                            executionMonitor.addProcessedEvent("Bill", bill.totalDueAmount().getValue(), null);
                        } else {
                            executionMonitor.addFailedEvent("Bill", "Bill failed");
                        }
                        return null;
                    }
                });
            } catch (Throwable t) {
                executionMonitor.addErredEvent("Billing Types", t);
            }
        }
    }

}
