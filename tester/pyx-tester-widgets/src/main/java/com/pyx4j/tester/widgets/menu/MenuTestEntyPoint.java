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
 * Created on Mar 24, 2016
 * @author vlads
 */
package com.pyx4j.tester.widgets.menu;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.style.theme.WindowsPalette;
import com.pyx4j.widgets.client.style.theme.WindowsTheme;

public class MenuTestEntyPoint implements EntryPoint {

    @Override
    public void onModuleLoad() {
        StyleManager.installTheme(new WindowsTheme(), new WindowsPalette());
        
        VerticalPanel content = new VerticalPanel();

        {
            Button button = new Button("Simple Button");
            content.add(button);
        }
        {
            Button button = new Button("Menu Button");
            ButtonMenuBar menu = new ButtonMenuBar();
            menu.addItem(new MenuItem("Action One", createCommand("Action One")));
            menu.addItem(new MenuItem("Action Two", createCommand("Action Two")));
            button.setMenu(menu);
            content.add(button);
        }

        {
            Button button = new Button("Sub Menu Button");
            ButtonMenuBar menu = new ButtonMenuBar();
            menu.addItem(new MenuItem("Action One", createCommand("Action One")));

            MenuBar detailsMenu = new MenuBar(true);
            menu.addItem(new MenuItem("Details ...", detailsMenu));
            detailsMenu.addItem(new MenuItem("Detail One", createCommand("Detail One")));
            detailsMenu.addItem(new MenuItem("Detail Two", createCommand("Detail Two")));

            menu.addItem(new MenuItem("Action Two", createCommand("Action Two")));
            button.setMenu(menu);
            content.add(button);
        }

        RootPanel.get().add(content);

    }

    private static Command createCommand(final String message) {
        return new Command() {
            @Override
            public void execute() {
                MessageDialog.info(message);
            }
        };
    }

}
