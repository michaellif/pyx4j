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
 * Created on Feb 19, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.examples.site.client.crm.customer;

import java.util.List;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.entity.client.ui.crud.IEntitySearchResultsPanel;
import com.pyx4j.examples.domain.crm.Customer;

public class CustomerSearchResultsPanel extends HorizontalPanel implements IEntitySearchResultsPanel<Customer> {

    private final CustomerListPanel customerListPanel;

    private final CustomerListMapPanel customerListMapPanel;

    public CustomerSearchResultsPanel() {
        super();
        setWidth("100%");
        customerListPanel = new CustomerListPanel();
        add(customerListPanel);
        customerListMapPanel = new CustomerListMapPanel();
        add(customerListMapPanel);
        setCellHorizontalAlignment(customerListMapPanel, HasHorizontalAlignment.ALIGN_RIGHT);

    }

    public void populateData(List<Customer> entities) {
        customerListPanel.populateData(entities);
        customerListMapPanel.populateData(entities);
    }

    public void setDistanceOverlay(LatLng latLng, double distance) {
        customerListMapPanel.setDistanceOverlay(latLng, distance);
    }

    public void clearData() {
        customerListPanel.clearData();
        customerListMapPanel.clearData();
    }
}
