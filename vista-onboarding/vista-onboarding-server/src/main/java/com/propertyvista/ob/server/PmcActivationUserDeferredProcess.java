/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-11
 * @author vlads
 */
package com.propertyvista.ob.server;

import java.util.concurrent.Callable;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CrmUserCredential;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.ob.rpc.dto.OnboardingApplicationStatus;
import com.propertyvista.ob.rpc.dto.OnboardingUserVisit;
import com.propertyvista.server.TaskRunner;
import com.propertyvista.server.common.security.AccessKey;

@SuppressWarnings("serial")
public class PmcActivationUserDeferredProcess extends PmcActivationDeferredProcess {

    private final OnboardingUserVisit visit;

    private final OnboardingUser onboardingUser;

    public PmcActivationUserDeferredProcess(OnboardingUser onboardingUser, OnboardingUserVisit visit) {
        super(onboardingUser.pmc());
        this.visit = visit;
        this.onboardingUser = onboardingUser;
    }

    @Override
    public void execute() {
        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                PmcActivationUserDeferredProcess.super.execute();
                return null;
            }
        });
    }

    @Override
    protected void onPmcCreated() {
        new UnitOfWork().execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                ServerSideFactory.create(CommunicationFacade.class).sendNewPmcEmail(onboardingUser, pmcId);
                return null;
            }

        });

        visit.setStatus(OnboardingApplicationStatus.accountCreated);

        TaskRunner.runInTargetNamespace(onboardingUser.pmc(), new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                EntityQueryCriteria<CrmUser> criteria = EntityQueryCriteria.create(CrmUser.class);
                criteria.eq(criteria.proto().email(), visit.getEmail());
                AccessKey.createAccessToken(Persistence.service().retrieve(criteria), CrmUserCredential.class, 1, false);
                return null;
            }
        });
    }
}
