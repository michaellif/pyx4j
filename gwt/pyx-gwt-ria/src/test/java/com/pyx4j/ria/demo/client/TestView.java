/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.ria.client.app.AbstractView;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.menu.ActionMenuItem;
import com.pyx4j.widgets.client.menu.Menu;
import com.pyx4j.widgets.client.menu.PopupMenuBar;
import com.pyx4j.widgets.client.menu.SubMenuItem;

public class TestView extends AbstractView {

    private final FlowPanel toolbarPane;

    public TestView(String label) {
        super(new VerticalPanel(), label, ImageFactory.getImages().image());
        VerticalPanel contentPane = (VerticalPanel) getContentPane();

        contentPane.add(new Label("ContentPane" + label));
        contentPane.add(new Label("ContentPane" + label));
        contentPane.add(new Label("ContentPane" + label));
        contentPane.add(new Label("ContentPane" + label));
        contentPane.add(new Label("ContentPane" + label));
        contentPane.add(new Label("ContentPane" + label));
        contentPane.add(new Label("ContentPane" + label));
        contentPane.add(new Label("ContentPane" + label));
        //        contentPane.setPixelSize(200, 200);

        toolbarPane = new FlowPanel();

        for (int i = 0; i < 1; i++) {
            Button panel = new Button(label);
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

    @Override
    public Widget getFooterPane() {
        return new Label("FooterPane" + getTitle());
    }

    @Override
    public Widget getToolbarPane() {
        return toolbarPane;
    }

    @Override
    public PopupMenuBar getMenu() {
        PopupMenuBar menuBar = new PopupMenuBar();
        menuBar.setAutoOpen(true);
        ActionMenuItem item1 = new ActionMenuItem(getTitle() + "1", true, new Command() {
            @Override
            public void execute() {
                //TODO Logger.debug(getTitle() + "1 clicked");
            }
        });
        ActionMenuItem item2 = new ActionMenuItem(getTitle() + "2", true, new Command() {

            @Override
            public void execute() {
                //TODO Logger.debug(getTitle() + "2 clicked");
            }
        });
        ActionMenuItem item3 = new ActionMenuItem(getTitle() + "3", true, new Command() {

            @Override
            public void execute() {
                //TODO Logger.debug(getTitle() + "3 clicked");
            }
        });
        Menu subMenuBar = new Menu(true);

        ActionMenuItem item11 = new ActionMenuItem(getTitle() + "11", true, new Command() {
            @Override
            public void execute() {
                //TODO Logger.debug(getTitle() + "11 clicked");
            }
        });
        ActionMenuItem item12 = new ActionMenuItem(getTitle() + "12", true, new Command() {

            @Override
            public void execute() {
                //TODO Logger.debug(getTitle() + "12 clicked");
            }
        });
        ActionMenuItem item13 = new ActionMenuItem(getTitle() + "13", true, new Command() {

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
        menuBar.addItem(new SubMenuItem("subMenu", subMenuBar));
        menuBar.addSeparator();
        menuBar.addItem(item3);

        return menuBar;
    }
}
