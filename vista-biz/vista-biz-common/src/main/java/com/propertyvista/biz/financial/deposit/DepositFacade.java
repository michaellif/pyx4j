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

import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public interface DepositFacade {
    /*
     * Creates deposit instance based on the corresponding policy. DepositType may limit acceptable target
     * products, for example, LastMonthDeposit may only accept ServiceType products.
     */
    Deposit createDeposit(DepositType depositType, ProductItemType productType, Lease lease);

    /*
     * Create all deposits required by the policy
     */
    List<Deposit> createRequiredDeposits(ProductItemType productType, Lease lease);

    /*
     * Every deposit must collect interest based on the corresponding policy rules and interest rates
     */
    void collectInterest(Deposit deposit, Lease lease);

    /*
     * Request to cover expenses may be rejected based on the expense type, so the boolean is returned
     */
    boolean coverAccountExpense(Deposit deposit, LeaseAdjustment expense);

    boolean coverProductExpense(Deposit deposit, BillableItem expense);

    /*
     * Update deposit if target product terms have changed
     */
    void onTargetProductChange(Deposit deposit);

    List<Deposit> getReturnList();
}
