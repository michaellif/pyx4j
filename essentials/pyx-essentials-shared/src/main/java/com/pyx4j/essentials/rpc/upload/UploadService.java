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
package com.pyx4j.essentials.rpc.upload;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.VoidSerializable;

public interface UploadService<E extends IEntity> extends IService {

    public static final String PostCorrelationID = "correlationID";

    public static final String PostUploadKey = "uploadKey";

    public static final String PostUploadDescription = "uploadDescription";

    public static final String ResponsePrefix = "UploadResponse";

    public static final String ResponseOk = "_OK_";

    public static final String ResponseProcessWillContinue = "_CONTINUE_";

    public void prepareUpload(AsyncCallback<UploadId> callback, E data);

    public void cancelUpload(AsyncCallback<VoidSerializable> callback, UploadId uploadId);

    public void getUploadResponse(AsyncCallback<UploadResponse> callback, UploadId uploadId);

}
