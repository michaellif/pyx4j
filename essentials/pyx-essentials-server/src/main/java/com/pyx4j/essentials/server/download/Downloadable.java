/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on May 8, 2010
 * @author vlads
 */
package com.pyx4j.essentials.server.download;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.sessionstorage.SessionBlobStorageFacade;
import com.pyx4j.gwt.shared.DownloadFormat;

public class Downloadable implements Serializable {

    private static final long serialVersionUID = -3467896728436154528L;

    private static final Logger log = LoggerFactory.getLogger(Downloadable.class);

    public static final String DOWNLOADABLE_LIST_SESSION_ATTRIBUTE = Downloadable.class.getName();

    private final long creationTimestamp;

    private static final long LIFE_DURATION = 5 * Consts.MIN2MSEC;

    private final String contentType;

    private final byte[] data;

    public Downloadable(byte[] data, String contentType) {
        this.data = data;
        this.contentType = contentType;
        this.creationTimestamp = System.currentTimeMillis();
    }

    public static String getContentType(DownloadFormat downloadFormat) {
        return MimeMap.getContentType(downloadFormat);
    }

    public boolean isOutDated() {
        return (System.currentTimeMillis() > getCreationTimestamp() + LIFE_DURATION);
    }

    public long getCreationTimestamp() {
        return this.creationTimestamp;
    }

    public byte[] getData() {
        return this.data;
    }

    public String getContentType() {
        return contentType;
    }

    @SuppressWarnings("unchecked")
    public void save(String fileName) {
        Map<String, Serializable> map = ServerSideFactory.create(SessionBlobStorageFacade.class).getStorage(DOWNLOADABLE_LIST_SESSION_ATTRIBUTE);
        cleanOld(map);
        map.put(fileName, this);
        log.debug("download prepared [{}]; {} bytes", fileName, this.data.length);
    }

    public void remove() {
        Map<String, Serializable> map = ServerSideFactory.create(SessionBlobStorageFacade.class).getStorage(DOWNLOADABLE_LIST_SESSION_ATTRIBUTE);
        map.values().remove(this);
    }

    public static void cancel(String fileName) {
        Map<String, Serializable> map = ServerSideFactory.create(SessionBlobStorageFacade.class).getStorage(DOWNLOADABLE_LIST_SESSION_ATTRIBUTE);
        if (map.remove(fileName) != null) {
            log.debug("canceled download {}", fileName);
        }
        cleanOld(map);
    }

    private static void cleanOld(Map<String, Serializable> map) {
        List<String> toRemove = new ArrayList<>();
        for (Entry<String, Serializable> entry : map.entrySet()) {
            Downloadable downloadable = (Downloadable) entry.getValue();
            if (downloadable.isOutDated()) {
                toRemove.add(entry.getKey());
            }
        }
        for (String key : toRemove) {
            map.remove(key);
            log.debug("remove outdated download {}", key);
        }
    }

    public static Downloadable getDownloadable(String fileName) {
        Map<String, Serializable> map = ServerSideFactory.create(SessionBlobStorageFacade.class).getStorage(DOWNLOADABLE_LIST_SESSION_ATTRIBUTE);
        try {
            return (Downloadable) map.get(fileName);
        } finally {
            cleanOld(map);
        }
    }

    public static String getDownloadableFileName(String pathInfo) {
        if (pathInfo == null) {
            return null;
        }
        int fileNameStart = pathInfo.lastIndexOf("/");
        if (fileNameStart == -1) {
            return null;
        }
        return pathInfo.substring(fileNameStart + 1);
    }

}
