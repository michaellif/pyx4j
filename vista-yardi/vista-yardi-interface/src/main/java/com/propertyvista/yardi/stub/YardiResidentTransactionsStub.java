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
import java.util.Calendar;

import com.yardi.entity.resident.ResidentTransactions;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.bean.Properties;

public interface YardiResidentTransactionsStub {

    String ping(PmcYardiCredential yc) throws RemoteException;

    Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException;

    ResidentTransactions getResidentTransactions(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException;

    ResidentTransactions getResidentTransaction(PmcYardiCredential yc, String propertyId, String tenantId) throws YardiServiceException, RemoteException;

    void importResidentTransactions(PmcYardiCredential yc, ResidentTransactions reversalTransactions) throws YardiServiceException, RemoteException;

    void getUnitInformation(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException;

    void getResidentsLeaseCharges(PmcYardiCredential yc, String propertyId, Calendar calendar) throws YardiServiceException, RemoteException;
}
