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

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.payment.PaymentBatchContext;
import com.propertyvista.biz.system.yardi.YardiServiceException;
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

    @Override
    public String getBatchNumber() {
        return Long.toString(batchId, 10);
    }

    public void ensureOpenBatch(PmcYardiCredential yc, String propertyCode) throws RemoteException, YardiServiceException {
        if (isOpen()) {
            Validate.isTrue(this.propertyCode.equals(propertyCode), "Single propertyCode " + this.propertyCode + " expected in one batch, but not "
                    + propertyCode);
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
        if (recordCount == 0) {
            cancelBatch();
        } else {
            String error = null;
            try {
                YardiSystemBatchesService.getInstance().postBatch(yc, this);
            } catch (RemoteException e) {
                error = new SimpleMessageFormat("Unable to post Batch {0} ({1}) to Yardi due to communication failure.").format(batchId, propertyCode);
                throw new ARException(error, e);
            } catch (YardiServiceException e) {
                error = new SimpleMessageFormat("Unable to post Batch {0} ({1}) to Yardi: {2}").format(batchId, propertyCode, e.getMessage());
                throw new ARException(error, e);
            } finally {
                if (error != null) {
                    ServerSideFactory.create(NotificationFacade.class).yardiUnableToPostPaymentBatch(error);
                }
            }
        }
    }

    @Override
    public void cancelBatch() throws ARException {
        String error = null;
        try {
            YardiSystemBatchesService.getInstance().cancelBatch(yc, this);
        } catch (RemoteException e) {
            error = new SimpleMessageFormat("Unable to cancel Yardi Batch {0} ({1}) due to communication failure").format(batchId, propertyCode);
            throw new ARException(error, e);
        } catch (YardiServiceException e) {
            error = new SimpleMessageFormat("Unable to cancel Yardi Batch {0} ({1}): {2}").format(batchId, propertyCode, e.getMessage());
            throw new ARException(error, e);
        } finally {
            if (error != null) {
                ServerSideFactory.create(NotificationFacade.class).yardiUnableToPostPaymentBatch(error);
            }
        }
    }

    @Override
    public boolean isBatchFull() {
        return (recordCount >= 500);
    }
}
