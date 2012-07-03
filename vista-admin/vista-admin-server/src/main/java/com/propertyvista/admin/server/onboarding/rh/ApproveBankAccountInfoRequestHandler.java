/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-02
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding.rh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.biz.system.OnboardingPaymentFacade;
import com.propertyvista.onboarding.ApproveBankAccountInfoRequestIO;
import com.propertyvista.onboarding.BankAccountInfoApproval;
import com.propertyvista.onboarding.ResponseIO;

public class ApproveBankAccountInfoRequestHandler extends AbstractRequestHandler<ApproveBankAccountInfoRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(ApproveBankAccountInfoRequestHandler.class);

    public ApproveBankAccountInfoRequestHandler() {
        super(ApproveBankAccountInfoRequestIO.class);
    }

    @Override
    public ResponseIO execute(ApproveBankAccountInfoRequestIO request) {
        log.info("User {} requested {} ", new Object[] { request.onboardingAccountId().getValue(), "UpdateBankAccountInfo" });

        for (BankAccountInfoApproval requestAcc : request.accounts()) {
            ServerSideFactory.create(OnboardingPaymentFacade.class).approveBankAccountInfo(request.onboardingAccountId().getValue(), requestAcc);
        }

        Persistence.service().commit();

        ResponseIO response = EntityFactory.create(ResponseIO.class);
        response.success().setValue(Boolean.TRUE);
        return response;
    }

}
