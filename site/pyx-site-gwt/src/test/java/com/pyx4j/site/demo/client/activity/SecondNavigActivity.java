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
 * Created on 2011-03-12
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.demo.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.site.demo.client.ui.SecondNavigView;

public class SecondNavigActivity extends AbstractActivity implements SecondNavigView.Presenter {

    private final SecondNavigView view;

    private final PlaceController placeController;

    @Inject
    public SecondNavigActivity(SecondNavigView view, PlaceController placeController) {
        this.view = view;
        this.placeController = placeController;
        view.setPresenter(this);
    }

    public SecondNavigActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void navigTo(Place place) {
        placeController.goTo(place);
    }

}
