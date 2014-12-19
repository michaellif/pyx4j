/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-24
 * @author ArtyomB
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.crm.server.services.dashboard.gadgets.UnitAvailabilityStatusListServiceImpl.LeasedStatusProvider;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class UnitAvailabilityStatusListServiceImplTest extends UnitAvailabilityStatusListServiceTestBase {

    public UnitAvailabilityStatusListServiceImplTest() {
        super(new UnitAvailabilityStatusListServiceImpl(new LeasedStatusProvider() {
            @Override
            public boolean isLeasedOn(LogicalDate asOfDate, AptUnit unitStub) {
                return false;
            }
        }));
    }

}
