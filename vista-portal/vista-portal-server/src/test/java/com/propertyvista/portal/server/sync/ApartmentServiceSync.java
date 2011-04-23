/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 23, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.sync;

import org.junit.Assert;

import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.rpc.pt.services.ApartmentService;

import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.UnitTestsAsyncCallback;

public class ApartmentServiceSync {

    private UnitSelection unitSelection;

    public UnitSelection retrieve() {
        unitSelection = null;

        ApartmentService apartmentService = TestServiceFactory.create(ApartmentService.class);
        apartmentService.retrieve(new UnitTestsAsyncCallback<UnitSelection>() {
            @Override
            public void onSuccess(UnitSelection result) {
                Assert.assertNotNull("Unit selection", result);
                unitSelection = result;
            }
        }, null);
        return unitSelection;
    }
}
