/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-08-16
 * @author vlads
 */
package com.propertyvista.ob.server;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.server.contexts.Lifecycle;

import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.domain.pmc.Pmc;

public class PmcActivationDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 1L;

    protected final Pmc pmcId;

    public PmcActivationDeferredProcess(Pmc pmcId) {
        this.pmcId = pmcId;
    }

    @Override
    public void execute() {
        try {
            Lifecycle.startElevatedUserContext();

            new UnitOfWork(TransactionScopeOption.Nested, ConnectionTarget.BackgroundProcess).execute(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    ServerSideFactory.create(PmcFacade.class).activatePmc(pmcId);
                    return null;
                }

            });

            onPmcCreated();
        } finally {
            Lifecycle.endContext();
        }
        completed = true;
    }

    protected void onPmcCreated() {
    }
}
