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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractListServiceDtoImpl;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.security.AuditRecord;
import com.propertyvista.crm.rpc.services.security.CrmAuditRecordsListerService;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.dto.AuditRecordDTO;

public class CrmAuditRecordsListerServiceImpl extends AbstractListServiceDtoImpl<AuditRecord, AuditRecordDTO> implements CrmAuditRecordsListerService {

    public CrmAuditRecordsListerServiceImpl() {
        super(AuditRecord.class, AuditRecordDTO.class);
    }

    @Override
    protected void bind() {

        bind(dtoProto.remoteAddr(), dboProto.remoteAddr());
        bind(dtoProto.when(), dboProto.created());
        bind(dtoProto.event(), dboProto.event());
        bind(dtoProto.app(), dboProto.app());
        bind(dtoProto.details(), dboProto.details());

    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<AuditRecordDTO>> callback, EntityListCriteria<AuditRecordDTO> dtoCriteria) {
        try {
            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
            super.list(callback, dtoCriteria);
        } finally {
            NamespaceManager.remove();
        }
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new IllegalStateException("delete operation is not supported");
    }

}
