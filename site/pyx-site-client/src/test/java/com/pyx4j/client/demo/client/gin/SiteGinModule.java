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
 * Created on Jan 29, 2011
 * @author Misha
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.pyx4j.client.demo.client.gin;

import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Singleton;

import com.pyx4j.client.demo.client.activity.GoodbyeActivity;
import com.pyx4j.client.demo.client.activity.HelloActivity;
import com.pyx4j.client.demo.client.mvp.AppActivityMapper;
import com.pyx4j.client.demo.client.ui.GoodbyeView;
import com.pyx4j.client.demo.client.ui.GoodbyeViewImpl;
import com.pyx4j.client.demo.client.ui.HelloView;
import com.pyx4j.client.demo.client.ui.HelloViewImpl;

public class SiteGinModule extends AbstractGinModule {

    @Override
    protected void configure() {

        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(PlaceController.class).to(InjectablePlaceController.class).in(Singleton.class);
        bind(ActivityMapper.class).to(AppActivityMapper.class).in(Singleton.class);

        bind(GoodbyeView.class).to(GoodbyeViewImpl.class).in(Singleton.class);
        bind(HelloView.class).to(HelloViewImpl.class).in(Singleton.class);
        bind(HelloActivity.class);
        bind(GoodbyeActivity.class);

    }
}