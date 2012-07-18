/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-17
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.admin.domain.pmc.OnboardingMerchantAccount;
import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;

public class PmcFacadeImpl implements PmcFacade {

    @Override
    public void terminateCancelledPmc(Pmc pmcId) {
        Pmc pmc = Persistence.service().retrieve(Pmc.class, pmcId.getPrimaryKey());

        pmc.status().setValue(PmcStatus.Terminated);

        // unreserve names
        pmc.namespace().setValue("__" + pmc.getPrimaryKey());
        pmc.dnsName().setValue("__" + pmc.getPrimaryKey());

        //remove all values
        pmc.name().setValue(null);
        pmc.dnsNameAliases().clear();
        pmc.features().set(null);
        pmc.equifaxInfo().set(null);
        pmc.paymentTypeInfo().set(null);

        Persistence.service().persist(pmc);

        Persistence.service().retrieveMember(pmc.merchantAccounts());
        for (OnboardingMerchantAccount merchantAccount : pmc.merchantAccounts()) {
            merchantAccount.bankId().setValue(null);
            merchantAccount.branchTransitNumber().setValue(null);
            merchantAccount.accountNumber().setValue(null);
            merchantAccount.chargeDescription().setValue(null);
            merchantAccount.merchantTerminalId().setValue(null);
            Persistence.service().persist(merchantAccount);
        }

        Persistence.service().commit();
    }
}
