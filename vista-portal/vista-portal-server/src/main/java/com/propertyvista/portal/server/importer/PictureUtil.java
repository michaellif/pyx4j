/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 23, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.importer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.Medium;
import com.propertyvista.domain.Picture;

/**
 * This is just a placeholder, this can be moved to another util method
 */
public class PictureUtil {

    private final static Logger log = LoggerFactory.getLogger(PictureUtil.class);

    public static Picture loadPicture(String filename, Class<?> clazz) {
        String fullFilename = IOUtils.resourceFileName(filename, clazz);
        try {
            byte[] bytes = IOUtils.getResource(fullFilename);
            if (bytes == null) {
                // this is on debug, since it is for the caller to make sure that the value is not null
                log.debug("Could not find picture [{}] in classpath", filename);
            } else {
                // log.info("Picture size is: " + picture.length);
                Picture picture = EntityFactory.create(Picture.class);
                picture.content().setValue(bytes);
                return picture;
            }
        } catch (Exception e) {
            log.error("Failed to read the file [{}]", filename, e);
            throw new Error("Failed to read the file [" + filename + "]");
        }
        return null;
    }

    public static Map<Medium, byte[]> loadbuildingMedia(String code) {
        File dir = new File(new File("data", "buildings"), code);
        if (!dir.isDirectory()) {
            return Collections.emptyMap();
        }
        Map<Medium, byte[]> data = new HashMap<Medium, byte[]>();

        loadFiles(new File(dir, "buildings"), data);
        loadFiles(new File(dir, "res_album"), data);

        return data;
    }

    private static void loadFiles(File dir, Map<Medium, byte[]> data) {
        if (!dir.isDirectory()) {
            return;
        }
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("th") || file.isDirectory()) {
                continue;
            }
            String mime = MimeMap.getContentType(FilenameUtils.getExtension(file.getName()));
            if (mime == null) {
                continue;
            }

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            InputStream in = null;
            try {
                Medium m = EntityFactory.create(Medium.class);

                m.file().filename().setValue(file.getName());
                m.file().caption().setValue(FilenameUtils.getBaseName(file.getName()));
                m.file().contentType().setValue(mime);

                in = new FileInputStream(file);
                IOUtils.copyStream(in, b, 1024);

                byte raw[] = b.toByteArray();
                m.file().fileSize().setValue(raw.length);

                data.put(m, raw);
            } catch (IOException e) {
                log.error("load file error", e);
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(b);
            }
        }

    }
}
