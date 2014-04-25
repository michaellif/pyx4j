/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 25, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.crm.server.util.CrmAppContext;

public class LeaseApplicationActionDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 1L;

    private final LeaseApplicationActionDTO actionDTO;

    private final long maxExpectedTimeMs;

    private final long startTimeMs;

    public LeaseApplicationActionDeferredProcess(LeaseApplicationActionDTO actionDTO, long maxExpectedTimeMs) {
        this.actionDTO = actionDTO;
        this.maxExpectedTimeMs = maxExpectedTimeMs;
        this.startTimeMs = SystemDateManager.getTimeMillis();
    }

    @Override
    public void execute() {

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() throws RuntimeException {
                switch (actionDTO.action().getValue()) {
                case Approve:
                    ServerSideFactory.create(LeaseFacade.class).approve(actionDTO.leaseId(), CrmAppContext.getCurrentUserEmployee(),
                            actionDTO.decisionReason().getValue());
                    break;
                case Decline:
                    ServerSideFactory.create(LeaseFacade.class).declineApplication(actionDTO.leaseId(), CrmAppContext.getCurrentUserEmployee(),
                            actionDTO.decisionReason().getValue());
                    break;
                case Cancel:
                    ServerSideFactory.create(LeaseFacade.class).cancelApplication(actionDTO.leaseId(), CrmAppContext.getCurrentUserEmployee(),
                            actionDTO.decisionReason().getValue());
                    break;
                default:
                    throw new IllegalArgumentException();
                }
                return null;
            }

        });
        completed = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse r = super.status();
        r.setProgressMaximum(100);
        r.setProgress((int) (100 * (SystemDateManager.getTimeMillis() - startTimeMs) / maxExpectedTimeMs));
        return r;
    }
}
