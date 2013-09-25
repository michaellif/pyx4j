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
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.productcatalog.ProductCatalogFacade;
import com.propertyvista.crm.rpc.services.building.catalog.ServiceCrudService;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;

public class ServiceCrudServiceImpl extends AbstractCrudServiceImpl<Service> implements ServiceCrudService {

    public ServiceCrudServiceImpl() {
        super(Service.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected Service init(InitializationData initializationData) {
        ServiceInitializationdata initData = (ServiceInitializationdata) initializationData;

        Service entity = EntityFactory.create(Service.class);
        entity.type().setValue(initData.type().getValue());
        entity.catalog().setPrimaryKey(initData.parent().getPrimaryKey());
        entity.catalog().setValueDetached();

        return entity;
    }

    @Override
    protected void enhanceRetrieved(Service dbo, Service to, RetrieveTarget retrieveTarget) {
        // Load detached data:

        /*
         * catalog retrieving is necessary for building element selection
         * (lister filtering by catalog().building() in @link ServiceItemFolder.AddItem())
         * and building element filtering in ServiceItemEditor
         */
        Persistence.service().retrieve(to.catalog());

        Persistence.service().retrieve(to.version().items());
        Persistence.service().retrieve(to.version().features());
        Persistence.service().retrieve(to.version().concessions());

        // next level:
        for (ProductItem item : to.version().items()) {
            Persistence.service().retrieve(item.element());
        }
        for (Feature feature : to.version().features()) {
            Persistence.service().retrieve(feature.version().items());
            // next level:
            for (ProductItem item : feature.version().items()) {
                Persistence.service().retrieve(item.element());
            }
        }
    }

    @Override
    public void retrieveCatalog(AsyncCallback<ProductCatalog> callback, Key entityId) {
        callback.onSuccess(Persistence.service().retrieve(ProductCatalog.class, entityId));
    }

    @Override
    protected void persist(Service bo, Service to) {
        super.persist(bo, to);

        // update unit market prices here:
        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitMarketPrice(bo);
    }
}
