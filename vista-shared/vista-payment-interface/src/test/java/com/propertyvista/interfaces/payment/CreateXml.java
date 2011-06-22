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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import com.propertyvista.interfaces.payment.ResponseMessage.StatusCode;
import com.propertyvista.interfaces.payment.TokenActionRequest.TokenAction;

public class CreateXml {

    public static void main(String[] args) throws Exception {
        requestExamples();
        responseExamples();
    }

    public static void requestExamples() throws Exception {
        JAXBContext context = JAXBContext.newInstance(RequestMessage.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        {
            System.out.println("\n\n-- Sale transaction");

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

            m.marshal(r, System.out);
        }

        {
            System.out.println("\n\n-- Token Add transaction");

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
            m.marshal(r, System.out);
        }

        {
            System.out.println("\n\n-- Sale using a token");

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

            m.marshal(r, System.out);
        }

        boolean printSchema = true;
        System.out.println("\n\n-- RequestMessage Schema");
        if (printSchema) {
            context.generateSchema(new SchemaOutputResolver() {

                @Override
                public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                    StreamResult sr = new StreamResult(new FilterOutputStream(System.out) {
                        @Override
                        public void close() {
                        }

                    });
                    sr.setSystemId("");
                    return sr;
                }
            });
        }

    }

    private static void responseExamples() throws Exception {
        JAXBContext context = JAXBContext.newInstance(ResponseMessage.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        {
            System.out.println("\n\n-- Sale/Token transaction Response(s)");

            ResponseMessage rm = new ResponseMessage();
            rm.merchantId = "BIRCHWTT";
            rm.status = ResponseMessage.StatusCode.OK;

            Response r = new Response();
            r.requestID = "payProc#1";
            r.code = "0000";
            r.auth = "T03006";
            r.text = "T03006 $255.59";
            rm.response.add(r);

            r = new Response();
            r.requestID = "payProc#2";
            r.code = "0000";
            r.text = "TOKEN ADDED";
            rm.response.add(r);

            r = new Response();
            r.requestID = "payProc#3";
            r.code = "1254";
            r.text = "EXPIRED CARD";
            rm.response.add(r);

            m.marshal(rm, System.out);
        }

        {
            System.out.println("\n\n-- System Not Avalable");

            ResponseMessage rm = new ResponseMessage();
            rm.status = StatusCode.SystemDown;
            rm.response = null;

            m.marshal(rm, System.out);
        }

        boolean printSchema = true;
        System.out.println("\n\n-- ResponseMessage Schema");
        if (printSchema) {
            context.generateSchema(new SchemaOutputResolver() {

                @Override
                public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                    StreamResult sr = new StreamResult(new FilterOutputStream(System.out) {
                        @Override
                        public void close() {
                        }

                    });
                    sr.setSystemId("");
                    return sr;
                }
            });
        }
    }
}
