/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 20, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.services.dev;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.security.server.EmailValidator;

import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.generator.util.CommonsGenerator;
import com.propertyvista.shared.services.dev.MockDataGenerator;

public class MockDataGeneratorImpl implements MockDataGenerator {

    private static Object generatorContext;

    private static synchronized void initGeneratorContext() {
        if (generatorContext == null) {
            generatorContext = DataGenerator.getGeneratorContext();
        } else {
            DataGenerator.setGeneratorContext(generatorContext);
        }
    }

    @Override
    public void getPerson(AsyncCallback<Person> callback) {
        try {
            initGeneratorContext();
            Person person = EntityFactory.create(Person.class);
            person.name().set(CommonsGenerator.createName());
            person.email().setValue(uniqueEmail(person.name()));
            callback.onSuccess(person);
        } finally {
            DataGenerator.cleanup();
        }
    }

    static synchronized String uniqueEmail(Name person) {
        String email;
        int count = 0;
        boolean exists;
        do {
            email = person.firstName().getStringView().toLowerCase() + "." + person.lastName().getStringView().toLowerCase();
            if (count != 0) {
                email += String.valueOf(count);
            }
            email += "@pyx4j.com";
            EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
            criteria.eq(criteria.proto().person().email(), EmailValidator.normalizeEmailAddress(email));
            exists = Persistence.service().exists(criteria);
            count++;
        } while (exists);
        return email;
    }

}
