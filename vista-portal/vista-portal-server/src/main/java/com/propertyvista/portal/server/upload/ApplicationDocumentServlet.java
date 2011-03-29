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

import com.propertyvista.portal.domain.pt.ApplicationDocument;
import com.propertyvista.portal.rpc.pt.ApplicationDocumentServletParameters;
import com.propertyvista.portal.server.pt.PtAppContext;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.download.MimeMap;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApplicationDocumentServlet extends HttpServlet {

    /**
     *
     */
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
        String id = request.getParameter(ApplicationDocumentServletParameters.DOCUMENT_ID);
        if (id == null) {
            response.getWriter().println("Document Id is missing");
            return;
        }
        EntityQueryCriteria<ApplicationDocument> criteria = EntityQueryCriteria.create(ApplicationDocument.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), new Long(id)));
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtAppContext.getCurrentUserApplication())); //for security use docs in current application context only
        ApplicationDocument adoc = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        if (adoc == null) {
            response.getWriter().println("Document not found");
            return;
        }
        String fname = adoc.filename().getValue();
        int t = fname.lastIndexOf(".");
        if (t != -1) {
            String extension = fname.substring(t + 1).trim();
            String contentType = MimeMap.getContentType(extension);
            if (contentType == null)
                throw new ServletException("Unknown file extension: " + fname);
            response.setContentType(contentType);
        } else {
            throw new ServletException("Uploaded file name does not have an extension");
        }

        response.getOutputStream().write(adoc.data().getValue());
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
