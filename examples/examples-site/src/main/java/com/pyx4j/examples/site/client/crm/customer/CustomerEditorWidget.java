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
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.client.ui.CEntityForm;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.entity.shared.criterion.PathSearch;
import com.pyx4j.essentials.client.crud.ActionsPanel;
import com.pyx4j.essentials.client.crud.EntityEditorPanel;
import com.pyx4j.essentials.client.crud.EntityEditorWidget;
import com.pyx4j.essentials.client.crud.EntityListPanel;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.examples.domain.crm.Order;
import com.pyx4j.examples.domain.crm.Province;
import com.pyx4j.examples.site.client.ExamplesSiteMap;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.gwt.commons.Print;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RecoverableAsyncCallback;
import com.pyx4j.site.client.AbstractSiteDispatcher;
import com.pyx4j.site.client.NavigationUri;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class CustomerEditorWidget extends EntityEditorWidget<Customer> {

    private final CustomerEditorMapPanel map;

    private final EntityListPanel<Order> ordersPanel;

    public CustomerEditorWidget() {
        super(Customer.class, ExamplesSiteMap.Crm.Customers.Edit.class, new EntityEditorPanel<Customer>(Customer.class) {

            @Override
            protected IObject<?>[][] getFormMembers() {
                return new IObject[][] {

                { meta().name(), meta().phone() },

                { meta().address().street(), meta().address().city() },

                { meta().address().province(), meta().address().zip() },

                { meta().note(), meta().note() },

                };
            }

            @Override
            protected void enhanceComponents(CEntityForm<Customer> form) {
                get(meta().note()).setWidth("100%");
                ((CComboBox<Province>) get(meta().address().province())).setOptions(EnumSet.allOf(Province.class));
            }

        });

        ClickHandler prevHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                populateOrderList(getEditorPanel().getEntity(), ordersPanel.getDataTable().getDataTableModel().getPageNumber() - 1);
            }

        };
        ClickHandler nextHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                populateOrderList(getEditorPanel().getEntity(), ordersPanel.getDataTable().getDataTableModel().getPageNumber() + 1);
            }

        };

        ordersPanel = new EntityListPanel<Order>(Order.class, prevHandler, nextHandler);
        List<ColumnDescriptor<Order>> columnDescriptors = new ArrayList<ColumnDescriptor<Order>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(ordersPanel.getMetaEntity(), ordersPanel.getMetaEntity().description(), "140px"));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(ordersPanel.getMetaEntity(), ordersPanel.getMetaEntity().status(), "80px"));

        ordersPanel.setColumnDescriptors(columnDescriptors);

        ordersPanel.setEditorPageType(ExamplesSiteMap.Crm.Orders.Edit.class);

        getCenterPanel().add(ordersPanel);

        map = new CustomerEditorMapPanel();
        getCenterPanel().add(map);

    }

    @Override
    protected ActionsPanel createActionsPanel() {
        ActionsPanel actionsPanel = createActionsPanel(EditorAction.BACK);
        actionsPanel.addItem("New Order", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Customer customer = getEditorPanel().getEntity();
                if (customer.getPrimaryKey() == null) {
                    MessageDialog.warn("New Order is not allowed", "Customer should be saved first.");
                } else {
                    AbstractSiteDispatcher.show(new NavigationUri(ExamplesSiteMap.Crm.Orders.Edit.class, "entity_id", "new", "parent_id", String
                            .valueOf(customer.getPrimaryKey())));
                }
            }
        });

        actionsPanel.addItem("Print", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Print.it(getEditorPanel().toStringForPrint() + ordersPanel.toStringForPrint() + map.toString());

            }
        });
        return actionsPanel;
    }

    @Override
    protected Customer createNewEntity() {
        Customer customer = EntityFactory.create(Customer.class);
        return customer;
    }

    @Override
    public void populateForm(Customer customer) {
        super.populateForm(customer);
        map.populate(customer);

        ordersPanel.clearData();
        if (customer != null) {
            populateOrderList(customer, 0);
        }

    }

    @Override
    protected void onUnload() {
        ordersPanel.clearData();
        super.onUnload();
    }

    void populateOrderList(Customer customer, final int pageNumber) {
        AsyncCallback<EntitySearchResult<?>> callback = new RecoverableAsyncCallback<EntitySearchResult<?>>() {

            @SuppressWarnings("unchecked")
            public void onSuccess(EntitySearchResult<?> result) {
                ordersPanel.populateData((List<Order>) result.getData(), pageNumber, result.hasMoreData());
            }

            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        };

        EntitySearchCriteria<Order> criteria = new EntitySearchCriteria<Order>(Order.class);
        criteria.setValue(new PathSearch(criteria.meta().customer()), customer);
        RPCManager.execute(EntityServices.Search.class, criteria, callback);
    }
}
