/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.legal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.shared.ServiceExecution;

import com.propertyvista.crm.rpc.dto.legal.l1.L1GenerationWizardDTO;
import com.propertyvista.crm.rpc.services.legal.L1GenerationWizardService;

public class L1GenerationWizardServiceImpl implements L1GenerationWizardService {

    @Override
    public void init(AsyncCallback<L1GenerationWizardDTO> callback, com.pyx4j.entity.rpc.AbstractCrudService.InitializationData initializationData) {
        // TODO Auto-generated method stub

    }

    @Override
    public void retrieve(AsyncCallback<L1GenerationWizardDTO> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        // TODO Auto-generated method stub

    }

    @Override
    public void create(AsyncCallback<Key> callback, L1GenerationWizardDTO editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, L1GenerationWizardDTO editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<L1GenerationWizardDTO>> callback, EntityListCriteria<L1GenerationWizardDTO> criteria) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        // TODO Auto-generated method stub

    }

}
