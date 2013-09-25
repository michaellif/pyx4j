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
    protected void enhanceRetrieved(Employee bo, EmployeeDTO to, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieveMember(bo.portfolios());
        to.portfolios().set(bo.portfolios());

        Persistence.service().retrieveMember(bo.buildingAccess());
        to.buildingAccess().set(bo.buildingAccess());

        Persistence.service().retrieve(to.employees());

        CrmUserCredential crs = Persistence.service().retrieve(CrmUserCredential.class, bo.user().getPrimaryKey());
        to.restrictAccessToSelectedBuildingsAndPortfolios().setValue(!crs.accessAllBuildings().getValue(false));
        to.requiredPasswordChangeOnNextLogIn().setValue(crs.requiredPasswordChangeOnNextLogIn().getValue());
        to.roles().addAll(crs.roles());
        to.credentialUpdated().setValue(crs.credentialUpdated().getValue());

        // TODO put auditing configuration here
        to.userAuditingConfiguration().set(EntityFactory.create(UserAuditingConfigurationDTO.class));

        Persistence.service().retrieveMember(bo.notifications());
        to.notifications().set(bo.notifications());
        for (Notification item : to.notifications()) {
            Persistence.service().retrieve(item.buildings());
            BuildingFolderUtil.stripExtraData(to.buildingAccess());
            Persistence.service().retrieve(item.portfolios());
        }
    }

    @Override
    public void retrieve(AsyncCallback<EmployeeDTO> callback, Key entityId, RetrieveTarget retrieveTarget) {
        // Enforce access only to current user
        super.retrieve(callback, CrmAppContext.getCurrentUserEmployee().getPrimaryKey(), retrieveTarget);
    }

    @Override
    protected void persist(Employee bo, EmployeeDTO to) {
        assertSamePortfolios(to);
        // Enforce access only to current user
        to.setPrimaryKey(CrmAppContext.getCurrentUserEmployee().getPrimaryKey());
        to.user().setPrimaryKey(VistaContext.getCurrentUserPrimaryKey());

        super.persist(bo, to);

        // Update name label in UI
        Context.getVisit().getUserVisit().setName(bo.name().getStringView());
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
