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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityDiff;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.server.EmailValidator;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.UserManagementFacade;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.organization.EmployeeCrudService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.UserAuditingConfigurationDTO;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.operations.domain.security.OnboardingUserCredential;
import com.propertyvista.server.common.security.UserAccessUtils;
import com.propertyvista.server.domain.security.BehaviorHolder;
import com.propertyvista.server.domain.security.CrmUserCredential;
import com.propertyvista.server.jobs.TaskRunner;

public class EmployeeCrudServiceImpl extends AbstractCrudServiceDtoImpl<Employee, EmployeeDTO> implements EmployeeCrudService {

    public EmployeeCrudServiceImpl() {
        super(Employee.class, EmployeeDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceRetrieved(Employee entity, EmployeeDTO dto, RetrieveTarget RetrieveTarget) {
        // Load detached data:
        Persistence.service().retrieveMember(entity.portfolios());
        dto.portfolios().set(entity.portfolios());

        Persistence.service().retrieveMember(entity.buildingAccess());
        dto.buildingAccess().set(entity.buildingAccess());
        BuildingFolderUtil.stripExtraData(dto.buildingAccess());

        Persistence.service().retrieve(dto.employees());

        //TODO proper Role
        if (SecurityController.checkBehavior(VistaCrmBehavior.Organization) && (entity.user().getPrimaryKey() != null)) {
            CrmUserCredential crs = Persistence.service().retrieve(CrmUserCredential.class, entity.user().getPrimaryKey());
            dto.enabled().set(crs.enabled());
            dto.restrictAccessToSelectedBuildingsAndPortfolios().setValue(!crs.accessAllBuildings().getValue(false));
            dto.requiredPasswordChangeOnNextLogIn().setValue(crs.requiredPasswordChangeOnNextLogIn().getValue());
            dto.roles().addAll(crs.roles());
            dto.credentialUpdated().setValue(crs.credentialUpdated().getValue());

            dto.userAuditingConfiguration().set(EntityFactory.create(UserAuditingConfigurationDTO.class));
        }

        Persistence.service().retrieveMember(entity.notifications());
        dto.notifications().set(entity.notifications());
        for (Notification item : dto.notifications()) {
            Persistence.service().retrieve(item.buildings());
            BuildingFolderUtil.stripExtraData(item.buildings());
            Persistence.service().retrieve(item.portfolios());
        }
    }

    @Override
    protected void persist(Employee dbo, EmployeeDTO in) {
        if (dbo.id().isNull()) {
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(dbo);
        }

        if (!in.restrictAccessToSelectedBuildingsAndPortfolios().getValue(false)) {
            dbo.buildingAccess().clear();
            dbo.portfolios().clear();
        }

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
            user.email().setValue(EmailValidator.normalizeEmailAddress(dbo.email().getStringView()));
            Persistence.service().persist(user);

            if (isNew) {
                dbo.user().set(user);
                Persistence.service().persist(dbo);
            }

            BehaviorHolder behaviorsOriginal = EntityFactory.create(BehaviorHolder.class);
            CrmUserCredential credential;
            CrmUserCredential credentialOrig = null;
            if (!isNew) {
                credential = Persistence.service().retrieve(CrmUserCredential.class, in.user().getPrimaryKey());
                credentialOrig = credential.duplicate();
                behaviorsOriginal.permissions().addAll(ServerSideFactory.create(UserManagementFacade.class).getBehaviors(credential));
            } else {
                ServerSideFactory.create(AuditFacade.class).created(user);
                credential = EntityFactory.create(CrmUserCredential.class);
                credential.setPrimaryKey(user.getPrimaryKey());
                credential.user().set(user);
                credential.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(in.password().getValue()));
            }
            credential.enabled().set(in.enabled());
            credential.roles().clear();
            credential.roles().addAll(in.roles());
            credential.accessAllBuildings().setValue(!in.restrictAccessToSelectedBuildingsAndPortfolios().getValue(false));
            credential.requiredPasswordChangeOnNextLogIn().setValue(in.requiredPasswordChangeOnNextLogIn().getValue());

            Persistence.service().persist(credential);

            if (credentialOrig != null) {
                String diff = EntityDiff.getChanges(credentialOrig, credential, credential.credentialUpdated());
                if (diff.length() > 0) {
                    ServerSideFactory.create(AuditFacade.class).record(AuditRecordEventType.CredentialUpdate, credential.user(), "{0}, {1} ", user.email(),
                            diff);
                }
            }

            credential.interfaceUid().setValue(UserAccessUtils.getCrmUserInterfaceUid(credential));
            Persistence.service().persist(credential);

            BehaviorHolder behaviorsUpdated = EntityFactory.create(BehaviorHolder.class);
            behaviorsUpdated.permissions().addAll(ServerSideFactory.create(UserManagementFacade.class).getBehaviors(credential));

            if (!EqualsHelper.equals(behaviorsOriginal.permissions(), behaviorsUpdated.permissions())) {
                ServerSideFactory.create(AuditFacade.class).record(AuditRecordEventType.PermitionsUpdate, credential.user(), "{0}, {1} ", user.email(),
                        EntityDiff.getChanges(behaviorsOriginal, behaviorsUpdated));
            }

            final Pmc pmc = VistaDeployment.getCurrentPmc();

            final OnboardingUser onbUser;
            final OnboardingUserCredential onbUserCred;

            boolean isPropVistaAccOwner = behaviorsOriginal.permissions().getValue().contains(VistaCrmBehavior.PropertyVistaAccountOwner);
            boolean isPropVistaAccOwnerSet = behaviorsUpdated.permissions().getValue().contains(VistaCrmBehavior.PropertyVistaAccountOwner);

            if (isNew || !isPropVistaAccOwner) {
                onbUser = EntityFactory.create(OnboardingUser.class);
                onbUserCred = EntityFactory.create(OnboardingUserCredential.class);
            } else {
                final EntityQueryCriteria<OnboardingUserCredential> onbUCrt = EntityQueryCriteria.create(OnboardingUserCredential.class);
                onbUCrt.add(PropertyCriterion.eq(onbUCrt.proto().crmUser(), user.getPrimaryKey()));

                onbUserCred = TaskRunner.runInOperationsNamespace(new Callable<OnboardingUserCredential>() {
                    @Override
                    public OnboardingUserCredential call() {

                        return Persistence.service().retrieve(onbUCrt);
                    }
                });

                onbUser = TaskRunner.runInOperationsNamespace(new Callable<OnboardingUser>() {
                    @Override
                    public OnboardingUser call() {
                        if (onbUserCred != null) {
                            return Persistence.service().retrieve(OnboardingUser.class, onbUserCred.user().getPrimaryKey());
                        } else {
                            return null;
                        }
                    }
                });
            }

            if (onbUser != null) {
                if (isPropVistaAccOwnerSet) {
                    onbUser.firstName().setValue(in.name().firstName().getValue());
                    onbUser.lastName().setValue(in.name().lastName().getValue());
                    onbUser.name().setValue(user.name().getValue());
                    onbUser.email().setValue(user.email().getValue());

                    onbUserCred.crmUser().setValue(user.getPrimaryKey());
                    onbUserCred.behavior().setValue(VistaOnboardingBehavior.Client);
                    onbUserCred.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(in.password().getValue()));
                    onbUserCred.enabled().setValue(in.enabled().getValue());
                    onbUserCred.onboardingAccountId().setValue(null);
                    onbUserCred.requiredPasswordChangeOnNextLogIn().setValue(in.requiredPasswordChangeOnNextLogIn().getValue());
                    onbUserCred.interfaceUid().setValue(credential.interfaceUid().getValue());

                    Boolean res = TaskRunner.runInOperationsNamespace(new Callable<Boolean>() {
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
                    TaskRunner.runInOperationsNamespace(new Callable<Void>() {
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

    @Override
    public void clearSecurityQuestion(AsyncCallback<VoidSerializable> asyncCallback, EmployeeDTO employeeId) {
        Employee emp = Persistence.service().retrieve(Employee.class, employeeId.getPrimaryKey());
        ServerSideFactory.create(UserManagementFacade.class).clearSecurityQuestion(CrmUserCredential.class, emp.user());
        Persistence.service().commit();
        asyncCallback.onSuccess(null);
    }

    @Override
    public void sendPasswordResetEmail(AsyncCallback<VoidSerializable> asyncCallback, EmployeeDTO employeeId) {
        Employee emp = Persistence.service().retrieve(Employee.class, employeeId.getPrimaryKey());
        Persistence.service().retrieve(emp.user());
        ServerSideFactory.create(CommunicationFacade.class).sendCrmPasswordRetrievalToken(emp.user());
        Persistence.service().commit();
    }
}
