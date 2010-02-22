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
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.entity.client.ui.crud.IEntitySearchResultsPanel;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RecoverableAsyncCallback;

public class CustomerSearchResultsPanel extends HorizontalPanel implements IEntitySearchResultsPanel<Customer> {

    private final CustomerListPanel customerListPanel;

    private final CustomerListMapPanel customerListMapPanel;

    public CustomerSearchResultsPanel() {
        super();
        customerListPanel = new CustomerListPanel();
        add(customerListPanel);
        customerListMapPanel = new CustomerListMapPanel();
        add(customerListMapPanel);

        AsyncCallback<Vector<? extends IEntity>> callback = new RecoverableAsyncCallback<Vector<? extends IEntity>>() {

            public void onSuccess(Vector<? extends IEntity> result) {
                List<Customer> entities = new ArrayList<Customer>();
                for (IEntity entity : result) {
                    if (entity instanceof Customer) {
                        entities.add((Customer) entity);
                    }
                }
                customerListPanel.populateData(entities);
                customerListMapPanel.populateData(entities);
            }

            public void onFailure(Throwable caught) {
            }
        };

        RPCManager.execute(EntityServices.Query.class, EntityCriteria.create(Customer.class), callback);
    }
}
