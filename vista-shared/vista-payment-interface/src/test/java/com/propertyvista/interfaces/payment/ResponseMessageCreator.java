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

        MarshallUtil.marshal(rm, System.out);
    }

    public static void makeSystemDown() throws JAXBException {
        ResponseMessage rm = new ResponseMessage();
        rm.status = StatusCode.SystemDown;
        rm.response = null;

        MarshallUtil.marshal(rm, System.out);
    }
}
