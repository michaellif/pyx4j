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

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

public abstract class AbstractReportsView implements IReportsView {

    private Presenter presenter;

    private final FormFlexPanel panel;

    private CEntityForm<ReportSettings> settingsForm;

    public AbstractReportsView() {
        panel = new FormFlexPanel();
        panel.setSize("100%", "100%");
        settingsForm = null;
        presenter = null;

    }

    @Override
    public <E extends ReportSettings> void setReportSettings(E reportSettings) {
        if (settingsForm != null) {
            panel.remove(settingsForm);
        }
        settingsForm = (CEntityForm<ReportSettings>) getReportSettingsForm(reportSettings);
        if (settingsForm != null) {
            settingsForm.initContent();
            panel.setWidget(1, 0, settingsForm);
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

    public abstract CEntityForm<? extends ReportSettings> getReportSettingsForm(ReportSettings reportSettings);

}
