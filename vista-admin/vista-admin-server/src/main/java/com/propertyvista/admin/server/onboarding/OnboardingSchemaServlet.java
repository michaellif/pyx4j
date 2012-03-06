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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.server.xml.XMLEntitySchemaWriter;

import com.propertyvista.onboarding.RequestMessageIO;
import com.propertyvista.onboarding.ResponseMessageIO;

@SuppressWarnings("serial")
public class OnboardingSchemaServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(OnboardingSchemaServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            response.setContentType("text/xml");
            XMLEntitySchemaWriter.printSchema(response.getOutputStream(), false, RequestMessageIO.class, ResponseMessageIO.class);

        } catch (Throwable e) {
            log.error("Error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
