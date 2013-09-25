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
package com.propertyvista.crm.server.services.security;

import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractListServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.crm.rpc.services.security.CrmAuditRecordsListerService;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.dto.AuditRecordDTO;
import com.propertyvista.operations.domain.security.AuditRecord;
import com.propertyvista.server.common.security.VistaContext;
import com.propertyvista.server.jobs.TaskRunner;

public class CrmAuditRecordsListerServiceImpl extends AbstractListServiceDtoImpl<AuditRecord, AuditRecordDTO> implements CrmAuditRecordsListerService {

    public CrmAuditRecordsListerServiceImpl() {
        super(AuditRecord.class, AuditRecordDTO.class);
    }

    @Override
    protected void bind() {
        bind(toProto.remoteAddr(), boProto.remoteAddr());
        bind(toProto.when(), boProto.created());
        bind(toProto.event(), boProto.event());
        bind(toProto.application(), boProto.app());
        bind(toProto.details(), boProto.details());
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
    protected void enhanceListRetrieved(AuditRecord entity, AuditRecordDTO dto) {
        if (!entity.user().isNull()) {
            AbstractUser user = Persistence.service().retrieve(VistaContext.getVistaUserClass(entity.userType().getValue()), entity.user().getValue());
            if (user != null) {
                dto.userName().setValue(user.getStringView());
            }
        }
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new IllegalStateException("delete operation is not supported");
    }

}
