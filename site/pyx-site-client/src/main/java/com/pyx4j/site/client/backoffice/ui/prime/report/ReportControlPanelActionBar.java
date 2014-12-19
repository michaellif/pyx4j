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
 * Created on Jul 31, 2012
 * @author ArtyomB
 */
package com.pyx4j.site.client.backoffice.ui.prime.report;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.gwt.client.deferred.DeferredProgressListener;
import com.pyx4j.gwt.client.deferred.DeferredProgressPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.PaneTheme;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Toolbar;

public abstract class ReportControlPanelActionBar extends SimplePanel {

    private static final I18n i18n = I18n.get(ReportControlPanelActionBar.class);

    private Button generateReportButton;

    private Button abortButton;

    private final Toolbar controlPanelToolbar;

    private SimplePanel progressPanelHolder;

    private DeferredProgressPanel progressPanel;

    public ReportControlPanelActionBar() {

        setStylePrimaryName(PaneTheme.StyleName.ReportControlPanelActionBar.name());

        controlPanelToolbar = new Toolbar();

        controlPanelToolbar.addItem(generateReportButton = new Button(i18n.tr("Run Report"), new Command() {
            @Override
            public void execute() {
                runReportGeneration();
            }
        }));

        controlPanelToolbar.addItem(progressPanelHolder = new SimplePanel());

        controlPanelToolbar.addItem(abortButton = new Button(i18n.tr("Abort"), new Command() {
            @Override
            public void execute() {
                abortReportGeneration();
            }
        }));
        abortButton.setVisible(false);

        setWidget(controlPanelToolbar);
    }

    public void setEnabled(boolean isEnabled) {
        generateReportButton.setEnabled(isEnabled);
    }

    public abstract void runReportGeneration();

    public abstract void abortReportGeneration();

    public void onReportGenerationStarted(String deferredProgressCorelationId, DeferredProgressListener deferredProgressListener) {
        if (progressPanel != null) {
            progressPanel.cancelProgress();
        }
        progressPanel = new DeferredProgressPanel(i18n.tr("Generating Report"), false, deferredProgressListener);
        progressPanel.setSize("200px", "30px");
        progressPanel.startProgress(deferredProgressCorelationId);
        progressPanelHolder.setWidget(progressPanel);
        generateReportButton.setVisible(false);
        abortButton.setVisible(true);
    }

    public void onReportGenerationStopped() {
        abortButton.setVisible(false);
        new Timer() {

            @Override
            public void run() {
                if (progressPanel != null) {
                    progressPanel.cancelProgress();
                    progressPanel = null;
                    progressPanelHolder.setWidget(null);
                }
                generateReportButton.setVisible(true);
                abortButton.setVisible(false);
            }
        }.schedule(2000);
    }
}
