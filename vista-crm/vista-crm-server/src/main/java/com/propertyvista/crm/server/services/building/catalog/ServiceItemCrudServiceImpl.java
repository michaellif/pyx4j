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

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.building.catalog.ServiceItemCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.financial.offering.ProductItem;

public class ServiceItemCrudServiceImpl extends GenericCrudServiceImpl<ProductItem> implements ServiceItemCrudService {

    public ServiceItemCrudServiceImpl() {
        super(ProductItem.class);
    }

    @Override
    protected void enhanceRetrieved(ProductItem entity, boolean fromList) {
        if (!fromList) {
            // Load detached data:
            if (entity.element().isValueDetached()) {
                Persistence.service().retrieve(entity.element());
            }
        }
    }

    @Override
    protected void enhanceSave(ProductItem entity) {
        if (entity.element().isValueDetached()) {
            Persistence.service().merge(entity.element());
        }
    }
}
