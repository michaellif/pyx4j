/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 23, 2013
 * @author stanp
 */
package com.propertyvista.crm.server.services.vista2pmc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.vista2pmc.ILSConfigCrudService;
import com.propertyvista.domain.settings.ILSConfig;
import com.propertyvista.dto.vista2pmc.ILSConfigDTO;

public class ILSConfigCrudServiceImpl extends AbstractCrudServiceDtoImpl<ILSConfig, ILSConfigDTO> implements ILSConfigCrudService {

    public ILSConfigCrudServiceImpl() {
        super(ILSConfig.class, ILSConfigDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    public void retrieve(AsyncCallback<Key> callback) {
        ILSConfig config = Persistence.service().retrieve(EntityQueryCriteria.create(ILSConfig.class));
        if (config == null) {
            config = EntityFactory.create(ILSConfig.class);
            Persistence.service().persist(config);
            Persistence.service().commit();
        }
        callback.onSuccess(config.getPrimaryKey());
    }
}
