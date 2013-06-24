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
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.biz.system.UserManagementFacade;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.ob.rpc.dto.OnboardingApplicationStatus;
import com.propertyvista.ob.rpc.dto.OnboardingCrmURL;
import com.propertyvista.ob.rpc.dto.OnboardingUserVisit;
import com.propertyvista.ob.rpc.dto.PmcAccountCreationRequest;
import com.propertyvista.ob.rpc.services.PmcRegistrationService;
import com.propertyvista.ob.server.PmcActivationUserDeferredProcess;
import com.propertyvista.operations.domain.security.OnboardingUserCredential;
import com.propertyvista.server.common.security.AccessKey;
import com.propertyvista.server.domain.security.CrmUserCredential;
import com.propertyvista.server.jobs.TaskRunner;

public class PmcRegistrationServiceImpl implements PmcRegistrationService {

    private final static Logger log = LoggerFactory.getLogger(PmcRegistrationServiceImpl.class);

    private static final I18n i18n = I18n.get(PmcRegistrationServiceImpl.class);

    @Override
    public void createAccount(AsyncCallback<String> callback, final PmcAccountCreationRequest request) {

        final OnboardingUserCredential credential = TaskRunner.runInOperationsNamespace(new Callable<OnboardingUserCredential>() {
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

                pmc.equifaxInfo().status().setValue(PmcEquifaxStatus.NotRequested);

                pmc.features().countryOfOperation().setValue(request.countryOfOperation().getValue());
                pmc.features().occupancyModel().setValue(Boolean.TRUE);
                pmc.features().productCatalog().setValue(Boolean.TRUE);
                pmc.features().leases().setValue(Boolean.TRUE);
                pmc.features().onlineApplication().setValue(Boolean.FALSE);
                pmc.features().xmlSiteExport().setValue(Boolean.FALSE);
                pmc.features().defaultProductCatalog().setValue(true);

                //TODO remove this IF when tenantSure Is Ok to go Live!
                if (VistaDeployment.isVistaProduction() && VistaTODO.tenantSureDisabledForProduction) {
                    pmc.features().tenantSureIntegration().setValue(Boolean.FALSE);
                } else {
                    pmc.features().tenantSureIntegration().setValue(Boolean.TRUE);
                }

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
        visit.setOnboardingUserPrimaryKey(credential.getPrimaryKey());

        String deferredCorrelationId = DeferredProcessRegistry.fork(new PmcActivationUserDeferredProcess(credential, visit), ThreadPoolNames.IMPORTS);

        visit.setAccountCreationDeferredCorrelationId(deferredCorrelationId);

        callback.onSuccess(deferredCorrelationId);
    }

    @Override
    public void obtainCrmURL(AsyncCallback<OnboardingCrmURL> callback) {
        final OnboardingUserVisit visit = Context.getUserVisit(OnboardingUserVisit.class);

        OnboardingCrmURL onboardingCrmURL = new OnboardingCrmURL();

        onboardingCrmURL.urlVisible = TaskRunner.runInOperationsNamespace(new Callable<String>() {

            @Override
            public String call() throws Exception {
                EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
                criteria.eq(criteria.proto().namespace(), visit.pmcNamespace);
                Pmc pmc = Persistence.service().retrieve(criteria);

                if ((pmc != null) && (pmc.status().getValue() == PmcStatus.Active)) {
                    return VistaDeployment.getBaseApplicationURL(pmc, VistaApplication.crm, true);
                } else {
                    return null;
                }
            }

        });

        String token = TaskRunner.runInTargetNamespace(visit.pmcNamespace, new Callable<String>() {

            @Override
            public String call() throws Exception {
                EntityQueryCriteria<CrmUserCredential> criteria = EntityQueryCriteria.create(CrmUserCredential.class);
                criteria.eq(criteria.proto().onboardingUser(), visit.onboardingUserPrimaryKey);
                CrmUserCredential credential = Persistence.service().retrieve(criteria);
                if ((credential != null) && (!credential.accessKey().isNull())) {
                    Persistence.ensureRetrieve(credential.user(), AttachLevel.Attached);
                    return AccessKey.compressToken(credential.user().email().getValue(), credential.accessKey().getValue());
                } else {
                    return null;
                }
            }

        });

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
