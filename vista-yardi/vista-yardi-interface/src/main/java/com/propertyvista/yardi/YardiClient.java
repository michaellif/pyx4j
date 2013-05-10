/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-15
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi;

import org.apache.axis2.AxisFault;

import com.yardi.ws.ItfResidentTransactions20_SysBatch;
import com.yardi.ws.ItfResidentTransactions2_0;
import com.yardi.ws.ItfServiceRequests;

import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiConstants.Action;

public interface YardiClient {

    public void setPmcYardiCredential(PmcYardiCredential pmcYardiCredential);

    public abstract void transactionIdStart();

    public abstract ItfResidentTransactions2_0 getResidentTransactionsService() throws AxisFault;

    public abstract ItfResidentTransactions20_SysBatch getResidentTransactionsSysBatchService() throws AxisFault;

    public abstract ItfServiceRequests getMaintenanceRequestsService() throws AxisFault;

    public abstract Action getCurrentAction();

    public abstract void setCurrentAction(Action currentAction);

}