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
 * @version $Id$
 */
package com.pyx4j.site.client.ui.reports;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Composite;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

public class ReportSettingsFormControlPanel extends Composite {

    private static final I18n i18n = I18n.get(ReportSettingsFormControlPanel.class);

    private Button apply;

    private Anchor advancedModeToggle;

    private ApplyCallback<ReportSettings> applyCallback;

    private IReportSettingsForm<ReportSettings> settingsForm;

    private boolean isAdvanced;

    public ReportSettingsFormControlPanel() {

        isAdvanced = false;

        Toolbar controlPanel = new Toolbar();
        controlPanel.setStyleName(DefaultSiteCrudPanelsTheme.StyleName.FooterToolbar.name());

        controlPanel.addItem(advancedModeToggle = new Anchor(i18n.tr("advanced"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setAdvanced(!isAdvanced);
            }
        }), true);

        controlPanel.addItem(apply = new Button(i18n.tr("Apply"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (applyCallback != null & settingsForm != null) {
                    applyCallback.apply(settingsForm.getValue());
                }
            }
        }), true);

        initWidget(controlPanel);
    }

    public final void setApplyCallback(ApplyCallback<ReportSettings> applyCallback) {
        this.applyCallback = applyCallback;
    }

    public final void attachSettingsForm(IReportSettingsForm<ReportSettings> settingsForm) {
        this.settingsForm = settingsForm;
        this.advancedModeToggle.setVisible(settingsForm != null && settingsForm instanceof IAdvancedReportSettingsForm);
        setAdvanced(false);
    }

    private void setAdvanced(boolean isAdvanced) {
        this.isAdvanced = isAdvanced;
        advancedModeToggle.setHTML(new SafeHtmlBuilder().appendEscaped(isAdvanced ? i18n.tr("hide") : i18n.tr("advanced")).toSafeHtml());
        if (settingsForm != null) {
            ((IAdvancedReportSettingsForm<?>) settingsForm).setAdvancedMode(isAdvanced);
        }
    }
}
