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
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.server.preloader;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.examples.domain.DemoData;
import com.pyx4j.examples.domain.ExamplesBehavior;
import com.pyx4j.examples.domain.User;
import com.pyx4j.examples.domain.UserCredential;

public class PreloadUsers extends AbstractDataPreloader {

    private int userCount;

    private User createUser(String name, String email, ExamplesBehavior behavior) {
        User user = EntityFactory.create(User.class);
        UserCredential credential = EntityFactory.create(UserCredential.class);

        user.email().setValue(email);
        user.name().setValue(name);

        credential.user().set(user);
        credential.credential().setValue(email);

        credential.enabled().setValue(Boolean.TRUE);
        credential.behavior().setValue(behavior);

        PersistenceServicesFactory.getPersistenceService().persist(user);
        credential.setPrimaryKey(user.getPrimaryKey());
        PersistenceServicesFactory.getPersistenceService().persist(credential);

        userCount++;
        return user;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(User.class, UserCredential.class);
    }

    @Override
    public String create() {
        userCount = 0;

        createUser("CRM Admin", DemoData.CRM_ADMIN_USER_PREFIX + "001" + DemoData.USERS_DOMAIN, ExamplesBehavior.CRM_ADMIN);

        createUser("Misha", "michael.lifschitz@gmail.com", ExamplesBehavior.CRM_ADMIN);
        createUser("Vlad", "skarzhevskyy@gmail.com", ExamplesBehavior.CRM_ADMIN);
        createUser("Tester", "test1@pyx4j.com", ExamplesBehavior.CRM_ADMIN);
        if (isGAEDevelopment()) {
            createUser("Developer", "test@example.com", ExamplesBehavior.CRM_ADMIN);
        }

        for (int i = 1; i < DemoData.maxCustomers; i++) {
            createUser("Customer No" + CommonsStringUtils.d000(i), DemoData.CRM_CUSTOMER_USER_PREFIX + CommonsStringUtils.d000(i) + DemoData.USERS_DOMAIN,
                    ExamplesBehavior.CRM_CUSTOMER);
        }

        for (int i = 1; i < DemoData.maxEmployee; i++) {
            createUser("Emp No" + CommonsStringUtils.d000(i), DemoData.CRM_EMPLOYEE_USER_PREFIX + CommonsStringUtils.d000(i) + DemoData.USERS_DOMAIN,
                    ExamplesBehavior.CRM_EMPLOYEE);
        }

        StringBuilder b = new StringBuilder();
        b.append("Created " + userCount + " Users");
        return b.toString();
    }
}
