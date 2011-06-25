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
import com.propertyvista.interfaces.payment.TokenActionRequest;
import com.propertyvista.interfaces.payment.TokenActionRequest.TokenAction;
import com.propertyvista.interfaces.payment.TokenPaymentInstrument;
import com.propertyvista.interfaces.payment.TransactionRequest;
import com.propertyvista.interfaces.payment.examples.utils.ExampleClient;

public class TokenTransactions {

    public static void main(String[] args) throws Exception {
        addToken();
        deactivateToken();
        saleUsingToken();
    }

    public static void addToken() throws Exception {
        RequestMessage r = new RequestMessage();
        r.setInterfaceEntity("PaymentProcessor1");
        r.setInterfaceEntityPassword("top-secret");

        r.setMerchantId("BIRCHWTT");

        TokenActionRequest addToken = new TokenActionRequest();
        addToken.setAction(TokenAction.Add);
        addToken.setCode("45125206MCRD5111");
        addToken.setReference("JSMITH-MCRD");
        addToken.setCard(new CreditCardInfo());
        addToken.getCard().setCardNumber("5191111111111111");
        addToken.getCard().setExpiryDate(new SimpleDateFormat("yyyy-MM").parse("2012-12"));
        r.addRequest(addToken);

        ResponseMessage response = ExampleClient.execute(r);

        System.out.println("response Status " + response.getStatus());

        System.out.println("response Code   " + response.getResponse().get(0).getCode());
        System.out.println("response Text   " + response.getResponse().get(0).getText());
    }

    public static void saleUsingToken() throws Exception {
        RequestMessage r = new RequestMessage();
        r.setInterfaceEntity("PaymentProcessor1");
        r.setInterfaceEntityPassword("top-secret");

        r.setMerchantId("BIRCHWTT");

        TransactionRequest pReq = new TransactionRequest();
        pReq.setRequestID("payProc#t1");
        pReq.setTxnType(TransactionRequest.TransactionType.Sale);

        TokenPaymentInstrument token = new TokenPaymentInstrument();
        token.setCode("45125206MCRD5111");
        pReq.setPaymentInstrument(token);

        pReq.setAmount(142.59f);
        pReq.setReference("TOKENTRANSEXAMPLE");

        r.addRequest(pReq);

        ResponseMessage response = ExampleClient.execute(r);

        System.out.println("response Status " + response.getStatus());

        System.out.println("response Req.   " + response.getResponse().get(0).getRequestID());
        System.out.println("response Code   " + response.getResponse().get(0).getCode());
        System.out.println("response Text   " + response.getResponse().get(0).getText());
    }

    public static void deactivateToken() throws Exception {
        RequestMessage r = new RequestMessage();
        r.setInterfaceEntity("PaymentProcessor1");
        r.setInterfaceEntityPassword("top-secret");

        r.setMerchantId("BIRCHWTT");

        TokenActionRequest deactivateToken = new TokenActionRequest();
        deactivateToken.setAction(TokenAction.Deactivate);
        deactivateToken.setCode("45125206MCRD5111");
        r.addRequest(deactivateToken);

        ResponseMessage response = ExampleClient.execute(r);

        System.out.println("response Status " + response.getStatus());

        System.out.println("response Code   " + response.getResponse().get(0).getCode());
        System.out.println("response Text   " + response.getResponse().get(0).getText());
    }

}
