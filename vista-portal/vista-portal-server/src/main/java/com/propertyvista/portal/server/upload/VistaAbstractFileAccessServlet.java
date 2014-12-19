/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 30, 2013
 * @author stanp
 */
package com.propertyvista.portal.server.upload;

import java.io.IOException;
import java.util.HashMap;

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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.IHasFile;
import com.pyx4j.essentials.server.upload.FileUploadRegistry;

import com.propertyvista.domain.blob.IFileBlob;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.server.common.blob.ETag;

public class VistaAbstractFileAccessServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final static Logger log = LoggerFactory.getLogger(VistaAbstractFileAccessServlet.class);

    private int cacheExpiresHours = 24;

    private final HashMap<String, BlobEntry> blobRegistry = new HashMap<String, BlobEntry>();

    public <B extends IFileBlob> void register(Class<? extends IHasFile<B>> fileClass, Class<B> blobClass) {
        blobRegistry.put(fileClass.getSimpleName(), new BlobEntry(fileClass, blobClass));
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        String rate = config.getInitParameter("cacheExpiresHours");
        if (rate != null) {
            cacheExpiresHours = Integer.parseInt(rate);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filename = request.getRequestURI();

        String[] segments = FilenameUtils.getPathNoEndSeparator(request.getPathInfo()).split("/");

        String id = null, fileClassName = null;
        BlobEntry blobEntry = null;
        Class<? extends IFileBlob> blobClass = null;
        if (segments.length == 2) {
            fileClassName = segments[0];
            id = segments[1];
            blobEntry = blobRegistry.get(fileClassName);
            if (blobEntry != null) {
                blobClass = blobEntry.blobClass;
            }
        }

        if (blobClass == null || CommonsStringUtils.isEmpty(id) || "0".equals(id)) {
            response.setStatus(HttpServletResponse.SC_GONE);
            return;
        }

        IFile<?> file = null;
        if (id.startsWith(DeploymentConsts.TRANSIENT_FILE_PREF)) {
            // treat id as accessKey
            file = FileUploadRegistry.get(id.substring(DeploymentConsts.TRANSIENT_FILE_PREF.length()));
        } else {
            // ensure access allowed
            IHasFile<?> holder = Persistence.secureRetrieve(blobEntry.hasFileClass, new Key(id));
            if (holder != null) {
                file = holder.file();
            }
        }
        IFileBlob blob = null;
        if (file != null) {
            blob = Persistence.service().retrieve(blobClass, file.blobKey().getValue());
        }
        if (file == null || blob == null) {
            log.debug("no such document {} {}", id, filename);
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
        }

        if (!file.contentMimeType().isNull()) {
            response.setContentType(file.contentMimeType().getValue());
        }

        response.setContentType(blob.contentType().getValue());
        response.getOutputStream().write(blob.data().getValue());
    }

    static class BlobEntry {

        public final Class<? extends IHasFile<?>> hasFileClass;

        public final Class<? extends IFileBlob> blobClass;

        BlobEntry(Class<? extends IHasFile<?>> fileClass, Class<? extends IFileBlob> blobClass) {
            this.hasFileClass = fileClass;
            this.blobClass = blobClass;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            } else if (other instanceof BlobEntry) {
                BlobEntry otherEntry = (BlobEntry) other;
                return hasFileClass == otherEntry.hasFileClass && blobClass == otherEntry.blobClass;
            } else {
                return super.equals(other);
            }
        }

        @Override
        public int hashCode() {
            return 31 * hasFileClass.hashCode() + blobClass.hashCode();
        }
    }

}
