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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.onboarding.AccountInfoResponseIO;
import com.propertyvista.onboarding.GetAccountInfoRequestIO;
import com.propertyvista.onboarding.ResponseIO;

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

        response.vistaCrmUrl().setValue(VistaDeployment.getBaseApplicationURL(pmc, VistaBasicBehavior.CRM, true));
        response.residentPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(pmc, VistaBasicBehavior.TenantPortal, false));
        response.prospectPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(pmc, VistaBasicBehavior.ProspectiveApp, true));

        return response;
    }
}
