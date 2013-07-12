/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import java.util.concurrent.Callable;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.VistaUserType;
import com.propertyvista.operations.domain.security.AuditRecord;
import com.propertyvista.operations.rpc.AuditRecordOperationsDTO;
import com.propertyvista.operations.rpc.services.AuditRecordCrudService;
import com.propertyvista.server.common.security.VistaContext;
import com.propertyvista.server.jobs.TaskRunner;

public class AuditRecordCrudServiceImpl extends AbstractCrudServiceDtoImpl<AuditRecord, AuditRecordOperationsDTO> implements AuditRecordCrudService {

    public AuditRecordCrudServiceImpl() {
        super(AuditRecord.class, AuditRecordOperationsDTO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.remoteAddr(), dboProto.remoteAddr());
        bind(dtoProto.userKey(), dboProto.user());
        bind(dtoProto.when(), dboProto.created());
        bind(dtoProto.worldTime(), dboProto.worldTime());
        bind(dtoProto.sessionId(), dboProto.sessionId());
        bind(dtoProto.event(), dboProto.event());
        bind(dtoProto.pmc(), dboProto.namespace());
        bind(dtoProto.application(), dboProto.app());
        bind(dtoProto.details(), dboProto.details());
    }

    @Override
    protected void enhanceListRetrieved(final AuditRecord entity, final AuditRecordOperationsDTO dto) {
        super.enhanceListRetrieved(entity, dto);
        dto.targetEntity().setValue(CommonsStringUtils.nvl_concat(entity.entityClass().getStringView(), entity.entityId().getStringView(), ":"));

        if (!entity.user().isNull()) {
            final Class<? extends AbstractUser> userClass = VistaContext.getVistaUserClass(entity.userType().getValue());

            if (entity.userType().getValue() == VistaUserType.operations) {
                TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                    @Override
                    public Void call() {
                        AbstractUser user = Persistence.service().retrieve(userClass, entity.user().getValue());
                        if (user != null) {
                            dto.userName().setValue(user.email().getValue());
                        }
                        return null;
                    }
                });
            } else {
                TaskRunner.runInTargetNamespace(entity.pmc(), new Callable<Void>() {
                    @Override
                    public Void call() {
                        AbstractUser user = Persistence.service().retrieve(userClass, entity.user().getValue());
                        if (user != null) {
                            dto.userName().setValue(user.email().getValue());
                        }
                        return null;
                    }
                });
            }
        }

    }

    @Override
    protected void enhanceRetrieved(AuditRecord entity, AuditRecordOperationsDTO dto, RetrieveTarget retrieveTarget) {
        enhanceListRetrieved(entity, dto);
    }
}
