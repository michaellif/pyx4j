/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.organization;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.organization.CrmUserService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.UserAuditingConfigurationDTO;
import com.propertyvista.server.common.security.VistaContext;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class CrmUserServiceImpl extends AbstractCrudServiceDtoImpl<Employee, EmployeeDTO> implements CrmUserService {

    public CrmUserServiceImpl() {
        super(Employee.class, EmployeeDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Employee entity, EmployeeDTO dto) {
        Persistence.service().retrieve(dto.portfolios());
        Persistence.service().retrieve(dto.employees());

        CrmUserCredential crs = Persistence.service().retrieve(CrmUserCredential.class, entity.user().getPrimaryKey());
        dto.accessAllBuildings().set(crs.accessAllBuildings());
        dto.requireChangePasswordOnNextLogIn().setValue(crs.requiredPasswordChangeOnNextLogIn().getValue());
        dto.roles().addAll(crs.roles());

        // TODO put auditing configuration here
        dto.userAuditingConfiguration().set(EntityFactory.create(UserAuditingConfigurationDTO.class));
    }

    @Override
    public void retrieve(AsyncCallback<EmployeeDTO> callback, Key entityId, RetrieveTraget retrieveTraget) {
        // Enforce access only to current user
        super.retrieve(callback, CrmAppContext.getCurrentUserEmployee().getPrimaryKey(), retrieveTraget);
    }

    @Override
    public void save(AsyncCallback<EmployeeDTO> callback, EmployeeDTO dto) {
        // Enforce access only to current user
        dto.setPrimaryKey(CrmAppContext.getCurrentUserEmployee().getPrimaryKey());
        dto.user().setPrimaryKey(VistaContext.getCurrentUserPrimaryKey());
        super.save(callback, dto);
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<EmployeeDTO>> callback, EntityListCriteria<EmployeeDTO> dtoCriteria) {
        throw new IllegalArgumentException();
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new IllegalArgumentException();
    }

    @Override
    public void create(AsyncCallback<EmployeeDTO> callback, EmployeeDTO dto) {
        throw new IllegalArgumentException();
    }

}
