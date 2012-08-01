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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityForm;

public abstract class AbstractReportsView implements IReportsView {

    private Presenter presenter;

    private final DockLayoutPanel viewPanel;

    private final Map<Class<? extends ReportSettings>, ReportFactory> reportFactoryMap;

    private CEntityForm<ReportSettings> settingsForm;

    private final ScrollPanel settingsFormPanel;

    private final ScrollPanel reportViewPanel;

    private final ReportSettingsFormControlPanel reportSettingsControls;

    public AbstractReportsView(Map<Class<? extends ReportSettings>, ReportFactory> reportFactoryMap) {
        this.reportFactoryMap = reportFactoryMap;
        viewPanel = new DockLayoutPanel(Unit.EM);
        viewPanel.addNorth(new HTML(), 1);
        viewPanel.setSize("100%", "100%");

        settingsFormPanel = new ScrollPanel();
        viewPanel.addNorth(settingsFormPanel, 13);

        reportSettingsControls = new ReportSettingsFormControlPanel() {
            @Override
            public void onApply() {
                if (presenter != null) {
                    presenter.apply(settingsForm.getValue());
                }
            }

            @Override
            public void onSettingsModeToggled(boolean isAdvanced) {
                updateSettingsMode(isAdvanced);
            }

        };
        reportSettingsControls.getElement().getStyle().setProperty("borderTopStyle", "solid");
        reportSettingsControls.getElement().getStyle().setProperty("borderBottomStyle", "solid");
        reportSettingsControls.getElement().getStyle().setProperty("borderTopWidth", "1px");
        reportSettingsControls.getElement().getStyle().setProperty("borderBottomWidth", "1px");

        viewPanel.addNorth(reportSettingsControls, 2.5);

        reportViewPanel = new ScrollPanel();
        reportViewPanel.getElement().getStyle().setPadding(1, Unit.EM);
        viewPanel.add(reportViewPanel);

        settingsForm = null;
        presenter = null;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return viewPanel;
    }

    @Override
    public void setReportSettings(ReportSettings reportSettings) {
        settingsFormPanel.setWidget(null);
        reportViewPanel.setWidget(null);

        if (reportSettings == null) {
        } else {
            ReportFactory factory = reportFactoryMap.get(reportSettings.getInstanceValueClass());
            if (factory == null) {
                throw new Error("factory not found for report: " + reportSettings.getInstanceValueClass().getName());
            } else if (factory instanceof HasAdvancedModeReportFactory) {
                boolean isAdvancedMode = ((HasAdvancedSettings) reportSettings).isInAdvancedMode().isBooleanTrue();
                settingsForm = isAdvancedMode ? ((HasAdvancedModeReportFactory) factory).getAdvancedReportSettingsForm() : factory.getReportSettingsForm();
                reportSettingsControls.enableSettingsModeToggle(isAdvancedMode);
            } else {
                settingsForm = factory.getReportSettingsForm();
                reportSettingsControls.disableModeToggle();
            }
            settingsFormPanel.setWidget(settingsForm);
            settingsForm.populate(reportSettings);

            reportViewPanel.setWidget(factory.getReport());
        }
    }

    private void updateSettingsMode(boolean isAdvanced) {
        ((HasAdvancedSettings) settingsForm.getValue()).isInAdvancedMode().setValue(isAdvanced);
        setReportSettings(settingsForm.getValue());
    }

}
