/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-24
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

public class AuthorizationOnlyRESEND {

    public static void main(String[] args) throws Exception {
        RequestMessage r = new RequestMessage();
        r.setMerchantId("BIRCHWTT");

        r.setInterfaceEntity("PaymentProcessor1");
        r.setInterfaceEntityPassword("top-secret");

        TransactionRequest ccReq = new TransactionRequest();
        ccReq.setRequestId("payProc#1r");
        ccReq.setTxnType(TransactionRequest.TransactionType.AuthorizeOnly);
        ccReq.setResend(true);
        CreditCardInfo cc = new CreditCardInfo();
        cc.setCardNumber("5191111111111111");
        cc.setExpiryDate(new SimpleDateFormat("yyyy-MM").parse("2014-12"));
        ccReq.setPaymentInstrument(cc);

        ccReq.setAmount(1000);
        ccReq.setReference("1234");

        r.addRequest(ccReq);

        ResponseMessage response = ExampleClient.execute(r);

        System.out.println("response Status " + response.getStatus());

        System.out.println("response Req.   " + response.getResponse().get(0).getRequestId());
        System.out.println("response Code   " + response.getResponse().get(0).getCode());
        System.out.println("response Dupl   " + response.getResponse().get(0).getDuplicate());
        System.out.println("response Auth   " + response.getResponse().get(0).getAuth());
        System.out.println("response Text   " + response.getResponse().get(0).getText());
    }
}
