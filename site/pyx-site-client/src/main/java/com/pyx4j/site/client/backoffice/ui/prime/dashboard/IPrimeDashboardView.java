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
 * Created on Sep 29, 2014
 * @author michaellif
 */
package com.pyx4j.site.client.backoffice.ui.prime.dashboard;

import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView;
import com.pyx4j.site.client.backoffice.ui.prime.dashboard.IPrimeDashboardView.IPrimeDashboardPresenter;

import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView.IPrimePanePresenter;

public interface IPrimeDashboardView extends IPrimePaneView<IPrimeDashboardPresenter> {

    public interface IPrimeDashboardPresenter extends IPrimePanePresenter {

        void save();

        void print();

    }

    void setReadOnly(boolean isReadOnly);
}