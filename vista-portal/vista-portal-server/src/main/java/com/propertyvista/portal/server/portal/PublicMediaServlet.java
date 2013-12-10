/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-04
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
import com.pyx4j.commons.Key;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.upload.FileUploadRegistry;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.media.ThumbnailSize;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ETag;
import com.propertyvista.server.common.blob.ThumbnailService;
import com.propertyvista.server.domain.FileImageThumbnailBlobDTO;

/**
 * This service does extra read from DB to read BlobKey, We may decide in future not to do this.
 * 
 * @see com.propertyvista.portal.rpc.DeploymentConsts#mediaImagesServletMapping
 */
@SuppressWarnings("serial")
public class PublicMediaServlet extends HttpServlet {

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
        String id = FilenameUtils.getPathNoEndSeparator(filename);
        ThumbnailSize thumbnailSize = null;
        try {
            thumbnailSize = ThumbnailSize.valueOf(FilenameUtils.getBaseName(filename));
        } catch (IllegalArgumentException notThumbnail) {
        }

        if (CommonsStringUtils.isEmpty(id) || "0".equals(id)) {
            response.setStatus(HttpServletResponse.SC_GONE);
            serveNotSet(thumbnailSize, response);
            return;
        }

        //TODO deserialize key
        MediaFile file = null;
        if (id.startsWith(DeploymentConsts.TRANSIENT_FILE_PREF)) {
            // treat id as accessKey
            file = FileUploadRegistry.get(id.substring(DeploymentConsts.TRANSIENT_FILE_PREF.length()));
        } else {
            // treat id as MediaFile key
            file = Persistence.service().retrieve(MediaFile.class, new Key(id));
        }
        if (file == null) {
            log.debug("no media {} {}", id, filename);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            serveNotFound(thumbnailSize, response);
            return;
        } else if (file.blobKey().isNull()) {
            log.debug("no media {} {} is not file", id, filename);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            serveNotFound(thumbnailSize, response);
            return;
        }
        if (!PublicVisibilityType.global.equals(file.visibility().getValue())) {
            if (!SecurityController.checkBehavior(VistaBasicBehavior.CRM)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        String token = ETag.getEntityTag(file, thumbnailSize);
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

        if (thumbnailSize == null) {
            if (!file.contentMimeType().isNull()) {
                response.setContentType(file.contentMimeType().getValue());
            }
            BlobService.serve(file.blobKey().getValue(), response);
        } else {
            if (!ThumbnailService.serve(file.blobKey().getValue(), thumbnailSize, response)) {
                log.debug("no blob {} for media {}", file.blobKey().getValue(), id);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                serveNotFound(thumbnailSize, response);
            }
        }
    }

    private void serveNotFound(ThumbnailSize thumbnailSize, HttpServletResponse response) throws IOException {
        if (thumbnailSize == null) {
            thumbnailSize = ThumbnailSize.xsmall;
        }
        serveResourceImage("building-not-found.jpg", thumbnailSize, response);
    }

    private void serveNotSet(ThumbnailSize thumbnailSize, HttpServletResponse response) throws IOException {
        if (thumbnailSize == null) {
            thumbnailSize = ThumbnailSize.xsmall;
        }
        serveResourceImage("building-not-set.jpg", thumbnailSize, response);
    }

    private void serveResourceImage(String filename, ThumbnailSize thumbnailSize, HttpServletResponse response) throws IOException {
        FileImageThumbnailBlobDTO blob = (FileImageThumbnailBlobDTO) CacheService.get(PublicMediaServlet.class.getName() + filename);
        if (blob == null) {
            byte raw[] = IOUtils.getBinaryResource(filename, PublicMediaServlet.class);
            if (raw == null) {
                return;
            }
            blob = ThumbnailService.createThumbnailBlob(filename, raw, ThumbnailService.getDefaultResampleParams(ImageTarget.Building),
                    ImageConsts.BUILDING_XSMALL, ImageConsts.BUILDING_SMALL, ImageConsts.BUILDING_MEDIUM, ImageConsts.BUILDING_LARGE, null);
            CacheService.put(PublicMediaServlet.class.getName() + filename, blob);
        }
        ThumbnailService.serve(blob, thumbnailSize, response);
    }
}
