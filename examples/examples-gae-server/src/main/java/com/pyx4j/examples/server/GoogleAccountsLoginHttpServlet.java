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
 * Created on Sep 20, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.server;

import java.util.List;
import java.util.Locale;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.examples.domain.User;
import com.pyx4j.examples.domain.UserCredential;

@SuppressWarnings("serial")
public class GoogleAccountsLoginHttpServlet extends com.pyx4j.security.server.GoogleAccountsLoginHttpServlet {

    @Override
    protected void onLoginCompleted() {
        UserService userService = UserServiceFactory.getUserService();
        if (!userService.isUserLoggedIn()) {
            return;
        }
        String email = userService.getCurrentUser().getEmail();
        if (CommonsStringUtils.isEmpty(email)) {
            return;
        }
        email = email.toLowerCase(Locale.ENGLISH);
        EntityQueryCriteria<User> criteria = EntityQueryCriteria.create(User.class);
        criteria.add(PropertyCriterion.eq(criteria.meta().email(), email));
        List<User> users = PersistenceServicesFactory.getPersistenceService().query(criteria);
        if (users.size() != 1) {
            return;
        }
        User user = users.get(0);
        UserCredential userCredential = PersistenceServicesFactory.getPersistenceService().retrieve(UserCredential.class, user.getPrimaryKey());
        if (userCredential == null) {
            throw new RuntimeException("Invalid user account, contact support");
        }

        ExamplesAuthenticationServicesImpl.beginSession(user, userCredential);
    }
}
