/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.onboarding.rh;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.onboarding.AccountInfoIO;
import com.propertyvista.onboarding.AccountInfoResponseIO;
import com.propertyvista.onboarding.GetAccountInfoRequestIO;
import com.propertyvista.onboarding.OnboardingPmcAccountStatus;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.operations.domain.security.OnboardingUserCredential;
import com.propertyvista.operations.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class GetAccountInfoRequestHandler extends AbstractRequestHandler<GetAccountInfoRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(GetAccountInfoRequestHandler.class);

    public GetAccountInfoRequestHandler() {
        super(GetAccountInfoRequestIO.class);
    }

    @Override
    public ResponseIO execute(GetAccountInfoRequestIO request) {
        log.info("User {} requested {}", new Object[] { request.onboardingAccountId().getValue(), "GetAccountInfo" });

        AccountInfoResponseIO response = EntityFactory.create(AccountInfoResponseIO.class);
        response.success().setValue(Boolean.TRUE);

        EntityQueryCriteria<Pmc> pmcCrt = EntityQueryCriteria.create(Pmc.class);
        pmcCrt.add(PropertyCriterion.eq(pmcCrt.proto().onboardingAccountId(), request.onboardingAccountId().getValue()));
        List<Pmc> pmcs = Persistence.service().query(pmcCrt);

        if (pmcs.size() == 0) {
            response.success().setValue(Boolean.FALSE);
            log.info("Error occured.  User {}, action {}", new Object[] { request.onboardingAccountId(), "GetAccountInfo" });
            return response;
        }

        Pmc pmc = pmcs.get(0);
        if (pmc == null) {
            response.success().setValue(Boolean.FALSE);
            log.info("Error occured.  User {}, action {}", new Object[] { request.onboardingAccountId(), "GetAccountInfo" });
            return response;
        }

        switch (pmc.status().getValue()) {
        case Created:
            response.accountStatus().setValue(OnboardingPmcAccountStatus.Application);
            break;
        case Active:
            response.accountStatus().setValue(OnboardingPmcAccountStatus.Active);
            break;
        case Suspended:
            response.accountStatus().setValue(OnboardingPmcAccountStatus.Suspended);
            break;
        default:
            response.success().setValue(Boolean.FALSE);
            return response;
        }

        response.vistaCrmUrl().setValue(VistaDeployment.getBaseApplicationURL(pmc, VistaApplication.crm, true));
        response.residentPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(pmc, VistaApplication.residentPortal, false));
        response.prospectPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(pmc, VistaApplication.prospect, true));

        EntityQueryCriteria<OnboardingUserCredential> credentialCrt = EntityQueryCriteria.create(OnboardingUserCredential.class);
        credentialCrt.add(PropertyCriterion.eq(credentialCrt.proto().pmc(), pmc));
        for (OnboardingUserCredential credential : Persistence.service().query(credentialCrt)) {
            Persistence.service().retrieve(credential.user());

            if (credential.enabled().isBooleanTrue()) {
                if (pmc.status().getValue() != PmcStatus.Created) {
                    String curNameSpace = NamespaceManager.getNamespace();
                    try {
                        NamespaceManager.setNamespace(pmc.namespace().getValue());
                        EntityQueryCriteria<CrmUserCredential> crmUCrt = EntityQueryCriteria.create(CrmUserCredential.class);
                        crmUCrt.add(PropertyCriterion.eq(crmUCrt.proto().roles().$().behaviors(), VistaCrmBehavior.PropertyVistaAccountOwner));
                        crmUCrt.add(PropertyCriterion.eq(crmUCrt.proto().onboardingUser(), credential.user().getPrimaryKey()));

                        CrmUserCredential crmCred = Persistence.service().retrieve(crmUCrt);
                        if (crmCred == null) {
                            continue;
                        }

                        if (!crmCred.enabled().isBooleanTrue()) {
                            continue;
                        }
                    } finally {
                        NamespaceManager.setNamespace(curNameSpace);
                    }
                }
                AccountInfoIO accountInfo = EntityFactory.create(AccountInfoIO.class);
                accountInfo.firstName().setValue(credential.user().firstName().getValue());
                accountInfo.lastName().setValue(credential.user().lastName().getValue());
                accountInfo.email().setValue(credential.user().email().getValue());

                response.accounts().add(accountInfo);
            }

        }
        return response;
    }
}
