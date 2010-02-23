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
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RecoverableAsyncCallback;
import com.pyx4j.site.client.InlineWidget;

public class CustomerListWidget extends VerticalPanel implements InlineWidget {

    private static Logger log = LoggerFactory.getLogger(CustomerListWidget.class);

    private final CustomerSearchCriteriaPanel searchCriteriaPanel;

    private final CustomerSearchResultsPanel searchResultsPanel;

    public CustomerListWidget() {
        searchCriteriaPanel = new CustomerSearchCriteriaPanel(this);
        searchCriteriaPanel.getElement().getStyle().setBackgroundColor("#F8F8F8");
        searchCriteriaPanel.getElement().getStyle().setProperty("padding", "10px 40px 10px 10px");
        searchCriteriaPanel.getElement().getStyle().setProperty("marginBottom", "10px");
        searchCriteriaPanel.setWidth("925px");
        searchCriteriaPanel.getElement().getStyle().setProperty("border", "solid 1px #F0F0F0");

        add(searchCriteriaPanel);
        setCellWidth(searchCriteriaPanel, "100%");
        searchResultsPanel = new CustomerSearchResultsPanel();
        add(searchResultsPanel);
        setCellWidth(searchResultsPanel, "100%");
    }

    @Override
    public void populate(Map<String, String> args) {

        //Execute default query if EntityCriteria is not set
        if (searchCriteriaPanel.getEntityCriteria() == null) {
            view();
        }
    }

    public void view() {
        final long start = System.currentTimeMillis();

        AsyncCallback<Vector<? extends IEntity>> callback = new RecoverableAsyncCallback<Vector<? extends IEntity>>() {

            public void onSuccess(Vector<? extends IEntity> result) {
                log.debug("Loaded Customers in {} msec ", System.currentTimeMillis() - start);
                List<Customer> entities = new ArrayList<Customer>();
                for (IEntity entity : result) {
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

        //TODO getSearchCriteria from searchCriteriaPanel (default if null)
        RPCManager.execute(EntityServices.Query.class, EntityCriteria.create(Customer.class), callback);
    }

}
