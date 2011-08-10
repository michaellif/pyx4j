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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.PreloadConfig;
import com.propertyvista.domain.User;
import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.UserCredential;

public class UserPreloader extends BaseVistaDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(UserPreloader.class);

    private int userCount;

    private int custCount;

    public UserPreloader(PreloadConfig config) {
        super(config);
    }

    private User createUser(String email, VistaBehavior behavior) {
        userCount++;
        return createUser(email, email, behavior);
    }

    public static User createUser(String email, String password, VistaBehavior behavior) {
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
        credential.credential().setValue(PasswordEncryptor.encryptPassword(email));
        credential.enabled().setValue(Boolean.TRUE);
        credential.behavior().setValue(behavior);

        PersistenceServicesFactory.getPersistenceService().persist(credential);

        return user;
    }

    @Override
    public String create() {

        createUser("michael.lifschitz@gmail.com", VistaBehavior.ADMIN);
        createUser("skarzhevskyy@gmail.com", VistaBehavior.ADMIN);

        if (ApplicationMode.isDevelopment()) {

            for (int i = 1; i <= config.getMaxAdmin(); i++) {
                createUser(DemoData.CRM_ADMIN_USER_PREFIX + CommonsStringUtils.d000(i) + DemoData.USERS_DOMAIN, VistaBehavior.ADMIN);
            }

            for (int i = 1; i <= config.getMaxPropertyManagers(); i++) {
                createUser(DemoData.CRM_PROPERTY_MANAGER_USER_PREFIX + CommonsStringUtils.d000(i) + DemoData.USERS_DOMAIN, VistaBehavior.PROPERTY_MANAGER);
            }

            for (int i = 1; i <= config.getMaxCustomers(); i++) {
                switch (custCount % 3) {
                case 0:
                    createUser(DemoData.CRM_CUSTOMER_USER_PREFIX + CommonsStringUtils.d000(i) + DemoData.USERS_DOMAIN, VistaBehavior.PROSPECTIVE_TENANT);
                    break;
                case 1:
                    createUser(DemoData.CRM_CUSTOMER_USER_PREFIX + CommonsStringUtils.d000(i) + DemoData.USERS_DOMAIN, VistaBehavior.PROSPECTIVE_TENANT);
                    break;
                case 2:
                    createUser(DemoData.CRM_CUSTOMER_USER_PREFIX + CommonsStringUtils.d000(i) + DemoData.USERS_DOMAIN, VistaBehavior.PROSPECTIVE_TENANT);
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
