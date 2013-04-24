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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.ar.yardi.YardiARIntegrationAgent;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.financial.yardi.YardiBillingAccount;
import com.propertyvista.domain.financial.yardi.YardiDebit;
import com.propertyvista.domain.financial.yardi.YardiCredit;

public class YardiChargeProcessor {
    YardiBillingAccount getAccount(RTCustomer cust) throws YardiServiceException {
        YardiBillingAccount account = YardiARIntegrationAgent.getYardiBillingAccount(cust);
        if (account == null) {
            throw new YardiServiceException("YardiBillingAccount is null for RTCustomer");
        }
        return account;
    }

    void removeOldCharges(YardiBillingAccount account) {
        // regular charges
        EntityQueryCriteria<YardiDebit> oldCharges = EntityQueryCriteria.create(YardiDebit.class);
        oldCharges.add(PropertyCriterion.eq(oldCharges.proto().billingAccount(), account));
        Persistence.service().delete(oldCharges);
        // negative charges
        EntityQueryCriteria<YardiCredit> oldCredits = EntityQueryCriteria.create(YardiCredit.class);
        oldCredits.add(PropertyCriterion.eq(oldCredits.proto().billingAccount(), account));
        Persistence.service().delete(oldCredits);
    }
}
