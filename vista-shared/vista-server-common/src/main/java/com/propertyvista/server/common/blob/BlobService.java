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
package com.propertyvista.server.common.blob;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.server.domain.FileBlob;

/**
 * Simple DB base Blob storage abstraction for future refactoring.
 */
@SuppressWarnings("deprecation")
public class BlobService {

    public static Key persist(byte[] content, String name, String contentType) {
        FileBlob entity = EntityFactory.create(FileBlob.class);
        entity.name().setValue(name);
        entity.content().setValue(content);
        entity.contentType().setValue(contentType);
        PersistenceServicesFactory.getPersistenceService().persist(entity);
        return entity.getPrimaryKey();
    }

    public static void delete(Key key) {
        PersistenceServicesFactory.getPersistenceService().delete(FileBlob.class, key);
    }

    public static void serve(Key key, HttpServletResponse response) throws IOException {
        FileBlob blob = PersistenceServicesFactory.getPersistenceService().retrieve(FileBlob.class, key);
        if (blob != null) {
            response.setContentLength(blob.content().getValue().length);
            ServletOutputStream out = response.getOutputStream();
            try {
                out.write(blob.content().getValue());
            } finally {
                IOUtils.closeQuietly(out);
            }
        }
    }
}
