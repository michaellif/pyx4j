/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 23, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.rdb.cfg.Configuration.MultitenancyType;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.generator.SecurityGenerator;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.operations.domain.security.OnboardingUserCredential;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.security.CrmUserCredential;
import com.propertyvista.shared.config.VistaDemo;

public class PmcCreator {

    private final static Logger log = LoggerFactory.getLogger(PmcCreator.class);

    public static void preloadPmc(Pmc pmc, OnboardingUser onbUser, OnboardingUserCredential onbUserCred) {
        assert onbUser != null;
        assert onbUserCred != null;

        final String namespace = NamespaceManager.getNamespace();
        NamespaceManager.setNamespace(pmc.namespace().getValue());
        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.BackgroundProcess).execute(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    RDBUtils.ensureNamespace();
                    if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                        RDBUtils.initAllEntityTables();
                    }
                    return null;
                }

            });

            AbstractDataPreloader preloader = VistaDataPreloaders.productionPmcPreloaders();
            preloader.setParameterValue(VistaDataPreloaderParameter.pmcName.name(), pmc.name().getStringView());
            log.info("Preload {}", preloader.create());

            CrmRole defaultRole = CrmRolesPreloader.getDefaultRole();
            CrmRole pvRole = CrmRolesPreloader.getPropertyVistaAccountOwnerRole();
            CrmUser crmUser = createCrmEmployee(onbUser.firstName().getValue(), onbUser.lastName().getValue(), onbUser.email().getValue(), null, onbUserCred,
                    defaultRole, pvRole);
            onbUserCred.crmUser().setValue(crmUser.getPrimaryKey());

            // Create support account by default
            createVistaSupportUsers();

            if (ApplicationMode.isDevelopment()) {
                for (int i = 1; i <= DemoData.UserType.PM.getDefaultMax(); i++) {
                    String email = DemoData.UserType.PM.getEmail(i);
                    CrmRole additinalRole = null;
                    if (i == 2) {
                        additinalRole = CrmRolesPreloader.getSupportRole();
                    }
                    createCrmEmployee(email, email, email, email, null, defaultRole, additinalRole);
                }
            }

        } finally {
            NamespaceManager.setNamespace(namespace);
        }
    }

    public static void createVistaSupportUsers() {
        createCrmEmployee("Support", "PropertyVista", CrmUser.VISTA_SUPPORT_ACCOUNT_EMAIL, null, null, CrmRolesPreloader.getDefaultRole(),
                CrmRolesPreloader.getSupportRole());
    }

    public static CrmUser createCrmEmployee(String firstName, String lastName, String email, String password, OnboardingUserCredential onbUserCred,
            CrmRole... roles) {
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

        user.name().setValue(CommonsStringUtils.nvl_concat(firstName, lastName, " "));
        user.email().setValue(email);

        Persistence.service().persist(user);

        Employee employee = EntityFactory.create(Employee.class); //creates employee in crm
        ServerSideFactory.create(IdAssignmentFacade.class).assignId(employee);
        employee.user().set(user);
        employee.name().firstName().setValue(firstName);
        employee.name().lastName().setValue(lastName);
        employee.email().setValue(email);
        if (onbUserCred != null) {
            employee.title().setValue("PMC Owner");
        }
        Persistence.service().persist(employee);

        CrmUserCredential credential = EntityFactory.create(CrmUserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.user().set(user);
        if (onbUserCred != null) {
            assert onbUserCred.user().getPrimaryKey() != null;
            credential.onboardingUser().setValue(onbUserCred.user().getPrimaryKey());
            credential.credential().setValue(onbUserCred.credential().getValue());
            credential.interfaceUid().setValue(onbUserCred.interfaceUid().getValue());
        } else if (password != null) {
            credential.credential().setValue(PasswordEncryptor.encryptPassword(password));
        }
        if (ApplicationMode.isDevelopment() || VistaDemo.isDemo()) {
            SecurityGenerator.assignSecurityQuestion(credential);
        }
        credential.enabled().setValue(Boolean.TRUE);
        credential.accessAllBuildings().setValue(Boolean.TRUE);
        for (CrmRole role : roles) {
            if (role != null) {
                credential.roles().add(role);
            }
        }
        Persistence.service().persist(credential);

        return user;
    }

}
