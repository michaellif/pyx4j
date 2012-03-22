/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.xml.XMLEntityWriter;
import com.pyx4j.essentials.server.xml.XMLStringWriter;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.interfaces.importer.xml.ImportXMLEntityNamingConvention;
import com.propertyvista.onboarding.RequestMessageIO;
import com.propertyvista.onboarding.ResponseMessageIO;
import com.propertyvista.server.domain.admin.Pmc;

@SuppressWarnings("serial")
public class OnboardingServiceServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(OnboardingServiceServlet.class);

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
        RequestMessageIO message;
        try {
            is = request.getInputStream();
            message = OnboardingXMLUtils.parse(RequestMessageIO.class, new InputSource(is));
            is.close();
        } catch (Throwable e) {
            log.error("Error", e);
            replyWithStatusCode(response, ResponseMessageIO.StatusCode.MessageFormatError, e);
            return;
        } finally {
            IOUtils.closeQuietly(is);
        }

        NamespaceManager.setNamespace(Pmc.adminNamespace);
        OnboardingProcessor pp;
        try {
            pp = new OnboardingProcessor();
            log.info("processing message {}", message.messageId().getValue());
            if (!pp.isValid(message)) {
                replyWithStatusCode(response, ResponseMessageIO.StatusCode.MessageFormatError, null);
                return;
            }

            if (!OnboardingSecurity.enter(message)) {
                replyWithStatusCode(response, ResponseMessageIO.StatusCode.AuthenticationFailed, null);
                return;
            }
        } catch (Throwable e) {
            log.error("Error", e);
            replyWithStatusCode(response, ResponseMessageIO.StatusCode.SystemDown, e);
            return;
        }

        try {
            ResponseMessageIO rm = pp.execute(message);
            log.info("reply {}", rm);
            response.setContentType("text/xml");
            replyWith(response, rm);
        } catch (Throwable e) {
            log.error("Error", e);
            replyWithStatusCode(response, ResponseMessageIO.StatusCode.SystemError, e);
        }

    }

    private void replyWithStatusCode(HttpServletResponse response, ResponseMessageIO.StatusCode statusCode, Throwable e) {
        ResponseMessageIO rm = EntityFactory.create(ResponseMessageIO.class);
        rm.status().setValue(statusCode);
        if ((e != null) && (ApplicationMode.isDevelopment())) {
            rm.errorMessage().setValue(e.getMessage());
        }
        replyWith(response, rm);
    }

    private void replyWith(HttpServletResponse response, IEntity message) {
        try {
            response.setContentType("text/xml");
            String namespace = null;
            javax.xml.bind.annotation.XmlSchema schema = message.getValueClass().getPackage().getAnnotation(javax.xml.bind.annotation.XmlSchema.class);
            if ((schema != null) && (CommonsStringUtils.isStringSet(schema.namespace()))) {
                namespace = schema.namespace();
            }
            XMLStringWriter xml = new XMLStringWriter(Charset.forName("UTF-8"), namespace);
            XMLEntityWriter xmlWriter = new XMLEntityWriter(xml, new ImportXMLEntityNamingConvention());
            xmlWriter.setEmitId(false);
            xmlWriter.write(message);
            response.getWriter().write(xml.toString());
        } catch (Throwable e) {
            log.error("reply error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
