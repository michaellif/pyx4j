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
 * @version $Id$
 */
package com.propertyvista.ob.server;

import java.util.concurrent.Callable;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.server.contexts.Visit;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.ob.server.services.OnboardingAuthenticationServiceImpl;
import com.propertyvista.server.jobs.TaskRunner;

public class PmcActivationUserDeferredProcess extends PmcActivationDeferredProcess {

    private static final long serialVersionUID = 8272802910189364700L;

    private final Visit visit;

    private final OnboardingUser sendNewPmcEmailToUser;

    public PmcActivationUserDeferredProcess(Pmc pmcId, Visit visit, OnboardingUser sendNewPmcEmailToUser) {
        super(pmcId);
        this.visit = visit;
        this.sendNewPmcEmailToUser = sendNewPmcEmailToUser;
    }

    @Override
    public void execute() {
        TaskRunner.runInAdminNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                PmcActivationUserDeferredProcess.super.execute();
                return null;
            }
        });
    }

    @Override
    protected void onPmcCreated() {
        ServerSideFactory.create(CommunicationFacade.class).sendNewPmcEmail(sendNewPmcEmailToUser, pmcId);
        visit.setAttribute(OnboardingAuthenticationServiceImpl.accountCreatedAttr, Boolean.TRUE);
        visit.setAclRevalidationRequired();
    }

}
