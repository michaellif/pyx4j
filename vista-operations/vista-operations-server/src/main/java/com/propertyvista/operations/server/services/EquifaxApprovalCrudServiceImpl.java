/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.shared.ServiceExecution;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.operations.rpc.dto.EquifaxSetupRequestDTO;
import com.propertyvista.operations.rpc.services.EquifaxApprovalCrudService;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcEquifaxInfo;

public class EquifaxApprovalCrudServiceImpl extends AbstractCrudServiceDtoImpl<PmcEquifaxInfo, EquifaxSetupRequestDTO> implements EquifaxApprovalCrudService {

    public EquifaxApprovalCrudServiceImpl() {
        super(PmcEquifaxInfo.class, EquifaxSetupRequestDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
        bind(dtoProto.businessInformation().dto_businessAddress(), dboProto.businessInformation().businessAddress());
        bind(dtoProto.personalInformation().dto_personalAddress(), dboProto.personalInformation().personalAddress());
    }

    @Override
    public void retrieve(AsyncCallback<EquifaxSetupRequestDTO> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget ) {
        Pmc pmc = Persistence.service().retrieve(Pmc.class, entityId);
        Persistence.service().retrieveMember(pmc.equifaxInfo());
        EquifaxSetupRequestDTO dto = createDTO(pmc.equifaxInfo());
        dto.pmc().setAttachLevel(AttachLevel.ToStringMembers);
        callback.onSuccess(dto);
    }

    @Override
    public void create(AsyncCallback<Key> callback, EquifaxSetupRequestDTO editableEntity) {
        throw new Error("invalid operation!");
    }

    @Override
    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, EquifaxSetupRequestDTO editableEntity) {
        throw new Error("invalid operation!");
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<EquifaxSetupRequestDTO>> callback, EntityListCriteria<EquifaxSetupRequestDTO> criteria) {
        throw new Error("invalid operation!");
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new Error("invalid operation!");
    }

    @Override
    public void reject(AsyncCallback<VoidSerializable> callback) {
        callback.onSuccess(null);
    }

    @Override
    public void applyAndSendToEquifax(AsyncCallback<VoidSerializable> callback) {
        callback.onSuccess(null);
    }

}
