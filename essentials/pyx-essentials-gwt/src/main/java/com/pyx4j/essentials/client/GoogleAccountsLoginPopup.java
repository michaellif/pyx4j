/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-09-20
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.widgets.client.PopupWindow;

public class GoogleAccountsLoginPopup {

    private static Logger log = LoggerFactory.getLogger(GoogleAccountsLoginPopup.class);

    static {
        registerCallbacks();
    }

    public static void open() {
        PopupWindow.open("/login", "Login", 514, 468);
    }

    private static void registerCallbacks() {
        PopupWindow.addSelectionHandler(new SelectionHandler<String>() {

            @Override
            public void onSelection(SelectionEvent<String> event) {
                if ("loginCompleated".equals(event.getSelectedItem())) {
                    log.debug("Login Compleated");
                    ClientContext.obtainAuthenticationData(null, true, false);
                }
            }
        });
    };
}
