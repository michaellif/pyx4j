/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 19, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.billingext.dto.PaymentRecordDTO;
import com.propertyvista.oapi.model.PaymentRecordIO;
import com.propertyvista.oapi.xml.BigDecimalIO;
import com.propertyvista.oapi.xml.LogicalDateIO;

public class PaymentRecordMarshaller implements Marshaller<PaymentRecordDTO, PaymentRecordIO> {

    private static class SingletonHolder {
        public static final PaymentRecordMarshaller INSTANCE = new PaymentRecordMarshaller();
    }

    private PaymentRecordMarshaller() {
    }

    public static PaymentRecordMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public PaymentRecordIO marshal(PaymentRecordDTO payment) {
        PaymentRecordIO paymentIO = new PaymentRecordIO();
        paymentIO.transactionId = payment.transactionId().getValue();
        paymentIO.externalTransactionId = payment.externalTransactionId().getValue();
        paymentIO.leaseId = payment.leaseId().getValue();
        paymentIO.amount = new BigDecimalIO(payment.amount().getValue());
        paymentIO.transactionDate = new LogicalDateIO(payment.transactionDate().getValue());
        paymentIO.paymentType = payment.paymentType().getValue();
        return paymentIO;
    }

    @Override
    public PaymentRecordDTO unmarshal(PaymentRecordIO paymentIO) {
        PaymentRecordDTO payment = EntityFactory.create(PaymentRecordDTO.class);
        payment.transactionId().setValue(paymentIO.transactionId);
        payment.externalTransactionId().setValue(paymentIO.externalTransactionId);
        payment.leaseId().setValue(paymentIO.leaseId);
        payment.amount().setValue(paymentIO.amount.value);
        payment.transactionDate().setValue(paymentIO.transactionDate.value);
        payment.paymentType().setValue(paymentIO.paymentType);
        return payment;
    }

}
