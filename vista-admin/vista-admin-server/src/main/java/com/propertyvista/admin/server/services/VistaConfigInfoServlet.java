/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.essentials.server.admin.ConfigInfoServlet;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.VistaBehavior;

@SuppressWarnings("serial")
public class VistaConfigInfoServlet extends ConfigInfoServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SecurityController.assertBehavior(VistaBehavior.ADMIN);
        super.doGet(request, response);
    }
}
