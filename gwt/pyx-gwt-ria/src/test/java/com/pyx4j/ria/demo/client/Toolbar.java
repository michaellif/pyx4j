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
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.demo.client;

import com.google.gwt.user.client.Command;

import com.pyx4j.ria.client.style.ThemeEditor;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.gray.GrayTheme;
import com.pyx4j.widgets.client.style.window.WindowsTheme;

public class Toolbar extends com.pyx4j.ria.client.Toolbar {

    public Toolbar(final DemoApplication app) {

        addItem(ImageFactory.getImages().image(), app.getOpenCommand(), "Add");
        addItem(ImageFactory.getImages().image(), app.getSaveCommand(), "Save");
        addItem(ImageFactory.getImages().image(), new Command() {
            @Override
            public void execute() {
                //TODO Logger.error("printAction", new Error("Test error"));
            }
        }, "Print");
        addSeparator();

        addItem("L&F Win", new Command() {
            @Override
            public void execute() {
                StyleManger.installTheme(new WindowsTheme());
            }
        });

        addItem("L&F Gray", new Command() {
            @Override
            public void execute() {
                StyleManger.installTheme(new GrayTheme());
            }
        });

        addItem(ImageFactory.getImages().image(), new Command() {
            @Override
            public void execute() {
                app.openView(new ThemeEditor());
            }
        }, "Theme Editor");

        addSeparator();

        addItem("Progress", new Command() {
            @Override
            public void execute() {
                app.runProgressBar();
            }
        });
    }
}
