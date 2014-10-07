/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 7, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident.services.financial;

import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.system.yardi.YardiARFacade;
import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.tenant.lease.Lease;

class LeaseYardiUpdateDeferredProcess extends AbstractDeferredProcess {

    private final static Logger log = LoggerFactory.getLogger(LeaseYardiUpdateDeferredProcess.class);

    private static final long serialVersionUID = 1L;

    private final Lease lease;

    private final ExecutionMonitor monitor;

    LeaseYardiUpdateDeferredProcess(Lease lease) {
        this.lease = lease;
        monitor = new ExecutionMonitor();
    }

    public LeaseYardiUpdateDeferredProcess(PaymentRecord paymentRecordId) {
        this(getLease(paymentRecordId));
    }

    private static Lease getLease(PaymentRecord paymentRecordId) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.eq(criteria.proto().billingAccount().payments(), paymentRecordId);
        return Persistence.service().retrieve(criteria);
    }

    @Override
    public void execute() {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {
                try {
                    ServerSideFactory.create(YardiARFacade.class).updateLease(lease, monitor);
                } catch (RemoteException e) {
                    log.debug("Yardi connection problem", e);
                } catch (YardiServiceException e) {
                    log.debug("Error updating lease form Yardi", e);
                }
                return null;
            }
        });
        completed = true;
    }

}
