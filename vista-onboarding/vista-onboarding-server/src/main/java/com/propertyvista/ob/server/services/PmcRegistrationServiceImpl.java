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
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.security.server.EmailValidator;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.ob.rpc.dto.OnboardingApplicationStatus;
import com.propertyvista.ob.rpc.dto.OnboardingCrmURL;
import com.propertyvista.ob.rpc.dto.OnboardingUserVisit;
import com.propertyvista.ob.rpc.dto.PmcAccountCreationRequest;
import com.propertyvista.ob.rpc.services.PmcRegistrationService;
import com.propertyvista.ob.server.PmcActivationUserDeferredProcess;
import com.propertyvista.server.TaskRunner;
import com.propertyvista.server.common.security.AccessKey;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class PmcRegistrationServiceImpl implements PmcRegistrationService {

    private final static Logger log = LoggerFactory.getLogger(PmcRegistrationServiceImpl.class);

    private static final I18n i18n = I18n.get(PmcRegistrationServiceImpl.class);

    @Override
    public void createAccount(AsyncCallback<String> callback, final PmcAccountCreationRequest request) {

        final OnboardingUser onboardingUser = TaskRunner.runInOperationsNamespace(new Callable<OnboardingUser>() {
            @Override
            public OnboardingUser call() throws Exception {

                if (!ServerSideFactory.create(PmcFacade.class).checkDNSAvailability(request.dnsName().getValue())) {
                    throw new UserRuntimeException(i18n.tr("Requested DNS name {0} already reserved", request.dnsName().getValue()));
                }

                Pmc pmc = EntityFactory.create(Pmc.class);

                pmc.name().setValue(request.name().getValue());
                pmc.dnsName().setValue(request.dnsName().getValue());

                pmc.equifaxInfo().status().setValue(PmcEquifaxStatus.NotRequested);

                pmc.features().countryOfOperation().setValue(request.countryOfOperation().getValue());
                pmc.features().productCatalog().setValue(Boolean.TRUE);
                pmc.features().leases().setValue(Boolean.TRUE);
                pmc.features().onlineApplication().setValue(Boolean.FALSE);
                pmc.features().defaultProductCatalog().setValue(true);

                pmc.features().tenantSureIntegration().setValue(Boolean.TRUE);

                ServerSideFactory.create(PmcFacade.class).create(pmc);

                OnboardingUser user = EntityFactory.create(OnboardingUser.class);
                user.pmc().set(pmc);
                user.firstName().setValue(request.firstName().getValue());
                user.lastName().setValue(request.lastName().getValue());
                user.email().setValue(EmailValidator.normalizeEmailAddress(request.email().getValue()));

                Persistence.service().persist(user);

                Persistence.service().commit();

                return user;
            }
        });

        log.info("New PMC {} created", onboardingUser.pmc());

        OnboardingUserVisit visit = Context.getUserVisit(OnboardingUserVisit.class);
        visit.setStatus(OnboardingApplicationStatus.accountCreation);
        visit.setPmcNamespace(onboardingUser.pmc().namespace().getValue());
        visit.setEmail(onboardingUser.email().getValue());

        String deferredCorrelationId = DeferredProcessRegistry.fork(new PmcActivationUserDeferredProcess(onboardingUser, visit), ThreadPoolNames.IMPORTS);

        visit.setAccountCreationDeferredCorrelationId(deferredCorrelationId);

        callback.onSuccess(deferredCorrelationId);
    }

    @Override
    public void obtainCrmURL(AsyncCallback<OnboardingCrmURL> callback) {
        final OnboardingUserVisit visit = Context.getUserVisit(OnboardingUserVisit.class);

        Pmc pmc = TaskRunner.runInOperationsNamespace(new Callable<Pmc>() {

            @Override
            public Pmc call() throws Exception {
                EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
                criteria.eq(criteria.proto().namespace(), visit.pmcNamespace);
                return Persistence.service().retrieve(criteria);
            }

        });

        if ((pmc == null) || (pmc.status().getValue() != PmcStatus.Active)) {
            throw new Error("Created PMC not found");
        }

        String token = TaskRunner.runInTargetNamespace(pmc, new Callable<String>() {

            @Override
            public String call() throws Exception {
                EntityQueryCriteria<CrmUserCredential> criteria = EntityQueryCriteria.create(CrmUserCredential.class);
                criteria.eq(criteria.proto().user().email(), visit.getEmail());
                CrmUserCredential credential = Persistence.service().retrieve(criteria);
                if ((credential != null) && (!credential.accessKey().isNull())) {
                    Persistence.ensureRetrieve(credential.user(), AttachLevel.Attached);
                    return AccessKey.compressToken(credential.user().email().getValue(), credential.accessKey().getValue());
                } else {
                    return null;
                }
            }

        });

        OnboardingCrmURL onboardingCrmURL = new OnboardingCrmURL();
        onboardingCrmURL.urlVisible = VistaDeployment.getBaseApplicationURL(pmc, VistaApplication.crm, true);

        if (token != null) {
            onboardingCrmURL.urlWithToken = getCrmAccessUrl(onboardingCrmURL.urlVisible, token);
        } else {
            onboardingCrmURL.urlWithToken = onboardingCrmURL.urlVisible;
        }

        callback.onSuccess(onboardingCrmURL);
    }

    private static String getCrmAccessUrl(String vistaCrmBaseUrl, String token) {
        return AppPlaceInfo.absoluteUrl(vistaCrmBaseUrl, true, CrmSiteMap.LoginWithToken.class, AuthenticationService.AUTH_TOKEN_ARG, token);

    }
}
