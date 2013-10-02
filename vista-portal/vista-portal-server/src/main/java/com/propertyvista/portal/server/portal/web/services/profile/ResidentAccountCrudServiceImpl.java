/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.web.services.profile;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.portal.rpc.portal.web.dto.ResidentAccountDTO;
import com.propertyvista.portal.rpc.portal.web.services.profile.ResidentAccountCrudService;

public class ResidentAccountCrudServiceImpl implements ResidentAccountCrudService {

    @Override
    public void init(AsyncCallback<ResidentAccountDTO> callback, com.pyx4j.entity.rpc.AbstractCrudService.InitializationData initializationData) {
        // TODO Auto-generated method stub

    }

    @Override
    public void retrieve(AsyncCallback<ResidentAccountDTO> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        ResidentAccountDTO account = EntityFactory.create(ResidentAccountDTO.class);
        callback.onSuccess(account);
    }

    @Override
    public void create(AsyncCallback<Key> callback, ResidentAccountDTO editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void save(AsyncCallback<Key> callback, ResidentAccountDTO editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<ResidentAccountDTO>> callback, EntityListCriteria<ResidentAccountDTO> criteria) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        // TODO Auto-generated method stub

    }

}
