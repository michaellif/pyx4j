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
import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.server.domain.FileBlob;

@SuppressWarnings("serial")
public class SiteImageResourceServlet extends HttpServlet {

    private final static Logger log = LoggerFactory.getLogger(SiteImageResourceServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String filename = request.getPathInfo();
        String id = FilenameUtils.getPathNoEndSeparator(filename);
        if (CommonsStringUtils.isEmpty(id) || "0".equals(id)) {
            response.setStatus(HttpServletResponse.SC_GONE);
            return;
        }

        //TODO deserialize key
        SiteImageResource doc = Persistence.service().retrieve(SiteImageResource.class, new Key(id));
        if (doc == null) {
            log.debug("no such document {} {}", id, filename);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        FileBlob blob = Persistence.service().retrieve(FileBlob.class, doc.fileInfo().blobKey().getValue());
        if (blob == null) {
            log.debug("no such blob {} {}", id, filename);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(blob.contentType().getValue());
        response.getOutputStream().write(blob.content().getValue());
    }

}
