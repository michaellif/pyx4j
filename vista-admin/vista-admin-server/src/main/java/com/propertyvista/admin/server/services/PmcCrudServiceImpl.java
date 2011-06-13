/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.PmcCrudService;
import com.propertyvista.portal.rpc.corp.PmcAccountCreationRequest;

public class PmcCrudServiceImpl implements PmcCrudService {

    @Override
    public void createAccount(AsyncCallback<PmcDTO> callback, PmcAccountCreationRequest request) {
        // TODO Auto-generated method stub
    }

    @Override
    public void create(AsyncCallback<PmcDTO> callback, PmcDTO editableEntity) {
        throw new Error("Not Implemented");
    }

    @Override
    public void retrieve(AsyncCallback<PmcDTO> callback, Key entityId) {
        // TODO Auto-generated method stub
    }

    @Override
    public void save(AsyncCallback<PmcDTO> callback, PmcDTO editableEntity) {
        // TODO Auto-generated method stub
    }

    @Override
    public void search(AsyncCallback<EntitySearchResult<PmcDTO>> callback, EntitySearchCriteria<PmcDTO> criteria) {
        // TODO Auto-generated method stub
    }

}
