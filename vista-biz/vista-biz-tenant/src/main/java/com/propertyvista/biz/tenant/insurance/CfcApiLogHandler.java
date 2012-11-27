/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.server.dev.EntityFileLogger;

public class CfcApiLogHandler implements SOAPHandler<SOAPMessageContext> {

    private static final Logger log = LoggerFactory.getLogger(CfcApiLogHandler.class);

    @Override
    public void close(MessageContext ctx) {

    }

    @Override
    public boolean handleFault(SOAPMessageContext smctx) {
        return true;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        if (log.isInfoEnabled()) {
            try {
                Boolean isOutbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                SOAPMessage message = context.getMessage();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                message.writeTo(out);
                EntityFileLogger.logXml("tenantSure", (isOutbound ? "out" : "in"), out.toString());
            } catch (Throwable e) {
                log.warn("CFC API communication trace failed: " + e.getMessage());
            }
        }
        return true;
    }

    @Override
    public Set<QName> getHeaders() {
        return new HashSet<QName>(Arrays.asList(new QName("http://api.cfcprograms.com/", "CFC_API")));
    }

}
