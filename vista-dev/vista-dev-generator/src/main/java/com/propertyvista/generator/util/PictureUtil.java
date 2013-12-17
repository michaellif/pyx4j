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
package com.propertyvista.generator.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.PublicVisibilityType;

/**
 * This is just a placeholder, this can be moved to another util method
 */
public class PictureUtil {

    private final static Logger log = LoggerFactory.getLogger(PictureUtil.class);

    public static Map<MediaFile, byte[]> loadResourceMedia(String filenamePrefix, Class<?> clazz) {
        Map<MediaFile, byte[]> data = new LinkedHashMap<MediaFile, byte[]>();
        loadResourcePicture(filenamePrefix + ".jpg", clazz, data);
        loadResourcePicture(filenamePrefix + "-1.jpg", clazz, data);
        loadResourcePicture(filenamePrefix + "-2.jpg", clazz, data);
        loadResourcePicture(filenamePrefix + "-3.jpg", clazz, data);
        return data;
    }

    private static void loadResourcePicture(String filename, Class<?> clazz, Map<MediaFile, byte[]> data) {
        try {
            byte raw[] = IOUtils.getResource(IOUtils.resourceFileName(filename, clazz));
            if (raw == null) {
                // this is on debug, since it is for the caller to make sure that the value is not null
                log.debug("Could not find picture [{}] in classpath", filename);
                return;
            }
            MediaFile m = EntityFactory.create(MediaFile.class);
            m.file().fileName().setValue(filename);
            m.visibility().setValue(PublicVisibilityType.global);
            m.caption().setValue(FilenameUtils.getBaseName(filename));
            String mime = MimeMap.getContentType(FilenameUtils.getExtension(filename));
            m.file().contentMimeType().setValue(mime);

            m.file().fileSize().setValue(raw.length);

            data.put(m, raw);
        } catch (IOException e) {
            log.error("Failed to read the file [{}]", filename, e);
            throw new Error("Failed to read the file [" + filename + "]");
        }
    }

    public static Map<MediaFile, byte[]> loadbuildingMedia(String code) {
        File dir = new File(new File("data", "buildings"), code);
        if (!dir.isDirectory()) {
            return Collections.emptyMap();
        }
        Map<MediaFile, byte[]> data = new HashMap<MediaFile, byte[]>();

        loadFiles(new File(dir, "buildings"), data);
        loadFiles(new File(dir, "res_album"), data);

        return data;
    }

    private static void loadFiles(File dir, Map<MediaFile, byte[]> data) {
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
                MediaFile m = EntityFactory.create(MediaFile.class);
                m.visibility().setValue(PublicVisibilityType.global);

                m.file().fileName().setValue(file.getName());
                m.caption().setValue(FilenameUtils.getBaseName(file.getName()));
                m.file().contentMimeType().setValue(mime);

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
