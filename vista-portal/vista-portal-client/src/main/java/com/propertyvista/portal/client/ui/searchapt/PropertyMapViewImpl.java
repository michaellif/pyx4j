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

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.propertyvista.portal.client.ui.maps.PropertiesMapWidget;
import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

import com.pyx4j.widgets.client.style.IStyleSuffix;

public class PropertyMapViewImpl extends SimplePanel implements PropertyMapView {

    public static String DEFAULT_STYLE_PREFIX = "PropertyList";

    private final PageLayout layout;

    private Presenter presenter;

    public static enum StyleSuffix implements IStyleSuffix {
        Left, Center, Header, Footer, TableBody, Row, Numerator, Cell, CellSize, CellDetails,

        MapButton, DetailsButton
    }

    private static I18n i18n = I18nFactory.getI18n(PropertyMapViewImpl.class);

    public PropertyMapViewImpl() {
        layout = new PageLayout();
        setWidget(layout);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        layout.setPresenter(this.presenter);
    }

    @Override
    public void populate(PropertySearchCriteria criteria, List<PropertyDTO> properties) {
        layout.populate(criteria, properties);
    }

    private class PageLayout extends FlowPanel {

        private final PropertiesMapWidget map;

        private final FlowPanel centerPanel;

        private final RefineApartmentSearchForm searchForm;

        private final FlowPanel leftPanel;

        ApartmentForm testForm = new ApartmentForm();

        List<ApartmentForm> apartments;

        //TODO
        //map.showMarker(property);

        PageLayout() {
            setWidth("100%");
            setHeight("100%");
            leftPanel = new FlowPanel();
            leftPanel.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Left.name());
            leftPanel.setWidth("20%");
            searchForm = new RefineApartmentSearchForm();
            searchForm.initialize();
            leftPanel.add(searchForm);
            add(leftPanel);

            centerPanel = new FlowPanel();
            centerPanel.setWidth("80%");
            centerPanel.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Center.name());

            map = new PropertiesMapWidget();
            centerPanel.add(map);
            testForm.initialize();
            centerPanel.add(testForm);
            add(centerPanel);

            apartments = new ArrayList<ApartmentForm>(20);
        }

        void addToLeftPanel(IsWidget child) {
            leftPanel.add(child);
        }

        void addToCenterPanel(IsWidget child) {
            centerPanel.add(child);
        }

        void clearCenterPanel() {
            centerPanel.clear();
        }

        void setMarker(PropertyDTO property) {
            map.showMarker(property);
        }

        void populate(PropertySearchCriteria criteria, List<PropertyDTO> properties) {
            map.populate(properties);
            searchForm.populate(criteria);
            //  for (PropertyDTO property :  properties) {

            testForm.populate(properties.get(0));
            //}
        }

        void setPresenter(Presenter presenter) {
            searchForm.setPresenter(presenter);
            testForm.setPresenter(presenter);

        }

    }

}
