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

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.ServiceItemCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.financial.offering.ServiceItem;

public class ServiceItemCrudServiceImpl extends GenericCrudServiceImpl<ServiceItem> implements ServiceItemCrudService {

    public ServiceItemCrudServiceImpl() {
        super(ServiceItem.class);
    }

    @Override
    protected void enhanceRetrieve(ServiceItem entity, boolean fromList) {
        if (!fromList) {
            // Load detached data:
            Persistence.service().retrieve(entity.element());
        }
    }
}
