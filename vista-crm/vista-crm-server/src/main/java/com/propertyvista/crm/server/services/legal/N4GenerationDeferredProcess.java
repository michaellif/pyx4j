/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.legal;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.biz.legal.N4ManagementFacade;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationQueryDTO.DeliveryMethod;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.tenant.lease.Lease;

public class N4GenerationDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 1L;

    private final AtomicInteger progress;

    private final int progressMax;

    private final List<Lease> delinquentLeases;

    private final Employee issuingEmployee;

    private final LogicalDate noticeDate;

    private final DeliveryMethod deliveryMethod;

    public N4GenerationDeferredProcess(List<Lease> delinquentLeases, Employee issuingEmployee, LogicalDate noticeDate, DeliveryMethod deliveryMethod) {
        progress = new AtomicInteger();
        progress.set(0);
        progressMax = delinquentLeases.size();

        this.delinquentLeases = delinquentLeases;
        this.issuingEmployee = issuingEmployee;
        this.noticeDate = noticeDate;
        this.deliveryMethod = deliveryMethod;
    }

    @Override
    public void execute() {
        ServerSideFactory.create(N4ManagementFacade.class).issueN4(delinquentLeases, issuingEmployee, noticeDate, deliveryMethod, progress);
        completed = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse status = super.status();
        status.setProgress(progress.get());
        status.setProgressMaximum(progressMax);
        return status;
    }

}
