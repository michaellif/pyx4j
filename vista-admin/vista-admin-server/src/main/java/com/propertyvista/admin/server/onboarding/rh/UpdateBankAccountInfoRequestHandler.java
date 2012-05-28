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

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.OboardingMerchantAccount;
import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.onboarding.BankAccountInfo;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.onboarding.UpdateBankAccountInfoRequestIO;

public class UpdateBankAccountInfoRequestHandler extends AbstractRequestHandler<UpdateBankAccountInfoRequestIO> {

    public UpdateBankAccountInfoRequestHandler() {
        super(UpdateBankAccountInfoRequestIO.class);
    }

    @Override
    public ResponseIO execute(UpdateBankAccountInfoRequestIO request) {
        ResponseIO response = EntityFactory.create(ResponseIO.class);

        List<OboardingMerchantAccount> updatedAccount = new ArrayList<OboardingMerchantAccount>();

        for (BankAccountInfo acc : request.accounts()) {
            EntityQueryCriteria<OboardingMerchantAccount> crmerch = EntityQueryCriteria.create(OboardingMerchantAccount.class);
            crmerch.add(PropertyCriterion.eq(crmerch.proto().onboardingAccountId(), request.onboardingAccountId().getValue()));
            crmerch.add(PropertyCriterion.eq(crmerch.proto().onboardingBankAccountId(), acc.onboardingBankAccountId().getValue()));
            OboardingMerchantAccount macc = Persistence.service().retrieve(crmerch);
            if (macc == null) {
                macc = EntityFactory.create(OboardingMerchantAccount.class);
            }
            macc.bankId().setValue(acc.bankId().getValue());
            macc.branchTransitNumber().setValue(acc.branchTransitNumber().getValue());
            macc.accountNumber().setValue(acc.accountNumber().getValue());
            macc.chargeDescription().setValue(acc.chargeDescription().getValue());
            macc.merchantTerminalId().setValue(acc.terminalId().getValue());
            Persistence.service().persist(macc);
            updatedAccount.add(macc);
        }

        // See if PMC is created, Then copy the same data to Pmc namespace
        EntityQueryCriteria<Pmc> crpmc = EntityQueryCriteria.create(Pmc.class);
        crpmc.add(PropertyCriterion.eq(crpmc.proto().onboardingAccountId(), request.onboardingAccountId().getValue()));
        Pmc pmc = Persistence.service().retrieve(crpmc);
        if ((pmc != null) && (pmc.status().getValue() != PmcStatus.Created)) {
            List<OboardingMerchantAccount> merchantAccountKeyUpdated = new ArrayList<OboardingMerchantAccount>();
            // Switch namespace.
            NamespaceManager.setNamespace(pmc.namespace().getValue());
            try {
                for (OboardingMerchantAccount acc : updatedAccount) {
                    // Check if account exists already.
                    MerchantAccount macc;
                    if (acc.merchantAccountKey().isNull()) {
                        macc = EntityFactory.create(MerchantAccount.class);
                        merchantAccountKeyUpdated.add(acc);
                    } else {
                        macc = Persistence.service().retrieve(MerchantAccount.class, acc.merchantAccountKey().getValue());
                    }

                    macc.bankId().setValue(acc.bankId().getValue());
                    macc.branchTransitNumber().setValue(acc.branchTransitNumber().getValue());
                    macc.accountNumber().setValue(acc.accountNumber().getValue());

                    if (macc.chargeDescription().getValue() == null) {
                        macc.chargeDescription().setValue(pmc.name().getValue());
                    }

                    macc.merchantTerminalId().setValue(acc.merchantTerminalId().getValue());

                    Persistence.service().persist(macc);
                    acc.merchantAccountKey().setValue(macc.getPrimaryKey());
                }
            } finally {
                NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
            }

            Persistence.service().persist(merchantAccountKeyUpdated);
        }

        Persistence.service().commit();

        response.success().setValue(Boolean.TRUE);
        return response;

    }
}
