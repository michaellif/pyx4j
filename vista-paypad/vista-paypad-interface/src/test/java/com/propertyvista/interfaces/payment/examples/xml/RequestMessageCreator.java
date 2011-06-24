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
package com.propertyvista.interfaces.payment.examples.xml;

import java.util.Date;

import javax.xml.bind.JAXBException;

import com.propertyvista.interfaces.payment.CreditCardInfo;
import com.propertyvista.interfaces.payment.RequestMessage;
import com.propertyvista.interfaces.payment.TokenActionRequest;
import com.propertyvista.interfaces.payment.TokenPaymentInstrument;
import com.propertyvista.interfaces.payment.TransactionRequest;
import com.propertyvista.interfaces.payment.TokenActionRequest.TokenAction;
import com.propertyvista.interfaces.payment.TransactionRequest.TransactionType;
import com.propertyvista.interfaces.payment.examples.utils.MarshallUtil;

public class RequestMessageCreator {

    public static void makeSaleTransaction() throws JAXBException {
        RequestMessage r = new RequestMessage();
        r.setInterfaceEntity("PaymentProcessor1");
        r.setMerchantId("BIRCHWTT");
        r.setInterfaceEntityPassword("top-secret");

        {
            TransactionRequest ccpay = new TransactionRequest();
            ccpay.setRequestID("payProc#1");
            ccpay.setResend(false);
            ccpay.setTxnType(TransactionRequest.TransactionType.Sale);
            CreditCardInfo cc = new CreditCardInfo();
            cc.setCardNumber("6011111111111117");
            cc.setExpiryDate(new Date());
            ccpay.setPaymentInstrument(cc);

            ccpay.setAmount(900);
            ccpay.setReference("August Rent, 46 Yonge, Appt 18");

            r.addRequest(ccpay);
        }

        {
            TransactionRequest ccpay = new TransactionRequest();
            ccpay.setRequestID("payProc#2");
            ccpay.setTxnType(TransactionRequest.TransactionType.Sale);
            CreditCardInfo cc = new CreditCardInfo();
            cc.setCardNumber("378282246310005");
            cc.setExpiryDate(new Date());
            ccpay.setPaymentInstrument(cc);

            ccpay.setAmount(940);
            ccpay.setReference("August Rent, 46 Yonge, Appt 19");

            r.addRequest(ccpay);
        }

        MarshallUtil.marshal(r, System.out);
    }

    public static void makeTokenAddTransction() throws JAXBException {
        RequestMessage r = new RequestMessage();
        r.setInterfaceEntity("PaymentProcessor1");
        r.setMerchantId("BIRCHWTT");
        r.setInterfaceEntityPassword("top-secret");
        TokenActionRequest addToken = new TokenActionRequest();
        addToken.setAction(TokenAction.Add);
        addToken.setCode("DC1107");
        addToken.setReference("46 Yonge, Appt 18");
        addToken.setCard(new CreditCardInfo());
        addToken.getCard().setCardNumber("6011111111111117");
        addToken.getCard().setExpiryDate(new Date());
        r.addRequest(addToken);
        MarshallUtil.marshal(r, System.out);
    }

    public static void makeSaleUsingToken() throws JAXBException {
        RequestMessage r = new RequestMessage();
        r.setInterfaceEntity("PaymentProcessor1");
        r.setMerchantId("BIRCHWTT");
        r.setInterfaceEntityPassword("top-secret");

        TransactionRequest tcpay = new TransactionRequest();
        tcpay.setTxnType(TransactionRequest.TransactionType.Sale);

        TokenPaymentInstrument token = new TokenPaymentInstrument();
        token.setCode("DC1107");
        tcpay.setPaymentInstrument(token);

        tcpay.setAmount(500.78f);
        tcpay.setReference("September Rent, 14 Yonge, Appt 456");
        r.addRequest(tcpay);

        MarshallUtil.marshal(r, System.out);
    }
}
