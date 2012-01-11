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

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertvista.generator.util.CommonsGenerator;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.security.CrmUserCredential;
import com.propertyvista.server.domain.security.TenantUserCredential;

public class UserPreloader extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(UserPreloader.class);

    static TenantUser createTenantUser(String name, String email, String password, VistaTenantBehavior... behaviors) {
        if (!ApplicationMode.isDevelopment()) {
            EntityQueryCriteria<TenantUser> criteria = EntityQueryCriteria.create(TenantUser.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
            List<TenantUser> users = Persistence.service().query(criteria);
            if (users.size() != 0) {
                log.debug("User already exists");
                return users.get(0);
            }
        }
        TenantUser user = EntityFactory.create(TenantUser.class);

        user.name().setValue(name);
        user.email().setValue(email);

        Persistence.service().persist(user);

        TenantUserCredential credential = EntityFactory.create(TenantUserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.user().set(user);
        credential.credential().setValue(PasswordEncryptor.encryptPassword(email));
        credential.enabled().setValue(Boolean.TRUE);
        credential.behaviors().addAll(Arrays.asList(behaviors));

        Persistence.service().persist(credential);

        return user;
    }

    public static CrmUser createUser(String name, String email, String password, Collection<VistaCrmBehavior> behaviors) {
        if (!ApplicationMode.isDevelopment()) {
            EntityQueryCriteria<CrmUser> criteria = EntityQueryCriteria.create(CrmUser.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
            List<CrmUser> users = Persistence.service().query(criteria);
            if (users.size() != 0) {
                log.debug("User already exists");
                return users.get(0);
            }
        }
        CrmUser user = EntityFactory.create(CrmUser.class);

        user.name().setValue(name);
        user.email().setValue(email);

        Persistence.service().persist(user);

        CrmUserCredential credential = EntityFactory.create(CrmUserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.user().set(user);
        credential.credential().setValue(PasswordEncryptor.encryptPassword(email));
        credential.enabled().setValue(Boolean.TRUE);
        credential.accessAllBuildings().setValue(Boolean.TRUE);
        credential.behaviors().addAll(behaviors);

        Persistence.service().persist(credential);

        return user;
    }

    @Override
    public String create() {
        int userCount = 0;
        for (int i = 1; i <= config().maxPropertyManagers; i++) {
            String email = DemoData.UserType.PM.getEmail(i);

            Employee emp = CommonsGenerator.createEmployee().duplicate(Employee.class);
            emp.title().setValue("Executive");
            emp.email().address().setValue(email);

            emp.user().set(createUser(emp.name().getStringView(), email, email, EnumSet.allOf(VistaCrmBehavior.class)));

            Persistence.service().persist(emp);

            userCount++;
        }

        for (int i = 1; i <= config().maxPropertyManagementEmployee; i++) {
            String email = DemoData.UserType.EMP.getEmail(i);

            Employee emp = CommonsGenerator.createEmployee().duplicate(Employee.class);
            emp.title().setValue(CommonsGenerator.randomEmployeeTitle());
            emp.email().address().setValue(email);

            emp.user().set(createUser(emp.name().getStringView(), email, email, EnumSet.of(RandomUtil.randomEnum(VistaCrmBehavior.class))));

            Persistence.service().persist(emp);
            userCount++;
        }
        return "Created " + userCount + " Employee/Users";
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(CrmUser.class, CrmUserCredential.class);
        } else {
            return "This is production";
        }
    }

}
