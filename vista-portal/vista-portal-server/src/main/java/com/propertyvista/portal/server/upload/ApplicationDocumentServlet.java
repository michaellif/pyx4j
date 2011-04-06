/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2011
 * @author sergei
 * @version $Id$
 */

package com.propertyvista.portal.server.upload;

import com.propertyvista.portal.rpc.pt.ApplicationDocumentServletParameters;
import com.propertyvista.portal.server.pt.PtAppContext;
import com.propertyvista.server.domain.ApplicationDocumentData;
import com.pyx4j.entity.server.PersistenceServicesFactory;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApplicationDocumentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String dataId = request.getParameter(ApplicationDocumentServletParameters.DATA_ID);
        if (dataId == null) {
            response.getWriter().println("dataId parameter is missing");
            return;
        }

        ApplicationDocumentData adata = PersistenceServicesFactory.getPersistenceService().retrieve(ApplicationDocumentData.class, new Long(dataId));
        if (adata == null) {
            throw new ServletException("Cannot retrieve binary data: adata is null");
        }
        if (!adata.application().id().getValue().equals(PtAppContext.getCurrentUserApplication().id().getValue())) {
            throw new ServletException("Cannot retrieve data: wrong application Id: " + adata.application().id().getValue() + " != "
                    + PtAppContext.getCurrentUserApplication().id().getValue());
        }
        if (adata.data() == null) {
            throw new ServletException("Cannot retrieve binary data: adata.data() is null");
        }
        response.setContentType(adata.contentType().getValue());
        response.getOutputStream().write(adata.data().getValue());
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }// </editor-fold>

}
