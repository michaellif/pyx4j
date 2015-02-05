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
 */
package com.propertyvista.crm.server.services.security;

import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.CrudEntityBinder;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.crm.rpc.services.security.CrmAuditRecordsListerService;
import com.propertyvista.crm.server.services.AbstractCrmCrudServiceImpl;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.VistaUserType;
import com.propertyvista.dto.AuditRecordDTO;
import com.propertyvista.operations.domain.security.AuditRecord;
import com.propertyvista.server.TaskRunner;

public class CrmAuditRecordsListerServiceImpl extends AbstractCrmCrudServiceImpl<AuditRecord, AuditRecordDTO> implements CrmAuditRecordsListerService {

    private static class Binder extends CrudEntityBinder<AuditRecord, AuditRecordDTO> {

        protected Binder() {
            super(AuditRecord.class, AuditRecordDTO.class);
        }

        @Override
        protected void bind() {
            bind(toProto.remoteAddr(), boProto.remoteAddr());
            bind(toProto.when(), boProto.created());
            bind(toProto.event(), boProto.event());
            bind(toProto.application(), boProto.app());
            bind(toProto.targetEntity(), boProto.entityClass());
            bind(toProto.targetEntityId(), boProto.entityId());
            bind(toProto.details(), boProto.details());
        }

    }

    public CrmAuditRecordsListerServiceImpl() {
        super(new Binder());
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<AuditRecord> dbCriteria, EntityListCriteria<AuditRecordDTO> dtoCriteria) {
        super.enhanceListCriteria(dbCriteria, dtoCriteria);
        dbCriteria.eq(dbCriteria.proto().namespace(), NamespaceManager.getNamespace());
    }

    @Override
    protected EntitySearchResult<AuditRecord> query(final EntityListCriteria<AuditRecord> criteria) {
        return TaskRunner.runInOperationsNamespace(new Callable<EntitySearchResult<AuditRecord>>() {
            @Override
            public EntitySearchResult<AuditRecord> call() {
                return Persistence.secureQuery(criteria);
            }
        });
    }

    @Override
    protected void enhanceListRetrieved(final AuditRecord bo, final AuditRecordDTO to) {
        if (!bo.user().isNull()) {
            final Class<? extends AbstractUser> userClass = VistaContext.getVistaUserClass(bo.userType().getValue());
            if (bo.userType().getValue() == VistaUserType.operations) {
                TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                    @Override
                    public Void call() {
                        AbstractUser user = Persistence.service().retrieve(userClass, bo.user().getValue());
                        if (user != null) {
                            to.userName().setValue(user.email().getValue());
                        }
                        return null;
                    }
                });
            } else {
                AbstractUser user = Persistence.service().retrieve(userClass, bo.user().getValue());
                if (user != null) {
                    to.userName().setValue(user.getStringView());
                }
            }
        }
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new IllegalStateException("delete operation is not supported");
    }

}
