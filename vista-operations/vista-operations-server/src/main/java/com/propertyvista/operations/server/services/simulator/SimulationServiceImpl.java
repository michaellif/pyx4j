/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 9, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.server.services.simulator;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.IEntityCacheService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.admin.AdminServiceImpl;
import com.pyx4j.essentials.server.dev.NetworkSimulationServiceFilter;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaSystemsSimulationConfig;
import com.propertyvista.operations.domain.dev.CardServiceSimulatorConfig;
import com.propertyvista.operations.domain.dev.EquifaxSimulatorConfig;
import com.propertyvista.operations.rpc.SimulationDTO;
import com.propertyvista.operations.rpc.services.simulator.SimulationService;

public class SimulationServiceImpl extends AdminServiceImpl implements SimulationService {

    @Override
    public void retrieve(AsyncCallback<SimulationDTO> callback, Key entityId, RetrieveTarget retrieveTarget) {
        SimulationDTO result = EntityFactory.create(SimulationDTO.class);
        result.setPrimaryKey(entityId);

        result.generalCacheEnabled().setValue(!CacheService.isDisabled());

        IEntityCacheService entityCacheService = ServerSideFactory.create(IEntityCacheService.class);
        result.entityCacheServiceEnabled().setValue(!entityCacheService.isDisabled());

        result.networkSimulation().set(NetworkSimulationServiceFilter.getNetworkSimulationConfig());

        result.systems().set(VistaSystemsSimulationConfig.getConfiguration());

        result.equifax().set(Persistence.service().retrieve(EntityQueryCriteria.create(EquifaxSimulatorConfig.class)));

        result.cardService().set(Persistence.service().retrieve(EntityQueryCriteria.create(CardServiceSimulatorConfig.class)));
        if (result.cardService().responseType().isNull()) {
            result.cardService().responseType().setValue(CardServiceSimulatorConfig.SimpulationType.SimulateTransations);
        }

        result.fundsTransferSimulationConfigurable().setValue(
                ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getBankingSimulatorConfiguration()
                        .isFundsTransferSimulationConfigurable());

        callback.onSuccess(result);
    }

    @Override
    public void save(AsyncCallback<Key> callback, SimulationDTO entity) {

        CacheService.setDisabled(!entity.generalCacheEnabled().isBooleanTrue());

        IEntityCacheService entityCacheService = ServerSideFactory.create(IEntityCacheService.class);
        entityCacheService.setDisabled(!entity.entityCacheServiceEnabled().isBooleanTrue());

        NetworkSimulationServiceFilter.setNetworkSimulationConfig(entity.networkSimulation());

        VistaSystemsSimulationConfig.setConfiguration(entity.systems());

        Persistence.service().persist(entity.equifax());
        Persistence.service().persist(entity.cardService());
        Persistence.service().commit();

        callback.onSuccess(entity.getPrimaryKey());
    }

    @Override
    public void create(AsyncCallback<Key> callback, SimulationDTO editableEntity) {
        throwSorryForPoorDesignError();
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<SimulationDTO>> callback, EntityListCriteria<SimulationDTO> criteria) {
        throwSorryForPoorDesignError();
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throwSorryForPoorDesignError();
    }

    @Override
    public void resetGlobalCache(AsyncCallback<VoidSerializable> callback) {
        throwSorryForPoorDesignError();
    }

    private static void throwSorryForPoorDesignError() {
        throw new Error("this method is not supposed to exits (it's only here in order to implement CRUD interface");
    }

}
