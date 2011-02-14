/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.caledon;

import com.propertyvista.payment.IPaymentProcessor;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;

import com.pyx4j.entity.shared.EntityFactory;

public class CaledonPaymentProcessor implements IPaymentProcessor {

    private final CaledonHttpClient client;

    public CaledonPaymentProcessor() {
        client = new CaledonHttpClient();
    }

    @Override
    public PaymentResponse realTimeSale(PaymentRequest request) {
        CaledonRequest crequest = new CaledonRequest();

        crequest.terminalID = "BIRCHWTT";
        crequest.transactionType = CaledonTransactionType.SALE.getValue();
        crequest.referenceNumber = request.referenceNumber().getValue();

        crequest.setAmount(request.amount().getValue());
        crequest.creditCardNumber = request.creditCardNumber().getValue();
        crequest.setExpiryDate(request.creditCardExpiryDate().getValue());

        CaledonResponse cresponse = client.transaction(crequest);

        PaymentResponse response = EntityFactory.create(PaymentResponse.class);

        return response;
    }

    @Override
    public PaymentResponse realTimeAuthorization(PaymentRequest request) {
        return null;
    }

}
