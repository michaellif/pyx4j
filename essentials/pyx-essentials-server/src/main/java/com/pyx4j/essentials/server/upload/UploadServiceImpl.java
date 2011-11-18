/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Aug 12, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.upload;

import java.util.Collection;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.upload.UploadId;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.essentials.rpc.upload.UploadService;
import com.pyx4j.essentials.server.deferred.DeferredProcessRegistry;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.rpc.shared.VoidSerializable;

public abstract class UploadServiceImpl<E extends IEntity> implements UploadService<E>, UploadReciver {

    private static I18n i18n = I18n.get(UploadServiceImpl.class);

    protected void onpPepareUpload(E data, UploadId id) {

    }

    @Override
    public void getMaxFileSize(AsyncCallback<Long> callback) {
        callback.onSuccess(getMaxSize());
    }

    @Override
    public void prepareUpload(AsyncCallback<UploadId> callback, E data) {
        UploadId id = new UploadId();
        onpPepareUpload(data, id);
        id.setDeferredCorrelationId(DeferredProcessRegistry.register(new UploadDeferredProcess(data)));
        callback.onSuccess(id);
    }

    @Override
    public void cancelUpload(AsyncCallback<VoidSerializable> callback, UploadId uploadId) {
        callback.onSuccess(null);
    }

    @Override
    public void getUploadResponse(AsyncCallback<UploadResponse> callback, UploadId uploadId) {
        UploadDeferredProcess process = (UploadDeferredProcess) DeferredProcessRegistry.get(uploadId.getDeferredCorrelationId());
        if (process != null) {
            DeferredProcessProgressResponse response = process.status();
            if (response.isCompleted()) {
                DeferredProcessRegistry.remove(uploadId.getDeferredCorrelationId());
                callback.onSuccess(process.getResponse());
            } else {
                throw new RuntimeException("Process not finished");
            }
        } else {
            throw new RuntimeException("Process " + uploadId.getDeferredCorrelationId() + " not found");
        }
    }

    /**
     * Must return LowerCase strings, no dot in front
     */
    public Collection<String> getSupportedExtensions() {
        return null;
    }

    @Override
    public void onUploadStart(String fileName) {
        Collection<String> extensions = getSupportedExtensions();
        if (extensions != null) {
            String extension = FilenameUtils.getExtension(fileName);
            if (extension != null) {
                extension = extension.toLowerCase(Locale.ENGLISH);
            }
            if (!extensions.contains(extension)) {
                throw new UserRuntimeException(i18n.tr("Unsupported File Type {0}", extension));
            }
        }
    }
}
