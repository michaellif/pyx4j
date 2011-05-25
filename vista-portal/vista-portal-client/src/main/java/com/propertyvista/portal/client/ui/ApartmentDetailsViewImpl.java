/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import java.util.Date;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.client.ui.maps.PropertiesMapWidget;
import com.propertyvista.portal.domain.dto.AptUnitDTO;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class ApartmentDetailsViewImpl extends SimplePanel implements ApartmentDetailsView {

    public static String DEFAULT_STYLE_PREFIX = "AppartmentDetails";

    public static enum StyleSuffix implements IStyleSuffix {
        Left, Center, PageHeader, DL, DD, DT, TableBody, TableHeader, Cell, TableRow, DetailsButton
    }

    private Presenter presenter;

    private final FlowPanel container;

//TODO import the library
    //  private final static DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#.##");

    //  private final static DecimalFormat AREA_FORMAT = new DecimalFormat("#");

    //private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("y-m-d");

    private static I18n i18n = I18nFactory.getI18n(PropertyMapViewImpl.class);

    public ApartmentDetailsViewImpl() {

        container = new FlowPanel();
        container.setSize("100%", "100%");
        container.setStyleName(DEFAULT_STYLE_PREFIX);

        setWidget(container);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(PropertyDetailsDTO property) {
        //TODO introduce better re-population strategy
        container.clear();
        FlowPanel leftPanel = new FlowPanel();
        leftPanel.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Left);
        leftPanel.setWidth("35%");

        //back to search
        Anchor back = new Anchor("Back to Search");
        back.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.navigTo(new PortalSiteMap.FindApartment.PropertyMap());
            }
        });
        leftPanel.add(back);

        HTML label = new HTML("<span>" + i18n.tr("Apartment Details") + "</span>");
        label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.PageHeader);
        leftPanel.add(label);

        PropertiesMapWidget map = new PropertiesMapWidget();
        map.setDistanceOverlay(new GeoPoint(43.697665, -79.402313), 1);
        leftPanel.add(map);
        container.add(leftPanel);

        FlowPanel centerPanel = new FlowPanel();
        centerPanel.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Center);
        centerPanel.setWidth("65%");

        DL dl = new DL();
        dl.setWidth("100%");
        dl.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DL);
        DT dt = new DT(i18n.tr("ADDRESS:"));
        dt.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DT);
        dl.add(dt);

        DD dd = new DD(null);
        dd.add(new HTML(property.address().getStringView()));
        dd.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DD);
        dl.add(dd);

        dt = new DT(i18n.tr("PRICE RANGE:"));
        dt.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DT);
        dl.add(dt);
        //TODO implement
        dd = new DD("$100 - $1700");
        dd.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DD);
        dl.add(dd);

        dt = new DT(i18n.tr("UNITS:"));
        dt.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DT);
        dt.add(new Label());
        dl.add(dt);
        //TODO implement
        dd = new DD(null);
        dd.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DD);

        UnitList ul = new UnitList();

        AptUnitDTO u = EntityFactory.create(AptUnitDTO.class);
        u.setMemberValue("unitType", "Studio");
        u.setMemberValue("area", 560);
        u.setMemberValue("avalableForRent", new Date());
        u.setMemberValue("unitRent", 900);
        ul.addUnit(u);

        u = EntityFactory.create(AptUnitDTO.class);
        u.setMemberValue("unitType", "Two Bedroom");
        u.setMemberValue("area", 1200);
        u.setMemberValue("avalableForRent", new Date());
        u.setMemberValue("unitRent", 1500);
        ul.addUnit(u);

        dd.add(ul);
        dl.add(dd);

        dt = new DT(i18n.tr("UTILITIES:"));
        dt.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DT);
        dl.add(dt);
        //TODO implement
        dd = new DD("Heat");
        dd.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DD);
        dl.add(dd);

        dt = new DT(i18n.tr("DESCRIPTION:"));
        dt.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DT);
        dl.add(dt);
        //TODO implement
        dd = new DD("bla-bla-bla");
        dd.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DD);
        dl.add(dd);

        dt = new DT(i18n.tr("AMENITIES:"));
        dt.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DT);
        dl.add(dt);
        //TODO implement
        dd = new DD("Amenities, amenities,...");
        dd.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DD);
        dl.add(dd);

        centerPanel.add(dl);

        container.add(centerPanel);
    }

    class DL extends ComplexPanel {
        public DL() {
            setElement(DOM.createElement("dl"));
        }

        @Override
        public void add(Widget w) {
            super.add(w, getElement());
        }

    }

    class DD extends ComplexPanel {
        public DD(String value) {
            Element el = DOM.createElement("dd");
            if (value != null && value.length() > 0)
                el.setInnerText(value);
            setElement(el);

        }

        @Override
        public void add(Widget w) {
            super.add(w, getElement());
        }

    }

    class DT extends ComplexPanel {
        public DT(String value) {
            Element el = DOM.createElement("dt");
            if (value != null && value.length() > 0)
                el.setInnerText(value);
            setElement(el);
        }

        @Override
        public void add(Widget w) {
            super.add(w, getElement());
        }

    }

    class UnitList extends FlexTable {

        UnitList() {
            addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.TableBody.name());
            setWidth("100%");
            //header
            setText(0, 0, i18n.tr("Unit"));
            setText(0, 1, i18n.tr("Price"));
            setText(0, 2, i18n.tr("Sq.Ft."));
            setText(0, 3, i18n.tr("Available"));
            setText(0, 4, i18n.tr("More Info"));
            getRowFormatter().addStyleName(0, DEFAULT_STYLE_PREFIX + StyleSuffix.TableHeader.name());
        }

        void populate(List<AptUnitDTO> units) {
            // removeAllRows(); VS: removes header as well. We do not want this
            int total = getRowCount();
            if (total > 1) {
                for (int i = 1; i < total; i++)
                    removeRow(1);
            }
            for (AptUnitDTO u : units) {
                addUnit(u);
            }
        }

        void addUnit(AptUnitDTO unit) {
            if (unit == null)
                return;
            int idx = getRowCount();
            setText(idx, 0, String.valueOf(unit.unitType().getValue()));
            getCellFormatter().addStyleName(idx, 0, DEFAULT_STYLE_PREFIX + StyleSuffix.Cell.name());

            setText(idx, 1, "$" + String.valueOf(unit.unitRent().getValue())/*
                                                                             * CURRENCY_FORMAT
                                                                             * .
                                                                             * format
                                                                             * (unit.unitRent
                                                                             * ().getValue(
                                                                             * ))
                                                                             */);
            getCellFormatter().addStyleName(idx, 1, DEFAULT_STYLE_PREFIX + StyleSuffix.Cell.name());

            setText(idx, 2, String.valueOf(unit.area().getValue())/*
                                                                   * AREA_FORMAT.format(unit
                                                                   * .area().getValue())
                                                                   */);
            getCellFormatter().addStyleName(idx, 2, DEFAULT_STYLE_PREFIX + StyleSuffix.Cell.name());

            setText(idx, 3, String.valueOf(unit.avalableForRent().getValue())/*
                                                                              * DATE_FORMAT.
                                                                              * format
                                                                              * (unit.
                                                                              * avalableForRent
                                                                              * (
                                                                              * ).getValue
                                                                              * ())
                                                                              */);
            getCellFormatter().addStyleName(idx, 3, DEFAULT_STYLE_PREFIX + StyleSuffix.Cell.name());

            setWidget(idx, 4, formatDetails(unit));
            getCellFormatter().addStyleName(idx, 4, DEFAULT_STYLE_PREFIX + StyleSuffix.Cell.name());

            getRowFormatter().addStyleName(idx, DEFAULT_STYLE_PREFIX + StyleSuffix.TableRow.name());

        }

        private Widget formatDetails(final AptUnitDTO unit) {
            FlowPanel cell = new FlowPanel();

            Button dbtn = new Button(i18n.tr("Details"));
            dbtn.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    presenter.goToUnitDetails((AptUnitDTO) null);

                }
            });
            dbtn.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DetailsButton.name());
            cell.add(dbtn);
            return cell;

        }
    }
}
