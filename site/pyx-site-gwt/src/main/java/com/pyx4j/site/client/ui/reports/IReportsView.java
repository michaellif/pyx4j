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
 * Created on Jul 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.client.ui.reports;

import com.pyx4j.gwt.client.deferred.DeferredProgressListener;
import com.pyx4j.site.client.ui.prime.IPrimePane;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

public interface IReportsView<R extends ReportMetadata> extends IPrimePane {

    public interface Presenter<R> extends IPrimePane.Presenter {

        void export(R settings);

        void apply(R settings, boolean forceRefresh);

        void loadReportMetadata(String reportMetadataId);

        void saveReportMetadata(R reportMetadata, String reportMetadataId, boolean allowOverwrite);

        void deleteReportMetadata(String reportMetadataId);

        void populateAvailableReportMetadata();

    }

    void setPresenter(Presenter<R> presenter);

    void setReportMetadata(R reportMetadtata, String reportMetadataId);

    void startReportGenerationProgress(String deferredProgressCorelationId, DeferredProgressListener deferredProgressListener);

    void setReportData(Object data);

    void setError(String errorMessage);

    void onReportMetadataSaveSucceed(String reportMetadataId);

    void onReportMetadataSaveFailed(String errorMessage);
}
