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
package com.propertyvista.field.server.services.building.catalog;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.field.rpc.services.building.catalog.FeatureCrudService;

public class FeatureCrudServiceImpl extends AbstractCrudServiceImpl<Feature> implements FeatureCrudService {

    public FeatureCrudServiceImpl() {
        super(Feature.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceRetrieved(Feature entity, Feature dto, RetrieveTarget retrieveTarget ) {
        // Load detached data:

        /*
         * catalog retrieving is necessary for building element filtering by catalog().building() in @link FeatureItemEditor
         */
        Persistence.service().retrieve(dto.catalog());
        Persistence.service().retrieve(dto.version().items());
        // next level:
        for (ProductItem item : dto.version().items()) {
            Persistence.service().retrieve(item.element());
        }
    }

}
