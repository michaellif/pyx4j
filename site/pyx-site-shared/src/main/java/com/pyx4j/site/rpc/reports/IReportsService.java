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
 * Created on Aug 2, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.rpc.reports;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

public interface IReportsService<R extends ReportMetadata> extends IService {

    /** prepares and returns a report for the provided report metadata */
    void generateReport(AsyncCallback<Serializable> callback, R reportMetadata);

    void generateReportAsync(AsyncCallback<String> callback, R reportMetadata);

    void getReport(AsyncCallback<Serializable> callback);

    void export(AsyncCallback<String> callback, R reportMetadata);

    /**
     * as far as I understand this is called in case report is ready for download, but user pressed cancel (i.e. it's not the same thing as DeferredPorcess
     * cancellation while it in progress)
     */
    void cancelExportedReport(AsyncCallback<VoidSerializable> callback, String downloadUrl);

}
