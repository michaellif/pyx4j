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
package com.pyx4j.gwt.client.upload;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.rpc.upload.UploadService;

public class FileUploadPanel<U extends IEntity, E extends IFile> extends UploadPanel<U, E> {

    private final FileUploadReciver<E> uploadReciver;

    public FileUploadPanel(UploadService<U, E> service, FileUploadReciver<E> uploadReciver) {
        super(service);
        this.uploadReciver = uploadReciver;
    }

    @Override
    protected void onUploadComplete(E serverUploadResponse) {
        super.onUploadComplete(serverUploadResponse);
        uploadReciver.onUploadComplete(serverUploadResponse);
    }
}
