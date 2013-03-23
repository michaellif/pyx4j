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
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.ar.ARAbstractPaymentManager;
import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.biz.system.YardiProcessFacade;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;

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
    protected void postPayment(PaymentRecord paymentRecord) throws ARException {

        YardiReceipt receipt = createReceipt(paymentRecord);
        Persistence.service().persist(receipt);

        Persistence.ensureRetrieve(paymentRecord.billingAccount(), AttachLevel.Attached);
        Persistence.service().retrieve(paymentRecord.billingAccount().lease());

        try {
            ServerSideFactory.create(YardiProcessFacade.class).updateLease(paymentRecord.billingAccount().lease());
            ServerSideFactory.create(YardiProcessFacade.class).postReceipt(receipt);
        } catch (RemoteException e) {
            throw new ARException("Posting receipt to Yardi is failed due to communication failure", e);
        } catch (YardiServiceException e) {
            throw new ARException("Posting receipt to Yardi is failed", e);
        }

    }

    @Override
    protected void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) throws ARException {
        YardiReceiptReversal reversal = EntityFactory.create(YardiReceiptReversal.class);
        reversal.paymentRecord().set(paymentRecord);
        reversal.amount().setValue(paymentRecord.amount().getValue());
        reversal.billingAccount().set(paymentRecord.billingAccount());
        reversal.description().setValue(i18n.tr("Payment from ''{0}'' was rejected", paymentRecord.createdDate().getValue().toString()));
        reversal.taxTotal().setValue(BigDecimal.ZERO);
        reversal.applyNSF().setValue(applyNSF);

        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        BillingCycle billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(paymentRecord.billingAccount().lease(), now);
        BillingCycle nextCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(billingCycle);

        reversal.billingCycle().set(nextCycle);
        reversal.postDate().setValue(now);

        Persistence.service().persist(reversal);

        try {
            ServerSideFactory.create(YardiProcessFacade.class).postReceiptReversal(reversal);
        } catch (RemoteException e) {
            throw new ARException("Posting receipt reversal to Yardi is failed due to communication failure", e);
        } catch (YardiServiceException e) {
            throw new ARException("Posting receipt reversal to Yardi is failed", e);
        }

        try {
            Persistence.service().retrieve(paymentRecord.billingAccount().lease());
            ServerSideFactory.create(YardiProcessFacade.class).updateLease(paymentRecord.billingAccount().lease());
        } catch (Throwable ignoreDataRetrivalFromYardy) {
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

    @Override
    protected boolean validatePayment(PaymentRecord payment) throws ARException {
        YardiReceipt receipt = createReceipt(payment);

        try {
            ServerSideFactory.create(YardiProcessFacade.class).validateReceipt(receipt);
        } catch (RemoteException e) {
            throw new ARException("Receipt validation is failed due to communication failure with Yardi", e);
        } catch (YardiServiceException e) {
            throw new ARException("Receipt validation is failed", e);
        }

        return true;
    }
}
