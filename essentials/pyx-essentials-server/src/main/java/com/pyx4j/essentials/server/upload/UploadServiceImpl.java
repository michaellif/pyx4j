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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.rpc.upload.UploadId;
import com.pyx4j.essentials.rpc.upload.UploadService;
import com.pyx4j.essentials.server.deferred.DeferredProcessServicesImpl;
import com.pyx4j.rpc.shared.VoidSerializable;

public class UploadServiceImpl implements UploadService {

    protected void onpPepareUpload(IEntity data, UploadId id) {

    }

    @Override
    public void prepareUpload(AsyncCallback<UploadId> callback, IEntity data) {
        UploadId id = new UploadId();
        onpPepareUpload(data, id);
        id.setDeferredCorrelationId(DeferredProcessServicesImpl.register(new UploadDeferredProcess()));
        callback.onSuccess(id);
    }

    @Override
    public void cancelUpload(AsyncCallback<VoidSerializable> callback, Key uploadKey) {
        callback.onSuccess(null);
    }

}
