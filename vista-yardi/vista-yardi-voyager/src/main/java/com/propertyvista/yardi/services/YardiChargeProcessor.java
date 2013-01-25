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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.YardiLease;
import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.yardi.YardiBillingAccount;
import com.propertyvista.domain.financial.yardi.YardiCharge;

public class YardiChargeProcessor {
    private final static Logger log = LoggerFactory.getLogger(YardiChargeProcessor.class);

    public void updateCharges(List<ResidentTransactions> allTransactions) {
        log.info("updateCharges: started...");

        for (ResidentTransactions rt : allTransactions) {
            for (Property prop : rt.getProperty()) {
                for (RTCustomer cust : prop.getRTCustomer()) {
                    // skip customer if lease expired
                    YardiLease yardiLease = cust.getCustomers().getCustomer().get(0).getLease();
                    if (new LogicalDate(yardiLease.getLeaseToDate()).before(new LogicalDate())) {
                        log.info("Transaction for: {} skipped, lease expired.", cust.getCustomerID());
                        continue;
                    }

                    log.info("Transaction for: " + cust.getCustomerID() + "/" + cust.getRTUnit().getUnitID());
                    // 1. get customer's YardiBillingAccount
                    YardiBillingAccount account = YardiProcessorUtils.getYardiBillingAccount(cust);
                    if (account == null) {
                        try {
                            Persistence.service().rollback();
                        } catch (Throwable ignore) {
                        }
                        continue;
                    }
                    // 2. remove previously added charges
                    EntityQueryCriteria<YardiCharge> oldCharges = EntityQueryCriteria.create(YardiCharge.class);
                    oldCharges.add(PropertyCriterion.eq(oldCharges.proto().billingAccount(), account));
                    Persistence.service().delete(oldCharges);
                    // 3. add new charges
                    // TODO - see if we can simply keep the unchanged charges instead of removing and adding them again
                    for (Transactions tr : cust.getRTServiceTransactions().getTransactions()) {
                        if (tr == null || tr.getCharge() == null) {
                            continue;
                        }
                        Persistence.service().persist(YardiProcessorUtils.createCharge(account, tr.getCharge().getDetail()));
                    }
                    Persistence.service().commit();
                }
            }
        }
    }
}
