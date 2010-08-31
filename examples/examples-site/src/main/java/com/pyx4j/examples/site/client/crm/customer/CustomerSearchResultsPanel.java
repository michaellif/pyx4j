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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.essentials.client.crud.EntityListPanel;
import com.pyx4j.examples.domain.crm.Customer;

public class CustomerSearchResultsPanel extends EntityListPanel<Customer> {

    private final CustomerListMapPanel customerListMapPanel;

    public CustomerSearchResultsPanel() {
        super(Customer.class);
        setWidth("450px");

        customerListMapPanel = new CustomerListMapPanel();
        customerListMapPanel.getElement().getStyle().setMarginLeft(5, Unit.PX);
        add(customerListMapPanel);
        setCellHorizontalAlignment(customerListMapPanel, HasHorizontalAlignment.ALIGN_RIGHT);
    }

    @Override
    public void populateData(List<Customer> entities, int pageNumber, boolean hasMoreData) {
        super.populateData(entities, pageNumber, hasMoreData);
        customerListMapPanel.populateData(entities);
    }

    public void setDistanceOverlay(LatLng latLng, double distance) {
        customerListMapPanel.setDistanceOverlay(latLng, distance);
    }

    @Override
    public void clearData() {
        super.clearData();
        customerListMapPanel.clearData();
    }

    @Override
    public List<ColumnDescriptor<Customer>> getColumnDescriptors() {
        List<ColumnDescriptor<Customer>> columnDescriptors = new ArrayList<ColumnDescriptor<Customer>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(getMetaEntity(), getMetaEntity().name(), "120px"));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(getMetaEntity(), getMetaEntity().phone(), "90px"));
        ColumnDescriptor<Customer> addrCol = ColumnDescriptorFactory.createColumnDescriptor(getMetaEntity(), getMetaEntity().address(), "180px");
        addrCol.setWordWrap(true);
        columnDescriptors.add(addrCol);
        return columnDescriptors;
    }
}
