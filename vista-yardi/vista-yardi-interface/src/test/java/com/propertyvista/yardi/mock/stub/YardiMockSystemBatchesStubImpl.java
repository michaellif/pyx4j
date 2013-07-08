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

import com.yardi.entity.resident.ResidentTransactions;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.mock.YardiMockServer;
import com.propertyvista.yardi.stub.YardiSystemBatchesStub;

public class YardiMockSystemBatchesStubImpl implements YardiSystemBatchesStub {

    @Override
    public long openReceiptBatch(PmcYardiCredential yc, String propertyCode) throws YardiServiceException, RemoteException {
        return YardiMockServer.instance().openReceiptBatch(propertyCode);
    }

    @Override
    public void addReceiptsToBatch(PmcYardiCredential yc, long batchId, ResidentTransactions residentTransactions) throws YardiServiceException,
            RemoteException {
        YardiMockServer.instance().addReceiptsToBatch(batchId, residentTransactions);
    }

    @Override
    public void postReceiptBatch(PmcYardiCredential yc, long batchId) throws YardiServiceException, RemoteException {
        YardiMockServer.instance().postReceiptBatch(batchId);
    }

    @Override
    public void cancelReceiptBatch(PmcYardiCredential yc, long batchId) throws YardiServiceException, RemoteException {
        YardiMockServer.instance().cancelReceiptBatch(batchId);

    }

}
