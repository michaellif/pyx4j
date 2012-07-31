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

import java.util.Map;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractReportsView implements IReportsView {

    private Presenter presenter;

    private final DockLayoutPanel panel;

    private final Map<Class<? extends ReportSettings>, ReportFactory> reportFactoryMap;

    private IReportSettingsForm<ReportSettings> settingsForm;

    public AbstractReportsView(Map<Class<? extends ReportSettings>, ReportFactory> reportFactoryMap) {
        this.reportFactoryMap = reportFactoryMap;
        panel = new DockLayoutPanel(Unit.EM);
        panel.addNorth(new HTML(), 1);
        panel.setSize("100%", "100%");
        settingsForm = null;
        presenter = null;
    }

    @Override
    public void setReportSettings(ReportSettings reportSettings) {
        if (settingsForm != null) {
            panel.remove(settingsForm);
        }
        if (reportSettings == null) {
            return;
        }

        ReportFactory factory = reportFactoryMap.get(reportSettings.getInstanceValueClass());
        if (factory == null) {
            throw new Error("factory not found for report: " + reportSettings.getInstanceValueClass().getName());
        }
        settingsForm = (IReportSettingsForm<ReportSettings>) factory.getReportSettingsForm(reportSettings);

        if (settingsForm != null) {
            panel.addNorth(settingsForm, 15);
            settingsForm.populate(reportSettings);
        }

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

}
