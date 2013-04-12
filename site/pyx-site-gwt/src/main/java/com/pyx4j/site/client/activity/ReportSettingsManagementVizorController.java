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
 * Created on Apr 12, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.activity;

import java.util.List;

import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.site.client.ui.reports.IReportsView;
import com.pyx4j.site.client.ui.reports.ReportSettingsManagementVizor;

public class ReportSettingsManagementVizorController extends AbstractVisorController {

    private final ReportSettingsManagementVizor visor;

    public ReportSettingsManagementVizorController(IPane parentView, final IReportsView.Presenter presenter) {
        super(parentView);
        visor = new ReportSettingsManagementVizor(this) {

            @Override
            public void onLoadRequest(String selectedReportSettingsId) {
                presenter.loadSettings(selectedReportSettingsId);
            }

            @Override
            public void onDeleteRequest(String selectedReportSettingsId) {
                presenter.deleteSettings(selectedReportSettingsId);
            }
        };
        visor.setAvailableReportSettingsIds(null);

    }

    @Override
    public void show() {
        getParentView().showVisor(visor);
    }

    public void setAvailableReportSettingsIds(List<String> reportSettingsIds) {
        visor.setAvailableReportSettingsIds(reportSettingsIds);
    }

}
