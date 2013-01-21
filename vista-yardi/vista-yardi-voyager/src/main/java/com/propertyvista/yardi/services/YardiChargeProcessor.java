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

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.ChargeDetail;
import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.yardi.YardiBillingAccount;
import com.propertyvista.domain.financial.yardi.YardiCharge;
import com.propertyvista.domain.financial.yardi.YardiService;
import com.propertyvista.domain.tenant.lease.Lease;

public class YardiChargeProcessor {
    private final static Logger log = LoggerFactory.getLogger(YardiChargeProcessor.class);

    public void updateCharges(List<ResidentTransactions> allTransactions) {
        log.info("updateCharges: started...");

        for (ResidentTransactions rt : allTransactions) {
            for (Property prop : rt.getProperty()) {
                for (RTCustomer cust : prop.getRTCustomer()) {
                    log.info("Transaction for: " + cust.getCustomerID() + "/" + cust.getRTUnit().getUnitID());
                    // 1. get customer's YardiBillingAccount
                    YardiBillingAccount account = getYardiBillingAccount(cust);
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
                        Persistence.service().persist(createCharge(account, tr.getCharge().getDetail()));
                    }
                    Persistence.service().commit();
                }
            }
        }
    }

    private YardiBillingAccount getYardiBillingAccount(RTCustomer customer) {
        EntityQueryCriteria<Lease> leaseCrit = EntityQueryCriteria.create(Lease.class);
        leaseCrit.add(PropertyCriterion.eq(leaseCrit.proto().leaseId(), customer.getCustomerID()));
        Lease lease = Persistence.service().retrieve(leaseCrit);
        if (lease == null) {
            // no lease found - quit
            return null;
        }
        EntityQueryCriteria<YardiBillingAccount> accntCrit = EntityQueryCriteria.create(YardiBillingAccount.class);
        accntCrit.add(PropertyCriterion.eq(accntCrit.proto().lease(), lease));
        YardiBillingAccount account = Persistence.service().retrieve(accntCrit);
        if (account == null) {
            // create new account
            account = EntityFactory.create(YardiBillingAccount.class);
            account.lease().set(lease);
            Persistence.service().persist(account);
        }

        return account;
    }

    private YardiCharge createCharge(YardiBillingAccount account, ChargeDetail detail) {
        YardiCharge charge = EntityFactory.create(YardiCharge.class);
        charge.billingAccount().set(account);
        if (detail.getService() != null) {
            try {
                charge.service().type().setValue(YardiService.Type.valueOf(detail.getService().getType()));
            } catch (Exception e) {
                log.info("ERROR - unknown service type: " + e);
            }
        }
        charge.chargeCode().setValue(detail.getChargeCode());
        charge.amount().setValue(new BigDecimal(detail.getAmount()));
        charge.description().setValue(detail.getDescription());
        charge.postDate().setValue(new LogicalDate(detail.getTransactionDate().getTime()));
        charge.transactionId().setValue(detail.getTransactionID());
        charge.amountPaid().setValue(new BigDecimal(detail.getAmountPaid()));
        charge.balanceDue().setValue(new BigDecimal(detail.getBalanceDue()));
        charge.comment().setValue(detail.getComment());
        charge.taxTotal().setValue(BigDecimal.ZERO);

        return charge;
    }
}
