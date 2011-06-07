/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

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

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.portal.domain.site.PageDescriptor;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class ResidentsNavigViewImpl extends SimplePanel implements ResidentsNavigView {
    public static String DEFAULT_STYLE_PREFIX = "ResidentsMenu";

    public static enum StyleSuffix implements IStyleSuffix {
        Holder, Tab, LabelHolder, StatusHolder, Label
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, current
    }

    private ResidentsNavigPresenter presenter;

    private NavigTabList tabsHolder;

    private AppPlace defaultitem;

    public ResidentsNavigViewImpl() {
        setStyleName(DEFAULT_STYLE_PREFIX);
        defaultitem = null;
    }

    @Override
    public void setPresenter(ResidentsNavigPresenter presenter) {
        this.presenter = presenter;

        clear();
        tabsHolder = new NavigTabList();
        for (PageDescriptor item : presenter.getResidentsNavig().items()) {
            tabsHolder.add(new NavigTab(item));
        }
        setWidget(tabsHolder);
        //TODO need a better way to initialize on the first landing
        if (defaultitem != null)
            return;
        defaultitem = new PortalSiteMap.Residents.Navigator.TenantProfile();
        AppSite.getPlaceController().goTo(defaultitem);

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

        NavigTab(PageDescriptor menuItem) {
            super();
            this.place = AppSite.getHistoryMapper().getPlace(menuItem.caption().getValue());

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

            label = new Label(menuItem.caption().getValue());
            label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Label.name());
            statusHolder.add(label);

            String placeid = AppSite.getPlaceId(place);
            String current = AppSite.getPlaceId(presenter.getWhere());

            if (placeid != null && placeid.equals(current)) {
                label.addStyleDependentName(StyleDependent.current.name());
            }

            getElement().getStyle().setFontWeight(FontWeight.BOLD);
            getElement().getStyle().setCursor(Cursor.DEFAULT);

            addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.navigTo(place);
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
