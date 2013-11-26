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
package com.propertyvista.yardi.ws;

import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.yardi.ws.ScreeningService;
import com.yardi.ws.ServiceResponse;
import com.yardi.ws.WSScreeningService;

import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.yardi.beans.Messages;
import com.propertyvista.yardi.beans.Message.MessageType;

public class WSScreeningServiceTest extends WSTestBase {

    URL wsdlURL = WSScreeningServiceTest.class.getClassLoader().getResource("ScreeningService.wsdl");

    @Before
    public void init() throws Exception {
        publish(WSScreeningServiceImpl.class);
    }

    @Test
    public void testContext() throws Exception {
        assertEquals(HttpURLConnection.HTTP_OK, getHttpStatusCode(getAddress()));
    }

    @Test
    public void testGetScreeningReportErrorRequestContent() throws Exception {

        ScreeningService client = new ScreeningService(wsdlURL);

        WSScreeningService service = client.getWSScreeningService();

        ServiceResponse response = service.getScreeningReport("ERROR REQUEST's CONTENT");
        String xml = (String) response.getServiceResponseResult().getContent().get(0);
        Assert.assertNotNull(xml);

        Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
        Assert.assertNotNull(xml);
        Assert.assertEquals(1, messages.getMessages().size());
        Assert.assertEquals(MessageType.Error, messages.getMessages().get(0).getType());
    }

    @Test
    public void testGetScreeningReportEmptyRequest() throws Exception {

        ScreeningService client = new ScreeningService(wsdlURL);

        WSScreeningService service = client.getWSScreeningService();

        ServiceResponse response = service.getScreeningReport("");
        String xml = (String) response.getServiceResponseResult().getContent().get(0);
        Assert.assertNotNull(xml);

        Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
        Assert.assertNotNull(xml);
        Assert.assertEquals(1, messages.getMessages().size());
        Assert.assertEquals(MessageType.Error, messages.getMessages().get(0).getType());
    }
}