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
 */
package com.pyx4j.site.client.backoffice.ui.prime.report;

import com.pyx4j.gwt.client.deferred.DeferredProgressListener;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView.IPrimePanePresenter;
import com.pyx4j.site.client.backoffice.ui.prime.report.IPrimeReportView.IPrimeReportPresenter;
import com.pyx4j.site.shared.domain.reports.ReportTemplate;

public interface IPrimeReportView<R extends ReportTemplate> extends IPrimePaneView<IPrimeReportPresenter<R>> {

    public interface IPrimeReportPresenter<R> extends IPrimePanePresenter {

        void export();

        void runReportGeneration();

        void abortReportGeneration();

        void loadReportMetadata(String reportMetadataId);

        void saveReportMetadata();

        void saveAsReportMetadata();

        void deleteReportMetadata(String reportMetadataId);

        void loadAvailableTemplates();

    }

    @Override
    void setPresenter(IPrimeReportPresenter<R> presenter);

    void setReportMetadata(R reportMetadtata);

    R getReportSettings();

    void startReportGenerationProgress(String deferredProgressCorelationId, DeferredProgressListener deferredProgressListener);

    void setReportData(Object data);

    void setError(String errorMessage);

    void reset();

    void onReportMetadataSaveSucceed();

    void onReportMetadataSaveFailed(String errorMessage);

}
