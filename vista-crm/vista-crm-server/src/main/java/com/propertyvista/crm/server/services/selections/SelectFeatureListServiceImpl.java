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
package com.propertyvista.crm.server.services.selections;

import com.pyx4j.entity.server.AbstractListServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.selections.SelectFeatureListService;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;

public class SelectFeatureListServiceImpl extends AbstractListServiceImpl<Feature> implements SelectFeatureListService {

    public SelectFeatureListServiceImpl() {
        super(Feature.class);
    }

    @Override
    protected void bind() {
        bind(toProto.id(), boProto.id());
        bindCompleteObject();
    }

    @Override
    protected void enhanceListRetrieved(Feature entity, Feature dto) {
        // Load detached data:
        Persistence.service().retrieve(dto.version().items());
        // next level:
        for (ProductItem item : dto.version().items()) {
            Persistence.service().retrieve(item.element());
        }
    }
}
