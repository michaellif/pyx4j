/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.dto.ScheduleDataDTO;
import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceCrudServiceImpl extends AbstractCrudServiceDtoImpl<MaintenanceRequest, MaintenanceRequestDTO> implements MaintenanceCrudService {

    public MaintenanceCrudServiceImpl() {
        super(MaintenanceRequest.class, MaintenanceRequestDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(MaintenanceRequest entity, MaintenanceRequestDTO dto) {
        enhanceAll(dto);
    }

    @Override
    protected void enhanceListRetrieved(MaintenanceRequest entity, MaintenanceRequestDTO dto) {
        enhanceAll(dto);
    }

    protected void enhanceAll(MaintenanceRequestDTO dto) {
        Persistence.service().retrieve(dto.tenant());
        Persistence.service().retrieve(dto.issueClassification());
        Persistence.service().retrieve(dto.issueClassification().subjectDetails());
        Persistence.service().retrieve(dto.issueClassification().subjectDetails().subject());
        Persistence.service().retrieve(dto.issueClassification().subjectDetails().subject().issueElement());
    }

    @Override
    public void sheduleAction(AsyncCallback<VoidSerializable> callback, ScheduleDataDTO data, Key entityId) {
        MaintenanceRequest entity = Persistence.service().retrieve(MaintenanceRequest.class, entityId);
        entity.scheduledDate().set(data.date());
        entity.scheduledTime().set(data.time());
        entity.status().setValue(MaintenanceRequestStatus.Scheduled);
        Persistence.service().merge(entity);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void resolveAction(AsyncCallback<VoidSerializable> callback, Key entityId) {
        MaintenanceRequest entity = Persistence.service().retrieve(MaintenanceRequest.class, entityId);
        entity.status().setValue(MaintenanceRequestStatus.Resolved);
        Persistence.service().merge(entity);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void cancelAction(AsyncCallback<VoidSerializable> callback, Key entityId) {
        MaintenanceRequest entity = Persistence.service().retrieve(MaintenanceRequest.class, entityId);
        entity.status().setValue(MaintenanceRequestStatus.Cancelled);
        Persistence.service().merge(entity);
        Persistence.service().commit();
        callback.onSuccess(null);
    }
}
