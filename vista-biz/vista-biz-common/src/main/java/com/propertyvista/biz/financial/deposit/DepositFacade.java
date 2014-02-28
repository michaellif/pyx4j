/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 13, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.financial.deposit;

import java.util.List;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.DepositLifecycle;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public interface DepositFacade {

    public class ProductTerm {
        public final LogicalDate from;

        public final LogicalDate to;

        public final ProductItem productItem;

        public ProductTerm(BillableItem product, Lease lease) {
            from = product.effectiveDate().isNull() ? lease.currentTerm().termFrom().getValue() : product.effectiveDate().getValue();
            to = product.expirationDate().isNull() ? lease.currentTerm().termTo().getValue() : product.expirationDate().getValue();
            productItem = product.item();
        }
    }

    /*
     * Creates deposit instance based on the corresponding policy. DepositType may limit acceptable target
     * products, for example, LastMonthDeposit may only accept ServiceType products.
     */
    public Deposit createDeposit(DepositType depositType, BillableItem billableItem);

    /*
     * Create all deposits required by the policy
     */
    public List<Deposit> createRequiredDeposits(BillableItem billableItem);

    /*
     * Create DepositLifecycle wrapper on supplied Deposit
     */
    public DepositLifecycle createDepositLifecycle(Deposit deposit, BillingAccount billingAccount);

    /*
     * Retrieves corresponding Deposit for DepositLifecycle wrapper
     */
    public Deposit getDeposit(DepositLifecycle depositLifecycle);

    /*
     * Finds all deposit from the current version of lease (looks through all leases if lease = null)
     */
    public Map<Deposit, ProductTerm> getCurrentDeposits(Lease lease);

    /*
     * Every deposit must collect interest based on the corresponding policy rules and interest rates
     */
    void collectInterest(Lease lease);

    /*
     * Request to cover expenses may be rejected based on the expense type, so the boolean is returned
     */
    boolean coverAccountExpense(DepositLifecycle deposit, LeaseAdjustment expense);

    boolean coverProductExpense(DepositLifecycle deposit, BillableItem expense);

    /*
     * Update deposit if target product terms have changed
     */
    void onTargetProductChange(DepositLifecycle deposit);

    /*
     * SecurityDeposit - return with delay of N(policy) days;
     * LastMonthDeposit - return 1 month before lease end;
     * MoveInDeposit - return on first day of lease, we need to verify it doesn't create late payment fee;
     */
    void issueDepositRefunds(Lease lease);

    /*
     * Update related deposits to Billed status
     */
    void onConfirmedBill(Bill bill);
}
