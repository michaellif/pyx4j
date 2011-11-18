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
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.EmployeeCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.User;
import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.server.domain.UserCredential;

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
            if (SecurityController.checkBehavior(VistaBehavior.PROPERTY_MANAGER) && (in.user().getPrimaryKey() != null)) {
                UserCredential crs = Persistence.service().retrieve(UserCredential.class, in.user().getPrimaryKey());
                dto.enabled().set(crs.enabled());
                dto.behavior().set(crs.behavior());
            }
        }
    }

    @Override
    protected void persistDBO(Employee dbo, EmployeeDTO in) {
        super.persistDBO(dbo, in);
        if (in.user().getPrimaryKey() != null) {
            User user = Persistence.service().retrieve(User.class, dbo.user().getPrimaryKey());

            if (Context.getVisit().getUserVisit().getPrincipalPrimaryKey().equals(dbo.user().getPrimaryKey())) {
                Context.getVisit().getUserVisit().setName(dbo.name().getStringView());
            }

            user.name().setValue(dbo.name().getStringView());
            user.email().setValue(dbo.email().address().getStringView());
            Persistence.service().persist(user);
            if (SecurityController.checkBehavior(VistaBehavior.PROPERTY_MANAGER)) {
                UserCredential crs = Persistence.service().retrieve(UserCredential.class, in.user().getPrimaryKey());
                crs.enabled().set(in.enabled());
                crs.behavior().set(in.behavior());
                Persistence.service().persist(crs);
            }
        }
    }
}
