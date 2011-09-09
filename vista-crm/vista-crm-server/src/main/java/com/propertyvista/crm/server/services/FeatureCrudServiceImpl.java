/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.FeatureCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ServiceCatalog;

public class FeatureCrudServiceImpl extends GenericCrudServiceImpl<Feature> implements FeatureCrudService {

    public FeatureCrudServiceImpl() {
        super(Feature.class);
    }

    @Override
    protected void enhanceRetrieve(Feature entity, boolean fromList) {
        if (!fromList) {
            // Load detached data:
            Persistence.service().retrieve(entity.catalog());
            Persistence.service().retrieve(entity.items());
        }
    }

    @Override
    public void retrieveCatalog(AsyncCallback<ServiceCatalog> callback, Key entityId) {
        callback.onSuccess(Persistence.service().retrieve(ServiceCatalog.class, entityId));
    }
}
