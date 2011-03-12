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
package com.pyx4j.site.demo.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.pyx4j.site.demo.client.activity.SecondNavigActivity;

public class SecondNavigActivityMapper implements ActivityMapper {

    Provider<SecondNavigActivity> secondNavigActivityProvider;

    @Inject
    public SecondNavigActivityMapper(final Provider<SecondNavigActivity> secondNavigActivityProvider) {
        super();
        this.secondNavigActivityProvider = secondNavigActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {
        return secondNavigActivityProvider.get().withPlace(place);
    }

}
