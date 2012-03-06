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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.organization.CrmUserService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.common.security.VistaContext;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class CrmUserServiceImpl extends AbstractCrudServiceDtoImpl<Employee, EmployeeDTO> implements CrmUserService {

    private static Logger log = LoggerFactory.getLogger(CrmUserServiceImpl.class);

    private static final I18n i18n = I18n.get(CrmUserServiceImpl.class);

    public CrmUserServiceImpl() {
        super(Employee.class, EmployeeDTO.class);
    }

    @Override
    protected void bind() {
        bind(Employee.class, dtoProto, dboProto);
    }

    @Override
    protected void enhanceRetrieved(Employee entity, EmployeeDTO dto) {
        Persistence.service().retrieve(dto.portfolios());
        Persistence.service().retrieve(dto.employees());

        CrmUserCredential crs = Persistence.service().retrieve(CrmUserCredential.class, entity.user().getPrimaryKey());
        dto.accessAllBuildings().set(crs.accessAllBuildings());
        dto.requireChangePasswordOnNextLogIn().setValue(crs.requiredPasswordChangeOnNextLogIn().getValue());
        dto.roles().addAll(crs.roles());
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
    public void changePassword(AsyncCallback<VoidSerializable> callback, PasswordChangeRequest request) {
        CrmUserCredential cr = Persistence.service().retrieve(CrmUserCredential.class, VistaContext.getCurrentUserPrimaryKey());
        if (!cr.enabled().isBooleanTrue()) {
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        if (!PasswordEncryptor.checkPassword(request.currentPassword().getValue(), cr.credential().getValue())) {
            log.info("Invalid password for user {}", Context.getVisit().getUserVisit().getEmail());
            if (AbstractAntiBot.authenticationFailed(Context.getVisit().getUserVisit().getEmail())) {
                throw new ChallengeVerificationRequired(i18n.tr("Too Many Failed Log In Attempts"));
            } else {
                throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
        }
        cr.accessKey().setValue(null);
        cr.credential().setValue(PasswordEncryptor.encryptPassword(request.newPassword().getValue()));
        Persistence.service().persist(cr);
        Persistence.service().commit();
        log.info("password changed by user {}", Context.getVisit().getUserVisit().getEmail(), VistaContext.getCurrentUserPrimaryKey());
        callback.onSuccess(null);
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
