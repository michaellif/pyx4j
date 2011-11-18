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

import com.propertvista.generator.PreloadData;
import com.propertvista.generator.util.CommonsGenerator;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.User;
import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.UserCredential;

public class UserPreloader extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(UserPreloader.class);

    public static User createUser(String email, String password, VistaBehavior behavior) {
        return createUser(email.substring(0, email.indexOf('@')), email, password, behavior);
    }

    public static User createUser(String name, String email, String password, VistaBehavior behavior) {
        if (!ApplicationMode.isDevelopment()) {
            EntityQueryCriteria<User> criteria = EntityQueryCriteria.create(User.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
            List<User> users = Persistence.service().query(criteria);
            if (users.size() != 0) {
                log.debug("User already exists");
                return users.get(0);
            }
        }
        User user = EntityFactory.create(User.class);

        user.name().setValue(name);
        user.email().setValue(email);

        Persistence.service().persist(user);

        UserCredential credential = EntityFactory.create(UserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.user().set(user);
        credential.credential().setValue(PasswordEncryptor.encryptPassword(email));
        credential.enabled().setValue(Boolean.TRUE);
        credential.behavior().setValue(behavior);

        Persistence.service().persist(credential);

        return user;
    }

    @Override
    public String create() {
        int userCount = 0;
        for (int i = 1; i <= config().maxPropertyManagers; i++) {
            String email = DemoData.UserType.PM.getEmail(i);

            Employee emp = CommonsGenerator.createEmployee().clone(Employee.class);
            emp.title().setValue("Executive");
            emp.email().address().setValue(email);

            emp.user().set(UserPreloader.createUser(emp.name().getStringView(), email, email, VistaBehavior.PROPERTY_MANAGER));

            Persistence.service().persist(emp);

            userCount++;
        }

        for (int i = 1; i <= config().maxPropertyManagementEmployee; i++) {
            String email = DemoData.UserType.EMP.getEmail(i);

            Employee emp = CommonsGenerator.createEmployee().clone(Employee.class);
            emp.title().setValue(RandomUtil.random(PreloadData.pmcEmployeeTitles));
            emp.email().address().setValue(email);

            emp.user().set(UserPreloader.createUser(emp.name().getStringView(), email, email, RandomUtil.random(VistaBehavior.getCrmBehaviors())));

            Persistence.service().persist(emp);
            userCount++;
        }
        return "Created " + userCount + " Employee/Users";
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
