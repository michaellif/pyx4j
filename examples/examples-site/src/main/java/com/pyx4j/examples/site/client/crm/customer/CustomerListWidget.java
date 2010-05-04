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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.entity.shared.criterion.PathSearch;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.geo.MapUtils;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RecoverableAsyncCallback;
import com.pyx4j.site.client.InlineWidget;

public class CustomerListWidget extends VerticalPanel implements InlineWidget {

    private static Logger log = LoggerFactory.getLogger(CustomerListWidget.class);

    private final CustomerSearchCriteriaPanel searchCriteriaPanel;

    private final CustomerSearchResultsPanel searchResultsPanel;

    public CustomerListWidget() {
        searchCriteriaPanel = new CustomerSearchCriteriaPanel(this);
        add(searchCriteriaPanel);
        setCellWidth(searchCriteriaPanel, "100%");
        searchResultsPanel = new CustomerSearchResultsPanel();
        add(searchResultsPanel);
        setCellWidth(searchResultsPanel, "100%");
    }

    @Override
    public void populate(Map<String, String> args) {
        //Execute default query
        view();
    }

    public void view() {
        searchResultsPanel.clearData();
        final long start = System.currentTimeMillis();

        AsyncCallback<EntitySearchResult<? extends IEntity>> callback = new RecoverableAsyncCallback<EntitySearchResult<? extends IEntity>>() {

            public void onSuccess(EntitySearchResult<? extends IEntity> result) {
                log.debug("Loaded Customers in {} msec ", System.currentTimeMillis() - start);
                List<Customer> entities = new ArrayList<Customer>();
                for (IEntity entity : result.getData()) {
                    if (entity instanceof Customer) {
                        entities.add((Customer) entity);
                    }
                }
                long startPopulate = System.currentTimeMillis();
                searchResultsPanel.populateData(entities);
                log.debug("Populated Customers in {} msec ", System.currentTimeMillis() - startPopulate);
            }

            public void onFailure(Throwable caught) {
            }
        };

        EntitySearchCriteria<Customer> criteria = searchCriteriaPanel.getEntityCriteria();
        criteria.setPageSize(20);
        // TODO CustomerListPanel get page ?
        criteria.setPageNumber(0);
        RPCManager.execute(EntityServices.Search.class, criteria, callback);

        //call Distance Overlay
        Integer areaRadius = (Integer) criteria.getValue(new PathSearch(criteria.meta().location(), "radius"));
        if (areaRadius != null) {
            GeoPoint geoPoint = (GeoPoint) criteria.getValue(new PathSearch(criteria.meta().location(), "from"));
            LatLng fromCoordinates = MapUtils.newLatLngInstance(geoPoint);
            searchResultsPanel.setDistanceOverlay(fromCoordinates, areaRadius);
        } else {
            searchResultsPanel.setDistanceOverlay(null, 0);
        }
    }

}
