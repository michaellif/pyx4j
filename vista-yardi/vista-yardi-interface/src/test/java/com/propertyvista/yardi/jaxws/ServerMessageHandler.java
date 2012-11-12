/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 11, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.jaxws;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class ServerMessageHandler implements SOAPHandler<SOAPMessageContext> {

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        System.out.println("Server : handleMessage(), request=" + isRequest);

        //if this is a request, true for outbound messages, false for inbound
        if (isRequest) {
//            try {
//                SOAPMessage soapMsg = context.getMessage();
//                SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
//
//                soapEnv.addChildElement("TEST");
//
//                soapMsg.saveChanges();
//
//            } catch (SOAPException e) {
//                System.err.println(e);
//            }
        }
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        System.out.println("Server : handleFault()......");
        return true;
    }

    @Override
    public void close(MessageContext context) {
        System.out.println("Server : close()......");
    }

    @Override
    public Set<QName> getHeaders() {
        System.out.println("Server : getHeaders()......");
        return null;
    }

}