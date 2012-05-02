/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.payment.CCInformation;
import com.propertyvista.payment.IPaymentProcessor;
import com.propertyvista.payment.Merchant;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;
import com.propertyvista.payment.caledon.CaledonPaymentProcessor;

class CreditCardProcessor {

    static void realTimeSale(PaymentRecord paymentRecord) {
        MerchantAccount account = PaymentUtils.retrieveMerchantAccount(paymentRecord);

        Merchant merchant = EntityFactory.create(Merchant.class);
        merchant.terminalID().setValue(account.merchantTerminalId().getValue());

        PaymentRequest request = EntityFactory.create(PaymentRequest.class);
        //TODO identify what should be there
        request.referenceNumber().setValue(paymentRecord.id().getStringView());
        request.amount().setValue(paymentRecord.amount().getValue().floatValue());
        CCInformation ccInfo = EntityFactory.create(CCInformation.class);
        CreditCardInfo cc = paymentRecord.paymentMethod().details().cast();
        ccInfo.creditCardNumber().setValue(cc.number().getValue());
        ccInfo.creditCardExpiryDate().setValue(cc.expiryDate().getValue());

        request.paymentInstrument().set(ccInfo);

        IPaymentProcessor proc = new CaledonPaymentProcessor();

        PaymentResponse response = proc.realTimeSale(merchant, request);
        if (response.code().getValue().equals("0000")) {
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Received);
            paymentRecord.transactionAuthorizationNumber().setValue(response.authorizationNumber().getValue());
            Persistence.service().merge(paymentRecord);
            Persistence.service().commit();
            ServerSideFactory.create(ARFacade.class).postPayment(paymentRecord);
        } else {
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Rejected);
            paymentRecord.transactionAuthorizationNumber().setValue(response.code().getValue());
            paymentRecord.transactionErrorMessage().setValue(response.message().getValue());
            Persistence.service().merge(paymentRecord);
            Persistence.service().commit();
            ServerSideFactory.create(ARFacade.class).postPayment(paymentRecord);
        }
    }
}
