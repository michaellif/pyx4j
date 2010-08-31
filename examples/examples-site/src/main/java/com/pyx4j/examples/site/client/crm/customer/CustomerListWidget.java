/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 16, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.examples.site.client.crm.customer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.shared.criterion.PathSearch;
import com.pyx4j.essentials.client.crud.EntityListPanel;
import com.pyx4j.essentials.client.crud.EntityListWithCriteriaWidget;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.examples.site.client.ExamplesSiteMap;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.geo.MapUtils;

public class CustomerListWidget extends EntityListWithCriteriaWidget<Customer> {

    private static Logger log = LoggerFactory.getLogger(CustomerListWidget.class);

    public CustomerListWidget() {
        super(Customer.class, ExamplesSiteMap.Crm.Customers.class, ExamplesSiteMap.Crm.Customers.Edit.class, new CustomerSearchCriteriaPanel(),
                new EntityListPanel<Customer>(Customer.class) {
                    @Override
                    public List<ColumnDescriptor<Customer>> getColumnDescriptors() {
                        return null;
                    }
                });
    }

    @Override
    protected void populate(final int pageNumber) {
        final CustomerSearchCriteriaPanel searchCriteriaPanel = (CustomerSearchCriteriaPanel) getSearchCriteriaPanel();
        final CustomerSearchResultsPanel searchResultsPanel = (CustomerSearchResultsPanel) getSearchResultsPanel();
        if (searchCriteriaPanel.hasDistanceCriteria()) {
            MapUtils.obtainLatLang(searchCriteriaPanel.getFromLocationZip(), new LatLngCallback() {

                @Override
                public void onSuccess(LatLng fromCoordinates) {

                    GeoPoint geoPoint = MapUtils.newGeoPointInstance(fromCoordinates);

                    searchCriteriaPanel.getForm().setPropertyValue(new PathSearch(searchCriteriaPanel.getForm().meta().location(), "from"), geoPoint);
                    CustomerListWidget.super.populate(pageNumber);

                    //call Distance Overlay
                    Integer areaRadius = searchCriteriaPanel.getAreaRadius();
                    if (areaRadius != null) {
                        searchResultsPanel.setDistanceOverlay(fromCoordinates, areaRadius);
                    } else {
                        searchResultsPanel.setDistanceOverlay(null, 0);
                    }
                }

                @Override
                public void onFailure() {
                    log.warn("Can't find LatLng for distanceOverlay");
                }
            });
        } else {
            searchResultsPanel.setDistanceOverlay(null, 0);
            searchCriteriaPanel.getForm().removePropertyValue(new PathSearch(searchCriteriaPanel.getForm().meta().location(), "from"));
            super.populate(pageNumber);
        }

    }

}
