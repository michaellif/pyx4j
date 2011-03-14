/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-12
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.domain.pt.TenantTabInfo;
import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.rpc.pt.VistaFormsDebugId;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class SecondNavigViewImpl extends SimplePanel implements SecondNavigView {

    public static String DEFAULT_STYLE_PREFIX = "vista_SubSteps";

    public static enum StyleSuffix implements IStyleSuffix {
        Holder, Tab, LabelHolder, StatusHolder, Label
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, latest, complete, invalid, current
    }

    private SecondNavigPresenter presenter;

    private NavigTabList tabsHolder;

    public SecondNavigViewImpl() {
        setStyleName(DEFAULT_STYLE_PREFIX);
    }

    @Override
    public void setPresenter(SecondNavigPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show() {
        clear();

        tabsHolder = new NavigTabList();
        for (TenantTabInfo tti : presenter.getTenantTabsInfo()) {
            tabsHolder.add(new NavigTab(tti));
        }

        setWidget(tabsHolder);
    }

    @Override
    public void hide() {
        clear();
    }

    class NavigTabList extends ComplexPanel {
        public NavigTabList() {
            setElement(DOM.createElement("ul"));
            setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Holder.name());
        }

        @Override
        public void add(Widget w) {
            super.add(w, getElement());
        }
    }

    class NavigTab extends ComplexPanel {

        private final AppPlace place;

        private final FlowPanel labelHolder;

        private final SimplePanel statusHolder;

        private final Label label;

        public AppPlace getPlace() {
            return place;
        }

        NavigTab(final TenantTabInfo tti) {
            super();
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

            this.place = new SiteMap.Info();
            label = new Label(tti.fullName().getValue());
            label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Label.name());
            label.ensureDebugId(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(place)));
            statusHolder.add(label);

            // TODO: status logic here:
//            switch (step.getStatus()) {
//            case invalid:
//                addStyleDependentName(StyleDependent.invalid);
//                break;
//            case complete:
//                addStyleDependentName(StyleDependent.complete);
//                break;
//            case latest:
//                addStyleDependentName(StyleDependent.latest);
//                break;
//            default:
//                break;
//            }

            if (place.equals(presenter.getWhere())) {
                label.addStyleDependentName(StyleDependent.current.name());
            }

            getElement().getStyle().setFontWeight(FontWeight.BOLD);

            getElement().getStyle().setCursor(Cursor.DEFAULT);

            // TODO: visited if logic here (?):
            boolean visited = true;
            if (visited) {
                addDomHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.navigTo(place);
                    }
                }, ClickEvent.getType());
                addDomHandler(new MouseOverHandler() {
                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        //label.getElement().getStyle().setProperty("color", "#555");
                    }
                }, MouseOverEvent.getType());
                addDomHandler(new MouseOutHandler() {
                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        //label.getElement().getStyle().setProperty("color", "#333");
                    }
                }, MouseOutEvent.getType());
                getElement().getStyle().setCursor(Cursor.POINTER);
            } else {
                if (!GWT.isProdMode()) {
                    addDomHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            presenter.navigTo(place);
                        }
                    }, ClickEvent.getType());
                }
            }

        }

        public void addStyleDependentName(StyleDependent style) {
            super.addStyleDependentName(style.name());
            labelHolder.addStyleDependentName(style.name());
            statusHolder.addStyleDependentName(style.name());
        }

        @Override
        public void add(Widget w) {
            super.add(w, getElement());
        }
    }
}
