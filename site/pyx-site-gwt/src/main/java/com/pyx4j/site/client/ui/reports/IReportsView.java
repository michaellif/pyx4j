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
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

public interface IReportsView extends IPane {

    public interface Presenter extends IPane.Presenter {

        void export(ReportMetadata settings);

        void apply(ReportMetadata settings);

        void loadSettings(String reportSettingsId);

        void saveSettings(ReportMetadata settings, String reportSettingsId, boolean allowOverwrite);

        void deleteSettings(String settings);

        void populateAvailableReportSettings();

    }

    void setPresenter(Presenter presenter);

    <E extends ReportMetadata> void setReportSettings(E reportSettings, String settingsId);

    void startReportGenerationProgress(String deferredProgressCorelationId, DeferredProgressListener deferredProgressListener);

    void setReportData(Object data);

    void setError(String errorMessage);

    void onReportSettingsSaveSucceed(String reportSettingsId);

    void onReportSettingsSaveFailed(String reason);

}
