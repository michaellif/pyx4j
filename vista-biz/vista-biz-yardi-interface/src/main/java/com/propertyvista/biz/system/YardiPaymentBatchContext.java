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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.payment.PaymentBatchContext;
import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.yardi.YardiPaymentPostingBatch;
import com.propertyvista.domain.financial.yardi.YardiPaymentPostingBatch.YardiPostingStatus;
import com.propertyvista.domain.financial.yardi.YardiPaymentPostingBatchRecord;
import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.services.YardiSystemBatchesService;

public class YardiPaymentBatchContext implements PaymentBatchContext {

    private final static Logger log = LoggerFactory.getLogger(YardiPaymentBatchContext.class);

    private PmcYardiCredential yc;

    private Long batchId;

    private String propertyCode;

    private int recordCount = 0;

    private YardiPaymentPostingBatch batch;

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

    public void ensureOpenBatch(PmcYardiCredential yc, Building building) throws RemoteException, YardiServiceException {
        String propertyCode = building.propertyCode().getValue();
        if (isOpen()) {
            Validate.isTrue(this.propertyCode.equals(propertyCode), "Single propertyCode " + this.propertyCode + " expected in one batch, but not "
                    + propertyCode);
        } else {
            this.batchId = YardiSystemBatchesService.getInstance().openReceiptBatch(yc, propertyCode);
            this.propertyCode = propertyCode;
            this.yc = yc;
            createBatch(building);
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
                log.debug("batch {} {} - posting", propertyCode, batchId);
                YardiSystemBatchesService.getInstance().postBatch(yc, this);
                log.debug("batch {} {} - posted", propertyCode, batchId);
                batch.status().setValue(YardiPostingStatus.Posted);
            } catch (RemoteException e) {
                error = new SimpleMessageFormat("Unable to post Batch {0} ({1}) to Yardi due to communication failure.").format(batchId, propertyCode);
                throw new ARException(error, e);
            } catch (YardiServiceException e) {
                error = new SimpleMessageFormat("Unable to post Batch {0} ({1}) to Yardi: {2}").format(batchId, propertyCode, e.getMessage());
                throw new ARException(error, e);
            } finally {
                if (error != null) {
                    ServerSideFactory.create(NotificationFacade.class).yardiUnableToPostPaymentBatch(error);
                    batch.postFailedErrorMessage().setValue(error);
                }

                if (batch.status().getValue() != YardiPostingStatus.Posted) {
                    batch.postFailed().setValue(true);
                }

                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
                    @Override
                    public Void execute() throws RuntimeException {
                        batch.finalizeDate().setValue(SystemDateManager.getDate());
                        Persistence.service().persist(batch);
                        return null;
                    }
                });
            }
        }
    }

    @Override
    public void cancelBatch() throws ARException {
        String error = null;
        try {
            log.debug("batch {} {} - canceling", propertyCode, batchId);
            YardiSystemBatchesService.getInstance().cancelBatch(yc, this);
            log.debug("batch {} {} - canceled", propertyCode, batchId);

            batch.status().setValue(YardiPostingStatus.Canceled);

        } catch (RemoteException e) {
            error = new SimpleMessageFormat("Unable to cancel Yardi Batch {0} ({1}) due to communication failure").format(batchId, propertyCode);
            throw new ARException(error, e);
        } catch (YardiServiceException e) {
            error = new SimpleMessageFormat("Unable to cancel Yardi Batch {0} ({1}): {2}").format(batchId, propertyCode, e.getMessage());
            throw new ARException(error, e);
        } finally {
            if (error != null) {
                ServerSideFactory.create(NotificationFacade.class).yardiUnableToPostPaymentBatch(error);
                batch.cancelFailedErrorMessage().setValue(error);
            }

            if (batch.status().getValue() != YardiPostingStatus.Canceled) {
                batch.cancelFailed().setValue(true);
            }

            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
                @Override
                public Void execute() throws RuntimeException {
                    batch.finalizeDate().setValue(SystemDateManager.getDate());
                    Persistence.service().persist(batch);
                    return null;
                }
            });
        }
    }

    @Override
    public boolean isBatchFull() {
        return (recordCount >= 500);
    }

    public void addRecord(YardiReceipt receipt) {
        addRecord(receipt.paymentRecord(), false);
    }

    public void confirmedRecord(YardiReceipt receipt) {
        confirmedRecord(receipt.paymentRecord(), false);
    }

    public void addRecord(YardiReceiptReversal reversal) {
        addRecord(reversal.paymentRecord(), true);
    }

    public void confirmedRecord(YardiReceiptReversal reversal) {
        confirmedRecord(reversal.paymentRecord(), true);
    }

    private void createBatch(final Building building) {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() throws RuntimeException {
                batch = EntityFactory.create(YardiPaymentPostingBatch.class);

                batch.externalBatchNumber().setValue(String.valueOf(getBatchId()));
                batch.building().set(building);
                batch.status().setValue(YardiPostingStatus.Open);

                Persistence.service().persist(batch);
                return null;
            }
        });

    }

    private void addRecord(final PaymentRecord paymentRecord, final boolean reversal) {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() throws RuntimeException {
                YardiPaymentPostingBatchRecord record = EntityFactory.create(YardiPaymentPostingBatchRecord.class);
                record.batch().set(batch);
                record.paymentRecord().set(paymentRecord);
                record.reversal().setValue(reversal);
                record.added().setValue(false);
                Persistence.service().persist(record);
                return null;
            }
        });
    }

    private void confirmedRecord(final PaymentRecord paymentRecord, final boolean reversal) {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() throws RuntimeException {
                YardiPaymentPostingBatchRecord recordUpdate = EntityFactory.create(YardiPaymentPostingBatchRecord.class);
                recordUpdate.added().setValue(true);

                EntityQueryCriteria<YardiPaymentPostingBatchRecord> criteria = EntityQueryCriteria.create(YardiPaymentPostingBatchRecord.class);
                criteria.eq(criteria.proto().batch(), batch);
                criteria.eq(criteria.proto().paymentRecord(), paymentRecord);
                criteria.eq(criteria.proto().reversal(), reversal);
                criteria.eq(criteria.proto().added(), false);

                if (Persistence.service().update(criteria, recordUpdate) != 1) {
                    throw new AssertionError("Failed to update YardiPaymentPostingBatchRecord " + paymentRecord.id().getValue());
                }
                return null;
            }
        });
    }
}
