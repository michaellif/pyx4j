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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.server.domain.ApplicationDocumentData;

@SuppressWarnings("serial")
public class ApplicationDocumentServlet extends HttpServlet {

    private final static Logger log = LoggerFactory.getLogger(ApplicationDocumentServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String filename = request.getPathInfo();
        String id = FilenameUtils.getPathNoEndSeparator(filename);
        if (CommonsStringUtils.isEmpty(id) || "0".equals(id)) {
            response.setStatus(HttpServletResponse.SC_GONE);
            return;
        }

        //TODO deserialize key
        ApplicationDocumentData adata = Persistence.service().retrieve(ApplicationDocumentData.class, new Key(id));
        if (adata == null) {
            log.debug("no such document {} {}", id, filename);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (SecurityController.checkAnyBehavior(VistaTenantBehavior.Prospective, VistaTenantBehavior.ProspectiveSubmited)) {
            if (!EqualsHelper.equals(adata.application().getPrimaryKey(), PtAppContext.getCurrentUserApplicationPrimaryKey())) {
                log.debug("no access to document {} {}", id, filename);
                if (ApplicationMode.isDevelopment()) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
                return;
            }
        } else if (!SecurityController.checkBehavior(VistaCrmBehavior.Tenants)) {
            log.debug("no access to document {} {}", id, filename);
            if (ApplicationMode.isDevelopment()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            return;
        }
        response.setContentType(adata.contentType().getValue());
        response.getOutputStream().write(adata.data().getValue());
    }

}
