/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 29, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.menu.PopupMenuBar;
import com.pyx4j.widgets.client.tabpanel.ITab;
import com.pyx4j.widgets.client.tabpanel.TabBar;
import com.pyx4j.widgets.client.tabpanel.TabPanelModel;

//TODO restore scroll position on view selection change

public class FolderSectionPanel extends SectionPanel implements BeforeSelectionHandler<ITab>, SelectionHandler<ITab>, CloseHandler<ITab> {

    private final List<IView> views = new ArrayList<IView>();

    private IView currentView;

    private final TabPanelModel tabPanel;

    private final boolean closable = true;

    private final HorizontalPanel toolbarHolderPane;

    private final Image menuButton;

    private PopupMenuBar menu;

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

        menuButton = new Image(ImageFactory.getImages().viewMenu());

        DOM.setStyleAttribute(menuButton.getElement(), "cursor", "pointer");
        DOM.setStyleAttribute(menuButton.getElement(), "cursor", "hand");
        DOM.setStyleAttribute(menuButton.getElement(), "margin", "3");
        menuButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                menu.showRelativeTo(menuButton);
            }
        });

    }

    public void addView(IView view) {
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
                toolbarHolderPane.setCellHorizontalAlignment(toolbar, HasHorizontalAlignment.ALIGN_RIGHT);
            }
            menu = view.getMenu();
            if (menu != null) {
                toolbarHolderPane.add(menuButton);
                toolbarHolderPane.setCellWidth(menuButton, "1px");
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
        onResize();
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
            onResize();
        }
    }

}
