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
 * Created on May 3, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.examples.site.client.crm.customer;

import java.util.EnumSet;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.entity.shared.criterion.PathSearch;
import com.pyx4j.essentials.client.crud.EntitySearchCriteriaPanel;
import com.pyx4j.essentials.geo.GeoCriteriaEditableComponentFactory;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.examples.domain.crm.Order.OrderStatus;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;

public class CustomerSearchCriteriaPanel extends EntitySearchCriteriaPanel<Customer> {

    CustomerSearchCriteriaPanel() {
        super(Customer.class, new GeoCriteriaEditableComponentFactory());
    }

    @Override
    protected CComponent<?>[][] getComponents() {

        CComboBox<OrderStatus> orderStatus = new CComboBox<OrderStatus>("Order Status");
        orderStatus.setOptions(EnumSet.allOf(OrderStatus.class));

        form.bind(orderStatus, new PathSearch(form.meta().orderStatus(), null));

        CComponent<?>[][] components = new CComponent[][] {

        { form.create(form.meta().name()) },

        { form.create(form.meta().phone()) },

        { form.create(form.meta().address().street()) },

        { form.create(form.meta().address().city()) },

        { form.create(form.meta().locationCriteria()) },

        { form.create(form.meta().locationCriteria().radius()) },

        { orderStatus },

        };

        return components;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected void enhanceComponents() {
        final CEditableComponent<?, ?> fromLocationZipField = form.get(form.meta().locationCriteria());
        final CEditableComponent<?, ?> areaRadiusField = form.get(form.meta().locationCriteria().radius());

        ValueChangeHandler locationMadabilityHandler = new ValueChangeHandler() {

            @Override
            public void onValueChange(ValueChangeEvent event) {
                boolean needTwo = !areaRadiusField.isValueEmpty() || !fromLocationZipField.isValueEmpty();
                areaRadiusField.setMandatory(needTwo);
                fromLocationZipField.setMandatory(needTwo);
            }

        };

        areaRadiusField.addValueChangeHandler(locationMadabilityHandler);
        fromLocationZipField.addValueChangeHandler(locationMadabilityHandler);
    }

    @Override
    protected void enhanceEntitySearchCriteria(EntitySearchCriteria<Customer> criteria) {
        criteria.setSorts(null);
        criteria.asc(form.meta().name());
    }

}
