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
 * Created on May 16, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.ria.demo.client.proving;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.ria.demo.client.DemoApplication;

public class ProvingManager {

    public static void createMenu(MenuBar provingMenuBar, final DemoApplication app) {

        provingMenuBar.addItem(new MenuItem("Logger", new Command() {
            @Override
            public void execute() {
                app.openView(new LogRangeView());
            }
        }));

        provingMenuBar.addItem(new MenuItem("StatusBar", new Command() {
            @Override
            public void execute() {
                app.openView(new StatusBarProvingView());
            }
        }));

        provingMenuBar.addItem(new MenuItem("ui - Button Range", new Command() {
            @Override
            public void execute() {
                app.openView(new ButtonRangeView());
            }
        }));

    }
}
