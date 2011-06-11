/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.client.activity.NavigItem;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class MainNavigViewImpl extends SimplePanel implements MainNavigView {

    public static String DEFAULT_STYLE_PREFIX = "MainMenu";

    public static enum StyleSuffix implements IStyleSuffix {
        Holder, Tab, LabelHolder, StatusHolder, Label
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, current
    }

    private MainNavigPresenter presenter;

    private final NavigTabList tabsHolder;

    public MainNavigViewImpl() {
        setStyleName(DEFAULT_STYLE_PREFIX);
        tabsHolder = new NavigTabList();
        setWidget(tabsHolder);
    }

    @Override
    public void setPresenter(MainNavigPresenter presenter) {
        this.presenter = presenter;
        tabsHolder.clear();
    }

    @Override
    public void setMainNavig(List<NavigItem> items) {
        for (NavigItem item : items) {
            tabsHolder.add(new NavigTab(item));
        }
    }

    class NavigTabList extends ComplexPanel {
        private final List<NavigTab> tabs;

        public NavigTabList() {
            setElement(DOM.createElement("ul"));
            setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Holder.name());
            tabs = new LinkedList<MainNavigViewImpl.NavigTab>();
        }

        @Override
        public void add(Widget w) {
            NavigTab tab = (NavigTab) w;
            tab.addTabSelectedHandler(new TabSelectedHandler() {

                @Override
                public void onTabSelect(TabSelectedEvent event) {
                    for (NavigTab tab : tabs) {
                        if (tab.isSelected()) {
                            tab.deselect();
                            break;
                        }
                    }
                    event.getTab().select();
                }
            });
            tabs.add(tab);
            super.add(w, getElement());
        }
    }

    class NavigTab extends ComplexPanel {

        private final FlowPanel labelHolder;

        private final SimplePanel statusHolder;

        private final Label label;

        private boolean selected;

        final private HandlerManager handlerManager = new HandlerManager(this);

        final private NavigTab self;

        NavigTab(NavigItem menuItem) {
            super();

            self = this;

            String caption = menuItem.getCaption();
            final AppPlace place = menuItem.getPlace();

            setElement(DOM.createElement("li"));
            setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Tab.name());

            getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
            sinkEvents(Event.ONCLICK);

            labelHolder = new FlowPanel();
            labelHolder.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.LabelHolder.name());
            add(labelHolder);

            statusHolder = new SimplePanel();
            statusHolder.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.StatusHolder.name());
            labelHolder.add(statusHolder);

            label = new Label(caption);
            label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Label.name());
            statusHolder.add(label);

            Place currentPlace = presenter.getWhere();

            if (place.equals(currentPlace)) {
                select();
            } else {
                selected = false;
            }

            getElement().getStyle().setFontWeight(FontWeight.BOLD);
            getElement().getStyle().setCursor(Cursor.DEFAULT);

            addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.navigTo(place);
                    event.stopPropagation();
                    handlerManager.fireEvent(new TabSelectedEvent(self));

                }
            }, ClickEvent.getType());
            addDomHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    label.getElement().getStyle().setProperty("color", "#555");
                }
            }, MouseOverEvent.getType());
            addDomHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    label.getElement().getStyle().setProperty("color", "#7B8388");
                }
            }, MouseOutEvent.getType());
            getElement().getStyle().setCursor(Cursor.POINTER);

        }

        public void deselect() {
            selected = false;
            label.removeStyleDependentName(StyleDependent.current.name());
        }

        public void select() {
            label.addStyleDependentName(StyleDependent.current.name());
            selected = true;
        }

        public void addTabSelectedHandler(TabSelectedHandler handler) {
            handlerManager.addHandler(TabSelectedEvent.TYPE, handler);
        }

        @Override
        public void add(Widget w) {
            super.add(w, getElement());
        }

        public boolean isSelected() {
            return selected;
        }
    }
}
