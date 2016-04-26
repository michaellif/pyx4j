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

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeComposition;
import com.pyx4j.gwt.commons.concerns.HasSecureConcernedChildren;
import com.pyx4j.gwt.commons.ui.HorizontalPanel;
import com.pyx4j.gwt.commons.ui.SplitLayoutPanel;
import com.pyx4j.gwt.commons.ui.VerticalPanel;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.security.annotations.ActionId;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.security.shared.AccessControlContext;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.security.shared.Permission;
import com.pyx4j.security.shared.ProtectionDomain;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.CheckBox;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.MenuBar;
import com.pyx4j.widgets.client.MenuItem;
import com.pyx4j.widgets.client.dialog.DialogTheme;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.style.theme.MenuBarTheme;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;
import com.pyx4j.widgets.client.style.theme.WindowsPalette;

@SuppressWarnings("serial")
public class MenuTestEntyPoint implements EntryPoint, HasSecureConcernedChildren {

    class ActionOne implements ActionId {

    }

    class ActionTwo implements ActionId {

    }

    class DetailOne implements ActionId {

    }

    class DetailTwo implements ActionId {

    }

    class SomeAccessControlContext implements AccessControlContext {

        @Override
        public boolean implies(ProtectionDomain<?> domain) {
            return false;
        }

    }

    private static final boolean all = false;

    @Override
    public void onModuleLoad() {
        StyleManager.installTheme(
                new ThemeComposition(//
                        new WidgetsTheme(), new DialogTheme(), new MenuBarTheme()), //
                new WindowsPalette());

        SplitLayoutPanel content = new SplitLayoutPanel();
        content.getStyle().setProperty("border", "3px solid #e7e7e7");
        content.setHeight("500px");
        content.setWidth("800px");

        ClientLogger.setDebugOn(true);

        if (all) {
            Button button = new Button("Menu Button");
            MenuBar menu = new MenuBar();
            menu.addItem(new MenuItem("Action One", createCommand("Action One")));
            menu.addItem(new MenuItem("Action Two", createCommand("Action Two")));
            button.setMenu(menu);

            HorizontalPanel hPanel = new HorizontalPanel();
            hPanel.add(button);
            content.addWest(hPanel, 200);
        }

        if (all) {
            Button button = new Button("Secure");
            MenuBar menu = new MenuBar();
            menu.addItem(new MenuItem("Action One", createCommand("Action One"), ActionOne.class));
            menu.addItem(new MenuItem("Action Two", createCommand("Action Two"), ActionTwo.class));
            button.setMenu(menu);

            HorizontalPanel hPanel = new HorizontalPanel();
            hPanel.add(button);
            content.addEast(hPanel, 200);

            addSecureConcern(button);
        }

        {
            VerticalPanel vPanel = new VerticalPanel();
            content.addSouth(vPanel, 200);

            CheckBox actionOne = new CheckBox("Visible ActionOne");
            vPanel.add(actionOne);

            CheckBox actionTwo = new CheckBox("Visible ActionTwo");
            vPanel.add(actionTwo);

            CheckBox detailOne = new CheckBox("Visible DetailOne");
            vPanel.add(detailOne);

            CheckBox detailTwo = new CheckBox("Visible DetailTwo");
            vPanel.add(detailTwo);

            Button button = new Button("Apply Security");
            vPanel.add(button);
            button.setCommand(new Command() {

                @Override
                public void execute() {
                    Set<Permission> permissions = new HashSet<>();
                    if (actionOne.getValue()) {
                        permissions.add(new ActionPermission(ActionOne.class));
                    }

                    if (actionTwo.getValue()) {
                        permissions.add(new ActionPermission(ActionTwo.class));
                    }

                    if (detailOne.getValue()) {
                        permissions.add(new ActionPermission(DetailOne.class));
                    }

                    if (detailTwo.getValue()) {
                        permissions.add(new ActionPermission(DetailTwo.class));
                    }

                    ClientSecurityController.instance().authorize(null, permissions);
                    setSecurityContext(new SomeAccessControlContext());
                }
            });

        }

        {
            Button button = new Button("Secure Sub Menu Button");
            MenuBar menu = new MenuBar();
            menu.setTitle("Button MenuBar");
            menu.addItem(new MenuItem("Action One", createCommand("Action One"), ActionOne.class));

            MenuBar detailsMenu = new MenuBar(true);
            detailsMenu.setTitle("Details MenuBar");
            menu.addItem(new MenuItem("Details ...", detailsMenu));
            detailsMenu.addItem(new MenuItem("Detail One", createCommand("Detail One"), DetailOne.class));
            detailsMenu.addItem(new MenuItem("Detail Two", createCommand("Detail Two"), DetailTwo.class));

            menu.addItem(new MenuItem("Action Two", createCommand("Action Two"), ActionTwo.class));
            button.setMenu(menu);

            HorizontalPanel hPanel = new HorizontalPanel();
            hPanel.add(button);
            content.add(hPanel);

            addSecureConcern(button);
        }

        RootPanel.get().add(content);

        if (all) {
            HorizontalPanel hPanel = new HorizontalPanel();
            {
                Button button = new Button("Button");
                MenuBar menu = new MenuBar();
                menu.addItem(new MenuItem("Action One", createCommand("Action One")));
                menu.addItem(new MenuItem("Action Two", createCommand("Action Two")));
                button.setMenu(menu);
                hPanel.add(button);
            }
            {
                Button button = new Button("Button");
                MenuBar menu = new MenuBar();
                menu.addItem(new MenuItem("Action One", createCommand("Action One")));
                menu.addItem(new MenuItem("Action Two", createCommand("Action Two")));
                button.setMenu(menu);
                hPanel.add(button);
            }

            RootPanel.get().add(hPanel);
        }

        if (all) {
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
                MenuBar menu = new MenuBar();
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

    private SecureConcernsHolder secureConcernsHolder = new SecureConcernsHolder();

    @Override
    public SecureConcernsHolder secureConcernsHolder() {
        return secureConcernsHolder;
    }

}
