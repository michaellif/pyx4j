/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 26, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi;

import java.rmi.RemoteException;

import javax.xml.bind.JAXBException;

import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.yardi.bean.resident.ResidentTransactions;

public class GetResidentTransactionsLifecycle {
    private final static Logger log = LoggerFactory.getLogger(GetResidentTransactionsLifecycle.class);

    public void download(YardiClient c, YardiParameters yp) throws AxisFault, RemoteException, JAXBException {
        ResidentTransactions transactions = YardiTransactions.getResidentTransactions(c, yp);
        log.info("{}", transactions);
    }
}
