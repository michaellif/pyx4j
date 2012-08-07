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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.shared.reports.HasAdvancedSettings;
import com.pyx4j.entity.shared.reports.ReportMetadata;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.ViewImplBase;
import com.pyx4j.widgets.client.Button;

public abstract class AbstractReportsView extends ViewImplBase implements IReportsView {

    public enum Styles {

        SettingsFormPanel, ReportPanel;
    }

    private static final I18n i18n = I18n.get(AbstractReportsView.class);

    private IReportsView.Presenter presenter;

    private final FlowPanel viewPanel;

    private final Map<Class<? extends ReportMetadata>, ReportFactory> reportFactoryMap;

    private CEntityForm<ReportMetadata> settingsForm;

    private final SimplePanel settingsFormPanel;

    private final ScrollPanel reportPanel;

    private final ReportSettingsFormControlPanel reportSettingsControls;

    private Report report;

    public AbstractReportsView(Map<Class<? extends ReportMetadata>, ReportFactory> reportFactoryMap) {
        setSize("100%", "100%");
        this.reportFactoryMap = reportFactoryMap;
        this.settingsForm = null;
        this.presenter = null;

        viewPanel = new FlowPanel();
        viewPanel.setWidth("100%");
        viewPanel.setHeight("100%");

        settingsFormPanel = new SimplePanel();
        settingsFormPanel.setStylePrimaryName(Styles.SettingsFormPanel.name());
        viewPanel.add(settingsFormPanel);

        reportSettingsControls = new ReportSettingsFormControlPanel() {
            @Override
            public void onApply() {
                if (presenter != null & settingsForm != null) {
                    if (settingsForm.isValid()) {
                        presenter.apply(settingsForm.getValue());
                    } else {
                        settingsForm.setUnconditionalValidationErrorRendering(true);
                    }
                }
            }

            @Override
            public void onSettingsModeToggled(boolean isAdvanced) {
                updateSettingsMode(isAdvanced);
            }

        };
        viewPanel.add(reportSettingsControls);

        reportPanel = new ScrollPanel();
        reportPanel.setSize("100%", "100%");
        reportPanel.setStylePrimaryName(Styles.ReportPanel.name());
        viewPanel.add(reportPanel);

        addHeaderToolbarTwoItem(new Button(i18n.tr("Export")));

        addHeaderToolbarTwoItem(new Button(i18n.tr("Print")));

        addHeaderToolbarTwoItem(new Button(i18n.tr("Save As...")));
        addHeaderToolbarTwoItem(new Button(i18n.tr("Save")));

        addHeaderToolbarTwoItem(new Button(i18n.tr("Load..."), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                showVisor(createLoadPanel(), i18n.tr(""));

            }
        }));

        addHeaderToolbarTwoItem(new Button(i18n.tr("Refresh")));

        setCaption(i18n.tr("Reports"));
        setContentPane(new ScrollPanel(viewPanel));
    }

    @Override
    public void setPresenter(IReportsView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setReportSettings(ReportMetadata reportSettings) {
        settingsFormPanel.setWidget(null);

        if (reportSettings == null) {
            reportPanel.setWidget(null);
        } else {
            updateSettingsForm(reportSettings);
            setCaption(reportSettings.getEntityMeta().getCaption());

            ReportFactory<?> factory = reportFactoryMap.get(reportSettings.getInstanceValueClass());
            report = factory.getReport();
            reportPanel.setWidget(report);
        }
    }

    @Override
    public void setReportData(Object data) {
        if (report != null) {
            report.setData(data);
        }
    }

    private void updateSettingsMode(boolean isAdvanced) {
        if (settingsForm != null) {
            ((HasAdvancedSettings) settingsForm.getValue()).isInAdvancedMode().setValue(isAdvanced);
            updateSettingsForm(settingsForm.getValue());
        }
    }

    private void updateSettingsForm(ReportMetadata reportSettings) {
        ReportFactory<?> factory = reportFactoryMap.get(reportSettings.getInstanceValueClass());
        if (factory == null) {
            throw new Error("factory not found for report: " + reportSettings.getInstanceValueClass().getName());
        } else if (factory instanceof HasAdvancedModeReportFactory) {
            boolean isAdvancedMode = ((HasAdvancedSettings) reportSettings).isInAdvancedMode().isBooleanTrue();
            settingsForm = isAdvancedMode ? ((HasAdvancedModeReportFactory) factory).getAdvancedReportSettingsForm() : factory.getReportSettingsForm();
            reportSettingsControls.enableSettingsModeToggle(isAdvancedMode);
        } else {
            settingsForm = (CEntityForm<ReportMetadata>) factory.getReportSettingsForm();
            reportSettingsControls.disableModeToggle();
        }
        settingsFormPanel.setWidget(settingsForm);
        settingsForm.populate(reportSettings);
    }

    private Widget createLoadPanel() {
        LayoutPanel panel = new LayoutPanel();
        panel.setSize("100%", "100%");

        Button loadButton;
        panel.add(loadButton = new Button(i18n.tr("Load")));
        panel.setWidgetTopHeight(loadButton, 80, Unit.PCT, 1.5, Unit.EM);
        panel.setWidgetLeftWidth(loadButton, 10, Unit.PCT, 5, Unit.EM);
        return panel;
    }
}
