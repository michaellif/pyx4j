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
 * @version $Id$
 */
package com.pyx4j.ria.demo.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.ria.client.view.AbstractView;
import com.pyx4j.widgets.client.Button;

public class TestView extends AbstractView {

    private final FlowPanel toolbarPane;

    public TestView(String tabTitle) {
        super();

        ScrollPanel contentPane = new ScrollPanel();
        setContentPane(contentPane);
        setTabTitle(tabTitle);
        setTabImage(ImageFactory.getImages().image());

        VerticalPanel mainPane = new VerticalPanel();

        contentPane.setWidget(mainPane);

        mainPane.add(new Label("ContentPane" + tabTitle));
        mainPane.add(new Label("ContentPane" + tabTitle));
        mainPane.add(new Label("ContentPane" + tabTitle));
        mainPane.add(new Label("ContentPaneContentPaneContentPaneContentPaneContentPaneContentPaneContentPane" + tabTitle));
        mainPane.add(new Label("ContentPane" + tabTitle));
        mainPane.add(new Label("ContentPane" + tabTitle));
        mainPane.add(new Label("ContentPane" + tabTitle));
        mainPane.add(new Label("ContentPane" + tabTitle));
        mainPane.add(new Label("ContentPane" + tabTitle));
        mainPane.add(new Label("ContentPane" + tabTitle));
        mainPane.add(new Label("ContentPane" + tabTitle));
        mainPane.add(new Label("ContentPane" + tabTitle));
        mainPane.add(new Label("ContentPane" + tabTitle));

        toolbarPane = new FlowPanel();

        for (int i = 0; i < 1; i++) {
            Button panel = new Button(tabTitle);
            toolbarPane.add(panel);
        }

        for (int i = 0; i < 3; i++) {
            Button panel = new Button(new Image(ImageFactory.getImages().image()));
            toolbarPane.add(panel);
        }

        for (int i = 0; i < 3; i++) {
            Button panel = new Button(new Image(ImageFactory.getImages().image()), " B b b");
            toolbarPane.add(panel);
        }
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
}
