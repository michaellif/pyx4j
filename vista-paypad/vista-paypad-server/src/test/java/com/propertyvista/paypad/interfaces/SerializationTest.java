/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2011-06-23
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.paypad.interfaces;

import java.util.Date;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.Test;

import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.interfaces.payment.CreditCardInfo;
import com.propertyvista.interfaces.payment.RequestMessage;
import com.propertyvista.interfaces.payment.Response;
import com.propertyvista.interfaces.payment.ResponseMessage;
import com.propertyvista.interfaces.payment.TransactionRequest;

public class SerializationTest {

    @Test
    public void testRequestMessage() throws JAXBException {
        RequestMessage r = new RequestMessage();
        r.setInterfaceEntity("PaymentProcessor1");
        r.setMerchantId("BIRCHWTT");
        r.setPassword("top-secret");

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

        RequestMessage r2 = MarshallUtil.unmarshal(RequestMessage.class, MarshallUtil.marshall(r));

        Assert.assertEquals("MerchantId", r.getMerchantId(), r2.getMerchantId());
    }

    @Test
    public void testResponseMessage() throws JAXBException {

        ResponseMessage rm = new ResponseMessage();
        rm.setMerchantId("BIRCHW");
        rm.setStatus(ResponseMessage.StatusCode.OK);

        Response r = new Response();
        r.setRequestID("payProc#1");
        r.setCode("0000");
        r.setAuth("T03006");
        r.setText("T03006 $255.59");
        rm.addResponse(r);

        ResponseMessage rm2 = MarshallUtil.unmarshal(ResponseMessage.class, MarshallUtil.marshall(rm));

        Assert.assertEquals("status", rm.getStatus(), rm2.getStatus());
    }
}
