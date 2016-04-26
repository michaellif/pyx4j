/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Nov 14, 2014
 * @author michaellif
 */
package com.pyx4j.site.client.backoffice.activity.prime;

import com.google.gwt.activity.shared.AbstractActivity;

import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView.IPrimePanePresenter;
import com.pyx4j.site.rpc.AppPlace;

public abstract class AbstractPrimeActivity<E extends IPrimePaneView<?>> extends AbstractActivity implements IPrimePanePresenter {

    private final E view;

    private final AppPlace place;

    public AbstractPrimeActivity(E view, AppPlace place) {
        assert (view != null);
        assert (place != null);

        this.view = view;
        this.place = place;
    }

    public E getView() {
        return view;
    }

    @Override
    public AppPlace getPlace() {
        return place;
    }

    /**
     * Called after View was added to Activity but before the view is attached to UI
     */
    protected void onStart() {

    }
}
