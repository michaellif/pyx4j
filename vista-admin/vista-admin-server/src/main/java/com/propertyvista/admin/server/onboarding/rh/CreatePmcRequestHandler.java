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
package com.propertyvista.admin.server.onboarding.rh;

import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.admin.server.onboarding.PmcNameValidator;
import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.onboarding.AccountInfoResponseIO;
import com.propertyvista.onboarding.CreatePMCRequestIO;
import com.propertyvista.onboarding.ResponseIO;

public class CreatePmcRequestHandler extends AbstractRequestHandler<CreatePMCRequestIO> {

    public CreatePmcRequestHandler() {
        super(CreatePMCRequestIO.class);
    }

    @Override
    public ResponseIO execute(CreatePMCRequestIO request) {
        AccountInfoResponseIO response = EntityFactory.create(AccountInfoResponseIO.class);
        response.success().setValue(Boolean.TRUE);

        EntityQueryCriteria<OnboardingUserCredential> credentialCrt = EntityQueryCriteria.create(OnboardingUserCredential.class);
        credentialCrt.add(PropertyCriterion.eq(credentialCrt.proto().onboardingAccountId(), request.onboardingAccountId().getValue()));
        List<OnboardingUserCredential> creds = Persistence.service().query(credentialCrt);

        if (creds.size() == 0) {
            response.success().setValue(Boolean.FALSE);
            return response;
        }

        final String dnsName = request.name().getValue();
        if (!PmcNameValidator.canCreatePmcName(dnsName, request.onboardingAccountId().getValue())) {
            response.success().setValue(Boolean.FALSE);
            return response;
        }

        Pmc pmc = EntityFactory.create(Pmc.class);
        pmc.dnsName().setValue(dnsName);
        pmc.namespace().setValue(dnsName.replace('-', '_'));
        pmc.name().setValue(request.name().getValue());
        pmc.onboardingAccountId().setValue(request.onboardingAccountId().getValue());

        // TODO For future
//        for (String dndAlias : request.dnsNameAliases()) {
//
//        }

        pmc.status().setValue(PmcStatus.Created);
        Persistence.service().persist(pmc);

        for (OnboardingUserCredential cred : creds) {
            cred.pmc().set(pmc);

            cred.onboardingAccountId().setValue(null); // We will lookup by pmc
            Persistence.service().persist(cred);
        }

        Persistence.service().commit();

        response.vistaCrmUrl().setValue(VistaDeployment.getBaseApplicationURL(pmc, VistaBasicBehavior.CRM, true));
        response.residentPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(pmc, VistaBasicBehavior.TenantPortal, true));
        response.prospectPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(pmc, VistaBasicBehavior.ProspectiveApp, true));

        return response;
    }
}
