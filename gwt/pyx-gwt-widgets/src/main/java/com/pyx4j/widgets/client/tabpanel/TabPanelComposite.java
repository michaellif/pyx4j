/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 29, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.tabpanel;

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.widgets.client.DecoratorPanel;
import com.pyx4j.widgets.client.style.Theme.CSSClass;

public class TabPanelComposite extends SimplePanel {

    private final TabPanelModel model;

    public TabPanelComposite() {
        model = new TabPanelModel();
        model.getDeck().setStyleName("gwt-TabPanelBottom");
        VerticalPanel panel = new VerticalPanel();
        TabBar tabBar = model.getTabBar();
        DeckPanel deck = model.getDeck();
        panel.add(tabBar);

        DecoratorPanel deckDecorator = new DecoratorPanel(true, true, true, true, 2, CSSClass.pyx4j_Section_SelectionBorder.name());
        deckDecorator.setWidget(deck);

        panel.add(deckDecorator);

        deckDecorator.setSize("100%", "100%");
        panel.setCellHeight(deck, "100%");
        panel.setCellWidth(deck, "100%");

        tabBar.setWidth("100%");
        panel.setCellHeight(tabBar, "1px");
        panel.setCellWidth(tabBar, "100%");

        DecoratorPanel tabPanelDecorator = new DecoratorPanel(true, true, true, true, 1, CSSClass.pyx4j_Section_Border.name());
        tabPanelDecorator.setWidget(panel);

        add(tabPanelDecorator);
        setStyleName("gwt-TabPanel");
    }

    public void insert(ITab tab, int beforeIndex, boolean closable) {
        model.insert(tab, beforeIndex, closable);
    }

    public void selectTab(int index) {
        model.select(index);
    }

    public boolean remove(ITab tab) {
        return model.remove(tab, false);
    }

    public ITab getTab(int index) {
        return model.getTabs().get(index);
    }

    public int getTabCount() {
        return model.getTabs().size();
    }

    public int getTabIndex(ITab tab) {
        return model.getTabs().indexOf(tab);
    }

}
