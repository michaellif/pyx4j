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
 * Created on Apr 29, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.ria.client.view.IView;
import com.pyx4j.ria.client.view.ViewMemento;
import com.pyx4j.widgets.client.tabpanel.ITab;
import com.pyx4j.widgets.client.tabpanel.TabBar;
import com.pyx4j.widgets.client.tabpanel.TabPanelModel;

public class FolderSectionPanel extends SectionPanel implements BeforeSelectionHandler<ITab>, SelectionHandler<ITab>, CloseHandler<ITab> {

    private final List<IView> views = new ArrayList<IView>();

    private IView currentView;

    private final TabPanelModel tabPanel;

    private final HorizontalPanel toolbarHolderPane;

    public FolderSectionPanel() {

        addStyleDependentName("folder");

        tabPanel = new TabPanelModel();

        tabPanel.addBeforeSelectionHandler(this);
        tabPanel.addSelectionHandler(this);
        tabPanel.addCloseHandler(this);

        TabBar tabBar = tabPanel.getTabBar();

        HorizontalPanel headerPane = new HorizontalPanel();

        headerPane.add(tabBar);
        headerPane.setCellWidth(tabBar, "100%");

        Image minimizeFolderImage = new Image(ImageFactory.getImages().minimizeFolder());
        minimizeFolderImage.setHeight("100%");

        //Fix for Chrome
        SimplePanel imageHolder = new SimplePanel();
        imageHolder.setSize("13px", "20px");
        DOM.setStyleAttribute(imageHolder.getElement(), "margin", "3px");

        imageHolder.add(minimizeFolderImage);
        headerPane.add(imageHolder);
        headerPane.setCellWidth(imageHolder, "100%");
        headerPane.setCellHeight(imageHolder, "100%");

        headerPane.setWidth("100%");
        setHeader1Pane(headerPane);

        toolbarHolderPane = new HorizontalPanel();
        toolbarHolderPane.setWidth("100%");

        setHeader2Pane(toolbarHolderPane);

        DeckPanel contentDeck = tabPanel.getDeck();
        setContentPane(contentDeck);

        //        menuButton = new Image(ImageFactory.getImages().viewMenu());
        //
        //        DOM.setStyleAttribute(menuButton.getElement(), "cursor", "pointer");
        //        DOM.setStyleAttribute(menuButton.getElement(), "cursor", "hand");
        //        DOM.setStyleAttribute(menuButton.getElement(), "margin", "3");
        //        menuButton.addClickHandler(new ClickHandler() {
        //            @Override
        //            public void onClick(ClickEvent event) {
        //                menu.show(menuButton);
        //            }
        //        });

    }

    public void addView(IView view, boolean closable) {
        views.add(view);
        view.setFolder(this);
        tabPanel.insert(view, tabPanel.size(), closable);
        if (views.size() == 1) {
            tabPanel.select(0);
        }
    }

    public boolean removeView(IView view, boolean forced) {
        if (tabPanel.remove(view, forced)) {
            views.remove(view);
            return true;
        }
        return false;
    }

    public void showView(IView view) {
        tabPanel.select(view);
    }

    public IView getCurrentView() {
        return currentView;
    }

    private void showToolbar(IView view) {
        if (toolbarHolderPane.getWidgetCount() > 0) {
            toolbarHolderPane.clear();
        }
        if (view != null) {
            Widget toolbar = view.getToolbarPane();
            if (toolbar != null) {
                toolbarHolderPane.add(toolbar);
            }
            MenuBar menu = view.getMenu();
            if (menu != null) {
                MenuBar menuButtonBar = new MenuBar();

                //TODO didn't find a proper way to create menu with icon
                MenuItem menuButtonItem = new MenuItem("<img src=images/view-menu.png ' alt=''>", true, menu);
                menuButtonBar.addItem(menuButtonItem);

                toolbarHolderPane.add(menuButtonBar);
                toolbarHolderPane.setCellWidth(menuButtonBar, "1px");
            }
        }
    }

    @Override
    public void onBeforeSelection(BeforeSelectionEvent<ITab> event) {
        IView currentView = getCurrentView();
        if (currentView != null) {
            ViewMemento viewMemento = currentView.getViewMemento();
            viewMemento.setHorizontalScrollPosition(getHorizontalScrollPosition());
            viewMemento.setVerticalScrollPosition(getVerticalScrollPosition());
        }
    }

    @Override
    public void onSelection(SelectionEvent<ITab> event) {
        IView view = (IView) event.getSelectedItem();
        showToolbar(view);
        currentView = view;
        //onResize();
        ViewMemento viewMemento = currentView.getViewMemento();
        setHorizontalScrollPosition(viewMemento.getHorizontalScrollPosition());
        int verticalScrollPosition = viewMemento.getVerticalScrollPosition();
        if (verticalScrollPosition == Integer.MAX_VALUE) {
            scrollToBottom();
        } else {
            setVerticalScrollPosition(verticalScrollPosition);
        }

    }

    @Override
    public void onClose(CloseEvent<ITab> event) {
        views.remove(event.getTarget());
        if (views.size() == 0) {
            showToolbar(null);
            //onResize();
        }
    }

}
