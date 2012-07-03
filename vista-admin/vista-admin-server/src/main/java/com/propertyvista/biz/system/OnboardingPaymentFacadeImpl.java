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
package com.propertyvista.biz.system;

import java.util.concurrent.Callable;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.admin.domain.pmc.OnboardingMerchantAccount;
import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.onboarding.BankAccountInfo;
import com.propertyvista.onboarding.BankAccountInfoApproval;
import com.propertyvista.server.jobs.TaskRunner;

public class OnboardingPaymentFacadeImpl implements OnboardingPaymentFacade {

    @Override
    public void updateBankAccountInfo(String onboardingAccountId, final BankAccountInfo requestAcc) {
        EntityQueryCriteria<Pmc> crpmc = EntityQueryCriteria.create(Pmc.class);
        crpmc.add(PropertyCriterion.eq(crpmc.proto().onboardingAccountId(), onboardingAccountId));
        Pmc pmc = Persistence.service().retrieve(crpmc);

        EntityQueryCriteria<OnboardingMerchantAccount> crmerch = EntityQueryCriteria.create(OnboardingMerchantAccount.class);
        crmerch.add(PropertyCriterion.eq(crmerch.proto().onboardingAccountId(), onboardingAccountId));
        crmerch.add(PropertyCriterion.eq(crmerch.proto().onboardingBankAccountId(), requestAcc.onboardingBankAccountId().getValue()));

        OnboardingMerchantAccount omacc = Persistence.service().retrieve(crmerch);
        if (omacc == null) {
            omacc = EntityFactory.create(OnboardingMerchantAccount.class);
            omacc.onboardingAccountId().setValue(onboardingAccountId);
            omacc.onboardingBankAccountId().setValue(requestAcc.onboardingBankAccountId().getValue());
        }

        boolean changed = false;
        if (!omacc.bankId().equals(requestAcc.bankId())) {
            changed = true;
        }
        if (!omacc.branchTransitNumber().equals(requestAcc.branchTransitNumber())) {
            changed = true;
        }
        if (!omacc.accountNumber().equals(requestAcc.accountNumber())) {
            changed = true;
        }

        if (changed) {
            omacc.bankId().setValue(requestAcc.bankId().getValue());
            omacc.branchTransitNumber().setValue(requestAcc.branchTransitNumber().getValue());
            omacc.accountNumber().setValue(requestAcc.accountNumber().getValue());

            // There are no such value in onboarding
            if (!requestAcc.chargeDescription().isNull()) {
                omacc.chargeDescription().setValue(requestAcc.chargeDescription().getValue());
            }

            // Reset the merchantTerminalId. It needs to be re-approved
            omacc.merchantTerminalId().setValue(null);

            omacc.pmc().set(pmc);

            Persistence.service().persist(omacc);
        }

        // See if PMC is created, Then copy the same data to Pmc namespace
        if ((pmc != null) && (pmc.status().getValue() != PmcStatus.Created)) {
            final Key referencedMerchantAccountKey = omacc.merchantAccountKey().getValue();
            final boolean changed2 = changed;
            Key merchantAccountKey = TaskRunner.runInTargetNamespace(pmc.namespace().getValue(), new Callable<Key>() {
                @Override
                public Key call() {
                    MerchantAccount macc;
                    if (referencedMerchantAccountKey != null) {
                        macc = Persistence.service().retrieve(MerchantAccount.class, referencedMerchantAccountKey);
                    } else {
                        macc = EntityFactory.create(MerchantAccount.class);
                        macc.invalid().setValue(Boolean.FALSE);
                    }

                    macc.bankId().setValue(requestAcc.bankId().getValue());
                    macc.branchTransitNumber().setValue(requestAcc.branchTransitNumber().getValue());
                    macc.accountNumber().setValue(requestAcc.accountNumber().getValue());

                    // There are no such value in onboarding
                    if (!requestAcc.chargeDescription().isNull()) {
                        macc.chargeDescription().setValue(requestAcc.chargeDescription().getValue());
                    }

                    if (changed2) {
                        macc.merchantTerminalId().setValue(null);
                    }
                    Persistence.service().persist(macc);
                    return macc.getPrimaryKey();
                }
            });

            // Update back references
            if (omacc.merchantAccountKey().isNull()) {
                omacc.merchantAccountKey().setValue(merchantAccountKey);
                Persistence.service().persist(omacc);
            }

        } else {
            if (!omacc.merchantAccountKey().isNull()) {
                throw new Error("Account " + requestAcc.onboardingBankAccountId().getValue() + " has invalid reference");
            }
        }
    }

    @Override
    public void approveBankAccountInfo(String onboardingAccountId, BankAccountInfoApproval requestAcc) {
        EntityQueryCriteria<Pmc> crpmc = EntityQueryCriteria.create(Pmc.class);
        crpmc.add(PropertyCriterion.eq(crpmc.proto().onboardingAccountId(), onboardingAccountId));
        Pmc pmc = Persistence.service().retrieve(crpmc);

        EntityQueryCriteria<OnboardingMerchantAccount> crmerch = EntityQueryCriteria.create(OnboardingMerchantAccount.class);
        crmerch.add(PropertyCriterion.eq(crmerch.proto().onboardingAccountId(), onboardingAccountId));
        crmerch.add(PropertyCriterion.eq(crmerch.proto().onboardingBankAccountId(), requestAcc.onboardingBankAccountId().getValue()));

        final OnboardingMerchantAccount omacc = Persistence.service().retrieve(crmerch);
        if (omacc == null) {
            throw new Error("Account " + requestAcc.onboardingBankAccountId().getValue() + " not found");
        }

        if (!omacc.bankId().equals(requestAcc.bankId())) {
            throw new Error("Account " + requestAcc.onboardingBankAccountId().getValue() + " bankId mismatch " + requestAcc.bankId().getValue());
        }
        if (!omacc.branchTransitNumber().equals(requestAcc.branchTransitNumber())) {
            throw new Error("Account " + requestAcc.onboardingBankAccountId().getValue() + " branchTransitNumber mismatch "
                    + requestAcc.branchTransitNumber().getValue());
        }
        if (!omacc.accountNumber().equals(requestAcc.accountNumber())) {
            throw new Error("Account " + requestAcc.onboardingBankAccountId().getValue() + " accountNumber mismatch " + requestAcc.accountNumber().getValue());
        }
        omacc.merchantTerminalId().setValue(requestAcc.terminalId().getValue());
        omacc.pmc().set(pmc);

        Persistence.service().persist(omacc);

        // See if PMC is created, Then copy the same data to Pmc namespace
        if ((pmc != null) && (pmc.status().getValue() != PmcStatus.Created)) {
            if (omacc.merchantAccountKey().isNull()) {
                throw new Error("Account " + requestAcc.onboardingBankAccountId().getValue() + " has invalid reference");
            }

            TaskRunner.runInTargetNamespace(pmc.namespace().getValue(), new Callable<Void>() {
                @Override
                public Void call() {
                    MerchantAccount macc = Persistence.service().retrieve(MerchantAccount.class, omacc.merchantAccountKey().getValue());
                    macc.invalid().setValue(Boolean.FALSE);
                    macc.merchantTerminalId().setValue(omacc.merchantTerminalId().getValue());
                    Persistence.service().persist(macc);
                    return null;
                }
            });
        } else {
            if (!omacc.merchantAccountKey().isNull()) {
                throw new Error("Account " + requestAcc.onboardingBankAccountId().getValue() + " has invalid reference");
            }
        }

    }

}
