/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.ob.server.services;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.deferred.DeferredProcessRegistry;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.biz.system.UserManagementFacade;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.ob.rpc.dto.OnboardingApplicationStatus;
import com.propertyvista.ob.rpc.dto.OnboardingUserVisit;
import com.propertyvista.ob.rpc.dto.PmcAccountCreationRequest;
import com.propertyvista.ob.rpc.services.PmcRegistrationService;
import com.propertyvista.ob.server.PmcActivationUserDeferredProcess;
import com.propertyvista.server.jobs.TaskRunner;

public class PmcRegistrationServiceImpl implements PmcRegistrationService {

    private final static Logger log = LoggerFactory.getLogger(PmcRegistrationServiceImpl.class);

    private static final I18n i18n = I18n.get(PmcRegistrationServiceImpl.class);

    @Override
    public void createAccount(AsyncCallback<String> callback, final PmcAccountCreationRequest request) {

        final OnboardingUserCredential credential = TaskRunner.runInAdminNamespace(new Callable<OnboardingUserCredential>() {
            @Override
            public OnboardingUserCredential call() throws Exception {

                if (!ServerSideFactory.create(PmcFacade.class).checkDNSAvailability(request.dnsName().getValue())) {
                    throw new UserRuntimeException(i18n.tr("Requested DNS name {0} already reserved", request.dnsName().getValue()));
                }

                OnboardingUserCredential credential = ServerSideFactory.create(UserManagementFacade.class).createOnboardingUser(request.firstName().getValue(),
                        request.lastName().getValue(), request.email().getValue(), request.password().getValue(), VistaOnboardingBehavior.ProspectiveClient,
                        null);

                Pmc pmc = EntityFactory.create(Pmc.class);

                pmc.name().setValue(request.name().getValue());
                pmc.dnsName().setValue(request.dnsName().getValue());
                pmc.namespace().setValue(pmc.dnsName().getValue());

                pmc.onboardingAccountId().setValue(credential.onboardingAccountId().getValue());

                pmc.features().occupancyModel().setValue(Boolean.TRUE);
                pmc.features().productCatalog().setValue(Boolean.TRUE);
                pmc.features().leases().setValue(Boolean.TRUE);
                pmc.features().onlineApplication().setValue(Boolean.FALSE);
                pmc.features().xmlSiteExport().setValue(Boolean.FALSE);
                pmc.features().genericProductCatalog().setValue(Boolean.FALSE);

                ServerSideFactory.create(PmcFacade.class).create(pmc);

                credential.pmc().set(pmc);
                Persistence.service().persist(credential);

                Persistence.service().commit();

                return credential;
            }
        });

        log.info("New PMC {} created", credential.pmc());

        OnboardingUserVisit visit = Context.getUserVisit(OnboardingUserVisit.class);
        visit.setStatus(OnboardingApplicationStatus.accountCreation);
        visit.setPmcNamespace(credential.pmc().namespace().getValue());

        String deferredCorrelationId = DeferredProcessRegistry.fork(new PmcActivationUserDeferredProcess(credential.pmc(), visit, credential.user()),
                ThreadPoolNames.IMPORTS);

        visit.setAccountCreationDeferredCorrelationId(deferredCorrelationId);

        callback.onSuccess(deferredCorrelationId);
    }

    @Override
    public void obtainCrmURL(AsyncCallback<String> callback) {
        final OnboardingUserVisit visit = Context.getUserVisit(OnboardingUserVisit.class);

        callback.onSuccess(TaskRunner.runInAdminNamespace(new Callable<String>() {

            @Override
            public String call() throws Exception {
                EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
                criteria.eq(criteria.proto().namespace(), visit.pmcNamespace);
                Pmc pmc = Persistence.service().retrieve(criteria);

                String vistaCrmUrl = VistaDeployment.getBaseApplicationURL(pmc, VistaBasicBehavior.CRM, true);
                return vistaCrmUrl;
            }

        }));
    }

}
