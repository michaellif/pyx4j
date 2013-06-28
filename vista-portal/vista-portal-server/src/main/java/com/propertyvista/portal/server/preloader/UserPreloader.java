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
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.system.UserManagementFacade;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.generator.SecurityGenerator;
import com.propertyvista.generator.util.CommonsGenerator;
import com.propertyvista.operations.domain.security.OnboardingUserCredential;
import com.propertyvista.preloader.BaseVistaDevDataPreloader;
import com.propertyvista.server.domain.security.CrmUserCredential;
import com.propertyvista.server.domain.security.CustomerUserCredential;
import com.propertyvista.server.jobs.TaskRunner;
import com.propertyvista.shared.config.VistaDemo;

public class UserPreloader extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(UserPreloader.class);

    static CustomerUser createTenantUser(String name, String email, String password) {
        if (!ApplicationMode.isDevelopment()) {
            EntityQueryCriteria<CustomerUser> criteria = EntityQueryCriteria.create(CustomerUser.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
            List<CustomerUser> users = Persistence.service().query(criteria);
            if (users.size() != 0) {
                log.debug("User already exists");
                return users.get(0);
            }
        }
        CustomerUser user = EntityFactory.create(CustomerUser.class);

        user.name().setValue(name);
        user.email().setValue(email);

        Persistence.service().persist(user);

        CustomerUserCredential credential = EntityFactory.create(CustomerUserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.user().set(user);
        credential.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(email));
        credential.enabled().setValue(Boolean.TRUE);

        Persistence.service().persist(credential);

        return user;
    }

    public static CrmUser createCrmUser(String name, String email, String password, OnboardingUserCredential onbUserCred, CrmRole... roles) {
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
        credential.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(password));
        credential.enabled().setValue(Boolean.TRUE);
        credential.accessAllBuildings().setValue(Boolean.TRUE);
        for (CrmRole role : roles) {
            if (role != null) {
                credential.roles().add(role);
            }
        }

        if (ApplicationMode.isDevelopment() || VistaDemo.isDemo()) {
            SecurityGenerator.assignSecurityQuestion(credential);
        }

        if (onbUserCred != null) {
            credential.onboardingUser().setValue(onbUserCred.user().getPrimaryKey());
            credential.interfaceUid().setValue(onbUserCred.interfaceUid().getValue());
        }

        Persistence.service().persist(credential);

        return user;
    }

    @Override
    public String create() {
        int userCount = 0;

        CrmRole defaultRole = CrmRolesPreloader.getDefaultRole();
        CrmRole accountOwnerRole = CrmRolesPreloader.getPropertyVistaAccountOwnerRole();

        for (int i = 1; i <= config().maxPropertyManagers; i++) {
            String email = DemoData.UserType.PM.getEmail(i);

            Employee emp = CommonsGenerator.createEmployee().duplicate(Employee.class);
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(emp);
            emp.title().setValue("Executive");
            emp.email().setValue(email);

            CrmRole additinalRole = null;
            if (i == 2) {
                additinalRole = CrmRolesPreloader.getSupportRole();
            }

            emp.user().set(createCrmUser(emp.name().getStringView(), email, email, null, defaultRole, accountOwnerRole, additinalRole));

            Persistence.service().persist(emp);

            userCount++;
        }

        for (int i = 1; i <= config().maxPropertyManagementEmployee; i++) {
            String email = DemoData.UserType.EMP.getEmail(i);

            Employee emp = CommonsGenerator.createEmployee().duplicate(Employee.class);
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(emp);
            emp.title().setValue(CommonsGenerator.randomEmployeeTitle());
            emp.email().setValue(email);

            emp.user().set(createCrmUser(emp.name().getStringView(), email, email, null, defaultRole));

            Persistence.service().persist(emp);
            userCount++;
        }

        final String namespace = NamespaceManager.getNamespace();
        final Pmc pmc = VistaDeployment.getCurrentPmc();
        if (pmc != null) {
            for (int i = 1; i <= config().maxOnboardingUsers; i++) {
                final String email = DemoData.UserType.ONB.getEmail(i, namespace);

                final Employee emp = CommonsGenerator.createEmployee().duplicate(Employee.class);
                ServerSideFactory.create(IdAssignmentFacade.class).assignId(emp);
                emp.title().setValue(CommonsGenerator.randomEmployeeTitle());
                emp.email().setValue(email);

                final OnboardingUserCredential userCred = TaskRunner.runInOperationsNamespace(new Callable<OnboardingUserCredential>() {
                    @Override
                    public OnboardingUserCredential call() {
                        return ServerSideFactory.create(UserManagementFacade.class).createOnboardingUser(emp.name().firstName().getStringView(),
                                emp.name().lastName().getStringView(), email, email, VistaOnboardingBehavior.Client, null);
                    }
                });

                emp.user().set(createCrmUser(emp.name().getStringView(), email, email, userCred, defaultRole));

                Persistence.service().persist(emp);

                TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                    @Override
                    public Void call() {

                        userCred.pmc().set(pmc);
                        userCred.crmUser().setValue(emp.user().getPrimaryKey());
                        Persistence.service().persist(userCred);

                        return null;
                    }
                });

                userCount++;
            }
        }

        PmcCreator.createVistaSupportUsers();

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
