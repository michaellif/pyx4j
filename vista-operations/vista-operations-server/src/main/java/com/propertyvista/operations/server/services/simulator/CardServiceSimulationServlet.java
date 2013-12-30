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
import java.lang.reflect.Modifier;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.eft.caledoncards.CaledonFeeCalulationRequest;
import com.propertyvista.eft.caledoncards.CaledonFeeCalulationResponse;
import com.propertyvista.eft.caledoncards.CaledonFeeRequestTypes;
import com.propertyvista.eft.caledoncards.CaledonPaymentWithFeeRequest;
import com.propertyvista.eft.caledoncards.CaledonPaymentWithFeeResponse;
import com.propertyvista.eft.caledoncards.CaledonRequestToken;
import com.propertyvista.eft.caledoncards.CaledonResponse;
import com.propertyvista.eft.caledoncards.HttpRequestField;
import com.propertyvista.eft.caledoncards.HttpResponseField;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulatorConfig;

@SuppressWarnings("serial")
public class CardServiceSimulationServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(CardServiceSimulationServlet.class);

    @Override
    protected void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
        process(httpRequest, httpResponse, false);
    }

    @Override
    protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
        process(httpRequest, httpResponse, true);
    }

    protected void process(HttpServletRequest httpRequest, HttpServletResponse httpResponse, boolean convFeeApi) throws ServletException, IOException {
        NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
        String responseBody = null;
        CardServiceSimulatorConfig simulatorConfig = CardServiceSimulationUtils.getCardServiceSimulatorConfig();

        try {
            switch (simulatorConfig.responseType().getValue()) {
            case RespondEmpty:
                return;
            case RespondWithHttpCode:
                httpResponse.sendError(simulatorConfig.responseHttpCode().getValue(0), "Simulated code");
                return;
            case RespondWithText:
                responseBody = simulatorConfig.responseText().getStringView();
                break;
            case RespondWithCode:
                if (convFeeApi) {
                    responseBody = "&response_code=" + simulatorConfig.responseCode().getStringView() + "&response_text=Simulated response";
                } else {
                    responseBody = "TEXT=Simulated response &CODE=" + simulatorConfig.responseCode().getStringView();
                }
                break;
            case SimulateTransations:
                break;
            }

            if (responseBody == null) {
                responseBody = processTransaction(convFeeApi, httpRequest);
            }
        } catch (Throwable e) {
            log.error("card simulator error", e);
            if (convFeeApi) {
                responseBody = "&response_code=C001&response_text=Simulated " + e.getMessage();
            } else {
                responseBody = "TEXT=Simulated " + e.getMessage() + "&CODE=1000";
            }
        }

        if (!simulatorConfig.responseDelay().isNull()) {
            try {
                Thread.sleep(simulatorConfig.responseDelay().getValue());
            } catch (InterruptedException e) {
                throw new Error(e);
            }
        }

        writeResponse(responseBody, httpResponse);
    }

    private String processTransaction(boolean convFeeApi, HttpServletRequest httpRequest) throws IOException {
        if (convFeeApi) {
            String type = httpRequest.getParameter("type");
            if (CaledonFeeRequestTypes.FeeCalulation.getIntrfaceValue().equals(type)) {
                CaledonFeeCalulationRequest caledonRequest = buildCaledonRequest(httpRequest, new CaledonFeeCalulationRequest());
                CaledonFeeCalulationResponse caledonResponse = CardServiceSimulationProcessor.executeFeeCalulation(caledonRequest);
                return buildResponse(caledonResponse);
            } else if (CaledonFeeRequestTypes.PaymentWithFee.getIntrfaceValue().equals(type) || CaledonFeeRequestTypes.Void.getIntrfaceValue().equals(type)) {
                CaledonPaymentWithFeeRequest caledonRequest = buildCaledonRequest(httpRequest, new CaledonPaymentWithFeeRequest());
                CaledonPaymentWithFeeResponse caledonResponse = CardServiceSimulationProcessor.executePaymentWithFee(caledonRequest);
                return buildResponse(caledonResponse);
            } else {
                return "&response_code=C001&response_text=Simulation type Rejected";
            }
        } else {
            CaledonRequestToken caledonRequest = buildCaledonRequest(httpRequest, new CaledonRequestToken());
            CaledonResponse caledonResponse = CardServiceSimulationProcessor.execute(caledonRequest);
            return buildResponse(caledonResponse);
        }
    }

    private <E> E buildCaledonRequest(HttpServletRequest httpRequest, E caledonRequest) {
        for (Field field : caledonRequest.getClass().getFields()) {
            HttpRequestField nameDeclared = field.getAnnotation(HttpRequestField.class);
            if (nameDeclared == null) {
                continue;
            }
            String value = httpRequest.getParameter(nameDeclared.value());
            if (value != null) {
                try {
                    if (Modifier.isFinal(field.getModifiers())) {
                        if (!value.equals(field.get(caledonRequest))) {
                            throw new AssertionError("field expected " + field.get(caledonRequest) + " bug got " + value);
                        }
                    } else {
                        field.set(caledonRequest, value);
                    }
                } catch (Throwable e) {
                    log.error("object value access error", e);
                    throw new Error("System error", e);
                }
            }
        }
        return caledonRequest;
    }

    private String buildResponse(Object caledonResponse) throws IOException {
        StringBuilder responseBody = new StringBuilder();

        for (Field field : caledonResponse.getClass().getFields()) {
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
