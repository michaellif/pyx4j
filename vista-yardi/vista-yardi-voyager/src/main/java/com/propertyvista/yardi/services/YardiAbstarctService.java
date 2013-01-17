/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.ws.operations.Ping;
import com.yardi.ws.operations.PingResponse;

import com.propertyvista.yardi.YardiClient;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.YardiParameters;

public class YardiAbstarctService {

    private final static Logger log = LoggerFactory.getLogger(YardiAbstarctService.class);

    void validate(YardiParameters yp) {
        Validate.notEmpty(yp.getServiceURL(), "ServiceURL parameter can not be empty or null");
        Validate.notEmpty(yp.getUsername(), "Username parameter can not be empty or null");
        Validate.notEmpty(yp.getPassword(), "Password parameter can not be empty or null");
        Validate.notEmpty(yp.getServerName(), "ServerName parameter can not be empty or null");
        Validate.notEmpty(yp.getDatabase(), "Database parameter can not be empty or null");
        Validate.notEmpty(yp.getPlatform(), "Platform parameter can not be empty or null");
        Validate.notEmpty(yp.getInterfaceEntity(), "InterfaceEntity parameter can not be empty or null");
    }

    /**
     * The Ping function accepts no parameters, but will return the
     * assembly name of the function being called. Use it to test
     * connectivity.
     * 
     * @throws RemoteException
     * @throws AxisFault
     */
    public static void ping(YardiClient c) throws AxisFault, RemoteException {
        c.transactionId++;
        c.setCurrentAction(Action.ping);

        Ping ping = new Ping();
        PingResponse pr = c.getResidentTransactionsService().ping(ping);
        log.info("Connection to Yardi works: {}", pr.getPingResult());
    }
}
