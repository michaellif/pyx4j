/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.mock.stub;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;

import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.bean.Properties;
import com.propertyvista.yardi.mock.YardiMockServer;
import com.propertyvista.yardi.stub.YardiResidentTransactionsStub;

public class YardiMockResidentTransactionsStubImpl implements YardiResidentTransactionsStub {

    @Override
    public long getRequestsTime() {
        return 0;
    }

    @Override
    public String ping(PmcYardiCredential yc) throws AxisFault {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException {
        return null;
    }

    @Override
    public ResidentTransactions getAllResidentTransactions(PmcYardiCredential yc, String propertyId) throws YardiServiceException {
        return YardiMockServer.instance().getAllResidentTransactions(propertyId);
    }

    @Override
    public ResidentTransactions getResidentTransactionsForTenant(PmcYardiCredential yc, String propertyId, String tenantId) throws YardiServiceException {
        return YardiMockServer.instance().getResidentTransactionsForTenant(propertyId, tenantId);
    }

    @Override
    public void importResidentTransactions(PmcYardiCredential yc, ResidentTransactions reversalTransactions) throws YardiServiceException {
        // HQSL transactions testing hack
        Persistence.service().commit();

        YardiMockServer.instance().importResidentTransactions(reversalTransactions);

    }

    @Override
    public void getUnitInformation(PmcYardiCredential yc, String propertyId) throws YardiServiceException {
        // TODO Auto-generated method stub

    }

    @Override
    public ResidentTransactions getAllLeaseCharges(PmcYardiCredential yc, String propertyId, LogicalDate date) throws YardiServiceException, RemoteException {
        return YardiMockServer.instance().getAllLeaseCharges(propertyId);
    }

    @Override
    public ResidentTransactions getLeaseChargesForTenant(PmcYardiCredential yc, String propertyId, String tenantId, LogicalDate date)
            throws YardiServiceException, RemoteException {
        return YardiMockServer.instance().getLeaseChargesForTenant(propertyId, tenantId);
    }

}
