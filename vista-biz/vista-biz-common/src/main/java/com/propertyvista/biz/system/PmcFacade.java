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

import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.pmc.Pmc;

/**
 * PMC management
 */
public interface PmcFacade {

    /**
     * Warning: It will ignore value passed as <code>pmc.namespace()</code> and generate a <b>namespace</b> based on some internal rule.
     */
    public void create(Pmc pmc);

    public boolean isOnboardingEnabled(Pmc pmc);

    public void activatePmc(Pmc pmcId);

    public void cancelPmc(Pmc pmc);

    public void terminateCancelledPmc(Pmc pmcId);

    public void deleteAllPmcData(Pmc pmcId);

    public boolean checkDNSAvailability(String dnsName);

    public boolean reservedDnsName(String dnsName, String onboardingAccountId);

    public void persistMerchantAccount(Pmc pmc, MerchantAccount merchantAccount);

}
