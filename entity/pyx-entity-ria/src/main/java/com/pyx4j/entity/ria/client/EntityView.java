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
 * Created on Apr 28, 2009
 * @author michaellif
 * @version $Id: TestView.java 7492 2010-11-13 17:57:45Z michaellif $
 */
package com.pyx4j.entity.ria.client;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.ria.client.crud.EntityDetailsPart;
import com.pyx4j.entity.ria.client.crud.EntityDetailsTab;
import com.pyx4j.ria.client.view.AbstractView;
import com.pyx4j.widgets.client.tabpanel.Tab;

public class EntityView extends AbstractView {

    public EntityView(String tabTitle, ImageResource image) {
        super();

        setTabTitle(tabTitle);
        setTabImage(image);

        HorizontalPanel toolbarPanel = new HorizontalPanel();
        toolbarPanel.setWidth("100%");

        Toolbar toolbar = new Toolbar();
        toolbarPanel.add(toolbar);

        ImageResource viewMenu = RiaEntityImageBundle.INSTANCE.viewMenu();
        MenuBar actionsMenu = getMenu();
        MenuItem menuButtonItem = new MenuItem("<img src=" + viewMenu.getURL() + " ' alt=''>", true, actionsMenu);
        menuButtonItem.removeStyleName("gwt-MenuItem");
        menuButtonItem.getElement().getStyle().setCursor(Cursor.POINTER);

        MenuBar menuButtonBar = new MenuBar() {
            @Override
            public void onBrowserEvent(Event event) {
                if (event.getTypeInt() == Event.ONCLICK) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                super.onBrowserEvent(event);
            }
        };
        menuButtonBar.addItem(menuButtonItem);

        //toolbarPanel.add(menuButtonBar);

        setToolbarPane(toolbarPanel);
    }

    public MenuBar getMenu() {
        MenuBar menuBar = new MenuBar(true);
        menuBar.setAutoOpen(true);
        MenuItem item1 = new MenuItem(getTabTitle() + "1", true, new Command() {
            @Override
            public void execute() {
                //TODO Logger.debug(getTitle() + "1 clicked");
            }
        });
        MenuItem item2 = new MenuItem(getTabTitle() + "2", true, new Command() {

            @Override
            public void execute() {
                //TODO Logger.debug(getTitle() + "2 clicked");
            }
        });
        MenuItem item3 = new MenuItem(getTabTitle() + "3", true, new Command() {

            @Override
            public void execute() {
                //TODO Logger.debug(getTitle() + "3 clicked");
            }
        });
        MenuBar subMenuBar = new MenuBar(true);

        MenuItem item11 = new MenuItem(getTabTitle() + "11", true, new Command() {
            @Override
            public void execute() {
                //TODO Logger.debug(getTitle() + "11 clicked");
            }
        });
        MenuItem item12 = new MenuItem(getTabTitle() + "12", true, new Command() {

            @Override
            public void execute() {
                //TODO Logger.debug(getTitle() + "12 clicked");
            }
        });
        MenuItem item13 = new MenuItem(getTabTitle() + "13", true, new Command() {

            @Override
            public void execute() {
                //TODO Logger.debug(getTitle() + "13 clicked");
            }
        });
        subMenuBar.addItem(item11);
        subMenuBar.addItem(item12);
        subMenuBar.addItem(item13);

        menuBar.addItem(item1);
        menuBar.addItem(item2);
        menuBar.addSeparator();
        menuBar.addItem(new MenuItem("subMenu", subMenuBar));
        menuBar.addSeparator();
        menuBar.addItem(item3);

        return menuBar;
    }

    class Toolbar extends com.pyx4j.ria.client.Toolbar {

        public Toolbar() {

            addItem(RiaEntityImageBundle.INSTANCE.save(), "Save", new Command() {
                @Override
                public void execute() {
                    //TODO Logger.error("printAction", new Error("Test error"));
                }
            });
            addItem(RiaEntityImageBundle.INSTANCE.print(), "Print", new Command() {
                @Override
                public void execute() {
                    //TODO Logger.error("printAction", new Error("Test error"));
                }
            });

        }
    }

    public void addDetailsPart(EntityDetailsPart<?> part) {

        for (EntityDetailsTab<?> tab : part.getTabs()) {
            VerticalPanel page = new VerticalPanel();
            Label title = new Label(tab.getTitle());
            title.getElement().getStyle().setFontSize(1.5, Unit.EM);
            title.getElement().getStyle().setMargin(2, Unit.PX);
            title.getElement().getStyle().setMarginLeft(20, Unit.PX);
            page.add(title);
            page.add(tab.initNativeComponent());
            addPage(new Tab(new ScrollPanel(page), tab.getTitle(), null));
        }
    }
}
