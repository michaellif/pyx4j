/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 21, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.billingext.ExternalBillingFacade;
import com.propertyvista.domain.financial.billingext.dto.ChargeDTO;
import com.propertyvista.domain.financial.billingext.dto.PaymentRecordDTO;
import com.propertyvista.oapi.marshaling.ChargeMarshaller;
import com.propertyvista.oapi.marshaling.PaymentRecordMarshaller;
import com.propertyvista.oapi.model.ChargeIO;
import com.propertyvista.oapi.model.PaymentRecordIO;
import com.propertyvista.oapi.model.TransactionIO;

public class ReceivableService {

    public static void postTransaction(TransactionIO transaction) {
        if (transaction instanceof ChargeIO) {
            ChargeIO chargeIO = (ChargeIO) transaction;
            ChargeDTO chargeDTO = ChargeMarshaller.getInstance().unmarshal(chargeIO);

            ServerSideFactory.create(ExternalBillingFacade.class).postCharge(chargeDTO, chargeIO.leaseId);
        }
    }

    public static void runBilling(String propertyCode) {
        ServerSideFactory.create(ExternalBillingFacade.class).runBilling(propertyCode);
    }

    public static List<PaymentRecordIO> getNonProcessedPaymentRecords() {
        // ExternalPaymentFacade.onPaymentRecordCreated(PaymentRecord pr) will create an ExtendedPaymentRecord
        // that will be owned by BillingAccount, will reference original PaymentRecord, and will hold externalTransactionId
        // when that record is reconciled. So, here we return all the records where externalTransactionId is not set.
        List<PaymentRecordDTO> payments = ServerSideFactory.create(ExternalBillingFacade.class).getNonProcessedPaymentRecords();
        if (payments == null || payments.size() == 0) {
            return null;
        }

        List<PaymentRecordIO> records = new ArrayList<PaymentRecordIO>();
        for (PaymentRecordDTO prDTO : payments) {
            PaymentRecordIO pr = PaymentRecordMarshaller.getInstance().marshal(prDTO);
            records.add(pr);
        }

        return records;
    }

    public static void reconcilePaymentRecords(List<PaymentRecordIO> records) {
        // add PaymentRecordIO.externalTransactionId to the ExternalPaymentRecord referenced by PaymentRecordIO.transactionId
        // to flag the PaymentRecord as "processed" and be able to find it later when processing incoming payment transactions
        if (records == null || records.size() == 0) {
            return;
        }
        List<PaymentRecordDTO> payments = new ArrayList<PaymentRecordDTO>();
        for (PaymentRecordIO record : records) {
            payments.add(PaymentRecordMarshaller.getInstance().unmarshal(record));
        }
        ServerSideFactory.create(ExternalBillingFacade.class).reconcilePaymentRecords(payments);
    }
}
