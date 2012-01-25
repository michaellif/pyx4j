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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.organization.EmployeeCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class EmployeeCrudServiceImpl extends GenericCrudServiceDtoImpl<Employee, EmployeeDTO> implements EmployeeCrudService {

    public EmployeeCrudServiceImpl() {
        super(Employee.class, EmployeeDTO.class);
    }

    @Override
    protected void enhanceDTO(Employee in, EmployeeDTO dto, boolean fromList) {
        if (!fromList) {
            // Load detached data:
            Persistence.service().retrieve(dto.portfolios());
            Persistence.service().retrieve(dto.employees());

            //TODO proper Role
            if (SecurityController.checkBehavior(VistaCrmBehavior.Organization) && (in.user().getPrimaryKey() != null)) {
                CrmUserCredential crs = Persistence.service().retrieve(CrmUserCredential.class, in.user().getPrimaryKey());
                dto.enabled().set(crs.enabled());
                dto.accessAllBuildings().set(crs.accessAllBuildings());
                dto.requireChangePasswordOnNextLogIn().setValue(crs.requiredPasswordChangeOnNextLogIn().getValue());
            }
        }
    }

    @Override
    protected void persistDBO(Employee dbo, EmployeeDTO in) {
        super.persistDBO(dbo, in);
        if (in.user().getPrimaryKey() != null) {
            CrmUser user = Persistence.service().retrieve(CrmUser.class, dbo.user().getPrimaryKey());

            if (Context.getVisit().getUserVisit().getPrincipalPrimaryKey().equals(dbo.user().getPrimaryKey())) {
                Context.getVisit().getUserVisit().setName(dbo.name().getStringView());
            }

            user.name().setValue(dbo.name().getStringView());
            user.email().setValue(dbo.email().getStringView());
            Persistence.service().persist(user);
            if (SecurityController.checkBehavior(VistaCrmBehavior.Organization)) {
                CrmUserCredential crs = Persistence.service().retrieve(CrmUserCredential.class, in.user().getPrimaryKey());
                crs.enabled().set(in.enabled());
                crs.accessAllBuildings().set(in.accessAllBuildings());
                crs.requiredPasswordChangeOnNextLogIn().setValue(in.requireChangePasswordOnNextLogIn().getValue());
                Persistence.service().persist(crs);
            }
        }
    }
}
