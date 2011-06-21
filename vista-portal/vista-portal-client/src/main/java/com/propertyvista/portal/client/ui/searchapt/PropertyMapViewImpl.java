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

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.portal.client.ui.maps.PropertiesMapWidget;
import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.domain.dto.PropertyListDTO;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

public class PropertyMapViewImpl extends DockPanel implements PropertyMapView {

    public static enum StyleSuffix implements IStyleSuffix {
        Left, Center
    }

    public static String DEFAULT_STYLE_PREFIX = "PropertyList";

    private Presenter presenter;

    private final PropertiesMapWidget map;

    private final FlowPanel centerPanel;

    private final RefineApartmentSearchForm searchForm;

    private final FlowPanel leftPanel;

    private final PropertyListForm propertyListForm;

    private static I18n i18n = I18nFactory.getI18n(PropertyMapViewImpl.class);

    public PropertyMapViewImpl() {
        setWidth("100%");
        setHeight("100%");
        searchForm = new RefineApartmentSearchForm();
        searchForm.initialize();

        leftPanel = new FlowPanel();
        leftPanel.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Left.name());
        leftPanel.add(searchForm);
        add(leftPanel, DockPanel.WEST);

        centerPanel = new FlowPanel();
        centerPanel.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Center.name());

        map = new PropertiesMapWidget();
        centerPanel.add(map);

        propertyListForm = new PropertyListForm();
        propertyListForm.initialize();
        centerPanel.add(propertyListForm);

        add(centerPanel, DockPanel.CENTER);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        searchForm.setPresenter(presenter);
        propertyListForm.setPresenter(presenter);
    }

    @Override
    public void populate(PropertySearchCriteria criteria, PropertyListDTO propertyList) {
        map.populate(propertyList);
        searchForm.populate(criteria);
        propertyListForm.populate(propertyList);
    }

    void setMarker(PropertyDTO property) {
        map.showMarker(property);
    }

    @Override
    public PropertySearchCriteria getValue() {
        return searchForm.getValue();
    }

}
