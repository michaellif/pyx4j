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

import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public interface DepositFacade {
    /*
     * DepositType may limit acceptable target products, for example, LastMonthDeposit will only accept
     * ServiceType products
     */
    Deposit createDeposit(DepositType type, BillableItem product);

    /*
     * Every deposit must collect interest based on the corresponding policy rules and interest rates
     */
    void collectInterest(Deposit deposit);

    /*
     * Request to cover expenses may be rejected based on the expense type, so the boolean is returned
     */
    boolean coverAccountExpense(Deposit deposit, LeaseAdjustment expense);

    boolean coverProductExpense(Deposit deposit, BillableItem expense);

    /*
     * Update deposit if target product terms have changed
     */
    void onTargetProductChange(Deposit deposit);
}
