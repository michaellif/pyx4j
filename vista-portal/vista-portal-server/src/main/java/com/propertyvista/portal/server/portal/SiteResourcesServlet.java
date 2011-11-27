/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;

import com.propertyvista.domain.File;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.portal.server.portal.services.SiteThemeServicesImpl;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ETag;

/**
 * @see com.propertyvista.portal.rpc.DeploymentConsts#siteResourcesServletMapping
 */
@SuppressWarnings("serial")
public class SiteResourcesServlet extends HttpServlet {

    private final static Logger log = LoggerFactory.getLogger(PublicMediaServlet.class);

    private int cacheExpiresHours = 24;

    @Override
    public void init(ServletConfig config) throws ServletException {
        String rate = config.getInitParameter("cacheExpiresHours");
        if (rate != null) {
            cacheExpiresHours = Integer.parseInt(rate);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filename = request.getPathInfo();
        String id = FilenameUtils.getBaseName(filename);

        if (CommonsStringUtils.isEmpty(id) || "0".equals(id)) {
            log.debug("wrong resources id {} in file {} ", id, filename);
            response.setStatus(HttpServletResponse.SC_GONE);
            return;
        }

        SiteDescriptor descriptor = SiteThemeServicesImpl.getSiteDescriptorFromCache();
        // TODO select different resources base on locale and name
        if (descriptor.logo().size() == 0) {
            log.debug("descriptor has no logos");
            response.setStatus(HttpServletResponse.SC_GONE);
            return;
        }
        File file = descriptor.logo().get(0).file();

        if (file.blobKey().isNull()) {
            log.debug("resources {} {} is not file", id, filename);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String token = ETag.getEntityTag(file, "");
        response.setHeader("Etag", token);

        if (!file.timestamp().isNull()) {
            long since = request.getDateHeader("If-Modified-Since");
            if ((since != -1) && (file.timestamp().getValue() < since)) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
            response.setDateHeader("Last-Modified", file.timestamp().getValue());
            // HTTP 1.0
            response.setDateHeader("Expires", System.currentTimeMillis() + Consts.HOURS2MSEC * cacheExpiresHours);
            // HTTP 1.1
            response.setHeader("Cache-Control", "public, max-age=" + ((long) Consts.HOURS2SEC * cacheExpiresHours));

        }
        if (ETag.checkIfNoneMatch(token, request.getHeader("If-None-Match"))) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        if (!file.contentMimeType().isNull()) {
            response.setContentType(file.contentMimeType().getValue());
        }
        BlobService.serve(file.blobKey().getValue(), response);
    }
}
