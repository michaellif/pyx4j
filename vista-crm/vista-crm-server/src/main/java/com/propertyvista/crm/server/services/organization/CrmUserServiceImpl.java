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

import java.util.Collection;
import java.util.HashSet;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityViolationException;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.organization.CrmUserService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.security.UserAuditingConfigurationDTO;
import com.propertyvista.server.common.security.VistaContext;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class CrmUserServiceImpl extends AbstractCrudServiceDtoImpl<Employee, EmployeeDTO> implements CrmUserService {

    private static final I18n i18n = I18n.get(CrmUserServiceImpl.class);

    public CrmUserServiceImpl() {
        super(Employee.class, EmployeeDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceRetrieved(Employee entity, EmployeeDTO dto, RetrieveTarget RetrieveTarget) {
        Persistence.service().retrieveMember(entity.portfolios());
        dto.portfolios().set(entity.portfolios());

        Persistence.service().retrieveMember(entity.buildingAccess());
        dto.buildingAccess().set(entity.buildingAccess());

        Persistence.service().retrieve(dto.employees());

        CrmUserCredential crs = Persistence.service().retrieve(CrmUserCredential.class, entity.user().getPrimaryKey());
        dto.restrictAccessToSelectedBuildingsOrPortfolio().setValue(!crs.accessAllBuildings().getValue(false));
        dto.requiredPasswordChangeOnNextLogIn().setValue(crs.requiredPasswordChangeOnNextLogIn().getValue());
        dto.roles().addAll(crs.roles());
        dto.credentialUpdated().setValue(crs.credentialUpdated().getValue());

        // TODO put auditing configuration here
        dto.userAuditingConfiguration().set(EntityFactory.create(UserAuditingConfigurationDTO.class));

        Persistence.service().retrieveMember(entity.notifications());
        dto.notifications().set(entity.notifications());
        for (Notification item : dto.notifications()) {
            Persistence.service().retrieve(item.buildings());
            BuildingFolderUtil.stripExtraData(dto.buildingAccess());
            Persistence.service().retrieve(item.portfolios());
        }
    }

    @Override
    public void retrieve(AsyncCallback<EmployeeDTO> callback, Key entityId, RetrieveTarget RetrieveTarget) {
        // Enforce access only to current user
        super.retrieve(callback, CrmAppContext.getCurrentUserEmployee().getPrimaryKey(), RetrieveTarget);
    }

    @Override
    protected void persist(Employee entity, EmployeeDTO dto) {
        assertSamePortfolios(dto);
        // Enforce access only to current user
        dto.setPrimaryKey(CrmAppContext.getCurrentUserEmployee().getPrimaryKey());
        dto.user().setPrimaryKey(VistaContext.getCurrentUserPrimaryKey());

        super.persist(entity, dto);

        // Update name label in UI
        Context.getVisit().getUserVisit().setName(entity.name().getStringView());
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
    public void create(AsyncCallback<Key> callback, EmployeeDTO dto) {
        throw new IllegalArgumentException();
    }

    private void assertSamePortfolios(EmployeeDTO dto) {
        Collection<Key> clientSidePortfolios = new HashSet<Key>();
        for (Portfolio portfolio : dto.portfolios()) {
            clientSidePortfolios.add(portfolio.getPrimaryKey());
        }

        Collection<Key> serverSidePortfolios = new HashSet<Key>();
        Employee serverSideEmployee = Persistence.service().retrieve(Employee.class, dto.getPrimaryKey());
        Persistence.service().retrieveMember(serverSideEmployee.portfolios());
        for (Portfolio portfolio : serverSideEmployee.portfolios()) {
            serverSidePortfolios.add(portfolio.getPrimaryKey());
        }

        for (Key portfolioKey : clientSidePortfolios) {
            if (!serverSidePortfolios.contains(portfolioKey)) {
                throw new SecurityViolationException(i18n.tr("Changing portfolios is forbidden"));
            }
        }

        for (Key porfolioKey : serverSidePortfolios) {
            if (!clientSidePortfolios.contains(porfolioKey)) {
                throw new SecurityViolationException(i18n.tr("Changing portfolios is forbidden"));
            }
        }
    }
}
