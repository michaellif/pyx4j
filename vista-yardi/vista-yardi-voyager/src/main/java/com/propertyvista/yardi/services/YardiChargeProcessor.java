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

import com.propertyvista.domain.financial.yardi.YardiAccount;
import com.propertyvista.domain.financial.yardi.YardiChargeDetail;
import com.propertyvista.domain.financial.yardi.YardiService;
import com.propertyvista.domain.financial.yardi.YardiTransactionDetail;
import com.propertyvista.domain.tenant.lease.Lease;

public class YardiChargeProcessor {
    private final static Logger log = LoggerFactory.getLogger(YardiChargeProcessor.class);

    public void updateCharges(List<ResidentTransactions> allTransactions) {
        log.info("updateCharges: started...");
        // check available leases
        EntityQueryCriteria<Lease> availLeases = EntityQueryCriteria.create(Lease.class);
        for (Lease lease : Persistence.service().query(availLeases)) {
            log.info("===> Found Lease: " + lease.leaseId().getValue());
        }

        for (ResidentTransactions rt : allTransactions) {
            for (Property prop : rt.getProperty()) {
                for (RTCustomer cust : prop.getRTCustomer()) {
                    log.info("Transaction for: " + cust.getCustomerID() + "/" + cust.getRTUnit().getUnitID());
                    // 1. get customer's YardiAccount
                    YardiAccount account = getYardiAccount(cust);
                    if (account == null) {
                        try {
                            Persistence.service().rollback();
                        } catch (Throwable ignore) {
                        }
                        continue;
                    }
                    // 2. remove previously added charges
                    EntityQueryCriteria<YardiChargeDetail> oldCharges = EntityQueryCriteria.create(YardiChargeDetail.class);
                    oldCharges.add(PropertyCriterion.eq(oldCharges.proto().account(), account));
                    Persistence.service().delete(oldCharges);
                    // 3. add new charges
                    // TODO - see if we can simply keep the unchanged charges instead of removing and adding them again
                    for (Transactions tr : cust.getRTServiceTransactions().getTransactions()) {
                        Persistence.service().persist(createCharge(account, tr.getCharge().getDetail()));
                    }
                    Persistence.service().commit();
                }
            }
        }
    }

    private YardiAccount getYardiAccount(RTCustomer customer) {
        EntityQueryCriteria<Lease> leaseCrit = EntityQueryCriteria.create(Lease.class);
        leaseCrit.add(PropertyCriterion.eq(leaseCrit.proto().leaseId(), customer.getCustomerID()));
        Lease lease = Persistence.service().retrieve(leaseCrit);
        if (lease == null) {
            // no lease found - quit
            return null;
        }
        EntityQueryCriteria<YardiAccount> accntCrit = EntityQueryCriteria.create(YardiAccount.class);
        accntCrit.add(PropertyCriterion.eq(accntCrit.proto().lease(), lease));
        YardiAccount account = Persistence.service().retrieve(accntCrit);
        if (account == null) {
            // create new account
            account = EntityFactory.create(YardiAccount.class);
            account.lease().set(lease);
            Persistence.service().persist(account);
        }

        return account;
    }

    private YardiChargeDetail createCharge(YardiAccount account, ChargeDetail detail) {
        YardiChargeDetail charge = EntityFactory.create(YardiChargeDetail.class);
        charge.account().set(account);
        try {
            charge.service().type().setValue(YardiService.Type.valueOf(detail.getService().getType()));
        } catch (Exception e) {
            log.info("ERROR - unknown service type: " + e);
        }
        charge.chargeCode().setValue(detail.getChargeCode());
        // transaction detail
        setTransactionDetail(charge, detail);

        return charge;
    }

    private void setTransactionDetail(YardiTransactionDetail yt, ChargeDetail detail) {
        yt.description().setValue(detail.getDescription());
        yt.transactionDate().setValue(new LogicalDate(detail.getTransactionDate().getTime()));
        yt.transactionId().setValue(detail.getTransactionID());
        yt.amountPaid().setValue(new BigDecimal(detail.getAmountPaid()));
        yt.balanceDue().setValue(new BigDecimal(detail.getBalanceDue()));
        yt.amount().setValue(new BigDecimal(detail.getAmount()));
        yt.comment().setValue(detail.getComment());
    }
}
