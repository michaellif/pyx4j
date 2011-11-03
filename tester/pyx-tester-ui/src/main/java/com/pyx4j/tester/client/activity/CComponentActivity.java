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
 * @version $Id$
 */
package com.pyx4j.tester.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.tester.client.TesterSite;
import com.pyx4j.tester.client.TesterVeiwFactory;
import com.pyx4j.tester.client.ui.event.CComponentBrowserEvent;
import com.pyx4j.tester.client.ui.event.CComponentBrowserHandler;
import com.pyx4j.tester.client.view.CComponentView;
import com.pyx4j.tester.client.view.CComponentView.ConsolePresenter;

public class CComponentActivity extends AbstractActivity implements ConsolePresenter {

    private final CComponentView view;

    public CComponentActivity(Place place) {
        this.view = (CComponentView) TesterVeiwFactory.retreive(CComponentView.class);
        withPlace(place);
        TesterSite.getEventBus().addHandler(CComponentBrowserEvent.getType(), new CComponentBrowserHandler() {

            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public void onBrowseEntity(CComponentBrowserEvent event) {

                final CComponent<?> component = event.getComponent();
                view.show(event.getComponent());

                component.addPropertyChangeHandler(new PropertyChangeHandler() {
                    @Override
                    public void onPropertyChange(PropertyChangeEvent event) {
                        view.show(component);
                    }
                });

                if (component instanceof CEditableComponent) {
                    CEditableComponent<?, ?> editableComponent = (CEditableComponent<?, ?>) component;
                    editableComponent.addValueChangeHandler(new ValueChangeHandler() {
                        @Override
                        public void onValueChange(ValueChangeEvent event) {
                            view.show(component);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {
        container.setWidget(view);
        view.show(null);
    }

    public CComponentActivity withPlace(Place place) {
        view.setPresenter(this);
        return this;
    }

}
