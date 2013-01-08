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
package com.propertyvista.crm.server.services.admin;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.shared.ServiceExecution;

import com.propertyvista.crm.rpc.dto.admin.PmcPaymentMethodsDTO;
import com.propertyvista.crm.rpc.services.admin.PmcPaymentMethodsCrudService;

public class PmcPaymentMethodsCrudServiceImpl implements PmcPaymentMethodsCrudService {

    @Override
    public void retrieve(AsyncCallback<PmcPaymentMethodsDTO> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTraget retrieveTraget) {
        callback.onSuccess(EntityFactory.create(PmcPaymentMethodsDTO.class));
    }

    @Override
    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, PmcPaymentMethodsDTO editableEntity) {
        callback.onSuccess(new Key(-1));
    }

    @Override
    public void create(AsyncCallback<Key> callback, PmcPaymentMethodsDTO editableEntity) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<PmcPaymentMethodsDTO>> callback, EntityListCriteria<PmcPaymentMethodsDTO> criteria) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new Error("Invalid Operation");
    }

}
