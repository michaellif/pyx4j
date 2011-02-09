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
package com.pyx4j.examples.site.client.crm.order;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.essentials.client.crud.ActionsPanel;
import com.pyx4j.essentials.client.crud.EntityListPanel;
import com.pyx4j.essentials.client.crud.EntityListWithCriteriaWidget;
import com.pyx4j.essentials.client.crud.EntitySearchCriteriaPanel;
import com.pyx4j.examples.domain.crm.Order;
import com.pyx4j.examples.domain.crm.Resource;
import com.pyx4j.examples.site.client.ExamplesSiteMap;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;

public class OrderListWidget extends EntityListWithCriteriaWidget<Order> {

    public OrderListWidget() {
        super(Order.class, ExamplesSiteMap.Crm.Orders.class, ExamplesSiteMap.Crm.Orders.Edit.class, new EntitySearchCriteriaPanel<Order>(Order.class) {

            @Override
            protected CComponent<?>[][] getComponents() {
                return new CComponent[][] {

                { form.create(form.proto().customerName()) },

                { form.create(form.proto().customerPhone()) },

                { form.create(form.proto().orderNumber()) },

                { form.create(form.proto().description()) },

                { form.create(form.proto().resource()) },

                { form.create(form.proto().status()) },

                };
            }

            @Override
            protected void enhanceComponents() {
                ((CComboBox<Order.OrderStatus>) form.get(form.proto().status())).setOptions(EnumSet.allOf(Order.OrderStatus.class));
            }

            @Override
            public void onDetach() {
                // HACK
                ((CEntityComboBox<Resource>) form.get(form.proto().resource())).resetOptions();
                super.onDetach();
            }

        }, new EntityListPanel<Order>(Order.class) {

            @Override
            public List<ColumnDescriptor<Order>> getColumnDescriptors() {
                List<ColumnDescriptor<Order>> columnDescriptors = new ArrayList<ColumnDescriptor<Order>>();
                columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(getMetaEntity(), getMetaEntity().customerName(), "100px"));
                columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(getMetaEntity(), getMetaEntity().orderNumber(), "50px"));
                columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(getMetaEntity(), getMetaEntity().resource(), "90px"));
                columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(getMetaEntity(), getMetaEntity().description(), "110px"));
                columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(getMetaEntity(), getMetaEntity().status(), "80px"));
                return columnDescriptors;
            }
        });

    }

    @Override
    protected ActionsPanel createActionsPanel() {
        return createActionsPanel(Action.REPORT);
    }

}