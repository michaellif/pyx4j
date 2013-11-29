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

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.rpc.services.building.catalog.FeatureCrudService;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;

public class FeatureCrudServiceImpl extends AbstractCrudServiceImpl<Feature> implements FeatureCrudService {

    public FeatureCrudServiceImpl() {
        super(Feature.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected Feature init(InitializationData initializationData) {
        FeatureInitializationData initData = (FeatureInitializationData) initializationData;

        Feature entity = EntityFactory.create(Feature.class);
        entity.code().set(initData.code());
        entity.catalog().setPrimaryKey(initData.parent().getPrimaryKey());
        entity.catalog().setValueDetached();

        return entity;
    }

    @Override
    protected void enhanceRetrieved(Feature bo, Feature to, RetrieveTarget retrieveTarget) {
        // Load detached data:

        /*
         * catalog retrieving is necessary for building element filtering by catalog().building() in @link FeatureItemEditor
         */
        Persistence.service().retrieve(to.catalog());
        Persistence.service().retrieve(to.version().items());
        // next level:
        for (ProductItem item : to.version().items()) {
            Persistence.service().retrieve(item.element());
        }
    }

}
