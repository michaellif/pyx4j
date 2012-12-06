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

import com.propertyvista.domain.financial.billingext.dto.PaymentDTO;
import com.propertyvista.oapi.model.PaymentIO;
import com.propertyvista.oapi.xml.BigDecimalIO;
import com.propertyvista.oapi.xml.StringIO;

public class PaymentMarshaller implements Marshaller<PaymentDTO, PaymentIO> {

    private static class SingletonHolder {
        public static final PaymentMarshaller INSTANCE = new PaymentMarshaller();
    }

    private PaymentMarshaller() {
    }

    public static PaymentMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public PaymentIO marshal(PaymentDTO dto) {
        PaymentIO io = new PaymentIO();
        io.transactionId = dto.transactionId().getValue();
        io.leaseId = dto.leaseId().getValue();
        io.amount = new BigDecimalIO(dto.amount().getValue());
        io.description = new StringIO(dto.description().getValue());
        io.paymentType = new StringIO(dto.paymentType().getValue());
        return io;
    }

    @Override
    public PaymentDTO unmarshal(PaymentIO io) {
        PaymentDTO dto = EntityFactory.create(PaymentDTO.class);
        dto.transactionId().setValue(io.transactionId);
        dto.leaseId().setValue(io.leaseId);
        MarshallerUtils.ioToEntity(dto.amount(), io.amount);
        MarshallerUtils.ioToEntity(dto.description(), io.description);
        MarshallerUtils.ioToEntity(dto.paymentType(), io.paymentType);
        return dto;
    }

}
