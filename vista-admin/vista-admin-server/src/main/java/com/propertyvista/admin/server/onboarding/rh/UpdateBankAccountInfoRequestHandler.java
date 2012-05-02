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
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.onboarding.UpdateBankAccountInfoRequestIO;
import com.propertyvista.server.domain.admin.Pmc;

public class UpdateBankAccountInfoRequestHandler extends AbstractRequestHandler<UpdateBankAccountInfoRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(UpdateBankAccountInfoRequestHandler.class);

    public UpdateBankAccountInfoRequestHandler() {
        super(UpdateBankAccountInfoRequestIO.class);
    }

    @Override
    public ResponseIO execute(UpdateBankAccountInfoRequestIO request) {
        ResponseIO response = EntityFactory.create(ResponseIO.class);
        response.success().setValue(Boolean.TRUE);

        EntityQueryCriteria<Pmc> crpmc = EntityQueryCriteria.create(Pmc.class);
        crpmc.add(PropertyCriterion.eq(crpmc.proto().onboardingAccountId(), request.onboardingAccountId().getValue()));
        List<Pmc> pmcs = Persistence.service().query(crpmc);

        if (pmcs.size() != 1) {
            log.debug("INp Pmc for onboarding accountid {} rs {}", request.onboardingAccountId().getValue(), pmcs.size());
            response.success().setValue(Boolean.FALSE);

            return response;
        }

        // Switch namespace.
        Pmc pmc = pmcs.get(0);
        NamespaceManager.setNamespace(pmc.namespace().getValue());

        // Check if account exists already.
        EntityQueryCriteria<MerchantAccount> crmerch = EntityQueryCriteria.create(MerchantAccount.class);
        crmerch.add(PropertyCriterion.eq(crmerch.proto().onboardingMerchantAccountId(), request.onboardingAccountId().getValue()));
        crmerch.add(PropertyCriterion.eq(crmerch.proto().bankId(), request.bankId().getValue()));
        crmerch.add(PropertyCriterion.eq(crmerch.proto().branchTransitNumber(), request.branchTransitNumber().getValue()));
        crmerch.add(PropertyCriterion.eq(crmerch.proto().accountNumber(), request.accountNumber().getValue()));

        List<MerchantAccount> accs = Persistence.service().query(crmerch);

        MerchantAccount acc = null;
        if (accs.size() != 1) {
            acc = EntityFactory.create(MerchantAccount.class);

            acc.bankId().setValue(request.bankId().getValue());
            acc.branchTransitNumber().setValue(request.branchTransitNumber().getValue());
            acc.accountNumber().setValue(request.accountNumber().getValue());
            acc.merchantTerminalId().setValue(request.onboardingAccountId().getValue());
        } else {
            acc = accs.get(0);
        }

        acc.merchantTerminalId().setValue(request.merchantTerminalId().getValue());

        Persistence.service().persist(acc);
        Persistence.service().commit();

        return response;

    }
}
