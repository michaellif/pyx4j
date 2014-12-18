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
 * Created on Sep 17, 2014
 * @author michaellif
 */
package com.pyx4j.site.client.backoffice.ui.prime.report;

import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.gwt.client.deferred.DeferredProgressListener;
import com.pyx4j.site.client.backoffice.ui.PaneTheme;
import com.pyx4j.site.shared.domain.reports.ReportTemplate;

public class ReportControlPanel<R extends ReportTemplate> extends FlowPanel {

    private final ReportControlPanelActionBar propertyFormControlBar;

    private final CForm<R> controlPanelForm;

    public ReportControlPanel(final AbstractPrimeReport<R> abstractReport, CForm<R> controlPanelForm) {
        this.controlPanelForm = controlPanelForm;
        controlPanelForm.init();

        setStylePrimaryName(PaneTheme.StyleName.ReportControlPanel.name());

        propertyFormControlBar = new ReportControlPanelActionBar() {

            @Override
            public void runReportGeneration() {
                abstractReport.runReportGeneration();
            }

            @Override
            public void abortReportGeneration() {
                abstractReport.abortReportGeneration();
            }

        };

        add(controlPanelForm);
        add(propertyFormControlBar);

    }

    public void populateReportSettings(R reportMetadata) {
        controlPanelForm.populate(reportMetadata);
    }

    public R getReportSettings() {
        return controlPanelForm.getValue();
    }

    public boolean isValid() {
        controlPanelForm.setVisitedRecursive();
        return controlPanelForm.isValid();
    }

    public void onReportGenerationStarted(String deferredProgressCorelationId, DeferredProgressListener deferredProgressListener) {
        propertyFormControlBar.onReportGenerationStarted(deferredProgressCorelationId, deferredProgressListener);
    }

    public void onReportGenerationStopped() {
        propertyFormControlBar.onReportGenerationStopped();
    }
}
