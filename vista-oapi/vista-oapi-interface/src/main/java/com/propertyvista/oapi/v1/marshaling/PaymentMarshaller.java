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

import com.propertyvista.domain.financial.billingext.dto.PaymentDTO;
import com.propertyvista.oapi.AbstractMarshaller;
import com.propertyvista.oapi.v1.model.PaymentIO;
import com.propertyvista.oapi.xml.BigDecimalIO;
import com.propertyvista.oapi.xml.StringIO;

public class PaymentMarshaller extends AbstractMarshaller<PaymentDTO, PaymentIO> {

    private static class SingletonHolder {
        public static final PaymentMarshaller INSTANCE = new PaymentMarshaller();
    }

    private PaymentMarshaller() {
    }

    public static PaymentMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    protected PaymentIO marshal(PaymentDTO payment) {
        if (payment == null || payment.isNull()) {
            return null;
        }
        PaymentIO io = new PaymentIO();
        io.transactionId = payment.transactionId().getValue();
        io.leaseId = payment.leaseId().getValue();

        io.amount = createIo(BigDecimalIO.class, payment.amount());
        io.description = createIo(StringIO.class, payment.description());
        io.paymentType = createIo(StringIO.class, payment.paymentType());
        return io;
    }

    @Override
    protected PaymentDTO unmarshal(PaymentIO io) {
        PaymentDTO dto = EntityFactory.create(PaymentDTO.class);
        dto.transactionId().setValue(io.transactionId);
        dto.leaseId().setValue(io.leaseId);
        setValue(dto.amount(), io.amount);
        setValue(dto.description(), io.description);
        setValue(dto.paymentType(), io.paymentType);
        return dto;
    }

}
