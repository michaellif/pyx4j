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

import javax.xml.bind.JAXBException;

import com.propertyvista.interfaces.payment.ResponseMessage.StatusCode;

public class ResponseMessageCreator {

    public static void makeSaleTransactionResponse() throws JAXBException {
        ResponseMessage rm = new ResponseMessage();
        rm.setMerchantId("BIRCHWTT");
        rm.setStatus(ResponseMessage.StatusCode.OK);

        Response r = new Response();
        r.setRequestID("payProc#1");
        r.setCode("0000");
        r.setAuth("T03006");
        r.setText("T03006 $255.59");
        rm.addResponse(r);

        r = new Response();
        r.setRequestID("payProc#2");
        r.setCode("0000");
        r.setText("TOKEN ADDED");
        rm.addResponse(r);

        r = new Response();
        r.setRequestID("payProc#3");
        r.setCode("1254");
        r.setText("EXPIRED CARD");
        rm.addResponse(r);

        MarshallUtil.marshal(rm, System.out);
    }

    public static void makeSystemDown() throws JAXBException {
        ResponseMessage rm = new ResponseMessage();
        rm.setStatus(StatusCode.SystemDown);

        MarshallUtil.marshal(rm, System.out);
    }
}
