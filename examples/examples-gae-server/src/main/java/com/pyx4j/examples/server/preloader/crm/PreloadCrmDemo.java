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
import com.pyx4j.examples.domain.crm.DomainUtils;
import com.pyx4j.examples.domain.crm.Order;
import com.pyx4j.examples.domain.crm.Province;
import com.pyx4j.examples.domain.crm.Resource;
import com.pyx4j.examples.domain.crm.Order.OrderStatus;
import com.pyx4j.geo.GeoPoint;

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

        createCustomer("Jordan  Desai", "280 Willow Ave", "Toronto", Province.ON, "905-762-8993", 43.678425, -79.288309, "zTjkzWpfief8_5_qRPRXNg", 0.0);
        createCustomer("Michael Smith", "336 Walmer Rd", "Toronto", Province.ON, "905-884-8935", 43.6699245, -79.4069179, "c2uz0sLNVRTt7ns8we04SQ", 0.0);
        createCustomer("Elina Belso", "142 Inglewood Dr", "Toronto", Province.ON, "905-789-4328", 43.856316, -79.245758, "Vv_-2MF_pRa2CioSQ8n5GQ", 0.0);
        createCustomer("Melissa Boroda", "68 Hillcroft Dr", "Markham", Province.ON, "905-786-6782", 43.8347269, -79.2804875, "gd3vuyTCTVI_sWme9e3XtQ", 0.0);
        createCustomer("Karen Slater", "32 Roosevelt Dr", "Richmond Hill", Province.ON, "905-436-6873", 43.8416335, -79.4345507, "aMX7PEQiwHGr7s9Drb8izQ", 0.0);
        createCustomer("Michael Lifshits", "93 Crestwood Road", "Thornhill", Province.ON, "905-762-3333", 43.7987419, -79.4303477, "CtvPWRDBcLG4DFyGOL6MVw",
                0.0);
        createCustomer("Corben Garnet", "112 Cayuga Ave", "Toronto", Province.ON, "905-785-1222", 43.6785793, -79.4792064, "sFjyj3QuReGVP6mIuTYZyw", 0.0);
        createCustomer("Madison Ronen", "16 Estoril St", "Richmond Hill", Province.ON, "905-785-0099", 43.8559356, -79.4250644, "Qdcvg04dx-4DfW3kGb7R5g", 0.0);
        createCustomer("Hannah Kim", "3438 Africa Crescent", "Mississauga", Province.ON, "905-112-6563", 43.5801851, -79.6409809, "c5EMP1IvQ1-uHrocBtMGqA", 0.0);
        createCustomer("Cayli Lee", "412 Beresford Ave", "Toronto", Province.ON, "905-789-1020", 43.656315, -79.4799619, "HZ6wBO2_FpSCuKXwxPqyxw", 0.0);
        createCustomer("Josh Shin", "6 Ivy Lea Crescent", "Toronto", Province.ON, "905-671-0923", 43.6425977, -79.4971738, "NzxoTRzFT_JNZaceVgNNaQ", 0.0);
        createCustomer("Ben Lifshits", "48 Aitken Cir", "Markham", Province.ON, "905-678-2137", 43.8756108, -79.3160648, "lrmp9Wb7URLcgw5y6DftZw", 0.0);
        createCustomer("Jamie Tchernov", "136 Fawnbrook Cir", "Markham", Province.ON, "905-011-6741", 43.8731424, -79.2997474, "YUYYsfiOsIknefKHG_yy_g", 0.0);
        createCustomer("Susan Ifrah", "14 Poplar Ave", "Toronto", Province.ON, "905-783-1292", 43.639466, -79.5419609, "pU5cSsccSyPQwbzLy4TkpQ", 0.0);
        createCustomer("Danielle Bundas", "2137 Sheridan Park Dr", "Mississauga", Province.ON, "905-783-2263", 43.52842, -79.6594981, "xklJz7H4zQ4qDwAGrNB8vQ",
                0.0);
        createCustomer("Daniel Charron", "64 Tanbark Crescent", "Toronto", Province.ON, " 905-913-7833", 43.7439792, -79.362369, "U9zfxMN3gDLDAvP5f49K9A", 0.0);
        createCustomer("Melanie Collins", "56 Ellis Park Rd", "Toronto", Province.ON, "905-673-1372", 43.648012, -79.4712705, "EsR6iSjPKtf2H3oSBalGUQ", 0.0);
        createCustomer("Jack Cukier", "1730 Featherston Ct", "Mississauga", Province.ON, " 905-881-7973", 43.5508894, -79.668833, "gpalQquOfU-HC7srs11TrA", 0.0);
        createCustomer("Mike Mudryk", "2 Appian Dr", "Toronto", Province.ON, "905-893-7224", 43.7830531, -79.3712206, "pkpx5IK7m-8OuX2v01STaA", 0.0);
        createCustomer("Ina Tavroges", "122 S Kingsway", "Toronto", Province.ON, "905-783-7833", 43.641344, -79.4792224, "EwOHbtQNXH_TwU7xQQ-b8A", 0.0);
        createCustomer("Sally Pludwinsky", "130 Thistle Down Blvd", "Toronto", Province.ON, "905-871-0034", 43.7391628, -79.5520249, "pePSHj3PIIfnBB6rQNEZiQ",
                0.0);
        createCustomer("Sarah Bakov", "778 Huntingwood Dr", "Toronto", Province.ON, "905-834-0384", 43.7911686, -79.2992792, "BAyWqEL9tNtud5UkM8W7zA", 0.0);
        createCustomer("Naomi Ecclestone", "30 Wingate Crescent", "Richmond Hill", Province.ON, " 905-992-6784", 43.8681119, -79.3982085,
                "by0lZZ2w90RJihGjmN5IAQ", 0.0);
        createCustomer("Nick Sheftz", "36 Cabana Dr", "Toronto", Province.ON, "905-112-3329", 43.763765, -79.5694451, "XCFz1QV_7zPm5u5dj_WG5Q", 0.0);
        createCustomer("Nicolas Mitretodas", "62 Wildrose Crescent", "Markham", Province.ON, "905-232-4155", 43.8143245, -79.3899493, "4m5Xj-GBgO-eF-uQWekvzg",
                0.0);
        createCustomer("Gabrielle Drotenko", "1470 Orchard Haven Ridge", "Mississauga", Province.ON, "905-783-8641", 43.5962587, -79.5618033,
                "VOUmmUfefSkCgoJe7dAVxA", 0.0);
        createCustomer("Nathan Levy", "2502 Jane St", "Toronto", Province.ON, "905-783-8642", 43.735778, -79.5122005, "IGr0K2NdMlTv11X9Le3F4Q", 0.0);
        createCustomer("Henri Lewis", "32 Topcliff Ave", "Toronto", Province.ON, "905-678-8641", 43.7571145, -79.5082499, "jRMUhcsnTosXaifNxJR3UA", 0.0);
        createCustomer("Benzi Invandaevech", "100 New Forest Square", "Toronto", Province.ON, "905-862-6342", 43.822197, -79.3083987, "jspp1j-bjppx8fdvNCdL0g",
                0.0);
        createCustomer("Gino Lindsay", "188 Sandy Haven Dr", "Toronto", Province.ON, "905-762-1112", 43.8078893, -79.3172662, "QDyNLqEc3Nq_Fr5y-5Y2gA", 0.0);
        createCustomer("Mehran Rek", "20 Sanderson Crescent", "Richmond Hill", Province.ON, "905-764-1234", 43.8510377, -79.452715, "mYBp6RjLqdhSQIfj8ex_vw",
                0.0);
        createCustomer("Farid Goldstein", "4 Greene Dr", "Brampton", Province.ON, "905-864-8644", 43.7045061, -79.7578686, "sgq8J7iL-4rCApLzO7ULhg", 0.0);
        createCustomer("Nikol Laszio", "22 Crewe Ave", "Toronto", Province.ON, "905-867-8634", 43.6936504, -79.3083712, "Zy6UWTQZWSh5IMUyTUlGqg", 0.0);
        createCustomer("Mikayla Drummond", "16 Mountview Ave", "Toronto", Province.ON, "905-864-6636", 43.6547416, -79.4611492, "GgQzQdzwdJ8DmCZEK5Qfog", 0.0);
        createCustomer("Tarryn Romm", "2644 Burnford Trail", "Mississauga", Province.ON, "905-864-8611", 43.5643042, -79.7216129, "F2bpjMw0yen-N2BGW2gJ0g", 0.0);
        createCustomer("Faith Katsnelson", "84 Ascolda Blvd", "Toronto", Province.ON, "905-783-7644", 43.7470193, -79.2366832, "-V9V76INSU0E_Cmh9nz3Hw", 0.0);
        createCustomer("Vlad Gotkin", "4706 Mississauga Rd", "Mississauga", Province.ON, "905-864-7642", 43.5688283, -79.6963535, "e6iFPzjnuPfO8-bCk1Zuxg", 0.0);
        createCustomer("Sergei Einstoss", "34 Stanford Rd", "Markham", Province.ON, "905-864-3211", 43.8685385, -79.3081106, "G6nxy32MSFBxFDzyqBFd3w", 0.0);
        createCustomer("Denis Gerstein", "10 Minford Ave", "Toronto", Province.ON, "905-891-0024", 43.7373197, -79.2901801, "wYYf15QWO-j8Mc88K-zfjQ", 0.0);
        createCustomer("Jake Krebs", "76 Havelock Gate", "Markham", Province.ON, "905-002-7326", 43.8543271, -79.245133, "gChNRWoIKEf7cXewYUErLw", 0.0);

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

    private void createCustomer(String name, String street, String city, Province province, String phone, Double latitude, Double longitude, String panoId,
            Double panoYaw) {
        Customer customer = EntityFactory.create(Customer.class);
        customer.name().setValue(name);
        customer.phone().setValue(phone);
        customer.address().street().setValue(street);
        customer.address().city().setValue(city);
        customer.address().province().setValue(province);
        customer.location().setValue(new GeoPoint(latitude, longitude));
        customer.panoId().setValue(panoId);
        customer.panoYaw().setValue(panoYaw);
        customer.note().setValue("Something important #" + customerCount);
        PersistenceServicesFactory.getPersistenceService().persist(customer);

        Order o1 = EntityFactory.create(Order.class);
        o1.description().setValue("Cat " + customerCount);
        o1.status().setValue(OrderStatus.ACTIVE);
        o1.resource().set(selectResource(customerCount + 1));
        o1.customer().set(customer);
        DomainUtils.denormalizationOrder(o1, customer);
        customer.orders().add(o1);
        customer.orderStatus().add(o1.status().getValue());
        PersistenceServicesFactory.getPersistenceService().persist(o1);
        orderCount++;

        Order o2 = EntityFactory.create(Order.class);
        o2.description().setValue("Dog " + customerCount);
        o2.status().setValue(OrderStatus.COMPLETED);
        o2.resource().set(selectResource(customerCount + 1));
        o2.customer().set(customer);
        DomainUtils.denormalizationOrder(o2, customer);
        customer.orders().add(o2);
        customer.orderStatus().add(o2.status().getValue());
        PersistenceServicesFactory.getPersistenceService().persist(o2);
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
