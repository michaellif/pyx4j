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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
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
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.organization.EmployeeCrudService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.UserAuditingConfigurationDTO;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.server.common.security.UserAccessUtils;
import com.propertyvista.server.domain.security.BehaviorHolder;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class EmployeeCrudServiceImpl extends AbstractCrudServiceDtoImpl<Employee, EmployeeDTO> implements EmployeeCrudService {

    public EmployeeCrudServiceImpl() {
        super(Employee.class, EmployeeDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected EmployeeDTO init(InitializationData initializationData) {
        EmployeeDTO newEmployee = EntityFactory.create(EmployeeDTO.class);

        newEmployee.enabled().setValue(true);

        return newEmployee;

    }

    @Override
    protected void enhanceRetrieved(Employee entity, EmployeeDTO dto, RetrieveTarget retrieveTarget) {
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

        if (SecurityController.checkBehavior(VistaCrmBehavior.Organization)) {
            if (!in.restrictAccessToSelectedBuildingsAndPortfolios().getValue(false)) {
                dbo.buildingAccess().clear();
                dbo.portfolios().clear();
            }
        }

        in.email().setValue(EmailValidator.normalizeEmailAddress(in.email().getStringView()));

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
                String diff = EntityDiff.getChanges(credentialOrig, credential);
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

            if (isNew) {
                ServerSideFactory.create(UserManagementFacade.class).createGlobalCrmUserIndex(user);
            } else {
                ServerSideFactory.create(UserManagementFacade.class).updateGlobalCrmUserIndex(user);
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
        asyncCallback.onSuccess(null);
    }
}
