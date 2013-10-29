/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services.simulator;

import java.io.IOException;
import java.lang.reflect.Field;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.operations.domain.dev.CardServiceSimulatorConfig;
import com.propertyvista.payment.caledon.CaledonRequestToken;
import com.propertyvista.payment.caledon.CaledonResponse;
import com.propertyvista.payment.caledon.HttpRequestField;
import com.propertyvista.payment.caledon.HttpResponseField;

@SuppressWarnings("serial")
public class CardServiceSimulationServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(CardServiceSimulationServlet.class);

    @Override
    protected void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
        NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
        String responseBody = null;
        try {
            CardServiceSimulatorConfig simulatorConfig = Persistence.service().retrieve(EntityQueryCriteria.create(CardServiceSimulatorConfig.class));
            if (simulatorConfig == null) {
                simulatorConfig = EntityFactory.create(CardServiceSimulatorConfig.class);
            }
            switch (simulatorConfig.responseType().getValue(CardServiceSimulatorConfig.SimpulationType.SimulateTransations)) {
            case RespondEmpty:
                return;
            case RespondWithHttpCode:
                httpResponse.sendError(simulatorConfig.responseHttpCode().getValue(0), "Simulated code");
                return;
            case RespondWithText:
                responseBody = simulatorConfig.responseText().getStringView();
                break;
            case RespondWithCode:
                responseBody = "TEXT=Simulated response &CODE=" + simulatorConfig.responseCode().getStringView();
                break;
            case SimulateTransations:
                break;
            }

            if (responseBody == null) {
                CaledonRequestToken caledonRequest = buildCaledonRequest(httpRequest);
                CaledonResponse caledonResponse = CardServiceSimulationProcessor.execute(caledonRequest);
                responseBody = buildResponse(caledonResponse);
            }
        } catch (Throwable e) {
            log.error("card simulator error", e);
            responseBody = "TEXT=Simulated " + e.getMessage() + "&CODE=1000";
        }
        writeResponse(responseBody, httpResponse);
    }

    private CaledonRequestToken buildCaledonRequest(HttpServletRequest httpRequest) {
        CaledonRequestToken caledonRequest = new CaledonRequestToken();

        for (Field field : CaledonRequestToken.class.getFields()) {
            HttpRequestField nameDeclared = field.getAnnotation(HttpRequestField.class);
            if (nameDeclared == null) {
                continue;
            }
            String value = httpRequest.getParameter(nameDeclared.value());
            if (value != null) {
                try {
                    field.set(caledonRequest, value);
                } catch (Throwable e) {
                    log.error("object value access error", e);
                    throw new Error("System error", e);
                }
            }
        }
        return caledonRequest;
    }

    private String buildResponse(CaledonResponse caledonResponse) throws IOException {
        StringBuilder responseBody = new StringBuilder();

        for (Field field : CaledonResponse.class.getDeclaredFields()) {
            HttpResponseField nameDeclared = field.getAnnotation(HttpResponseField.class);
            if (nameDeclared == null) {
                continue;
            }
            try {
                Object value = field.get(caledonResponse);
                if (value != null) {
                    if (responseBody.length() > 0) {
                        responseBody.append("&");
                    }
                    responseBody.append(nameDeclared.value());
                    responseBody.append("=");
                    responseBody.append(value);
                }
            } catch (Throwable e) {
                log.error("object value access error", e);
                throw new Error("System error", e);
            }
        }

        return responseBody.toString();
    }

    private void writeResponse(String responseBody, HttpServletResponse httpResponse) throws IOException {
        log.info("card simulator response {}", responseBody);
        httpResponse.setContentType("text/plain");
        byte[] buf = responseBody.getBytes();
        httpResponse.setContentLength(buf.length);
        ServletOutputStream out = httpResponse.getOutputStream();
        try {
            out.write(buf);
            out.flush();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

}
