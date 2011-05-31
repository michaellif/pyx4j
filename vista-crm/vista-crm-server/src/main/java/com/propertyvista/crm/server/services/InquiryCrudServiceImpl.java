/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

import com.propertyvista.crm.rpc.services.InquiryCrudService;
import com.propertyvista.domain.tenant.Inquiry;
import com.propertyvista.dto.InquiryDTO;

public class InquiryCrudServiceImpl implements InquiryCrudService {

    private final static Logger log = LoggerFactory.getLogger(InquiryCrudServiceImpl.class);

    @Override
    public void create(AsyncCallback<InquiryDTO> callback, InquiryDTO editableEntity) {
        Inquiry entity = GenericConverter.down(editableEntity, Inquiry.class);
        PersistenceServicesFactory.getPersistenceService().persist(entity);
        callback.onSuccess(GenericConverter.up(entity, InquiryDTO.class));
    }

    @Override
    public void retrieve(AsyncCallback<InquiryDTO> callback, String entityId) {
        Inquiry entity = PersistenceServicesFactory.getPersistenceService().retrieve(Inquiry.class, entityId);
        callback.onSuccess(GenericConverter.up(entity, InquiryDTO.class));
    }

    @Override
    public void save(AsyncCallback<InquiryDTO> callback, InquiryDTO editableEntity) {
        Inquiry entity = GenericConverter.down(editableEntity, Inquiry.class);
        PersistenceServicesFactory.getPersistenceService().merge(entity);
        callback.onSuccess(GenericConverter.up(entity, InquiryDTO.class));
    }

    @Override
    public void search(AsyncCallback<EntitySearchResult<InquiryDTO>> callback, EntitySearchCriteria<InquiryDTO> criteria) {
        EntitySearchCriteria<Inquiry> c = GenericConverter.down(criteria, Inquiry.class);
        //TODO add building specific criteria
        callback.onSuccess(GenericConverter.up(EntityServicesImpl.secureSearch(c), InquiryDTO.class));
    }
}
