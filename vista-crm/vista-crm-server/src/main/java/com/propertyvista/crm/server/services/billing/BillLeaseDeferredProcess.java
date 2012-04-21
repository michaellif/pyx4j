/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.billing;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.server.deferred.IDeferredProcess;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.domain.tenant.lease.Lease;

class BillLeaseDeferredProcess implements IDeferredProcess {

    private static final long serialVersionUID = 132587695984533361L;

    protected volatile boolean canceled;

    private boolean compleate;

    private final Lease lease;

    BillLeaseDeferredProcess(Lease lease) {
        this.lease = lease;
    }

    @Override
    public void execute() {
        if (!compleate) {
            try {
                Persistence.service().startBackgroundProcessTransaction();

                ServerSideFactory.create(BillingFacade.class).runBilling(lease);
                Persistence.service().commit();
            } finally {
                Persistence.service().endTransaction();
            }
        }
        compleate = true;
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        if (compleate) {
            DeferredProcessProgressResponse r = new DeferredProcessProgressResponse();
            r.setCompleted();
            return r;
        } else {
            DeferredProcessProgressResponse r = new DeferredProcessProgressResponse();
            r.setProgress(10);
            r.setProgressMaximum(10);
            if (canceled) {
                r.setCanceled();
            }
            return r;
        }
    }

}
