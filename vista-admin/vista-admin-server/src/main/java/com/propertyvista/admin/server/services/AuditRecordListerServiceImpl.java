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

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;

import com.propertyvista.admin.domain.security.AuditRecord;
import com.propertyvista.admin.rpc.services.AuditRecordListerService;
import com.propertyvista.dto.AuditRecordDTO;

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

}
