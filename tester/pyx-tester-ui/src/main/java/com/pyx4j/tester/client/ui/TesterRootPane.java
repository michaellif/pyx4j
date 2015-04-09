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
 * Created on Oct 6, 2011
 * @author michaellif
 */
package com.pyx4j.tester.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.RootPane;
import com.pyx4j.tester.client.TesterSiteMap;
import com.pyx4j.tester.client.mvp.ConsoleActivityMapper;
import com.pyx4j.tester.client.mvp.MainActivityMapper;
import com.pyx4j.tester.client.mvp.NavigActivityMapper;

public class TesterRootPane extends RootPane<TesterLayoutPanel> implements IsWidget {

    public TesterRootPane() {
        super(new TesterLayoutPanel());

        bind(new NavigActivityMapper(), asWidget().getNavigDisplay());
        bind(new MainActivityMapper(), asWidget().getContentDisplay());
        bind(new ConsoleActivityMapper(), asWidget().getConsoleDisplay());
    }

    @Override
    protected void onPlaceChange(Place place) {
        if (place instanceof TesterSiteMap.FormTester) {
            asWidget().setConsoleVisible(true);
        } else {
            asWidget().setConsoleVisible(false);
        }
    }

}
