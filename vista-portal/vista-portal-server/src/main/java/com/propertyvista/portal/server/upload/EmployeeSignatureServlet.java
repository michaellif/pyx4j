/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-24
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.upload;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.company.EmployeeSignature;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.server.domain.EmployeeSignatureBlob;

public class EmployeeSignatureServlet extends HttpServlet {

    private final static long serialVersionUID = 262881074310245383L;

    private final static Logger log = LoggerFactory.getLogger(EmployeeSignatureServlet.class);

    private final static int cacheExpiresHours = 24;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Key pk = getPrimaryKey(request.getPathInfo());
        if (pk == null) {
            response.setStatus(HttpServletResponse.SC_GONE);
            return;
        }

        EmployeeSignature signature = Persistence.service().retrieve(EmployeeSignature.class, pk);
        if (signature == null) {
            log.debug("no such document {} {}", pk, request.getPathInfo());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!checkAccessRights(signature)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        EmployeeSignatureBlob blob = Persistence.service().retrieve(EmployeeSignatureBlob.class, signature.blobKey().getValue());
        if (blob == null) {
            log.debug("no such blob {} {}", pk, request.getPathInfo());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setDateHeader("Last-Modified", signature.timestamp().getValue());
        // HTTP 1.0
        response.setDateHeader("Expires", System.currentTimeMillis() + Consts.HOURS2MSEC * cacheExpiresHours);
        // HTTP 1.1
        response.setHeader("Cache-Control", "public, max-age=" + ((long) Consts.HOURS2SEC * cacheExpiresHours));

        response.setContentType(blob.contentType().getValue());
        response.getOutputStream().write(blob.data().getValue());

    }

    private Key getPrimaryKey(String pathInfo) {
        String id = FilenameUtils.getPathNoEndSeparator(pathInfo);
        if (CommonsStringUtils.isEmpty(id) || "0".equals(id)) {
            return null;
        }
        Key key = null;
        try {
            Key tempKey = new Key(id);
            tempKey.asLong();
            key = tempKey;
        } catch (Throwable e) {
            // intentionally left blank: tempKey.asLong() will throw an error if id is wrong.
        }
        return key;
    }

    private boolean checkAccessRights(EmployeeSignature legalLetter) {
        // TODO implement access rights for employee signature
        return SecurityController.checkBehavior(VistaBasicBehavior.CRM);
    }

}
