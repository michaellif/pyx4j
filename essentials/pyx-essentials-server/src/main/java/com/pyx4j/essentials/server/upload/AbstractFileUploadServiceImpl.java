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

import org.apache.commons.io.FilenameUtils;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.rpc.upload.UploadResponse;

public abstract class AbstractFileUploadServiceImpl<E extends IFile> extends AbstractUploadServiceImpl<E, E> {

    protected final Class<E> fileEntityClass;

    public AbstractFileUploadServiceImpl(Class<E> fileEntityClass) {
        this.fileEntityClass = fileEntityClass;
    }

    protected abstract E fileUploadRecived(byte[] data, E uploadData);

    @Override
    public ProcessingStatus onUploadReceived(UploadData data, DeferredUploadProcess<E, E> process, UploadResponse<E> response) {
        response.fileContentType = MimeMap.getContentType(FilenameUtils.getExtension(response.fileName));

        E fileInstance = process.getData();
        if (fileInstance == null) {
            fileInstance = EntityFactory.create(fileEntityClass);
        }
        fileInstance.fileName().setValue(response.fileName);
        fileInstance.fileSize().setValue(response.fileSize);
        fileInstance.timestamp().setValue(response.timestamp);
        fileInstance.contentMimeType().setValue(response.fileContentType);

        response.data = fileUploadRecived(data.data, fileInstance);

        if (!response.data.blobKey().isNull()) {
            FileUploadRegistry.register(response.data);
        }

        return ProcessingStatus.completed;
    }
}
