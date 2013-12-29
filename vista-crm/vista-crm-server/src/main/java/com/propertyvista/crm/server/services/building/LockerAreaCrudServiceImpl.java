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
package com.propertyvista.crm.server.services.building;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;

import com.propertyvista.crm.rpc.services.building.LockerAreaCrudService;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.dto.LockerAreaDTO;

public class LockerAreaCrudServiceImpl extends AbstractCrudServiceDtoImpl<LockerArea, LockerAreaDTO> implements LockerAreaCrudService {

    public LockerAreaCrudServiceImpl() {
        super(LockerArea.class, LockerAreaDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected LockerAreaDTO init(InitializationData initializationData) {
        LockerAreaDTO lockerArea = EntityFactory.create(LockerAreaDTO.class);

        // do not allow null members!
        lockerArea.totalLockers().setValue(0);
        lockerArea.largeLockers().setValue(0);
        lockerArea.regularLockers().setValue(0);
        lockerArea.smallLockers().setValue(0);

        return lockerArea;
    }
}
