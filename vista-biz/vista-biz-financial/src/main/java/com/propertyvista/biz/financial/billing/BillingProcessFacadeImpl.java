/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 16, 2012
 * @author michaellif
 */
package com.propertyvista.biz.financial.billing;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.billing.BillingCycle;

public class BillingProcessFacadeImpl implements BillingProcessFacade {

    @Override
    public void initializeFutureBillingCycles(LogicalDate forDate, ExecutionMonitor executionMonitor) {
        BillingProcessManager.instance().initializeFutureBillingCycles(forDate, executionMonitor);
    }

    @Override
    public void runBilling(LogicalDate date, ExecutionMonitor executionMonitor) {
        BillingProcessManager.instance().runBilling(date, executionMonitor);
    }

    @Override
    public void runBilling(BillingCycle billingCycle, ExecutionMonitor executionMonitor) {
        BillingProcessManager.instance().runBilling(billingCycle, executionMonitor);
    }
}
