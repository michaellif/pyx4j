/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 9, 2015
 * @author ernestog
 */
package com.propertyvista.biz.preloader.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.generator.SecurityGenerator;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.preloader.CrmRolesPreloader;
import com.propertyvista.biz.preloader.UserPreloaderFacade;
import com.propertyvista.biz.system.UserManagementFacade;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CrmUserCredential;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.CustomerUserCredential;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.domain.security.common.AbstractUserCredential;

public class UserPreloaderFacadeImpl implements UserPreloaderFacade {

    private final static Logger log = LoggerFactory.getLogger(UserPreloaderFacadeImpl.class);

    @Override
    public void createVistaSupportUsers() {
        createCrmEmployee("Support", "PropertyVista", CrmUser.VISTA_SUPPORT_ACCOUNT_EMAIL, null, null, CrmRolesPreloader.getDefaultRole());

    }

    @Override
    public CrmUser createCrmEmployee(String firstName, String lastName, String email, String password, Key onboardingUserKey, CrmRole... roles) {
        if (!ApplicationMode.isDevelopment()) {
            CrmUser cmrUser = getPmcUserByEmail(CrmUser.class, email);
            if (cmrUser != null) {
                return cmrUser;
            }
        }
        CrmUser user = EntityFactory.create(CrmUser.class);

        user.name().setValue(CommonsStringUtils.nvl_concat(firstName, lastName, " "));
        user.email().setValue(email);

        Persistence.service().persist(user);

        Employee employee = EntityFactory.create(Employee.class); //creates employee in crm
        ServerSideFactory.create(IdAssignmentFacade.class).assignId(employee);
        employee.user().set(user);
        employee.name().firstName().setValue(firstName);
        employee.name().lastName().setValue(lastName);
        employee.email().setValue(email);

        Persistence.service().persist(employee);

        CrmUserCredential credential = EntityFactory.create(CrmUserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.onboardingUser().setValue(onboardingUserKey);

        credential.user().set(user);

        if (password != null) {
            credential.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(password));
        }
        if (ApplicationMode.isDevelopment() || ApplicationMode.isDemo()) {
            assignSecurityQuestion(credential);
        }
        credential.enabled().setValue(Boolean.TRUE);
        credential.accessAllBuildings().setValue(Boolean.TRUE);
        for (CrmRole role : roles) {
            if (role != null) {
                credential.roles().add(role);
            }
        }
        Persistence.service().persist(credential);
        ServerSideFactory.create(UserManagementFacade.class).createGlobalCrmUserIndex(user);

        return user;
    }

    @Override
    public void assignSecurityQuestion(AbstractUserCredential<?> credential) {
        SecurityGenerator.assignSecurityQuestion(credential);
    }

    @Override
    public CrmUser createCrmUser(String name, String email, String password, CrmRole... roles) {
        if (!ApplicationMode.isDevelopment()) {
            CrmUser cmrUser = getPmcUserByEmail(CrmUser.class, email);
            if (cmrUser != null) {
                return cmrUser;
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

        if (ApplicationMode.isDevelopment() || ApplicationMode.isDemo()) {
            ServerSideFactory.create(UserPreloaderFacade.class).assignSecurityQuestion(credential);
        }

        Persistence.service().persist(credential);
        ServerSideFactory.create(UserManagementFacade.class).createGlobalCrmUserIndex(user);

        return user;
    }

    @Override
    public CustomerUser createTenantUser(String name, String email, String password) {
        if (!ApplicationMode.isDevelopment()) {
            CustomerUser customerUser = getPmcUserByEmail(CustomerUser.class, email);
            if (customerUser != null) {
                return customerUser;
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

    private <U extends AbstractPmcUser> U getPmcUserByEmail(Class<U> userTypeClass, String email) {
        EntityQueryCriteria<U> criteria = EntityQueryCriteria.create(userTypeClass);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        List<U> users = Persistence.service().query(criteria);
        if (users.size() != 0) {
            log.debug("User already exists");
            return users.get(0);
        } else {
            return null;
        }
    }

}
