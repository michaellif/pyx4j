/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-05-07
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.report;

import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.rpc.report.ReportServices;
import com.pyx4j.essentials.server.deferred.DeferredProcessServicesImpl;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.rpc.shared.VoidSerializable;

public class ReportServicesImpl implements ReportServices {

    public static class SearchImpl implements ReportServices.Search {

        @Override
        public String execute(ReportRequest request) {
            return DeferredProcessServicesImpl.register(new SearchReportDeferredProcess(request));
        }

    }

    public static class CancelDownloadImpl implements ReportServices.CancelDownload {

        @Override
        public VoidSerializable execute(String request) {
            String fileName = Downloadable.getDownloadableFileName(request);
            if (fileName != null) {
                Downloadable.cancel(fileName);
            }
            return null;
        }

    }

}
