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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.PersistenceServicesFactory;

import com.propertyvista.domain.Medium;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ThumbnailService;

/**
 * This service does extra read from DB to read BlobKey, We may decide in future not to do this.
 */
@SuppressWarnings("serial")
public class PublicMediaServlet extends HttpServlet {

    private final static Logger log = LoggerFactory.getLogger(PublicMediaServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filename = request.getPathInfo();
        String id = FilenameUtils.getPathNoEndSeparator(filename);
        if (CommonsStringUtils.isEmpty(id)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        ThumbnailSize thumbnailSize = null;
        try {
            thumbnailSize = ThumbnailSize.valueOf(FilenameUtils.getBaseName(filename));
        } catch (IllegalArgumentException notThumbnail) {
        }

        //TODO deserialize key
        Medium medium = PersistenceServicesFactory.getPersistenceService().retrieve(Medium.class, new Key(id));
        if ((medium == null) || (medium.file().blobKey().isNull())) {
            log.trace("no image");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } else {
            if (thumbnailSize == null) {
                if (!medium.file().contentType().isNull()) {
                    response.setContentType(medium.file().contentType().getValue());
                }
                BlobService.serve(medium.file().blobKey().getValue(), response);
            } else {
                ThumbnailService.serve(medium.file().blobKey().getValue(), thumbnailSize, response);
            }
        }
    }
}
