/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 21, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.security;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.dto.account.LoginAttemptDTO;
import com.propertyvista.crm.rpc.services.security.CrmLoginAttemptsListerService;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.operations.domain.security.AuditRecord;

public class CrmLoginAttemptsListerServiceImpl extends AbstractCrudServiceDtoImpl<AuditRecord, LoginAttemptDTO> implements CrmLoginAttemptsListerService {

    private static final I18n i18n = I18n.get(CrmLoginAttemptsListerServiceImpl.class);

    public CrmLoginAttemptsListerServiceImpl() {
        super(AuditRecord.class, LoginAttemptDTO.class);
    }

    @Override
    protected void bind() {
        bind(toProto.userKey(), boProto.user());
        bind(toProto.remoteAddress(), boProto.remoteAddr());
        bind(toProto.outcome(), boProto.event());
        bind(toProto.time(), boProto.created());
    }

    @Override
    public void list(final AsyncCallback<EntitySearchResult<LoginAttemptDTO>> callback, final EntityListCriteria<LoginAttemptDTO> dtoCriteria) {
        try {
            // Warning: we are going to set an admin namespace inside the super.list() via overriden ehnanceListCriteria()
            super.list(callback, dtoCriteria);
        } finally {
            NamespaceManager.remove();
        }
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new Error("delete is not implemented");
    }

    @Override
    public void retrieve(AsyncCallback<LoginAttemptDTO> callback, Key entityId, RetrieveTarget retrieveTarget ) {
        throw new Error("retrieve is not implemented");
    }

    @Override
    public void save(AsyncCallback<Key> callback, LoginAttemptDTO dto) {
        throw new Error("save is not implemented");
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<AuditRecord> dbCriteria, EntityListCriteria<LoginAttemptDTO> dtoCriteria) {
        super.enhanceListCriteria(dbCriteria, dtoCriteria);

        // security measures 
        if (!SecurityController.checkBehavior(VistaCrmBehavior.Organization)) {
            dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().user(), VistaContext.getCurrentUserPrimaryKey()));
        }

        dbCriteria.or(PropertyCriterion.eq(dbCriteria.proto().event(), AuditRecordEventType.Login),
                PropertyCriterion.eq(dbCriteria.proto().event(), AuditRecordEventType.LoginFailed));

        // we have to do it BEFORE setNamespace() because getCurrentPmc() works only in pmc's namespace        
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().namespace(), VistaDeployment.getCurrentPmc().namespace()));

        NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
    }
}
