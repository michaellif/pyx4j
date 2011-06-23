/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 22, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.interfaces.payment;

import java.util.Date;

import javax.xml.bind.JAXBException;

import com.propertyvista.interfaces.payment.TokenActionRequest.TokenAction;

public class RequestMessageCreator {

    public static void makeSaleTransaction() throws JAXBException {
        RequestMessage r = new RequestMessage();
        r.interfaceEntity = "PaymentProcessor1";
        r.merchantId = "BIRCHWTT";
        r.password = "top-secret";
        TransactionRequest ccpay = new TransactionRequest();
        ccpay.requestID = "payProc#1";
        ccpay.resend = false;
        ccpay.txnType = TransactionRequest.TransactionType.Sale;
        ccpay.paymentInstrument = new CreditCardInfo();
        ((CreditCardInfo) ccpay.paymentInstrument).cardNumber = "6011111111111117";
        ((CreditCardInfo) ccpay.paymentInstrument).expiryDate = new Date();

        ccpay.amount = 900;
        ccpay.reference = "August Rent, 46 Yonge, Appt 18";

        r.requests.add(ccpay);

        ccpay = new TransactionRequest();
        ccpay.requestID = "payProc#2";
        ccpay.txnType = TransactionRequest.TransactionType.Sale;
        ccpay.paymentInstrument = new CreditCardInfo();
        ((CreditCardInfo) ccpay.paymentInstrument).cardNumber = "378282246310005";
        ((CreditCardInfo) ccpay.paymentInstrument).expiryDate = new Date();

        ccpay.amount = 940;
        ccpay.reference = "August Rent, 46 Yonge, Appt 19";

        r.requests.add(ccpay);

        MarshallUtil.marshal(r, System.out);
    }

    public static void makeTokenAddTransction() throws JAXBException {
        RequestMessage r = new RequestMessage();
        r.interfaceEntity = "PaymentProcessor1";
        r.merchantId = "BIRCHWTT";
        r.password = "top-secret";
        TokenActionRequest addToken = new TokenActionRequest();
        addToken.action = TokenAction.Add;
        addToken.code = "DC1107";
        addToken.reference = "46 Yonge, Appt 18";
        addToken.card = new CreditCardInfo();
        addToken.card.cardNumber = "6011111111111117";
        addToken.card.expiryDate = new Date();
        r.requests.add(addToken);
        MarshallUtil.marshal(r, System.out);
    }

    public static void makeSaleUsingToken() throws JAXBException {
        RequestMessage r = new RequestMessage();
        r.interfaceEntity = "PaymentProcessor1";
        r.merchantId = "BIRCHWTT";
        r.password = "top-secret";
        TransactionRequest tcpay = new TransactionRequest();
        tcpay.txnType = TransactionRequest.TransactionType.Sale;
        tcpay.paymentInstrument = new TokenPaymentInstrument();
        ((TokenPaymentInstrument) tcpay.paymentInstrument).code = "DC1107";
        tcpay.amount = 500.78f;
        tcpay.reference = "September Rent, 14 Yonge, Appt 456";
        r.requests.add(tcpay);

        MarshallUtil.marshal(r, System.out);
    }
}
