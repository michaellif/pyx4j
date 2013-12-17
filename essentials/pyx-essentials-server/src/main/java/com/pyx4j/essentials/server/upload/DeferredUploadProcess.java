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

import com.pyx4j.entity.shared.AbstractIFileBlob;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.IDeferredProcess;

/**
 * Upload status and progress holder.
 * The reply is stored in this class.
 * 
 * You may override onUploadProcessed() and execute() implementation of this process to add more Deferred processing
 */

@SuppressWarnings("serial")
public class DeferredUploadProcess<U extends IEntity, B extends AbstractIFileBlob> implements IDeferredProcess {

    private final DeferredProcessProgressResponse status;

    private final U uploadInitiationData;

    private IFile<B> response;

    public DeferredUploadProcess(U uploadInitiationData) {
        this.status = new DeferredProcessProgressResponse();
        this.uploadInitiationData = uploadInitiationData;
    }

    protected void onUploadProcessed(final UploadedData data, final IFile<B> response) {
        this.status().setCompleted();
    }

    public final void uploadProcessed(final UploadedData data, final IFile<B> response) {
        this.response = response;
        onUploadProcessed(data, response);
    }

    @Override
    public void execute() {
    }

    @Override
    public void cancel() {
        status.setCanceled();
    }

    @Override
    public DeferredProcessProgressResponse status() {
        return status;
    }

    public U getUploadInitiationData() {
        return uploadInitiationData;
    }

    public IFile<B> getResponse() {
        return response;
    }

}
