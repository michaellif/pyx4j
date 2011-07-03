/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 23, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.searchapt;

import java.util.LinkedList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.EntityDataSource;
import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.common.domain.ref.City;
import com.propertyvista.portal.client.ui.decorations.CriteriaWidgetDecorator;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;
import com.propertyvista.portal.rpc.portal.services.PortalSiteServices;

public class RefineApartmentSearchForm extends CEntityForm<PropertySearchCriteria> {

    public static String DEFAULT_STYLE_PREFIX = "RefineApartmentSearch";

    public static enum StyleSuffix implements IStyleSuffix {
        SearchHeader, RowHeader, ButtonPanel, Holder, LabelHolder, Tab, StatusHolder, Label
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, current
    }

    public static enum SearchType {
        ByCity, ByProximity;

        public static String getName() {
            return "SearchType";
        }
    }

    private static I18n i18n = I18nFactory.getI18n(PropertyMapViewImpl.class);

    private PropertyMapView.Presenter presenter;

    private final VerticalPanel container;

    private final NavigTabList tabsHolder;

    private final FlowPanel citySearchPanel;

    private final FlowPanel proximityPanel;

    public RefineApartmentSearchForm() {
        super(PropertySearchCriteria.class);
        container = new VerticalPanel();
        container.setStyleName(DEFAULT_STYLE_PREFIX);
        Label label = new Label(i18n.tr("FIND AN APARTMENT"));
        label.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.SearchHeader.name());
        container.add(label);

        tabsHolder = new NavigTabList();
        NavigTab selectedTab = new NavigTab(i18n.tr("BY CITY"), SearchType.ByCity);
        selectedTab.select();
        tabsHolder.add(selectedTab);
        tabsHolder.add(new NavigTab(i18n.tr("BY PROXIMITY"), SearchType.ByProximity));
        container.add(tabsHolder);

        citySearchPanel = new FlowPanel();
        citySearchPanel.setWidth("100%");
        container.add(citySearchPanel);

        proximityPanel = new FlowPanel();
        proximityPanel.setWidth("100%");
        proximityPanel.setVisible(false);
        container.add(citySearchPanel);

    }

    @Override
    public IsWidget createContent() {

        addFieldTo(new CriteriaWidgetDecorator(inject(proto().city())), citySearchPanel);

        addField(new CriteriaWidgetDecorator(inject(proto().numOfBeds())));
        addField(new CriteriaWidgetDecorator(inject(proto().numOfBath())));

        FlowPanel updatePanel = new FlowPanel();
        Button updateBtn = new Button(i18n.tr("Search"));
        updateBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.refineSearch();
            }

        });
        updatePanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.ButtonPanel);
        updatePanel.add(updateBtn);

        container.add(updatePanel);
        return container;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        CEditableComponent<?, ?> c = super.create(member);
        if (member == proto().city()) {
            ((CEntityComboBox<City>) c).setOptionsDataSource(new EntityDataSource<City>() {

                @Override
                public void obtain(EntityQueryCriteria<City> criteria, AsyncCallback<List<City>> handlingCallback, boolean background) {
                    ((PortalSiteServices) GWT.create(PortalSiteServices.class)).retrieveCityList((AsyncCallback) handlingCallback);
                }

            });
        }
        return c;
    }

    private void addField(Widget widget) {
        widget.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        container.add(widget);
    }

    private void addFieldTo(Widget widget, Panel parent) {
        widget.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        parent.add(widget);
    }

    public PropertyMapView.Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(PropertyMapView.Presenter presenter) {
        this.presenter = presenter;
    }

    private void onTabChange(NavigTab tab) {
        NavigTab selected = tabsHolder.getSelectedTab();
        selected.deselect();
        tab.select();
        boolean bycity = tab.getSearchtype() == SearchType.ByCity;
        citySearchPanel.setVisible(bycity);
        proximityPanel.setVisible(!bycity);
    }

    class NavigTabList extends ComplexPanel {
        private final List<NavigTab> tabs;

        public NavigTabList() {
            setElement(DOM.createElement("ul"));
            tabs = new LinkedList<RefineApartmentSearchForm.NavigTab>();
            setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Holder.name());
        }

        @Override
        public void add(Widget w) {
            NavigTab tab = (NavigTab) w;
            tabs.add(tab);
            super.add(w, getElement());
        }

        public List<NavigTab> getTabs() {
            return tabs;
        }

        public NavigTab getSelectedTab() {
            if (tabs == null)
                return null;
            for (NavigTab tab : tabs) {
                if (tab.isSelected()) {
                    return tab;
                }
            }
            return null;
        }

    }

    class NavigTab extends ComplexPanel {

        private final FlowPanel labelHolder;

        private final SimplePanel statusHolder;

        private final Label label;

        private final String caption;

        private boolean selected;

        private final SearchType searchtype;

        private final NavigTab me;

        private NavigTab(String caption, SearchType searchtype) {
            super();
            me = this;
            selected = false;
            this.caption = caption;
            this.searchtype = searchtype;

            setElement(DOM.createElement("li"));
            setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Tab.name());
            getElement().setAttribute(SearchType.getName(), searchtype.name());

            getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
            sinkEvents(Event.ONCLICK);

            labelHolder = new FlowPanel();
            labelHolder.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.LabelHolder.name());
            add(labelHolder);

            statusHolder = new SimplePanel();
            statusHolder.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.StatusHolder.name());
            labelHolder.add(statusHolder);

            label = new Label(this.caption);
            label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Label.name());
            statusHolder.add(label);
            getElement().getStyle().setFontWeight(FontWeight.BOLD);
            getElement().getStyle().setCursor(Cursor.DEFAULT);

            addDomHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    onTabChange(me);

                }
            }, ClickEvent.getType());

            getElement().getStyle().setCursor(Cursor.POINTER);

        }

        public SearchType getSearchtype() {
            return searchtype;
        }

        public void deselect() {
            selected = false;
            label.removeStyleDependentName(StyleDependent.current.name());
        }

        public void select() {
            label.addStyleDependentName(StyleDependent.current.name());
            selected = true;
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
