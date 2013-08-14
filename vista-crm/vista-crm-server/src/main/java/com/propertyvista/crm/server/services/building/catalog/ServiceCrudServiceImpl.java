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
        bindCompleteDBO();
    }

    @Override
    protected void enhanceRetrieved(Service dbo, Service dto, RetrieveTarget retrieveTarget) {
        // Load detached data:

        /*
         * catalog retrieving is necessary for building element selection
         * (lister filtering by catalog().building() in @link ServiceItemFolder.AddItem())
         * and building element filtering in ServiceItemEditor
         */
        Persistence.service().retrieve(dto.catalog());

        Persistence.service().retrieve(dto.version().items());
        Persistence.service().retrieve(dto.version().features());
        Persistence.service().retrieve(dto.version().concessions());

        // next level:
        for (ProductItem item : dto.version().items()) {
            Persistence.service().retrieve(item.element());
        }
        for (Feature feature : dto.version().features()) {
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
    protected void persist(Service entity, Service dto) {
        super.persist(entity, dto);

        // update unit market prices here:
        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitMarketPrice(entity);
    }
}
