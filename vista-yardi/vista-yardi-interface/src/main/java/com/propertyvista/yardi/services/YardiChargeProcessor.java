/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import com.yardi.entity.resident.RTCustomer;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.financial.ar.yardi.YardiARIntegrationAgent;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.yardi.YardiCredit;
import com.propertyvista.domain.financial.yardi.YardiDebit;

public class YardiChargeProcessor {

    BillingAccount getAccount(final Key yardiInterfaceId, RTCustomer cust) throws YardiServiceException {
        BillingAccount account = YardiARIntegrationAgent.getYardiBillingAccount(yardiInterfaceId, cust);
        if (account == null) {
            throw new YardiServiceException("YardiBillingAccount is null for RTCustomer");
        }
        return account;
    }

    void removeOldCharges(BillingAccount account) {
        // regular charges
        EntityQueryCriteria<YardiDebit> oldCharges = EntityQueryCriteria.create(YardiDebit.class);
        oldCharges.eq(oldCharges.proto().billingAccount(), account);
        Persistence.service().delete(oldCharges);
        // negative charges
        EntityQueryCriteria<YardiCredit> oldCredits = EntityQueryCriteria.create(YardiCredit.class);
        oldCredits.eq(oldCredits.proto().billingAccount(), account);
        Persistence.service().delete(oldCredits);
    }
}
