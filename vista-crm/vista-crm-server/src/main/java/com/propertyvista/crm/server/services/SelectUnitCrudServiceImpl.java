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

import com.propertyvista.crm.rpc.services.SelectUnitCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class SelectUnitCrudServiceImpl extends GenericCrudServiceImpl<AptUnit> implements SelectUnitCrudService {

    public SelectUnitCrudServiceImpl() {
        super(AptUnit.class);
    }

    @Override
    protected void enhanceRetrieve(AptUnit entity, boolean fromList) {
        // load detached entities (temporary):
        Persistence.service().retrieve(entity.floorplan());
        // TODO actually just this is necessary, but it' doesn't implemented still:
        //Persistence.service().retrieve(entity.floorplan().name());
        //Persistence.service().retrieve(entity.floorplan().marketingName());

        super.enhanceRetrieve(entity, fromList);
    }
}
