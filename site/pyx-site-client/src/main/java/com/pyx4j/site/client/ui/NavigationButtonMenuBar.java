/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Feb 8, 2016
 * @author vlads
 */
package com.pyx4j.site.client.ui;

import com.pyx4j.security.annotations.ActionId;
import com.pyx4j.site.client.NavigationCommand;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;
import com.pyx4j.widgets.client.Button.SecureMenuItem;

public class NavigationButtonMenuBar extends ButtonMenuBar {

    public NavigationButtonMenuBar() {
    }

    public SecureMenuItem addItem(String text, AppPlace place, Class<? extends ActionId> actionId) {
        return addItem(text, new NavigationCommand(place), actionId);
    }
}
