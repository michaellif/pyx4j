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

import com.pyx4j.ria.client.view.AbstractView;
import com.pyx4j.widgets.client.DeckLayoutPanel;
import com.pyx4j.widgets.client.tabpanel.TabBar;
import com.pyx4j.widgets.client.tabpanel.TabPanel;

public class FolderSectionPanel extends SectionPanel implements BeforeSelectionHandler<AbstractView>, SelectionHandler<AbstractView>,
        CloseHandler<AbstractView> {

    private final List<AbstractView> views = new ArrayList<AbstractView>();

    private AbstractView currentView;

    private final TabPanel<AbstractView> tabPanel;

    public FolderSectionPanel() {

        addStyleDependentName("folder");

        tabPanel = new TabPanel<AbstractView>();

        tabPanel.addBeforeSelectionHandler(this);
        tabPanel.addSelectionHandler(this);
        tabPanel.addCloseHandler(this);

        TabBar tabBar = tabPanel.getTabBar();

        setHeaderPane(tabBar);

        DeckLayoutPanel contentDeck = tabPanel.getDeck();
        setContentPane(contentDeck);
    }

    public void addView(AbstractView view, boolean closable) {
        views.add(view);
        tabPanel.insert(view, tabPanel.size(), closable);
        if (views.size() == 1) {
            tabPanel.select(0);
        }
    }

    public boolean removeView(AbstractView view, boolean forced) {
        if (tabPanel.remove(view, forced)) {
            views.remove(view);
            return true;
        }
        return false;
    }

    public void showView(AbstractView view) {
        tabPanel.select(view);
    }

    public AbstractView getCurrentView() {
        return currentView;
    }

    @Override
    public void onBeforeSelection(BeforeSelectionEvent<AbstractView> event) {
    }

    @Override
    public void onSelection(SelectionEvent<AbstractView> event) {
        AbstractView view = event.getSelectedItem();
        currentView = view;
    }

    @Override
    public void onClose(CloseEvent<AbstractView> event) {
        views.remove(event.getTarget());
    }

}
