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
 * Created on Aug 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.gwt.rpc.deferred;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.VoidSerializable;

public interface DeferredProcessService extends IService {

    public void getStatus(AsyncCallback<DeferredProcessProgressResponse> callback, String deferredCorrelationId, boolean finalize);

    public void cancel(AsyncCallback<VoidSerializable> callback, String deferredCorrelationId);

    public void continueExecution(AsyncCallback<DeferredProcessProgressResponse> callback, String deferredCorrelationId);

}