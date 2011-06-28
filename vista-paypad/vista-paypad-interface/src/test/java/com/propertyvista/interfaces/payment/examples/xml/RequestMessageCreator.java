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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBException;

import com.propertyvista.interfaces.payment.CreditCardInfo;
import com.propertyvista.interfaces.payment.RequestMessage;
import com.propertyvista.interfaces.payment.TokenActionRequest;
import com.propertyvista.interfaces.payment.TokenActionRequest.TokenAction;
import com.propertyvista.interfaces.payment.TokenPaymentInstrument;
import com.propertyvista.interfaces.payment.TransactionRequest;
import com.propertyvista.interfaces.payment.examples.utils.MarshallUtil;

public class RequestMessageCreator {

    public static void makeSaleTransaction() throws JAXBException {
        RequestMessage r = new RequestMessage();
        r.setInterfaceEntity("PaymentProcessor1");
        r.setInterfaceEntityPassword("top-secret");

        r.setMerchantId("TESTMERC");
        r.setMerchantPassword("classified");

        {
            TransactionRequest ccpay = new TransactionRequest();
            ccpay.setRequestId("payProc#1");
            ccpay.setResend(false);
            ccpay.setTxnType(TransactionRequest.TransactionType.Sale);
            CreditCardInfo cc = new CreditCardInfo();
            cc.setCardNumber("6011111111111117");
            cc.setExpiryDate(new Date());
            ccpay.setPaymentInstrument(cc);

            ccpay.setAmount(900);
            ccpay.setReference("AugustRent46YongeAppt18");

            r.addRequest(ccpay);
        }

        {
            TransactionRequest ccpay = new TransactionRequest();
            ccpay.setRequestId("payProc#2");
            ccpay.setTxnType(TransactionRequest.TransactionType.Sale);
            CreditCardInfo cc = new CreditCardInfo();
            cc.setCardNumber("378282246310005");
            cc.setExpiryDate(new Date());
            ccpay.setPaymentInstrument(cc);

            ccpay.setAmount(940);
            ccpay.setReference("AugustRent46YongeAppt19");

            r.addRequest(ccpay);
        }

        MarshallUtil.marshal(r, System.out);
    }

    public static void makeTokenAddTransction() throws JAXBException, ParseException {
        RequestMessage r = new RequestMessage();
        r.setInterfaceEntity("PaymentProcessor1");
        r.setInterfaceEntityPassword("top-secret");

        r.setMerchantId("TESTMERC");
        r.setMerchantPassword("classified");

        TokenActionRequest addToken = new TokenActionRequest();
        addToken.setRequestId("Store001");
        addToken.setAction(TokenAction.Add);
        addToken.setCode("DC1107");
        addToken.setReference("46YongeAppt18");
        addToken.setCard(new CreditCardInfo());
        addToken.getCard().setCardNumber("6011111111111117");
        addToken.getCard().setExpiryDate(new SimpleDateFormat("yyyy-MM").parse("2017-09"));
        r.addRequest(addToken);
        MarshallUtil.marshal(r, System.out);
    }

    public static void makeSaleUsingToken() throws JAXBException {
        RequestMessage r = new RequestMessage();
        r.setInterfaceEntity("PaymentProcessor1");
        r.setInterfaceEntityPassword("top-secret");

        r.setMerchantId("TESTMERC");
        r.setMerchantPassword("classified");

        TransactionRequest tcpay = new TransactionRequest();
        tcpay.setRequestId("pay#101");
        tcpay.setTxnType(TransactionRequest.TransactionType.Sale);

        TokenPaymentInstrument token = new TokenPaymentInstrument();
        token.setCode("DC1107");
        tcpay.setPaymentInstrument(token);

        tcpay.setAmount(50078);
        tcpay.setReference("SeptemberRent14YongeAppt456");
        r.addRequest(tcpay);

        MarshallUtil.marshal(r, System.out);
    }
}
