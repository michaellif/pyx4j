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
package com.propertyvista.portal.client.ui.searchapt;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockPanel;

import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.portal.client.ui.decorations.PortalHeaderDecorator;
import com.propertyvista.portal.client.ui.maps.PropertiesMapWidget;
import com.propertyvista.portal.domain.dto.PropertyListDTO;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

public class PropertyMapViewImpl extends DockPanel implements PropertyMapView {

    public static String DEFAULT_STYLE_PREFIX = "PropertyList";

    private static I18n i18n = I18nFactory.getI18n(PropertyMapViewImpl.class);

    public static enum StyleSuffix implements IStyleSuffix {
        Header
    }

    public enum ViewType {
        MapView, ListView;

        public static String getName(ViewType tp) {
            if (tp == MapView) {
                return i18n.tr("MAP VIEW");
            } else if (tp == ListView) {
                return i18n.tr("LIST VIEW");
            } else {
                return "";
            }
        }
    }

    private Presenter presenter;

    private final PropertiesMapWidget map;

    private final RefineApartmentSearchForm searchForm;

    private final PropertyListForm propertyListForm;

    public PropertyMapViewImpl() {
        setWidth("100%");
        setHeight("100%");
        setStyleName(DEFAULT_STYLE_PREFIX);
        searchForm = new RefineApartmentSearchForm();
        searchForm.initialize();
        add(searchForm, DockPanel.WEST);
        setCellWidth(searchForm, "220px");

        final DeckPanel deck = new DeckPanel();
        deck.setHeight("100%");

        PortalHeaderDecorator header = new PortalHeaderDecorator(i18n.tr("SEARCH RESULTS"), "100%");
        header.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Header.name());

        final Anchor viewSelector = new Anchor(ViewType.getName(ViewType.MapView));
        viewSelector.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                if (deck.getVisibleWidget() == 0) {
                    deck.showWidget(1);
                    viewSelector.setText(ViewType.getName(ViewType.ListView));

                } else {
                    deck.showWidget(0);
                    viewSelector.setText(ViewType.getName(ViewType.MapView));

                }

            }
        });
        header.addToTheRight(viewSelector);
        add(header, DockPanel.NORTH);

        map = new PropertiesMapWidget();

        propertyListForm = new PropertyListForm();
        propertyListForm.initialize();

        deck.add(propertyListForm);
        deck.add(map);

        deck.showWidget(0);

        add(deck, DockPanel.CENTER);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        searchForm.setPresenter(presenter);
        propertyListForm.setPresenter(presenter);
    }

    @Override
    public void populate(PropertySearchCriteria criteria, PropertyListDTO propertyList) {
        searchForm.populate(criteria);
        map.populate(propertyList);
        propertyListForm.populate(propertyList);
    }

    @Override
    public PropertySearchCriteria getValue() {
        return searchForm.getValue();
    }

}
