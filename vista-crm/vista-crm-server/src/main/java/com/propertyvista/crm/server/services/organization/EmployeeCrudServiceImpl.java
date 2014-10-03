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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityDiff;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.server.EmailValidator;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.UserManagementFacade;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.dto.company.EmployeePrivilegesDTO;
import com.propertyvista.crm.rpc.services.organization.EmployeeCrudService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CrmUserCredential;
import com.propertyvista.domain.security.UserAuditingConfigurationDTO;
import com.propertyvista.server.common.security.UserAccessUtils;
import com.propertyvista.server.domain.security.BehaviorHolder;

public class EmployeeCrudServiceImpl extends AbstractCrudServiceDtoImpl<Employee, EmployeeDTO> implements EmployeeCrudService {

    public EmployeeCrudServiceImpl() {
        super(Employee.class, EmployeeDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected EmployeeDTO init(InitializationData initializationData) {
        EmployeeDTO newEmployee = EntityFactory.create(EmployeeDTO.class);

        newEmployee.privileges().enabled().setValue(true);
        newEmployee.privileges().restrictAccessToSelectedBuildingsAndPortfolios().setValue(false);
        newEmployee.privileges().changePassword().setValue(true);

        return newEmployee;

    }

    @Override
    protected void enhanceListRetrieved(Employee bo, EmployeeDTO to) {
        if (SecurityController.check(DataModelPermission.permissionRead(EmployeePrivilegesDTO.class))) {
            CrmUserCredential crs = Persistence.service().retrieve(CrmUserCredential.class, bo.user().getPrimaryKey());
            to.privileges().roles().addAll(crs.roles());
            for (CrmRole role : crs.roles()) {
                to.privileges().behaviors().addAll(role.behaviors());
            }
        }
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<Employee> boCriteria, EntityListCriteria<EmployeeDTO> toCriteria) {
        toCriteria.removeAllCriterions(toCriteria.proto().user().credential());
        if (SecurityController.check(DataModelPermission.permissionRead(EmployeePrivilegesDTO.class))) {
            PropertyCriterion roleCriteria = toCriteria.getCriterion(toCriteria.proto().privileges().roles());
            if (roleCriteria != null) {
                toCriteria.getFilters().remove(roleCriteria);
                if (roleCriteria.getRestriction() == Restriction.EQUAL) {
                    boCriteria.eq(boCriteria.proto().user().credential().roles(), roleCriteria.getValue());
                } else if (roleCriteria.getRestriction() == Restriction.NOT_EQUAL) {
                    boCriteria.ne(boCriteria.proto().user().credential().roles(), roleCriteria.getValue());
                } else {
                    throw new IllegalArgumentException();
                }
            }
            PropertyCriterion behaviorsCriteria = toCriteria.getCriterion(toCriteria.proto().privileges().behaviors());
            if (behaviorsCriteria != null) {
                toCriteria.getFilters().remove(behaviorsCriteria);
                if (behaviorsCriteria.getRestriction() == Restriction.EQUAL) {
                    boCriteria.eq(boCriteria.proto().user().credential().roles().$().behaviors(), behaviorsCriteria.getValue());
                } else if (behaviorsCriteria.getRestriction() == Restriction.NOT_EQUAL) {
                    boCriteria.notExists(boCriteria.proto().user().credential().roles(),
                            PropertyCriterion.eq(boCriteria.proto().user().credential().roles().$().behaviors(), behaviorsCriteria.getValue()));
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }
        super.enhanceListCriteria(boCriteria, toCriteria);
    }

    @Override
    protected void enhanceRetrieved(Employee bo, EmployeeDTO to, RetrieveTarget retrieveTarget) {
        // Load detached data:
        Persistence.service().retrieveMember(bo.portfolios());
        to.portfolios().set(bo.portfolios());

        Persistence.service().retrieveMember(bo.buildingAccess());
        to.buildingAccess().set(bo.buildingAccess());
        BuildingFolderUtil.stripExtraData(to.buildingAccess());

        Persistence.service().retrieve(to.employees());

        //TODO proper Role
        CrmUserCredential crs = null;
        if (SecurityController.check(DataModelPermission.permissionRead(EmployeePrivilegesDTO.class)) && (bo.user().getPrimaryKey() != null)) {
            crs = Persistence.service().retrieve(CrmUserCredential.class, bo.user().getPrimaryKey());
            to.privileges().enabled().set(crs.enabled());
            to.privileges().restrictAccessToSelectedBuildingsAndPortfolios().setValue(!crs.accessAllBuildings().getValue(false));
            to.privileges().changePassword().setValue(crs.requiredPasswordChangeOnNextLogIn().getValue());
            to.privileges().roles().addAll(crs.roles());
            to.privileges().credentialUpdated().setValue(crs.credentialUpdated().getValue());

            to.userAuditingConfiguration().set(EntityFactory.create(UserAuditingConfigurationDTO.class));
        }
        if (bo.user().getPrimaryKey() != null) {
            if (crs != null) {
                crs = Persistence.service().retrieve(CrmUserCredential.class, bo.user().getPrimaryKey());
            }
            if (crs != null) {
                to.privileges().isSecurityQuestionSet().setValue(!CommonsStringUtils.isEmpty(crs.securityQuestion().getValue()));
            }
        }

        Persistence.service().retrieveMember(bo.notifications());
        to.notifications().set(bo.notifications());
        for (Notification item : to.notifications()) {
            Persistence.service().retrieve(item.buildings());
            BuildingFolderUtil.stripExtraData(item.buildings());
            Persistence.service().retrieve(item.portfolios());
        }

        Persistence.service().retrieve(to.signature());
    }

    @Override
    protected boolean persist(Employee dbo, EmployeeDTO in) {
        if (dbo.id().isNull()) {
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(dbo);
        }

        if (SecurityController.check(DataModelPermission.permissionUpdate(EmployeePrivilegesDTO.class))) {
            if (!in.privileges().restrictAccessToSelectedBuildingsAndPortfolios().getValue(false)) {
                dbo.buildingAccess().clear();
                dbo.portfolios().clear();
            }
        }

        in.email().setValue(EmailValidator.normalizeEmailAddress(in.email().getStringView()));

        boolean updated = super.persist(dbo, in);

        if (SecurityController.check(DataModelPermission.permissionUpdate(EmployeePrivilegesDTO.class))) {
            CrmUser user;
            boolean isNew = false;

            if (in.user().getPrimaryKey() != null) {
                user = Persistence.service().retrieve(CrmUser.class, dbo.user().getPrimaryKey());
            } else {
                user = dbo.user();
                isNew = true;
            }

            if (ServerContext.getVisit().getUserVisit().getPrincipalPrimaryKey().equals(dbo.user().getPrimaryKey())) {
                ServerContext.getVisit().getUserVisit().setName(dbo.name().getStringView());
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
                credential.credential().setValue(
                        ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(in.privileges().password().getValue()));
            }
            credential.enabled().set(in.privileges().enabled());
            credential.roles().clear();
            credential.roles().addAll(in.privileges().roles());
            credential.accessAllBuildings().setValue(!in.privileges().restrictAccessToSelectedBuildingsAndPortfolios().getValue(false));
            credential.requiredPasswordChangeOnNextLogIn().setValue(in.privileges().changePassword().getValue());

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

        return updated;
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
