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
 * Created on Aug 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.gwt.rpc.upload;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.AbstractIFileBlob;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.VoidSerializable;

/**
 * UI client facing part of upload, see UploadReciver
 * 
 * @param <U>
 *            Upload initiation data, Maybe file description or other option.
 *            Context of Upload dialog form.
 *            May be unused the use IEnity.
 * @param <R>
 *            The IFile Response
 * 
 */
public interface UploadService<U extends IEntity, B extends AbstractIFileBlob> extends IService {

    public static final String PostCorrelationID = "correlationID";

    public static final String ResponsePrefix = "UploadResponse";

    public static final String ResponseOk = "_OK_";

    public static final String ResponseProcessWillContinue = "_CONTINUE_";

    public void obtainMaxFileSize(AsyncCallback<Long> callback);

    public void obtainSupportedExtensions(AsyncCallback<Vector<String>> callback);

    public void prepareUploadProcess(AsyncCallback<UploadId> callback, U data);

    public void cancelUpload(AsyncCallback<VoidSerializable> callback, UploadId uploadId);

    public void getUploadResponse(AsyncCallback<IFile<B>> callback, UploadId uploadId);

}
