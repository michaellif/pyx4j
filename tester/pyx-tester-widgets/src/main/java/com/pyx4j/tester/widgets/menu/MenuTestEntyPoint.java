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

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeComposition;
import com.pyx4j.gwt.commons.ui.HorizontalPanel;
import com.pyx4j.gwt.commons.ui.SplitLayoutPanel;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.dialog.DialogTheme;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.style.theme.MenuBarTheme;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;
import com.pyx4j.widgets.client.style.theme.WindowsPalette;

public class MenuTestEntyPoint implements EntryPoint {

    @Override
    public void onModuleLoad() {
        StyleManager.installTheme(
                new ThemeComposition(//
                        new WidgetsTheme(), new DialogTheme(), new MenuBarTheme()), //
                new WindowsPalette());

        SplitLayoutPanel content = new SplitLayoutPanel();
        content.getStyle().setProperty("border", "3px solid #e7e7e7");
        content.setHeight("500px");
        content.setWidth("500px");

        {
            Button button = new Button("Menu Button");
            ButtonMenuBar menu = new ButtonMenuBar();
            menu.addItem(new MenuItem("Action One", createCommand("Action One")));
            menu.addItem(new MenuItem("Action Two", createCommand("Action Two")));
            button.setMenu(menu);

            HorizontalPanel hPanel = new HorizontalPanel();
            hPanel.add(button);
            content.addWest(hPanel, 200);
        }

        {
            Button button = new Button("Menu Button");
            ButtonMenuBar menu = new ButtonMenuBar();
            menu.addItem(new MenuItem("Action One", createCommand("Action One")));
            menu.addItem(new MenuItem("Action Two", createCommand("Action Two")));
            button.setMenu(menu);

            HorizontalPanel hPanel = new HorizontalPanel();
            hPanel.add(button);
            content.addEast(hPanel, 200);
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

            HorizontalPanel hPanel = new HorizontalPanel();
            hPanel.add(button);
            content.add(hPanel);
        }

        RootPanel.get().add(content);

        {
            HorizontalPanel hPanel = new HorizontalPanel();
            {
                Button button = new Button("Button");
                ButtonMenuBar menu = new ButtonMenuBar();
                menu.addItem(new MenuItem("Action One", createCommand("Action One")));
                menu.addItem(new MenuItem("Action Two", createCommand("Action Two")));
                button.setMenu(menu);
                hPanel.add(button);
            }
            {
                Button button = new Button("Button");
                ButtonMenuBar menu = new ButtonMenuBar();
                menu.addItem(new MenuItem("Action One", createCommand("Action One")));
                menu.addItem(new MenuItem("Action Two", createCommand("Action Two")));
                button.setMenu(menu);
                hPanel.add(button);
            }

            RootPanel.get().add(hPanel);
        }

        {
            HorizontalPanel hPanel = new HorizontalPanel();
            {
                Button button = new Button(ImageFactory.getImages().action());
                hPanel.add(button);
            }
            {
                Button button = new Button(ImageFactory.getImages().action(), "Text");
                hPanel.add(button);
            }

            {
                Button button = new Button(ImageFactory.getImages().action(), "Actions");
                ButtonMenuBar menu = new ButtonMenuBar();
                menu.addItem(new MenuItem("Action One", createCommand("Action One")));
                menu.addItem(new MenuItem("Action Two", createCommand("Action Two")));
                button.setMenu(menu);
                hPanel.add(button);
            }

            RootPanel.get().add(hPanel);
        }
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
