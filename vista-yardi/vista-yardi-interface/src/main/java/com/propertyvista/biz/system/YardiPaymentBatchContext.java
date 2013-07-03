/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 2, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import java.rmi.RemoteException;

import org.apache.commons.lang.Validate;

import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.payment.PaymentBatchContext;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.services.YardiSystemBatchesService;

public class YardiPaymentBatchContext implements PaymentBatchContext {

    private PmcYardiCredential yc;

    private Long batchId;

    private String propertyCode;

    private int recordCount = 0;

    public boolean isOpen() {
        return this.batchId != null;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void ensureOpenBatch(PmcYardiCredential yc, String propertyCode) throws RemoteException, YardiServiceException {
        if (isOpen()) {
            Validate.isTrue(this.propertyCode.equals(propertyCode), "Single propertyCode in one batch");
        } else {
            this.batchId = YardiSystemBatchesService.getInstance().openReceiptBatch(yc, propertyCode);
            this.propertyCode = propertyCode;
            this.yc = yc;
        }
    }

    public void incrementRecordCount() {
        recordCount++;
    }

    @Override
    public void postBatch() throws ARException {
        try {
            YardiSystemBatchesService.getInstance().postBatch(yc, this);
        } catch (RemoteException e) {
            throw new ARException("Posting Batch to Yardi failed due to communication failure", e);
        } catch (YardiServiceException e) {
            throw new ARException("Posting Batch to Yardi failed", e);
        }
    }

    @Override
    public void cancelBatch() throws ARException {
        try {
            YardiSystemBatchesService.getInstance().cancelBatch(yc, this);
        } catch (RemoteException e) {
            throw new ARException("Posting Batch to Yardi failed due to communication failure", e);
        } catch (YardiServiceException e) {
            throw new ARException("Posting Batch to Yardi failed", e);
        }
    }

    @Override
    public boolean isBatchFull() {
        return (recordCount >= 500);
    }
}
