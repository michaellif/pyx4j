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
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.ria.client.view.AbstractView;
import com.pyx4j.widgets.client.GlassPanel;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.theme.GrayTheme;
import com.pyx4j.widgets.client.style.theme.WindowsTheme;
import com.pyx4j.widgets.client.tabpanel.Tab;

public class EntityView extends AbstractView {

    public EntityView(String tabTitle, ImageResource image) {
        super();

        addPage(new Tab(createPageContent("page 1"), "page 1", null));
        addPage(new Tab(createPageContent("page 2"), "page 2", null));
        addPage(new Tab(createPageContent("page 3"), "page 3", null));
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

    private ScrollPanel createPageContent(String title) {
        ScrollPanel contentPane = new ScrollPanel();

        VerticalPanel mainPane = new VerticalPanel();

        contentPane.setWidget(mainPane);

        mainPane.add(new Label("ContentPane" + title));
        mainPane.add(new Label("ContentPane" + title));
        mainPane.add(new Label("ContentPane" + title));
        mainPane.add(new Label("ContentPaneContentPaneContentPaneContentPaneContentPaneContentPaneContentPane" + title));
        mainPane.add(new Label("ContentPane" + title));
        mainPane.add(new Label("ContentPane" + title));
        mainPane.add(new Label("ContentPane" + title));
        mainPane.add(new Label("ContentPane" + title));
        mainPane.add(new Label("ContentPane" + title));
        mainPane.add(new Label("ContentPane" + title));
        mainPane.add(new Label("ContentPane" + title));
        mainPane.add(new Label("ContentPane" + title));
        mainPane.add(new Label("ContentPane" + title));

        return contentPane;
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

            addItem(RiaEntityImageBundle.INSTANCE.image(), null, "Add");
            addItem(RiaEntityImageBundle.INSTANCE.image(), null, "Save");
            addItem(RiaEntityImageBundle.INSTANCE.image(), new Command() {
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

            addItem(RiaEntityImageBundle.INSTANCE.image(), new Command() {
                @Override
                public void execute() {
                }
            }, "Theme Editor");

            addSeparator();

            addItem("Progress", new Command() {
                @Override
                public void execute() {
                }
            });

            addItem("Glass ON (5 sec)", new Command() {
                @Override
                public void execute() {
                    GlassPanel.show();
                    Timer timer = new Timer() {
                        @Override
                        public void run() {
                            GlassPanel.hide();
                        }
                    };
                    timer.schedule(1000 * 5);
                }
            });

        }
    }
}
