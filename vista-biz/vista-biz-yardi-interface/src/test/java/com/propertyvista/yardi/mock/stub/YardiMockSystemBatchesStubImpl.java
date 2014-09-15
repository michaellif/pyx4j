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

import com.yardi.entity.resident.ResidentTransactions;

import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.mock.YardiMockServer;
import com.propertyvista.yardi.stubs.YardiSystemBatchesStub;

public class YardiMockSystemBatchesStubImpl implements YardiSystemBatchesStub {

    @Override
    public long openReceiptBatch(PmcYardiCredential yc, String propertyCode) throws YardiServiceException {
        return YardiMockServer.instance().openReceiptBatch(propertyCode);
    }

    @Override
    public void addReceiptsToBatch(PmcYardiCredential yc, long batchId, ResidentTransactions residentTransactions) throws YardiServiceException {
        YardiMockServer.instance().addReceiptsToBatch(batchId, residentTransactions);
    }

    @Override
    public void addReceiptsReversalToBatch(PmcYardiCredential yc, long batchId, ResidentTransactions residentTransactions) throws YardiServiceException {
        YardiMockServer.instance().addReceiptsToBatch(batchId, residentTransactions);
    }

    @Override
    public void postReceiptBatch(PmcYardiCredential yc, long batchId) throws YardiServiceException {
        YardiMockServer.instance().postReceiptBatch(batchId);
    }

    @Override
    public void cancelReceiptBatch(PmcYardiCredential yc, long batchId) throws YardiServiceException {
        YardiMockServer.instance().cancelReceiptBatch(batchId);

    }

    @Override
    public String ping(PmcYardiCredential yc) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void validate(PmcYardiCredential yc) throws YardiServiceException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getPluginVersion(PmcYardiCredential yc) {
        // TODO Auto-generated method stub
        return null;
    }
}
