/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-23
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.paypad.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.j2se.util.MarshallUtil;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.interfaces.payment.RequestMessage;
import com.propertyvista.interfaces.payment.ResponseMessage;
import com.propertyvista.interfaces.payment.ResponseMessage.StatusCode;

@SuppressWarnings("serial")
public class PaymentServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(PaymentServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0); //prevents caching at the proxy server
        response.setDateHeader("X-StatusDate", System.currentTimeMillis());

        //TODO actually test the status of the system

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>System status</title></head>");
        out.println("<body>");
        out.println("<span>OK</span><p/>");
        out.println("<span>" + new Date().toString() + "</span><p/>");
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream is = null;
        RequestMessage message;
        try {
            is = request.getInputStream();
            JAXBContext context = JAXBContext.newInstance(RequestMessage.class);
            Unmarshaller um = context.createUnmarshaller();
            message = (RequestMessage) um.unmarshal(is);
            is.close();
        } catch (UnmarshalException e) {
            log.error("read xml request error", e);
            replyWithStatusCode(response, ResponseMessage.StatusCode.MessageFormatError);
            return;
        } catch (Throwable e) {
            log.error("Error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        } finally {
            IOUtils.closeQuietly(is);
        }

        try {
            ResponseMessage rm = PaymentProcessor.execute(message);
            log.info("reply {}", MarshallUtil.marshall(rm));
            response.setContentType("text/xml");
            MarshallUtil.marshal(rm, response.getOutputStream());
        } catch (Throwable e) {
            log.error("Error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void replyWithStatusCode(HttpServletResponse response, StatusCode statusCode) {
        ResponseMessage rm = new ResponseMessage();
        rm.setStatus(statusCode);
        try {
            response.setContentType("text/xml");
            MarshallUtil.marshal(rm, response.getOutputStream());
        } catch (Throwable e) {
            log.error("reply error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
