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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.biz.system.OnboardingPaymentFacade;
import com.propertyvista.onboarding.BankAccountInfo;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.onboarding.UpdateBankAccountInfoRequestIO;

public class UpdateBankAccountInfoRequestHandler extends AbstractRequestHandler<UpdateBankAccountInfoRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(UpdateBankAccountInfoRequestHandler.class);

    public UpdateBankAccountInfoRequestHandler() {
        super(UpdateBankAccountInfoRequestIO.class);
    }

    @Override
    public ResponseIO execute(UpdateBankAccountInfoRequestIO request) {
        log.info("User {} requested {} ", new Object[] { request.onboardingAccountId().getValue(), "UpdateBankAccountInfo" });

        EntityQueryCriteria<Pmc> crpmc = EntityQueryCriteria.create(Pmc.class);
        crpmc.add(PropertyCriterion.eq(crpmc.proto().onboardingAccountId(), request.onboardingAccountId()));
        Pmc pmc = Persistence.service().retrieve(crpmc);
        if (pmc == null) {
            ResponseIO response = EntityFactory.create(ResponseIO.class);
            response.success().setValue(Boolean.FALSE);
            response.errorMessage().setValue("PMC not found");
            return response;
        }

        for (BankAccountInfo requestAcc : request.accounts()) {
            ServerSideFactory.create(OnboardingPaymentFacade.class).updateBankAccountInfo(pmc, requestAcc);
        }

        Persistence.service().commit();
        ResponseIO response = EntityFactory.create(ResponseIO.class);
        response.success().setValue(Boolean.TRUE);
        return response;

    }
}
