/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.organization;

import java.util.concurrent.Callable;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.organization.EmployeeCrudService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.common.security.UserAccessUtils;
import com.propertyvista.server.domain.security.CrmUserCredential;
import com.propertyvista.server.jobs.TaskRunner;

public class EmployeeCrudServiceImpl extends AbstractCrudServiceDtoImpl<Employee, EmployeeDTO> implements EmployeeCrudService {

    public EmployeeCrudServiceImpl() {
        super(Employee.class, EmployeeDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Employee entity, EmployeeDTO dto) {
        // Load detached data:
        Persistence.service().retrieve(dto.portfolios());
        Persistence.service().retrieve(dto.employees());

        //TODO proper Role
        if (SecurityController.checkBehavior(VistaCrmBehavior.Organization) && (entity.user().getPrimaryKey() != null)) {
            CrmUserCredential crs = Persistence.service().retrieve(CrmUserCredential.class, entity.user().getPrimaryKey());
            dto.enabled().set(crs.enabled());
            dto.accessAllBuildings().set(crs.accessAllBuildings());
            dto.requireChangePasswordOnNextLogIn().setValue(crs.requiredPasswordChangeOnNextLogIn().getValue());
            dto.roles().addAll(crs.roles());
        }
    }

    @Override
    protected void persist(Employee dbo, EmployeeDTO in) {
        super.persist(dbo, in);
        if (SecurityController.checkBehavior(VistaCrmBehavior.Organization)) {
            CrmUser user;
            boolean isNew = false;

            if (in.user().getPrimaryKey() != null) {
                user = Persistence.service().retrieve(CrmUser.class, dbo.user().getPrimaryKey());
            } else {
                user = dbo.user();
                isNew = true;
            }

            if (Context.getVisit().getUserVisit().getPrincipalPrimaryKey().equals(dbo.user().getPrimaryKey())) {
                Context.getVisit().getUserVisit().setName(dbo.name().getStringView());
            }

            user.name().setValue(dbo.name().getStringView());
            user.email().setValue(PasswordEncryptor.normalizeEmailAddress(dbo.email().getStringView()));
            Persistence.service().persist(user);

            if (isNew) {
                dbo.user().set(user);
                Persistence.service().persist(dbo);
            }

            boolean isPropVistaAccOwner = false;
            CrmUserCredential credential;
            if (!isNew) {
                credential = Persistence.service().retrieve(CrmUserCredential.class, in.user().getPrimaryKey());

                for (CrmRole role : credential.roles()) {
                    for (VistaCrmBehavior vcb : role.behaviors()) {
                        if (vcb == VistaCrmBehavior.PropertyVistaAccountOwner) {
                            isPropVistaAccOwner = true;
                            break;
                        }
                    }

                    if (isPropVistaAccOwner) { // If ther a need to create onborading user
                        break;
                    }
                }
            } else {
                credential = EntityFactory.create(CrmUserCredential.class);
                credential.setPrimaryKey(user.getPrimaryKey());
                credential.user().set(user);
                credential.credential().setValue(PasswordEncryptor.encryptPassword(in.password().getValue()));
            }
            credential.enabled().set(in.enabled());
            credential.roles().clear();
            credential.roles().addAll(in.roles());
            credential.accessAllBuildings().set(in.accessAllBuildings());
            credential.requiredPasswordChangeOnNextLogIn().setValue(in.requireChangePasswordOnNextLogIn().getValue());
            Persistence.service().persist(credential);
            credential.interfaceUid().setValue(UserAccessUtils.getCrmUserInterfaceUid(credential));
            Persistence.service().persist(credential);

            boolean isPropVistaAccOwnerSet = false;
            for (CrmRole role : in.roles()) {
                for (VistaCrmBehavior vcb : role.behaviors()) {
                    if (vcb == VistaCrmBehavior.PropertyVistaAccountOwner) {
                        isPropVistaAccOwnerSet = true;
                        break;
                    }
                }

                if (isPropVistaAccOwnerSet) { // If ther a need to create onborading user
                    break;
                }
            }

            final String namespace = NamespaceManager.getNamespace();
            final Pmc pmc = TaskRunner.runInAdminNamespace(new Callable<Pmc>() {
                @Override
                public Pmc call() {
                    EntityQueryCriteria<Pmc> pmcCret = EntityQueryCriteria.create(Pmc.class);
                    pmcCret.add(PropertyCriterion.eq(pmcCret.proto().namespace(), namespace));

                    return Persistence.service().retrieve(pmcCret);
                }
            });

            final OnboardingUser onbUser;
            final OnboardingUserCredential onbUserCred;

            if (isNew || !isPropVistaAccOwner) {
                onbUser = EntityFactory.create(OnboardingUser.class);
                onbUserCred = EntityFactory.create(OnboardingUserCredential.class);
            } else {
                final EntityQueryCriteria<OnboardingUserCredential> onbUCrt = EntityQueryCriteria.create(OnboardingUserCredential.class);
                onbUCrt.add(PropertyCriterion.eq(onbUCrt.proto().crmUser(), user.getPrimaryKey()));

                onbUserCred = TaskRunner.runInAdminNamespace(new Callable<OnboardingUserCredential>() {
                    @Override
                    public OnboardingUserCredential call() {

                        return Persistence.service().retrieve(onbUCrt);
                    }
                });

                onbUser = TaskRunner.runInAdminNamespace(new Callable<OnboardingUser>() {
                    @Override
                    public OnboardingUser call() {
                        return Persistence.service().retrieve(OnboardingUser.class, onbUserCred.user().getPrimaryKey());
                    }
                });
            }

            if (isPropVistaAccOwnerSet) {
                onbUser.firstName().setValue(in.name().firstName().getValue());
                onbUser.lastName().setValue(in.name().lastName().getValue());
                onbUser.name().setValue(user.name().getValue());
                onbUser.email().setValue(user.email().getValue());

                onbUserCred.crmUser().setValue(user.getPrimaryKey());
                onbUserCred.behavior().setValue(VistaOnboardingBehavior.Client);
                onbUserCred.credential().setValue(PasswordEncryptor.encryptPassword(in.password().getValue()));
                onbUserCred.enabled().setValue(in.enabled().getValue());
                onbUserCred.onboardingAccountId().setValue(null);
                onbUserCred.requiredPasswordChangeOnNextLogIn().setValue(in.requireChangePasswordOnNextLogIn().getValue());
                onbUserCred.interfaceUid().setValue(credential.interfaceUid().getValue());

                Boolean res = TaskRunner.runInAdminNamespace(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        Persistence.service().persist(onbUser);

                        onbUserCred.pmc().set(pmc);
                        onbUserCred.setPrimaryKey(onbUser.getPrimaryKey());

                        onbUserCred.user().set(onbUser);

                        Persistence.service().persist(onbUserCred);
                        return Boolean.TRUE;
                    }
                });

                if (res == Boolean.TRUE) {
                    credential.onboardingUser().setValue(onbUser.getPrimaryKey());
                    Persistence.service().persist(credential);
                }

            } else { // Delete if role removed
                TaskRunner.runInAdminNamespace(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        if (onbUserCred.getPrimaryKey() != null)
                            Persistence.service().delete(OnboardingUserCredential.class, onbUserCred.getPrimaryKey());

                        if (onbUser.getPrimaryKey() != null)
                            Persistence.service().delete(OnboardingUser.class, onbUser.getPrimaryKey());
                        return null;
                    }
                });
            }
        }
    }
}
