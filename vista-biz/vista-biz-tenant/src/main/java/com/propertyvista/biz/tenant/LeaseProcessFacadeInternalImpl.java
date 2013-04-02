/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.ExecutionMonitor;

public class LeaseProcessFacadeInternalImpl implements LeaseProcessFacade {

    @Override
    public void leaseActivation(ExecutionMonitor executionMonitor, LogicalDate date) {
        new LeaseProcessManagerInternal().leaseActivation(executionMonitor, date);
    }

    @Override
    public void leaseRenewal(ExecutionMonitor executionMonitor, LogicalDate date) {
        new LeaseProcessManagerInternal().leaseRenewal(executionMonitor, date);
    }

    @Override
    public void leaseCompletion(ExecutionMonitor executionMonitor, LogicalDate date) {
        new LeaseProcessManagerInternal().leaseCompletion(executionMonitor, date);
    }

}
