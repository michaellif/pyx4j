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
 * Created on Nov 2, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.tester.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.tester.client.TesterVeiwFactory;
import com.pyx4j.tester.client.view.dashboard.DashboardLayoutView;

public class DashboardLayoutActivity extends AbstractActivity {

    private final DashboardLayoutView view;

    public DashboardLayoutActivity(Place place) {
        this.view = (DashboardLayoutView) TesterVeiwFactory.retreive(DashboardLayoutView.class);
        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {
        container.setWidget(view);
    }

    public DashboardLayoutActivity withPlace(Place place) {
        return this;
    }
}
