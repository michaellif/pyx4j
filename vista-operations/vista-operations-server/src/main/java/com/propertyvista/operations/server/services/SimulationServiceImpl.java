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
package com.propertyvista.operations.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.IEntityCacheService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.admin.AdminServiceImpl;
import com.pyx4j.essentials.server.dev.NetworkSimulationServiceFilter;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.DevSession;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaSystemsSimulationConfig;
import com.propertyvista.operations.domain.dev.EquifaxSimulatorConfig;
import com.propertyvista.operations.rpc.dto.SimulationDTO;
import com.propertyvista.operations.rpc.services.SimulationService;
import com.propertyvista.operations.server.services.simulator.CardServiceSimulationUtils;

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

        result.cardService().set(CardServiceSimulationUtils.getCardServiceSimulatorConfig());

        result.fundsTransferSimulationConfigurable().setValue(
                ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getBankingSimulatorConfiguration()
                        .isFundsTransferSimulationConfigurable());

        result.devSessionDuration().setValue(TimeUtils.durationFormatSeconds(DevSession.getSessionDuration()));

        int sessionDuration = 0;
        if (ServerSideConfiguration.instance().getOverrideSessionMaxInactiveInterval() != null) {
            sessionDuration = ServerSideConfiguration.instance().getOverrideSessionMaxInactiveInterval();
        } else {
            sessionDuration = Context.getRequest().getServletContext().getSessionCookieConfig().getMaxAge();
        }
        result.applicationSessionDuration().setValue(TimeUtils.durationFormatSeconds(sessionDuration));
        result.containerSessionTimeout().setValue(
                TimeUtils.durationFormatSeconds(Context.getRequest().getServletContext().getSessionCookieConfig().getMaxAge()));

        callback.onSuccess(result);
    }

    @Override
    public void save(AsyncCallback<Key> callback, SimulationDTO entity) {

        CacheService.setDisabled(!entity.generalCacheEnabled().isBooleanTrue());

        IEntityCacheService entityCacheService = ServerSideFactory.create(IEntityCacheService.class);
        entityCacheService.setDisabled(!entity.entityCacheServiceEnabled().isBooleanTrue());

        NetworkSimulationServiceFilter.setNetworkSimulationConfig(entity.networkSimulation());

        DevSession.setSessionDuration(TimeUtils.durationParseSeconds(entity.devSessionDuration().getValue()));

        int sessionDuration = TimeUtils.durationParseSeconds(entity.applicationSessionDuration().getValue());
        if (sessionDuration == Context.getRequest().getServletContext().getSessionCookieConfig().getMaxAge()) {
            ServerSideConfiguration.instance().setOverrideSessionMaxInactiveInterval(null);
        } else {
            ServerSideConfiguration.instance().setOverrideSessionMaxInactiveInterval(sessionDuration);
        }

        VistaSystemsSimulationConfig.setConfiguration(entity.systems());

        Persistence.service().persist(entity.equifax());
        Persistence.service().persist(entity.cardService());
        Persistence.service().commit();

        callback.onSuccess(entity.getPrimaryKey());
    }

    @Override
    public void init(AsyncCallback<SimulationDTO> callback, com.pyx4j.entity.rpc.AbstractCrudService.InitializationData initializationData) {
        throwSorryForPoorDesignError();
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
