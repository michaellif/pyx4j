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
 * Created on Feb 12, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.server.preloader.crm;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.examples.domain.crm.Order;
import com.pyx4j.examples.domain.crm.Resource;
import com.pyx4j.examples.domain.crm.Order.Status;

public class PreloadCrmDemo extends AbstractDataPreloader {

    private int resourceCount;

    private int customerCount;

    private int orderCount;

    private final Resource r[] = new Resource[4];

    private final String[] streets = new String[] { "Victoria Park Avenue", "Jarvis Street", "Bloor Street", "Don Mills Road" };

    @Override
    public String create() {
        resourceCount = 0;
        customerCount = 0;
        orderCount = 0;

        r[resourceCount++] = createNamed(Resource.class, "Heavy duty track");
        r[resourceCount++] = createNamed(Resource.class, "Bob");
        r[resourceCount++] = createNamed(Resource.class, "John");
        r[resourceCount++] = createNamed(Resource.class, "Alex");

        createCustomer("Anna");
        createCustomer("Basia");
        createCustomer("Diana");
        createCustomer("Vika");

        {
            Customer customer = EntityFactory.create(Customer.class);
            customer.name().setValue("Anna");

            PersistenceServicesFactory.getPersistenceService().persist(customer);
            customerCount++;
        }

        StringBuilder b = new StringBuilder();
        b.append("Created " + resourceCount + " Resources").append('\n');
        b.append("Created " + customerCount + " Customers").append('\n');
        b.append("Created " + orderCount + " Orders");
        return b.toString();
    }

    private Resource selectResource(int number) {
        if (r.length >= number) {
            return r[r.length % number];
        } else {
            return r[number];
        }
    }

    private void createCustomer(String name) {
        Customer customer = EntityFactory.create(Customer.class);
        customer.name().setValue(name);
        customer.phone().add("647-123-456" + customerCount);
        customer.street().setValue(streets[customerCount]);
        customer.notes().add("Somthing important");

        Order o1 = EntityFactory.create(Order.class);
        o1.description().setValue("Cat " + customerCount);
        o1.status().setValue(Status.ACTIVE);
        o1.resources().add(selectResource(customerCount + 1));
        customer.orders().add(o1);
        orderCount++;

        Order o2 = EntityFactory.create(Order.class);
        o2.description().setValue("Dog " + customerCount);
        o2.status().setValue(Status.COMPLETED);
        o2.resources().add(selectResource(customerCount + 1));
        o2.resources().add(r[0]);
        customer.orders().add(o2);
        orderCount++;

        PersistenceServicesFactory.getPersistenceService().persist(customer);
        customerCount++;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(Customer.class, Order.class, Resource.class);
    }

}
