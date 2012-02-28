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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.site.PortalImageResource;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.server.portal.services.SiteThemeServicesImpl;
import com.propertyvista.server.common.blob.ETag;
import com.propertyvista.server.domain.FileBlob;

@SuppressWarnings("serial")
public class SiteImageResourceServlet extends HttpServlet {

    private final static Logger log = LoggerFactory.getLogger(SiteImageResourceServlet.class);

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

        // our format: */id/name/vista.siteimgrc or */logo.*/vista.siteimgrc
        String filename = request.getServletPath();
        String segments[] = filename.split("/");
        ArrayUtils.reverse(segments);
        if (segments.length < 2 || !DeploymentConsts.siteImageResourceServletMapping.equals("/" + segments[0])) {
            log.debug("Invalid request format: " + filename);
            response.setStatus(HttpServletResponse.SC_GONE);
            return;
        }
        Key key = null;
        // check if logo was requested
        if (segments[1].startsWith("logo.")) {
            SiteDescriptor descriptor = SiteThemeServicesImpl.getSiteDescriptorFromCache();
            if (descriptor.logo().size() == 0) {
                log.debug("descriptor has no logos");
                response.setStatus(HttpServletResponse.SC_GONE);
                return;
            }
            // see if locale is given
            String locale = request.getParameter("locale");
            if (locale != null && locale.length() > 0) {
                for (PortalImageResource logo : descriptor.logo()) {
                    if (logo.locale().lang().getValue().name().equals(locale)) {
                        key = logo.imageResource().getPrimaryKey();
                        break;
                    }
                }
            }
            // if still no logo found, get default
            if (key == null) {
                // TODO define default locale per PMC; use first one for now
                key = descriptor.logo().get(0).imageResource().getPrimaryKey();
            }
        } else {
            String id = segments[2];
            if (CommonsStringUtils.isEmpty(id) || "0".equals(id)) {
                response.setStatus(HttpServletResponse.SC_GONE);
                return;
            }
            key = new Key(id);
        }

        //TODO deserialize key
        SiteImageResource file = Persistence.service().retrieve(SiteImageResource.class, key);
        if (file == null) {
            log.debug("no such document {} {}", key, filename);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (file.blobKey().isNull()) {
            log.debug("resources {} {} is not file", key, filename);
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

        FileBlob blob = Persistence.service().retrieve(FileBlob.class, file.blobKey().getValue());
        if (blob == null) {
            log.debug("no such blob {} {}", key, filename);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(blob.contentType().getValue());
        response.getOutputStream().write(blob.content().getValue());
    }

}
