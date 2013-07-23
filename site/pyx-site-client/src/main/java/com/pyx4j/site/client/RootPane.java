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
 * Created on Apr 19, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.activity.AppActivityManager;
import com.pyx4j.site.client.activity.AppActivityMapper;

public abstract class RootPane<E extends Widget> implements IsWidget {

    private final E container;

    public RootPane(E container) {
        this.container = container;
        bind(new ActivityMapper() {

            @Override
            public Activity getActivity(Place place) {
                onPlaceChange(place);
                return null;
            }

        }, new SimplePanel());
    }

    protected abstract void onPlaceChange(Place place);

    @Override
    public E asWidget() {
        return container;
    }

    protected void bind(ActivityMapper mapper, AcceptsOneWidget widget) {
        ActivityManager activityManager = new ActivityManager(mapper, AppSite.getEventBus());
        activityManager.setDisplay(widget);
    }

    protected void bind(AppActivityMapper mapper, AcceptsOneWidget widget) {
        AppActivityManager activityManager = new AppActivityManager(mapper, AppSite.getEventBus());
        activityManager.setDisplay(widget);
    }

}
