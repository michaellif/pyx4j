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

import com.propertyvista.crm.rpc.services.TenantScreeningCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.tenant.TenantScreening;

public class TenantScreeningCrudServiceImpl extends GenericCrudServiceImpl<TenantScreening> implements TenantScreeningCrudService {

    public TenantScreeningCrudServiceImpl() {
        super(TenantScreening.class);
    }

    @Override
    protected void enhanceRetrieve(TenantScreening entity, boolean fromList) {
        if (!fromList) {
            // load detached entities:
            Persistence.service().retrieve(entity.documents());
            Persistence.service().retrieve(entity.incomes());
            Persistence.service().retrieve(entity.assets());
            Persistence.service().retrieve(entity.guarantors());
        }
    }
}
