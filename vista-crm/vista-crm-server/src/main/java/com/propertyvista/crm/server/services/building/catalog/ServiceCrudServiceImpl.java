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
package com.propertyvista.crm.server.services.building.catalog;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.building.catalog.ServiceCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;

public class ServiceCrudServiceImpl extends GenericCrudServiceImpl<Service> implements ServiceCrudService {

    public ServiceCrudServiceImpl() {
        super(Service.class);
    }

    @Override
    protected void enhanceRetrieved(Service entity, boolean fromList) {
        if (!fromList) {
            // Load detached data:

            /*
             * catalog retrieving is necessary for building element selection
             * (lister filtering by catalog().building() in @link ServiceItemFolder.AddItem())
             * and building element filtering in ServiceItemEditor
             */
            Persistence.service().retrieve(entity.catalog());

            Persistence.service().retrieve(entity.items());
            Persistence.service().retrieve(entity.features());
            Persistence.service().retrieve(entity.concessions());

            // next level:
            for (ProductItem item : entity.items()) {
                Persistence.service().retrieve(item.element());
            }
            for (Feature feature : entity.features()) {
                Persistence.service().retrieve(feature.items());
                // next level:
                for (ProductItem item : feature.items()) {
                    Persistence.service().retrieve(item.element());
                }
            }
        }
    }

    @Override
    public void retrieveCatalog(AsyncCallback<ProductCatalog> callback, Key entityId) {
        callback.onSuccess(Persistence.service().retrieve(ProductCatalog.class, entityId));
    }
}
