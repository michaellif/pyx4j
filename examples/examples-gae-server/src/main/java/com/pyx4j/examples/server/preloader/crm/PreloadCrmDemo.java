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

import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.examples.domain.crm.Order;
import com.pyx4j.examples.domain.crm.Resource;
import com.pyx4j.examples.domain.crm.Order.Status;

public class PreloadCrmDemo extends AbstractDataPreloader {

    private int customerCount;

    private int orderCount;

    private final List<Resource> r = new Vector<Resource>();

    @Override
    public String create() {
        customerCount = 0;
        orderCount = 0;

        r.add(createNamed(Resource.class, "Heavy duty track"));
        r.add(createNamed(Resource.class, "Bob"));
        r.add(createNamed(Resource.class, "John"));
        r.add(createNamed(Resource.class, "Alex"));
        r.add(createNamed(Resource.class, "Isaac"));
        r.add(createNamed(Resource.class, "Owen"));
        r.add(createNamed(Resource.class, "Richard"));
        r.add(createNamed(Resource.class, "William"));
        r.add(createNamed(Resource.class, "Thomas"));
        r.add(createNamed(Resource.class, "Adam"));

        createCustomer("Jordan  Desai", "280 Willow Ave, Toronto, ON, Canada", "905-762-8993", 43.678425, -79.288309);
        createCustomer("Michael Smith", "336 Walmer Rd, Toronto, ON, Canada", "905-884-8935", 43.6699245, -79.4069179);
        createCustomer("Elina Belso", "142 Inglewood Dr, Toronto, ON, Canada", "905-789-4328", 43.856316, -79.245758);
        createCustomer("Melissa Boroda", "68 Hillcroft Dr, Markham, ON, Canada", "905-786-6782", 43.8347269, -79.2804875);
        createCustomer("Karen Slater", "32 Roosevelt Dr, Richmond Hill, ON, Canada", "905-436-6873", 43.8416335, -79.4345507);
        createCustomer("Corben Garnet", "112 Cayuga Ave, Toronto, ON, Canada", "905-785-1222", 43.6785793, -79.4792064);
        createCustomer("Madison Ronen", "16 Estoril St, Richmond Hill, ON, Canada", "905-785-0099", 43.8559356, -79.4250644);
        createCustomer("Hannah Kim", "3438 Africa Crescent, Mississauga, ON, Canada", "905-112-6563", 43.5801851, -79.6409809);
        createCustomer("Cayli Lee", "412 Beresford Ave, Toronto, ON, Canada", "905-789-1020", 43.656315, -79.4799619);
        createCustomer("Josh Shin", "6 Ivy Lea Crescent, Toronto, ON, Canada", "905-671-0923", 43.6425977, -79.4971738);
        createCustomer("Ben Lifshits", "48 Aitken Cir, Markham, ON, Canada", "905-678-2137", 43.8756108, -79.3160648);
        createCustomer("Jamie Tchernov", "136 Fawnbrook Cir, Markham, ON, Canada", "905-011-6741", 43.8731424, -79.2997474);
        createCustomer("Susan Ifrah", "14 Poplar Ave, Toronto, ON, Canada", "905-783-1292", 43.639466, -79.5419609);
        createCustomer("Danielle Bundas", "2137 Sheridan Park Dr, Mississauga, ON, Canada", "905-783-2263", 43.52842, -79.6594981);
        createCustomer("Daniel Charron", "64 Tanbark Crescent, Toronto, ON, Canada", " 905-913-7833", 43.7439792, -79.362369);
        createCustomer("Melanie Collins", "56 Ellis Park Rd, Toronto, ON, Canada", "905-673-1372", 43.648012, -79.4712705);
        createCustomer("Jack Cukier", "1730 Featherston Ct, Mississauga, ON, Canada", " 905-881-7973", 43.5508894, -79.668833);
        createCustomer("Mike Mudryk", "2 Appian Dr, Toronto, ON, Canada", "905-893-7224", 43.7830531, -79.3712206);
        createCustomer("Ina Tavroges", "122 S Kingsway, Toronto, ON, Canada", "905-783-7833", 43.641344, -79.4792224);
        createCustomer("Sally Pludwinsky", "130 Thistle Down Blvd, Toronto, ON, Canada", "905-871-0034", 43.7391628, -79.5520249);
        createCustomer("Sarah Bakov", "778 Huntingwood Dr, Toronto, ON, Canada", "905-834-0384", 43.7911686, -79.2992792);
        createCustomer("Naomi Ecclestone", "30 Wingate Crescent, Richmond Hill, ON, Canada", " 905-992-6784", 43.8681119, -79.3982085);
        createCustomer("Nick Sheftz", "36 Cabana Dr, Toronto, ON, Canada", "905-112-3329", 43.763765, -79.5694451);
        createCustomer("Nicolas Mitretodas", "62 Wildrose Crescent, Markham, ON, Canada", "905-232-4155", 43.8143245, -79.3899493);
        createCustomer("Gabrielle Drotenko", "1470 Orchard Haven Ridge, Mississauga, ON, Canada", "905-783-8641", 43.5962587, -79.5618033);
        createCustomer("Nathan Levy", "2502 Jane St, Toronto, ON, Canada", "905-783-8642", 43.735778, -79.5122005);
        createCustomer("Henri Lewis", "32 Topcliff Ave, Toronto, ON, Canada", "905-678-8641", 43.7571145, -79.5082499);
        createCustomer("Benzi Invandaevech", "100 New Forest Square, Toronto, ON, Canada", "905-862-6342", 43.822197, -79.3083987);
        createCustomer("Gino Lindsay", "188 Sandy Haven Dr, Toronto, ON, Canada", "905-762-1112", 43.8078893, -79.3172662);
        createCustomer("Mehran Rek", "20 Sanderson Crescent, Richmond Hill, ON, Canada", "905-764-1234", 43.8510377, -79.452715);
        createCustomer("Farid Goldstein", "4 Greene Dr, Brampton, ON, Canada", "905-864-8644", 43.7045061, -79.7578686);
        createCustomer("Nikol Laszio", "22 Crewe Ave, Toronto, ON, Canada", "905-867-8634", 43.6936504, -79.3083712);
        createCustomer("Mikayla Drummond", "16 Mountview Ave, Toronto, ON, Canada", "905-864-6636", 43.6547416, -79.4611492);
        createCustomer("Tarryn Romm", "2644 Burnford Trail, Mississauga, ON, Canada", "905-864-8611", 43.5643042, -79.7216129);
        createCustomer("Faith Katsnelson", "84 Ascolda Blvd, Toronto, ON, Canada", "905-783-7644", 43.7470193, -79.2366832);
        createCustomer("Vlad Gotkin", "4706 Mississauga Rd, Mississauga, ON, Canada", "905-864-7642", 43.5688283, -79.6963535);
        createCustomer("Sergei Einstoss", "34 Stanford Rd, Markham, ON, Canada", "905-864-3211", 43.8685385, -79.3081106);
        createCustomer("Denis Gerstein", "10 Minford Ave, Toronto, ON, Canada", "905-891-0024", 43.7373197, -79.2901801);
        createCustomer("Jake Krebs", "76 Havelock Gate, Markham, ON, Canada", "905-002-7326", 43.8543271, -79.245133);

        StringBuilder b = new StringBuilder();
        b.append("Created " + r.size() + " Resources").append('\n');
        b.append("Created " + customerCount + " Customers").append('\n');
        b.append("Created " + orderCount + " Orders");
        return b.toString();
    }

    private Resource selectResource(int number) {
        if (number < r.size()) {
            return r.get(number);
        } else {
            return r.get((r.size() - 1) % number);
        }
    }

    private void createCustomer(String name, String street, String phone, Double latitude, Double longitude) {
        Customer customer = EntityFactory.create(Customer.class);
        customer.name().setValue(name);
        customer.phone().add(phone);
        customer.street().setValue(street);
        customer.latitude().setValue(latitude);
        customer.longitude().setValue(longitude);
        customer.notes().add("Somthing important #" + customerCount);

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
        o2.resources().add(r.get(0));
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
