/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar.yardi;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.financial.ar.ARAbstractPaymentManager;
import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.biz.financial.payment.PaymentBatchContext;
import com.propertyvista.biz.system.UnableToPostTerminalYardiServiceException;
import com.propertyvista.biz.system.YardiARFacade;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;
import com.propertyvista.domain.property.asset.building.Building;

class ARYardiPaymentManager extends ARAbstractPaymentManager {

    private static final Logger log = LoggerFactory.getLogger(ARYardiPaymentManager.class);

    private static final I18n i18n = I18n.get(ARYardiPaymentManager.class);

    private ARYardiPaymentManager() {
    }

    private static class SingletonHolder {
        public static final ARYardiPaymentManager INSTANCE = new ARYardiPaymentManager();
    }

    static ARYardiPaymentManager instance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    protected PaymentBatchContext createPaymentBatchContext(Building building) throws ARException {
        try {
            return ServerSideFactory.create(YardiARFacade.class).createPaymentBatchContext(building);
        } catch (RemoteException e) {
            throw new ARException("Open Posting Batch to Yardi failed due to communication failure", e);
        } catch (YardiServiceException e) {
            throw new ARException(SimpleMessageFormat.format("Open Posting Batch to Yardi failed for building {0}", building.propertyCode()), e);
        }
    }

    @Override
    protected void postPayment(PaymentRecord paymentRecord, PaymentBatchContext paymentBatchContext) throws ARException {

        YardiReceipt receipt = createReceipt(paymentRecord);
        Persistence.service().persist(receipt);

        Persistence.ensureRetrieve(paymentRecord.billingAccount(), AttachLevel.Attached);
        Persistence.ensureRetrieve(paymentRecord.billingAccount().lease(), AttachLevel.Attached);

        try {
            // Do not update Lease in Batch posting process, It is done as separate process
            if (paymentBatchContext == null) {
                ServerSideFactory.create(YardiARFacade.class).updateLease(paymentRecord.billingAccount().lease());
            }
            ServerSideFactory.create(YardiARFacade.class).postReceipt(receipt, paymentBatchContext);
        } catch (RemoteException e) {
            throw new ARException(SimpleMessageFormat.format("Posting receipt {0} to Yardi failed due to communication failure; Lease Id {1}", //
                    paymentRecord.id(), paymentRecord.billingAccount().lease().leaseId()), e);
        } catch (YardiServiceException e) {
            throw new ARException(SimpleMessageFormat.format("Posting receipt {0} to Yardi failed; Lease Id {1}", //
                    paymentRecord.id(), paymentRecord.billingAccount().lease().leaseId()), e);
        }

        if (paymentBatchContext == null) {
            try {
                ServerSideFactory.create(YardiARFacade.class).updateLease(paymentRecord.billingAccount().lease());
            } catch (Throwable ignoreDataRetrivalFromYardy) {
                // We ignore error here because it will require unnecessary transaction reject 
                log.debug("ignoreDataRetrivalFromYardy", ignoreDataRetrivalFromYardy);
            }
        }

    }

    @Override
    protected void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) throws ARException {
        Persistence.ensureRetrieve(paymentRecord.billingAccount(), AttachLevel.Attached);

        YardiReceiptReversal reversal = EntityFactory.create(YardiReceiptReversal.class);
        reversal.paymentRecord().set(paymentRecord);
        reversal.amount().setValue(paymentRecord.amount().getValue());
        reversal.billingAccount().set(paymentRecord.billingAccount());
        reversal.description().setValue(i18n.tr("Payment from ''{0}'' was rejected", paymentRecord.createdDate().getValue().toString()));
        reversal.taxTotal().setValue(BigDecimal.ZERO);
        reversal.applyNSF().setValue(applyNSF);

        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        Persistence.ensureRetrieve(paymentRecord.billingAccount().lease(), AttachLevel.Attached);

        BillingCycle billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(paymentRecord.billingAccount().lease(), now);
        BillingCycle nextCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(billingCycle);

        reversal.billingCycle().set(nextCycle);
        reversal.postDate().setValue(now);

        Persistence.service().persist(reversal);

        try {
            ServerSideFactory.create(YardiARFacade.class).postReceiptReversal(reversal);
        } catch (RemoteException e) {
            throw new ARException(SimpleMessageFormat.format("Posting receipt {0} reversal to Yardi failed due to communication failure; Lease Id {1}", //
                    paymentRecord.id(), paymentRecord.billingAccount().lease().leaseId()), e);
        } catch (UnableToPostTerminalYardiServiceException e) {
            paymentRecord.notice().setValue(e.getMessage());
            Persistence.service().merge(paymentRecord);
            ServerSideFactory.create(NotificationFacade.class).yardiUnableToRejectPayment(paymentRecord, applyNSF);
        } catch (YardiServiceException e) {
            throw new ARException(SimpleMessageFormat.format("Posting receipt {0} reversal to Yardi failed; Lease Id {1}", //
                    paymentRecord.id(), paymentRecord.billingAccount().lease().leaseId()), e);
        }

        try {
            Persistence.service().retrieve(paymentRecord.billingAccount().lease());
            ServerSideFactory.create(YardiARFacade.class).updateLease(paymentRecord.billingAccount().lease());
        } catch (Throwable ignoreDataRetrivalFromYardy) {
            // We ignore error here because it will require unnecessary transaction reject
            log.debug("ignoreDataRetrivalFromYardy", ignoreDataRetrivalFromYardy);
        }
    }

    private YardiReceipt createReceipt(PaymentRecord paymentRecord) {
        YardiReceipt receipt = EntityFactory.create(YardiReceipt.class);
        receipt.paymentRecord().set(paymentRecord);
        receipt.amount().setValue(paymentRecord.amount().getValue().negate());
        receipt.billingAccount().set(paymentRecord.billingAccount());
        receipt.description().setValue(i18n.tr("Payment Received - Thank You"));

        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        Persistence.ensureRetrieve(paymentRecord.billingAccount(), AttachLevel.Attached);
        BillingCycle billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(paymentRecord.billingAccount().lease(), now);
        BillingCycle nextCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(billingCycle);

        receipt.billingCycle().set(nextCycle);
        receipt.postDate().setValue(now);

        return receipt;
    }

}