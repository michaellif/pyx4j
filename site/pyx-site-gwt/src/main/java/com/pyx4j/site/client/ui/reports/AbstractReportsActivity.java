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

import java.io.Serializable;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.reports.ReportMetadata;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.services.reports.IReportsService;

public abstract class AbstractReportsActivity extends AbstractActivity implements IReportsView.Presenter {

    private final IReportsView view;

    private final IReportsService reportsService;

    public AbstractReportsActivity(IReportsService reportsService, IReportsView view, AppPlace place) {
        this.reportsService = reportsService;
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setReportSettings(null);
    }

    @Override
    public void apply(ReportMetadata settings) {
        reportsService.generateReport(new DefaultAsyncCallback<Serializable>() {

            @Override
            public void onSuccess(Serializable result) {
                view.setReportData(result);
            }

        }, settings);
    }

}
