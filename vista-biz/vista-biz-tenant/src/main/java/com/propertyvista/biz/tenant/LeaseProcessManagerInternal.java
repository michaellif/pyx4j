/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.AndCriterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeaseProcessManagerInternal {

    public void leaseActivation(ExecutionMonitor executionMonitor, LogicalDate date) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), Lease.Status.Approved));
        criteria.add(PropertyCriterion.le(criteria.proto().currentTerm().termFrom(), date));

        ICursorIterator<Lease> i = Persistence.service().query(null, criteria, AttachLevel.IdOnly);
        final LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);
        try {
            while (i.hasNext()) {
                final Lease lease = i.next();
                try {
                    new UnitOfWork().execute(new Executable<Void, RuntimeException>() {
                        @Override
                        public Void execute() {
                            leaseFacade.activate(lease);
                            return null;
                        }
                    });

                    executionMonitor.addProcessedEvent("Lease Activation");
                } catch (Throwable error) {
                    executionMonitor.addFailedEvent("Lease Activation", error);
                }
            }
        } finally {
            i.close();
        }
    }

    public void leaseRenewal(ExecutionMonitor executionMonitor, LogicalDate date) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), Lease.Status.Active));
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().nextTerm()));
        criteria.add(PropertyCriterion.le(criteria.proto().nextTerm().termFrom(), date));

        ICursorIterator<Lease> i = Persistence.service().query(null, criteria, AttachLevel.IdOnly);
        final LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);
        try {
            while (i.hasNext()) {
                final Lease lease = i.next();
                try {
                    new UnitOfWork().execute(new Executable<Void, RuntimeException>() {
                        @Override
                        public Void execute() {
                            leaseFacade.renew(lease);
                            return null;
                        }
                    });

                    executionMonitor.addProcessedEvent("Lease Renewal");
                } catch (Throwable error) {
                    executionMonitor.addFailedEvent("Lease Renewal", error);
                }
            }
        } finally {
            i.close();
        }
    }

    public void leaseCompletion(ExecutionMonitor executionMonitor, LogicalDate date) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), Lease.Status.Active));
        criteria.add(PropertyCriterion.lt(criteria.proto().leaseTo(), date));
        //@formatter:off
        criteria.or(PropertyCriterion.isNull(criteria.proto().nextTerm()),                              // not renewed OR
                    new AndCriterion(PropertyCriterion.isNotNull(criteria.proto().nextTerm()),          // renewed but moving out
                                     PropertyCriterion.isNotNull(criteria.proto().completion())));
        //@formatter:on

        ICursorIterator<Lease> i = Persistence.service().query(null, criteria, AttachLevel.IdOnly);
        final LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);
        try {
            while (i.hasNext()) {
                final Lease lease = i.next();
                try {
                    new UnitOfWork().execute(new Executable<Void, RuntimeException>() {
                        @Override
                        public Void execute() {
                            leaseFacade.complete(lease);
                            return null;
                        }
                    });

                    executionMonitor.addProcessedEvent("Lease Completion");
                } catch (Throwable error) {
                    executionMonitor.addFailedEvent("Lease Completion", error);
                }
            }
        } finally {
            i.close();
        }
    }
}
