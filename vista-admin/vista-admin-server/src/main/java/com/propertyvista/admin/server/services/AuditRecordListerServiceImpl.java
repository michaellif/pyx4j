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
package com.propertyvista.admin.server.services;

import java.util.concurrent.Callable;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.admin.domain.security.AuditRecord;
import com.propertyvista.admin.rpc.services.AuditRecordListerService;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.security.AdminUser;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.dto.AuditRecordDTO;
import com.propertyvista.server.jobs.TaskRunner;

public class AuditRecordListerServiceImpl extends AbstractCrudServiceDtoImpl<AuditRecord, AuditRecordDTO> implements AuditRecordListerService {

    public AuditRecordListerServiceImpl() {
        super(AuditRecord.class, AuditRecordDTO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.remoteAddr(), dboProto.remoteAddr());
        bind(dtoProto.when(), dboProto.created());
        bind(dtoProto.event(), dboProto.event());
        bind(dtoProto.pmc(), dboProto.namespace());
        bind(dtoProto.app(), dboProto.app());
        bind(dtoProto.details(), dboProto.details());
    }

    @Override
    protected void enhanceListRetrieved(final AuditRecord entity, final AuditRecordDTO dto) {
        super.enhanceListRetrieved(entity, dto);
        dto.targetEntity().setValue(CommonsStringUtils.nvl_concat(entity.entityClass().getStringView(), entity.entityId().getStringView(), ":"));

        if (VistaNamespace.adminNamespace.equals(entity.namespace().getValue())) {
            TaskRunner.runInAdminNamespace(new Callable<Void>() {
                @Override
                public Void call() {
                    AdminUser user = Persistence.service().retrieve(AdminUser.class, entity.user().getValue());
                    if (user != null) {
                        dto.userName().setValue(user.email().getValue());
                    }
                    return null;
                }
            });
        } else {
            try {
                TaskRunner.runInTargetNamespace(entity.namespace().getValue(), new Callable<Void>() {
                    @Override
                    public Void call() {
                        CrmUser crmUser = Persistence.service().retrieve(CrmUser.class, entity.user().getValue());
                        if (crmUser != null) {
                            dto.userName().setValue(crmUser.email().getValue());
                        }
                        return null;
                    }
                });
            } catch (Throwable e) {
                dto.userName().setValue(e.getMessage());
            }
        }
    }
}
