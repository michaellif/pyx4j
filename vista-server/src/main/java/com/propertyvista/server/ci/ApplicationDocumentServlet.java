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
 * @version $Id:
 */

package com.propertyvista.server.ci;

import com.propertyvista.portal.domain.pt.ApplicationDocument;
import com.propertyvista.portal.server.pt.PtAppContext;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sergei
 */
public class ApplicationDocumentServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String id=request.getParameter("id");
        EntityQueryCriteria<ApplicationDocument> criteria = EntityQueryCriteria.create(ApplicationDocument.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), new Integer(id)));
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtAppContext.getCurrentUserApplication())); //for security use docs in current application context only
        ApplicationDocument adoc = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        if (adoc==null) {
            response.getWriter().println("Document not found");
            return;
        }
        String fname=adoc.filename().getValue().trim().toUpperCase();
        if (fname.endsWith(".PDF"))
            response.setContentType("application/pdf");
        else if (fname.endsWith(".TIF") || fname.endsWith(".TIFF"))
            response.setContentType("image/tiff");
        else if(fname.endsWith(".GIF"))
            response.setContentType("image/gif");
        else if(fname.endsWith(".PNG"))
            response.setContentType("image/png");
        else if(fname.endsWith(".BMP"))
            response.setContentType("image/bmp");
        else
            response.setContentType("image/jpeg");

        response.getOutputStream().write(adoc.data().getValue());
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
