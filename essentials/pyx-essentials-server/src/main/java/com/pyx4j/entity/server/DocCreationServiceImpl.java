/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Dec 11, 2014
 * @author vlads
 */
package com.pyx4j.entity.server;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.DocCreationRequest;
import com.pyx4j.entity.rpc.DocCreationService;
import com.pyx4j.entity.rpc.SheetCreationRequest;
import com.pyx4j.gwt.server.deferred.IDeferredProcess;
import com.pyx4j.rpc.shared.DeferredCorrelationId;

public class DocCreationServiceImpl implements DocCreationService {

    @Override
    public void startDocCreation(AsyncCallback<DeferredCorrelationId> callback, DocCreationRequest docCreationRequest) {
        callback.onSuccess(startDocCreation(docCreationRequest));
    }

    public DeferredCorrelationId startDocCreation(DocCreationRequest docCreationRequest) {
        if (docCreationRequest instanceof SheetCreationRequest) {
            //     return getSheetCreationManager().startDocCreation((SheetCreationRequest) docCreationRequest);
        }
        return null;
    }

    public abstract class SheetCreationDeferredProcess implements IDeferredProcess {

    }

//    SheetCreationManager getSheetCreationManager() {
//        return new SheetCreationManager();
//    }
}
