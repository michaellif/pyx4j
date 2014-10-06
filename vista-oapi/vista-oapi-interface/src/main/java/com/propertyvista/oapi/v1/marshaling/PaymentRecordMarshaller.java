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
package com.propertyvista.oapi.v1.marshaling;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.financial.billingext.dto.PaymentRecordDTO;
import com.propertyvista.oapi.AbstractMarshaller;
import com.propertyvista.oapi.v1.model.PaymentRecordIO;
import com.propertyvista.oapi.xml.BigDecimalIO;
import com.propertyvista.oapi.xml.LogicalDateIO;
import com.propertyvista.oapi.xml.StringIO;

public class PaymentRecordMarshaller extends AbstractMarshaller<PaymentRecordDTO, PaymentRecordIO> {

    private static class SingletonHolder {
        public static final PaymentRecordMarshaller INSTANCE = new PaymentRecordMarshaller();
    }

    private PaymentRecordMarshaller() {
    }

    public static PaymentRecordMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    protected PaymentRecordIO marshal(PaymentRecordDTO payment) {
        if (payment == null || payment.isNull()) {
            return null;
        }
        PaymentRecordIO paymentIO = new PaymentRecordIO();
        paymentIO.transactionId = getValue(payment.transactionId());

        paymentIO.externalTransactionId = createIo(StringIO.class, payment.externalTransactionId());
        paymentIO.leaseId = createIo(StringIO.class, payment.leaseId());
        paymentIO.amount = createIo(BigDecimalIO.class, payment.amount());
        paymentIO.transactionDate = createIo(LogicalDateIO.class, payment.transactionDate());
        paymentIO.paymentType = createIo(StringIO.class, payment.paymentType());
        return paymentIO;
    }

    @Override
    protected PaymentRecordDTO unmarshal(PaymentRecordIO paymentIO) {
        PaymentRecordDTO payment = EntityFactory.create(PaymentRecordDTO.class);
        payment.transactionId().setValue(paymentIO.transactionId);

        setValue(payment.externalTransactionId(), paymentIO.externalTransactionId);
        setValue(payment.leaseId(), paymentIO.leaseId);
        setValue(payment.amount(), paymentIO.amount);
        setValue(payment.transactionDate(), paymentIO.transactionDate);
        setValue(payment.paymentType(), paymentIO.paymentType);
        return payment;
    }

}
