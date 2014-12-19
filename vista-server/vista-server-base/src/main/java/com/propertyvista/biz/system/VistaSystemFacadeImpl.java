/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 22, 2013
 * @author vlads
 */
package com.propertyvista.biz.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.payment.PaymentProcessFacade;

public class VistaSystemFacadeImpl implements VistaSystemFacade {

    private final static Logger log = LoggerFactory.getLogger(VistaSystemFacadeImpl.class);

    private static boolean communicationsDisabled;

    @Override
    public void setCommunicationsDisabled(boolean disabled) {
        if (communicationsDisabled != disabled) {
            communicationsDisabled = disabled;
            log.info("Communication is now {}", communicationsDisabled ? "Disabled" : "Enabled");
        }
    }

    @Override
    public boolean isCommunicationsDisabled() {
        return communicationsDisabled;
    }

    @Override
    public void healthMonitorOperations(ExecutionMonitor executionMonitor, LogicalDate forDate) {
        new SystemHealthMonitor(executionMonitor).healthMonitor(forDate);
        ServerSideFactory.create(PaymentProcessFacade.class).healthMonitorOperations(executionMonitor, forDate);
    }

}
