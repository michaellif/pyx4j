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

import java.util.List;
import java.util.Set;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.portal.client.ui.maps.PropertiesMapWidget;
import com.propertyvista.portal.domain.dto.PropertyDTO;

public class PropertyMapViewImpl extends SimplePanel implements PropertyMapView {

    public static String DEFAULT_STYLE_PREFIX = "PropertyList";

    private final PropertiesMapWidget map;

    private final BuildingList buildingList;

    private Presenter presenter;

    public static enum StyleSuffix implements IStyleSuffix {
        Search, SearchResult, Header, Footer, TableBody, Row, Numerator, Cell, CellSize, CellDetails,

        MapButton, DetailsButton
    }

    private static I18n i18n = I18nFactory.getI18n(PropertyMapViewImpl.class);

    private final RefineApartmentSearchForm searchForm;

    public PropertyMapViewImpl() {
        FlowPanel container = new FlowPanel();
        container.setWidth("100%");
        container.setHeight("100%");

        FlowPanel refineSearch = new FlowPanel();
        refineSearch.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Search.name());
        refineSearch.setWidth("20%");
        searchForm = new RefineApartmentSearchForm();
        refineSearch.add(searchForm.createContent());
        container.add(refineSearch);

        FlowPanel searchResult = new FlowPanel();
        searchResult.setWidth("80%");
        searchResult.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.SearchResult.name());

        map = new PropertiesMapWidget();
        //map.setDistanceOverlay(new GeoPoint(43.697665, -79.402313), 5);
        searchResult.add(map);

        buildingList = new BuildingList();

        searchResult.add(buildingList);
        container.add(searchResult);

        setWidget(container);
    }

    class BuildingList extends FlexTable {

        BuildingList() {
            addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.TableBody.name());
            setWidth("100%");
            //header
            int col = 0;
            setText(0, col++, "");
            setText(0, col++, i18n.tr("Photo"));
            setText(0, col++, i18n.tr("Address"));
            setText(0, col++, i18n.tr("Size"));
            setText(0, col++, i18n.tr("Availability"));
            setText(0, col++, i18n.tr("Price"));
            getRowFormatter().addStyleName(0, DEFAULT_STYLE_PREFIX + StyleSuffix.Header.name());
        }

        void populate(List<PropertyDTO> properties) {
            // removeAllRows(); VS: removes header as well. We do not want this
            int total = getRowCount();
            if (total > 1) {
                for (int i = 1; i < total; i++)
                    removeRow(1);
            }
            for (PropertyDTO property : properties) {
                addProperty(property);
            }
        }

        void addProperty(PropertyDTO property) {
            if (property == null)
                return;
            int idx = getRowCount();
            int col = 0;

            setText(idx, col, String.valueOf(idx));
            getCellFormatter().addStyleName(idx, col, DEFAULT_STYLE_PREFIX + StyleSuffix.Numerator.name());
            getCellFormatter().addStyleName(idx, col, DEFAULT_STYLE_PREFIX + StyleSuffix.Cell.name());
            col++;

            setText(idx, col, i18n.tr("Photo"));
            getCellFormatter().addStyleName(idx, col, DEFAULT_STYLE_PREFIX + StyleSuffix.Cell.name());
            col++;

            setWidget(idx, col, formatAddressCell(property));
            getCellFormatter().addStyleName(idx, col, DEFAULT_STYLE_PREFIX + StyleSuffix.Cell.name());
            col++;

            setHTML(idx, col, formatStringSet(property.size().getValue()));
            getCellFormatter().addStyleName(idx, col, DEFAULT_STYLE_PREFIX + StyleSuffix.CellSize.name());
            getCellFormatter().addStyleName(idx, col, DEFAULT_STYLE_PREFIX + StyleSuffix.Cell.name());
            col++;

            setText(idx, col, "?");
            getCellFormatter().addStyleName(idx, col, DEFAULT_STYLE_PREFIX + StyleSuffix.Cell.name());
            col++;

            setWidget(idx, col, formatPriceCell(property, null));
            getCellFormatter().addStyleName(idx, col, DEFAULT_STYLE_PREFIX + StyleSuffix.Cell.name());

            getRowFormatter().addStyleName(idx, DEFAULT_STYLE_PREFIX + StyleSuffix.Row.name());

        }

        private String formatStringSet(Set<String> sizes) {
            if (sizes == null || sizes.size() == 0)
                return "";
            String html = "<ul>";
            for (String s : sizes)
                html += "<li>" + s + "</li>";
            html += "</ul>";
            return html;
        }

        private Widget formatAddressCell(final PropertyDTO property) {
            String address = property.address().getStringView();
            FlowPanel cell = new FlowPanel();
            if (address == null)
                return cell;
            HTML addr = new HTML(address);
            Button mapbtn = new Button(i18n.tr("Show on Map"));
            mapbtn.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    map.showMarker(property);
                }
            });
            mapbtn.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.MapButton.name());
            cell.add(addr);
            cell.add(mapbtn);
            return cell;

        }

        private Widget formatPriceCell(final PropertyDTO property, String price) {
            FlowPanel cell = new FlowPanel();

            HTML p = null;
            if (price != null)
                p = new HTML(price);
            Button dbtn = new Button(i18n.tr("Details"));
            dbtn.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    presenter.showPropertyDetails(property);

                }
            });
            dbtn.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DetailsButton.name());
            if (p != null)
                cell.add(p);
            cell.add(dbtn);
            return cell;

        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        searchForm.setPresenter(this.presenter);
    }

    @Override
    public void populate(List<PropertyDTO> properties) {
        map.populate(properties);
        buildingList.populate(properties);
    }

}
