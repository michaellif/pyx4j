/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services.steps.welcomewizard;

import java.util.Random;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class WelcomeWizardDemoData {

    private static final Random RND = new Random(9000);

    public static Customer applicantsCustomer() {
//        Person person = EntityFactory.create(Person.class);
//        person.name().namePrefix().setValue(Prefix.Mr);
//        person.name().firstName().setValue("Frodo");
//        person.name().lastName().setValue("Baggins");
//        person.email().setValue("frodob@shire.net");
//        person.birthDate().setValue(new LogicalDate(1997 - 1900, 1, 1));
//        person.sex().setValue(Sex.Male);

        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), PtAppContext.getCurrentUser()));
        Customer customer = Persistence.service().retrieve(criteria);
        return customer;
    }

    public static LogicalDate leaseStart() {
        return new LogicalDate();
    }

    public static Random rnd() {
        return RND;
    }

    public static AddressSimple applicantsAddress() {
//        EntityQueryCriteria<PersonScreening> criteria = EntityQueryCriteria.create(PersonScreening.class);
//        criteria.add(PropertyCriterion.eq(criteria.proto().screene(), applicantsCustomer()));
//        PersonScreening screening = Persistence.service().retrieve(criteria);
        AddressSimple address = EntityFactory.create(AddressSimple.class);
        address.city().setValue("Toronto");
        address.street1().setValue("1 Yonge Street, unit 500");

        EntityQueryCriteria<Country> criteria = EntityQueryCriteria.create(Country.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().name(), "Canada"));
        address.country().set(Persistence.service().retrieve(criteria));

        EntityQueryCriteria<Province> provinceCriteria = EntityQueryCriteria.create(Province.class);
        provinceCriteria.add(PropertyCriterion.eq(provinceCriteria.proto().name(), "Ontario"));
        address.province().set(Persistence.service().retrieve(provinceCriteria));

        address.postalCode().setValue("M2H 4Z9");

        return address;
    }
}
