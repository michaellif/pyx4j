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

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.organization.EmployeeCrudService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class EmployeeCrudServiceImpl extends AbstractCrudServiceDtoImpl<Employee, EmployeeDTO> implements EmployeeCrudService {

    public EmployeeCrudServiceImpl() {
        super(Employee.class, EmployeeDTO.class);
    }

    @Override
    protected void bind() {
        bind(Employee.class, dtoProto, dboProto);
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
                Persistence.service().persist(dbo);
            }

            CrmUserCredential credential;
            if (!isNew) {
                credential = Persistence.service().retrieve(CrmUserCredential.class, in.user().getPrimaryKey());
            } else {
                credential = EntityFactory.create(CrmUserCredential.class);
                credential.setPrimaryKey(user.getPrimaryKey());
                credential.credential().setValue(PasswordEncryptor.encryptPassword(in.password().getValue()));
            }
            credential.enabled().set(in.enabled());
            credential.roles().clear();
            credential.roles().addAll(in.roles());
            credential.accessAllBuildings().set(in.accessAllBuildings());
            credential.requiredPasswordChangeOnNextLogIn().setValue(in.requireChangePasswordOnNextLogIn().getValue());
            Persistence.service().persist(credential);
        }

    }

}
