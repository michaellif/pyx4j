/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 24, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.essentials.server.dev.StackTraceViewServlet;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.security.VistaOperationsBehavior;

@SuppressWarnings("serial")
public class VistaStackTraceViewServlet extends StackTraceViewServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SecurityController.assertBehavior(VistaOperationsBehavior.SystemAdmin);
        super.doGet(request, response);
    }

}
