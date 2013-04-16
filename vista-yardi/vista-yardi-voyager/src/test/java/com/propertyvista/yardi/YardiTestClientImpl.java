/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-11
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

public class YardiTestClientImpl implements YardiClient {

    @Override
    public void setPmcYardiCredential(PmcYardiCredential pmcYardiCredential) {
        // TODO Auto-generated method stub

    }

    @Override
    public void transactionIdStart() {
        // TODO Auto-generated method stub

    }

    @Override
    public ItfResidentTransactions2_0 getResidentTransactionsService() throws AxisFault {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ItfResidentTransactions20_SysBatch getResidentTransactionsSysBatchService() throws AxisFault {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ItfServiceRequests getMaintenanceRequestsService() throws AxisFault {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Action getCurrentAction() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCurrentAction(Action currentAction) {
        // TODO Auto-generated method stub

    }

}
