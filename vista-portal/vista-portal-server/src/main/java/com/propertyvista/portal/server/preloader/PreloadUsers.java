/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.VistaBehavior;
import com.propertyvista.portal.server.access.VistaAuthenticationServicesImpl;
import com.propertyvista.server.domain.UserCredential;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

public class PreloadUsers extends AbstractDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PreloadUsers.class);

    private int userCount;

    private int custCount;

    private User createUser(String email, VistaBehavior behavior) {
        if (!ApplicationMode.isDevelopment()) {
            EntityQueryCriteria<User> criteria = EntityQueryCriteria.create(User.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
            List<User> users = PersistenceServicesFactory.getPersistenceService().query(criteria);
            if (users.size() != 0) {
                log.debug("User alredy exists");
                return users.get(0);
            }
        }
        User user = EntityFactory.create(User.class);

        user.name().setValue(email.substring(0, email.indexOf('@')));
        user.email().setValue(email);

        PersistenceServicesFactory.getPersistenceService().persist(user);

        UserCredential credential = EntityFactory.create(UserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.user().set(user);
        credential.credential().setValue(VistaAuthenticationServicesImpl.encryptPassword(email));
        credential.enabled().setValue(Boolean.TRUE);
        credential.behavior().setValue(behavior);

        PersistenceServicesFactory.getPersistenceService().persist(credential);

        userCount++;
        return user;
    }

    @Override
    public String create() {

        createUser("michael.lifschitz@gmail.com", VistaBehavior.ADMIN);
        createUser("skarzhevskyy@gmail.com", VistaBehavior.ADMIN);

        if (ApplicationMode.isDevelopment()) {

            for (int i = 1; i <= DemoData.maxAdmin; i++) {
                createUser(DemoData.CRM_ADMIN_USER_PREFIX + CommonsStringUtils.d000(i) + DemoData.USERS_DOMAIN, VistaBehavior.ADMIN);
            }

            for (int i = 1; i <= DemoData.maxCustomers; i++) {
                switch (custCount % 3) {
                case 0:
                    createUser(DemoData.CRM_CUSTOMER_USER_PREFIX + CommonsStringUtils.d000(i) + DemoData.USERS_DOMAIN, VistaBehavior.POTENCIAL_TENANT);
                    break;
                case 1:
                    createUser(DemoData.CRM_CUSTOMER_USER_PREFIX + CommonsStringUtils.d000(i) + DemoData.USERS_DOMAIN, VistaBehavior.POTENCIAL_TENANT);
                    break;
                case 2:
                    createUser(DemoData.CRM_CUSTOMER_USER_PREFIX + CommonsStringUtils.d000(i) + DemoData.USERS_DOMAIN, VistaBehavior.POTENCIAL_TENANT);
                    break;
                }
                custCount++;

            }
        }

        return "Created " + userCount + " Users";
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(User.class, UserCredential.class);
        } else {
            return "This is production";
        }
    }

}
