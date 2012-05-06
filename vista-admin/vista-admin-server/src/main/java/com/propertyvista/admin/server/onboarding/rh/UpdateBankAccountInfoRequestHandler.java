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

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.domain.financial.MerchantAccount;
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

        try {
            for (BankAccountInfo acc : request.accounts()) {

                // Check if account exists already.
                EntityQueryCriteria<MerchantAccount> crmerch = EntityQueryCriteria.create(MerchantAccount.class);
                crmerch.add(PropertyCriterion.eq(crmerch.proto().onboardingMerchantAccountId(), request.onboardingAccountId().getValue()));
                crmerch.add(PropertyCriterion.eq(crmerch.proto().bankId(), acc.bankId().getValue()));
                crmerch.add(PropertyCriterion.eq(crmerch.proto().branchTransitNumber(), acc.branchTransitNumber().getValue()));
                crmerch.add(PropertyCriterion.eq(crmerch.proto().accountNumber(), acc.accountNumber().getValue()));

                List<MerchantAccount> accs = Persistence.service().query(crmerch);

                MerchantAccount macc = null;
                if (accs.size() != 1) {
                    macc = EntityFactory.create(MerchantAccount.class);

                    macc.bankId().setValue(acc.bankId().getValue());
                    macc.branchTransitNumber().setValue(acc.branchTransitNumber().getValue());
                    macc.accountNumber().setValue(acc.accountNumber().getValue());
                } else {
                    macc = accs.get(0);
                }

                if (macc.chargeDescription().getValue() == null)
                    macc.chargeDescription().setValue(pmc.name().getValue());

                macc.merchantTerminalId().setValue(acc.terminalId().getValue());

                Persistence.service().persist(macc);
            }

            Persistence.service().commit();

            return response;
        } catch (Throwable e) {
            log.debug(e.getMessage());
            Persistence.service().rollback();

            response.success().setValue(Boolean.FALSE);

            return response;
        }

    }
}
