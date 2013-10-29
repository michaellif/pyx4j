/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 29, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services.simulator;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.operations.domain.dev.CardServiceSimulatorConfig;
import com.propertyvista.operations.rpc.dto.CardServiceSimulatorConfigDTO;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationConfigService;

public class CardServiceSimulationConfigServiceImpl implements CardServiceSimulationConfigService {

    @Override
    public void init(AsyncCallback<CardServiceSimulatorConfigDTO> callback, com.pyx4j.entity.rpc.AbstractCrudService.InitializationData initializationData) {
        throw new IllegalArgumentException();
    }

    @Override
    public void create(AsyncCallback<Key> callback, CardServiceSimulatorConfigDTO editableEntity) {
        throw new IllegalArgumentException();
    }

    @Override
    public void retrieve(AsyncCallback<CardServiceSimulatorConfigDTO> callback, Key entityId,
            com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        CardServiceSimulatorConfig config = CardServiceSimulationUtils.getCardServiceSimulatorConfig();
        callback.onSuccess(config.duplicate(CardServiceSimulatorConfigDTO.class));

    }

    @Override
    public void save(AsyncCallback<Key> callback, CardServiceSimulatorConfigDTO editableEntity) {
        Persistence.service().persist(editableEntity.duplicate(CardServiceSimulatorConfig.class));
        Persistence.service().commit();
        callback.onSuccess(new Key(-1));
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<CardServiceSimulatorConfigDTO>> callback, EntityListCriteria<CardServiceSimulatorConfigDTO> criteria) {
        throw new IllegalArgumentException();
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new IllegalArgumentException();
    }

}
