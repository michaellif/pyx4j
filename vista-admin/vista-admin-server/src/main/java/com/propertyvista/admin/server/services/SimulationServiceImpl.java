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
package com.propertyvista.admin.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.IEntityCacheService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.essentials.server.admin.AdminServiceImpl;
import com.pyx4j.essentials.server.dev.NetworkSimulationServiceFilter;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.admin.rpc.SimulationDTO;
import com.propertyvista.admin.rpc.services.SimulationService;
import com.propertyvista.payment.pad.CaledonPadSftpClient;

public class SimulationServiceImpl extends AdminServiceImpl implements SimulationService {

    @Override
    public void retrieve(AsyncCallback<SimulationDTO> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTraget retrieveTraget) {
        SimulationDTO result = EntityFactory.create(SimulationDTO.class);
        result.setPrimaryKey(entityId);

        result.generalCacheEnabled().setValue(!CacheService.isDisabled());

        IEntityCacheService entityCacheService = ServerSideFactory.create(IEntityCacheService.class);
        result.entityCacheServiceEnabled().setValue(!entityCacheService.isDisabled());

        result.networkSimulation().set(NetworkSimulationServiceFilter.getNetworkSimulationConfig());

        result.usePadSimulator().setValue(CaledonPadSftpClient.usePadSimulator());

        callback.onSuccess(result);
    }

    @Override
    public void save(AsyncCallback<SimulationDTO> callback, SimulationDTO entity) {

        CacheService.setDisabled(!entity.generalCacheEnabled().isBooleanTrue());

        IEntityCacheService entityCacheService = ServerSideFactory.create(IEntityCacheService.class);
        entityCacheService.setDisabled(!entity.entityCacheServiceEnabled().isBooleanTrue());

        NetworkSimulationServiceFilter.setNetworkSimulationConfig(entity.networkSimulation());

        CaledonPadSftpClient.setUsePadSimulator(entity.usePadSimulator().getValue());

        retrieve(callback, entity.getPrimaryKey(), null);
    }

    @Override
    public void create(AsyncCallback<SimulationDTO> callback, SimulationDTO editableEntity) {
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
