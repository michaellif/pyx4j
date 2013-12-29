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
 * Created on Dec 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.report;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.shared.ApplicationBackend;
import com.pyx4j.config.shared.ApplicationBackend.ApplicationBackendType;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.rpc.report.ReportService;
import com.pyx4j.essentials.server.download.DownloadableServiceImpl;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;

public class ReportServiceImpl<E extends IEntity> extends DownloadableServiceImpl implements ReportService<E> {

    @Override
    public void createDownload(AsyncCallback<String> callback, ReportRequest reportRequest) {
        if (ApplicationBackend.getBackendType() == ApplicationBackendType.GAE) {
            callback.onSuccess(DeferredProcessRegistry.register(new SearchReportDeferredProcess<E>(reportRequest)));
        } else {
            callback.onSuccess(DeferredProcessRegistry.fork(new SearchReportDeferredProcess<E>(reportRequest), DeferredProcessRegistry.THREAD_POOL_DOWNLOADS));
        }
    }

}
