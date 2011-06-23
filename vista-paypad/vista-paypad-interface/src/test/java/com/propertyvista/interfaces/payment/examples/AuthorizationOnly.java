/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-23
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.payment.examples;

import java.text.SimpleDateFormat;

import com.propertyvista.interfaces.payment.CreditCardInfo;
import com.propertyvista.interfaces.payment.RequestMessage;
import com.propertyvista.interfaces.payment.ResponseMessage;
import com.propertyvista.interfaces.payment.TransactionRequest;
import com.propertyvista.interfaces.payment.examples.utils.ExampleClient;

public class AuthorizationOnly {

    public static void main(String[] args) throws Exception {
        RequestMessage r = new RequestMessage();
        r.setMerchantId("BIRCHWTT");

        r.setInterfaceEntity("PaymentProcessor1");
        r.setPassword("top-secret");

        TransactionRequest ccpay = new TransactionRequest();
        ccpay.setRequestID("payProc#1");
        ccpay.setTxnType(TransactionRequest.TransactionType.AuthorizeOnly);
        CreditCardInfo cc = new CreditCardInfo();
        cc.setCardNumber("5191111111111111");
        cc.setExpiryDate(new SimpleDateFormat("yyyy-MM").parse("2012-14"));
        ccpay.setPaymentInstrument(cc);

        ccpay.setAmount(1000);
        ccpay.setReference("1234");

        r.addRequest(ccpay);

        ResponseMessage response = ExampleClient.execute(r);

        System.out.println("response Status " + response.getStatus());

        System.out.println("response Code   " + response.getResponse().get(0).getCode());
        System.out.println("response Auth   " + response.getResponse().get(0).getAuth());
        System.out.println("response Text   " + response.getResponse().get(0).getText());
    }
}
