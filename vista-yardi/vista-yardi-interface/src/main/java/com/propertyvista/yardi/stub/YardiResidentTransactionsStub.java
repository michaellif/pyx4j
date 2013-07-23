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
package com.propertyvista.yardi.stub;

import java.rmi.RemoteException;

import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.bean.Properties;

public interface YardiResidentTransactionsStub {

    long getRequestsTime();

    String ping(PmcYardiCredential yc) throws RemoteException;

    Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException;

    ResidentTransactions getAllResidentTransactions(PmcYardiCredential yc, String propertyId) throws YardiServiceException, YardiPropertyNoAccessException,
            RemoteException;

    ResidentTransactions getResidentTransactionsForTenant(PmcYardiCredential yc, String propertyId, String tenantId) throws YardiServiceException,
            RemoteException;

    void importResidentTransactions(PmcYardiCredential yc, ResidentTransactions reversalTransactions) throws YardiServiceException, RemoteException;

    void getUnitInformation(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException;

    ResidentTransactions getAllLeaseCharges(PmcYardiCredential yc, String propertyId, LogicalDate date) throws YardiServiceException, RemoteException;

    ResidentTransactions getLeaseChargesForTenant(PmcYardiCredential yc, String propertyId, String tenantId, LogicalDate date) throws YardiServiceException,
            RemoteException, YardiResidentNoTenantsExistException;
}
