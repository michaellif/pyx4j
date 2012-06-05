/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 25, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

public class BillingInitialBalanceProcessor extends AbstractBillingProcessor {

    BillingInitialBalanceProcessor(AbstractBillingManager billingManager) {
        super(billingManager);
    }

    @Override
    protected void execute() {
        createInitialBalanceRecord();
    }

    private void createInitialBalanceRecord() {
        //TODO

        //1. get initial balance
        // if not null
        //2. create current bill charges/adjustments etc..
        //3. create initial debit/credit so initial debit/credit + charges = initial balance
        //

    }

}
