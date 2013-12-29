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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.blob.MediaFileBlob;

/**
 * Simple DB base Blob storage abstraction for future refactoring.
 */
public class BlobService {

    public static Key persist(byte[] content, String name, String contentType) {
        MediaFileBlob entity = EntityFactory.create(MediaFileBlob.class);
        entity.name().setValue(name);
        entity.data().setValue(content);
        entity.contentType().setValue(contentType);
        Persistence.service().persist(entity);
        return entity.getPrimaryKey();
    }

    public static void delete(Key key) {
        Persistence.service().delete(MediaFileBlob.class, key);
    }

    public static void serve(Key key, HttpServletResponse response) throws IOException {
        MediaFileBlob blob = Persistence.service().retrieve(MediaFileBlob.class, key);
        if (blob != null) {
            response.setContentLength(blob.data().getValue().length);
            OutputStream out = response.getOutputStream();
            try {
                out.write(blob.data().getValue());
            } finally {
                IOUtils.closeQuietly(out);
            }
        }
    }

    public static void save(Key key, File destination) throws IOException {
        MediaFileBlob blob = Persistence.service().retrieve(MediaFileBlob.class, key);
        if (blob != null) {
            FileUtils.forceMkdir(destination.getParentFile());
            OutputStream out = new FileOutputStream(destination);
            try {
                out.write(blob.data().getValue());
            } finally {
                IOUtils.closeQuietly(out);
            }
        }
    }
}
