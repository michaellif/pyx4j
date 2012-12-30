/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-12-29
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.upload;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.server.contexts.Context;

public class FileUploadRegistry {

    private static final String SESSION_ATTRIBUTE = FileUploadRegistry.class.getName();

    public static void register(IFile file) {
        @SuppressWarnings("unchecked")
        Map<String, IFile> userUploadedFiles = (Map<String, IFile>) Context.getVisit().getAttribute(SESSION_ATTRIBUTE);
        if (userUploadedFiles == null) {
            userUploadedFiles = new HashMap<String, IFile>();
            Context.getVisit().setAttribute(SESSION_ATTRIBUTE, (Serializable) userUploadedFiles);
        }
        file.accessKey().setValue(UUID.randomUUID().toString());
        userUploadedFiles.put(file.accessKey().getValue(), file);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IFile> T get(String accessKey) {
        Map<String, IFile> userUploadedFiles = (Map<String, IFile>) Context.getVisit().getAttribute(SESSION_ATTRIBUTE);
        if (userUploadedFiles == null) {
            return null;
        } else {
            return (T) userUploadedFiles.get(accessKey);
        }
    }

    public static boolean allowModifications(IFile entity, MemberMeta meta, Object valueOrig, Object valueNew) {
        if (entity.accessKey().isNull()) {
            return false;
        }
        IFile userUploadedFile = get(entity.accessKey().getValue());
        if (userUploadedFile == null) {
            return false;
        } else {
            return userUploadedFile.blobKey().equals(valueNew);
        }
    }
}
