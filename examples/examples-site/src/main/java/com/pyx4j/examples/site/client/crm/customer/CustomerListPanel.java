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

import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.entity.client.ui.datatable.DataItem;
import com.pyx4j.entity.client.ui.datatable.DataTable;
import com.pyx4j.entity.client.ui.datatable.DataTableModel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.examples.domain.crm.Customer;

public class CustomerListPanel extends HorizontalPanel {

    private final DataTableModel<Customer> dataTableModel;

    public CustomerListPanel() {

        Customer metaCastomer = EntityFactory.create(Customer.class);

        List<ColumnDescriptor<Customer>> columnDescriptors = new ArrayList<ColumnDescriptor<Customer>>();

        ColumnDescriptor<Customer> name = ColumnDescriptorFactory.createColumnDescriptor(metaCastomer.name());
        name.setWidth("120px");
        columnDescriptors.add(name);
        ColumnDescriptor<Customer> street = ColumnDescriptorFactory.createColumnDescriptor(metaCastomer.street());
        street.setWidth("300px");
        columnDescriptors.add(street);

        dataTableModel = new DataTableModel<Customer>(metaCastomer.getEntityMeta(), columnDescriptors);

        DataTable<Customer> dataTable = new DataTable<Customer>(dataTableModel, true);

        add(dataTable);

    }

    public void populateData(List<Customer> customers) {
        List<DataItem<Customer>> dataItems = new ArrayList<DataItem<Customer>>();
        for (Customer customer : customers) {
            dataItems.add(new DataItem<Customer>(customer));
        }
        dataTableModel.populateData(dataItems, 0, 10);
    }
}
