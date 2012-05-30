/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-30
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.admin.domain.pmc.OnboardingMerchantAccount;
import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.DemoData.DemoPmc;

public class PmcCreatorDev {

    public static Pmc createPmc(String pmcName) {
        Pmc pmc = EntityFactory.create(Pmc.class);
        pmc.name().setValue(pmcName + " Demo");
        pmc.status().setValue(PmcStatus.Active);
        pmc.dnsName().setValue(pmcName);
        pmc.namespace().setValue(pmcName.replace('-', '_'));

        Persistence.service().persist(pmc);

        if (pmcName.equals(DemoData.DemoPmc.vista.name())) {
            {
                OnboardingMerchantAccount merchantAccount = EntityFactory.create(OnboardingMerchantAccount.class);
                merchantAccount.pmc().set(pmc);
                merchantAccount.merchantTerminalId().setValue("BIRCHWTT");
                merchantAccount.bankId().setValue("001");
                merchantAccount.branchTransitNumber().setValue("00550");
                merchantAccount.accountNumber().setValue("12345678");
                merchantAccount.chargeDescription().setValue("Pay for VistaT");
                Persistence.service().persist(merchantAccount);
            }
            {
                OnboardingMerchantAccount merchantAccount = EntityFactory.create(OnboardingMerchantAccount.class);
                merchantAccount.pmc().set(pmc);
                merchantAccount.merchantTerminalId().setValue("BIRCHWT1");
                merchantAccount.bankId().setValue("002");
                merchantAccount.branchTransitNumber().setValue("00750");
                merchantAccount.accountNumber().setValue("01234567");

                merchantAccount.chargeDescription().setValue("Pay for Vista1");

                Persistence.service().persist(merchantAccount);
            }
        } else {
            int ordinal = 0;
            try {
                DemoPmc pmcId = DemoData.DemoPmc.valueOf(pmcName);
                ordinal = pmcId.ordinal();
            } catch (IllegalArgumentException ignore) {
            }
            if (ordinal != 0) {
                for (int n = 0; n <= 3; n++) {
                    OnboardingMerchantAccount merchantAccount = EntityFactory.create(OnboardingMerchantAccount.class);
                    merchantAccount.pmc().set(pmc);
                    merchantAccount.merchantTerminalId().setValue("BIRCHW" + ordinal + n);
                    merchantAccount.bankId().setValue(ordinal + "02");
                    merchantAccount.branchTransitNumber().setValue("0100" + n);
                    merchantAccount.accountNumber().setValue("01234567");

                    merchantAccount.chargeDescription().setValue("Pay for " + pmcName + " " + n);

                    Persistence.service().persist(merchantAccount);
                }
            }

        }

        return pmc;
    }
}
