/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.payment;

import java.io.IOException;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import com.propertyvista.interfaces.payment.TokenActionRequest.TokenAction;

public class CreateXml {

    public static void main(String[] args) throws Exception {

        JAXBContext context = JAXBContext.newInstance(RequestMessage.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        {
            System.out.println("-- Sale transaction");

            RequestMessage r = new RequestMessage();
            r.interfaceEntity = "PaymentProcessor1";
            r.merchantId = "BIRCHWTT";
            r.password = "top-secret";
            TransactionRequest ccpay = new TransactionRequest();
            ccpay.txnType = TransactionType.SALE;
            ccpay.paymentInstrument = new CreditCardInfo();
            ((CreditCardInfo) ccpay.paymentInstrument).cardNumber = "6011111111111117";
            ((CreditCardInfo) ccpay.paymentInstrument).expiryDate = new Date();
            r.request = ccpay;

            m.marshal(r, System.out);
        }

        {
            System.out.println("-- Token Add transaction");

            RequestMessage r = new RequestMessage();
            r.interfaceEntity = "PaymentProcessor1";
            r.merchantId = "BIRCHWTT";
            r.password = "top-secret";
            TokenActionRequest addToken = new TokenActionRequest();
            addToken.action = TokenAction.ADD;
            addToken.code = "DC1107";
            addToken.card = new CreditCardInfo();
            addToken.card.cardNumber = "6011111111111117";
            addToken.card.expiryDate = new Date();
            r.request = addToken;
            m.marshal(r, System.out);
        }

        {
            System.out.println("-- Sale using a token");

            RequestMessage r = new RequestMessage();
            r.interfaceEntity = "PaymentProcessor1";
            r.merchantId = "BIRCHWTT";
            r.password = "top-secret";
            TransactionRequest tcpay = new TransactionRequest();
            tcpay.txnType = TransactionType.SALE;
            tcpay.paymentInstrument = new TokenPaymentInstrument();
            ((TokenPaymentInstrument) tcpay.paymentInstrument).code = "DC1107";
            r.request = tcpay;

            m.marshal(r, System.out);
        }

        context.generateSchema(new SchemaOutputResolver() {

            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                StreamResult sr = new StreamResult(System.out);
                sr.setSystemId("");
                return sr;
            }
        });

    }

}
