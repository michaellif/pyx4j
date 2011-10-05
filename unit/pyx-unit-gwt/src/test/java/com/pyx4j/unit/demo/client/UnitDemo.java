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
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.demo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.unit.client.ui.TestRunnerDialog;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.theme.WindowsPalette;
import com.pyx4j.widgets.client.style.theme.WindowsTheme;

public class UnitDemo implements EntryPoint {

    @Override
    public void onModuleLoad() {
        StyleManger.installTheme(new WindowsTheme(), new WindowsPalette());

        final Button startButton = new Button("Start Tests");
        RootPanel.get().add(startButton, 0, 0);
        startButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TestRunnerDialog.createAsync();
            }
        });
    }

}
