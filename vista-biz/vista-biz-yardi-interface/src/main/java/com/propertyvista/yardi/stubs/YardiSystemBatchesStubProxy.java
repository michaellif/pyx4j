/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 5, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.stubs;

import java.rmi.RemoteException;

import com.yardi.entity.resident.ResidentTransactions;

import com.propertyvista.biz.system.yardi.YardiPropertyNoAccessException;
import com.propertyvista.biz.system.yardi.YardiResponseException;
import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;

public class YardiSystemBatchesStubProxy extends YardiAbstractStubProxy implements YardiSystemBatchesStub {

    public YardiSystemBatchesStubProxy() {
        setMessageErrorHandler(noPropertyAccessHandler);
    }

    private YardiSystemBatchesStub getStub(PmcYardiCredential yc) {
        return getStubInstance(YardiSystemBatchesStub.class, yc);
    }

    @Override
    public String ping(PmcYardiCredential yc) {
        return getStub(yc).ping(yc);
    }

    @Override
    public String getPluginVersion(PmcYardiCredential yc) {
        return getStub(yc).getPluginVersion(yc);
    }

    @Override
    public void validate(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        getStub(yc).validate(yc);
    }

    @Override
    public long openReceiptBatch(PmcYardiCredential yc, String propertyCode) throws YardiServiceException, RemoteException {
        long result = getStub(yc).openReceiptBatch(yc, propertyCode);
        if (result > 0) {
            return result;
        } else if (result == -1) {
            throw new YardiServiceException("Web service user has insufficient privileges for this operation");
        } else if (result == -2) {
            throw new YardiPropertyNoAccessException("Interface Entity does not have access to Yardi Property " + propertyCode);
        } else {
            throw new YardiServiceException("Could not open Receipt batch");
        }
    }

    @Override
    public void addReceiptsToBatch(PmcYardiCredential yc, long batchId, ResidentTransactions residentTransactions) throws YardiServiceException,
            RemoteException {
        try {
            getStub(yc).addReceiptsToBatch(yc, batchId, residentTransactions);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
    }

    @Override
    public void postReceiptBatch(PmcYardiCredential yc, long batchId) throws YardiServiceException, RemoteException {
        try {
            getStub(yc).postReceiptBatch(yc, batchId);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
    }

    @Override
    public void cancelReceiptBatch(PmcYardiCredential yc, long batchId) throws YardiServiceException, RemoteException {
        try {
            getStub(yc).cancelReceiptBatch(yc, batchId);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
    }

}
