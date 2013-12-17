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
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.shared.AbstractIFileBlob;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.rpc.upload.UploadId;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;

public abstract class AbstractUploadServiceImpl<U extends IEntity, B extends AbstractIFileBlob> implements UploadService<U, B>, UploadReciver<U, B> {

    private static final I18n i18n = I18n.get(AbstractUploadServiceImpl.class);

    protected AbstractUploadServiceImpl() {
    }

    protected void onPepareUpload(U data, UploadId id) {
    }

    @Override
    public void obtainMaxFileSize(AsyncCallback<Long> callback) {
        callback.onSuccess(getMaxSize());
    }

    /**
     * Must return LowerCase strings, no dot in front
     */
    @Override
    public Collection<String> getSupportedExtensions() {
        return null;
    }

    @Override
    public void obtainSupportedExtensions(AsyncCallback<Vector<String>> callback) {
        Collection<String> extensions = getSupportedExtensions();
        if (extensions != null) {
            callback.onSuccess(new Vector<String>(extensions));
        } else {
            callback.onSuccess(null);
        }
    }

    protected DeferredUploadProcess<U, B> createUploadDeferredProcess(U data) {
        return new DeferredUploadProcess<U, B>(data);
    }

    @Override
    public final void prepareUploadProcess(AsyncCallback<UploadId> callback, U data) {
        UploadId id = new UploadId();
        onPepareUpload(data, id);
        id.setDeferredCorrelationId(DeferredProcessRegistry.register(createUploadDeferredProcess(data)));
        callback.onSuccess(id);
    }

    @Override
    public void onUploadStarted(String fileName, String contentMimeType) {
        Collection<String> extensions = getSupportedExtensions();
        if (extensions != null) {
            String extension = FilenameUtils.getExtension(fileName);
            if (extension != null) {
                extension = extension.toLowerCase(Locale.ENGLISH);
            }
            if (!extensions.contains(extension)) {
                throw new UserRuntimeException(i18n.tr("Unsupported {0} File Type {1}", getUploadFileTypeName(), extension));
            }
        }
    }

    protected abstract void processUploadedData(U uploadInitiationData, UploadedData uploadedData, IFile<B> response);

    @Override
    public final IFile<B> onUploadReceived(U uploadInitiationData, UploadedData uploadedData) {
        @SuppressWarnings("unchecked")
        IFile<B> fileInstance = EntityFactory.create(IFile.class);
        fileInstance.fileName().setValue(uploadedData.fileName);
        fileInstance.fileSize().setValue(uploadedData.binaryContentSize);
        fileInstance.timestamp().setValue(uploadedData.timestamp);
        fileInstance.contentMimeType().setValue(uploadedData.contentMimeType);

        processUploadedData(uploadInitiationData, uploadedData, fileInstance);

        if (!fileInstance.blobKey().isNull()) {
            FileUploadRegistry.register(fileInstance);
        }

        return fileInstance;
    }

    @Override
    public void cancelUpload(AsyncCallback<VoidSerializable> callback, UploadId uploadId) {
        callback.onSuccess(null);
    }

    @Override
    public final void getUploadResponse(AsyncCallback<IFile<B>> callback, UploadId uploadId) {
        @SuppressWarnings("unchecked")
        DeferredUploadProcess<U, B> process = (DeferredUploadProcess<U, B>) DeferredProcessRegistry.get(uploadId.getDeferredCorrelationId());
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

}
