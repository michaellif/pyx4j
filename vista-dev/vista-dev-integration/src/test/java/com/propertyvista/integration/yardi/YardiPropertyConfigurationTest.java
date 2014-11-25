/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import java.rmi.RemoteException;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.yardi.YardiTestBase;
import com.propertyvista.yardi.mock.model.YardiMock;
import com.propertyvista.yardi.mock.model.manager.YardiBuildingManager;
import com.propertyvista.yardi.mock.model.manager.YardiConfigurationManager;
import com.propertyvista.yardi.mock.model.manager.impl.YardiMockModelUtils;
import com.propertyvista.yardi.mock.model.stub.impl.YardiMockILSGuestCardStubImpl;
import com.propertyvista.yardi.mock.model.stub.impl.YardiMockResidentTransactionsStubImpl;
import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;

public class YardiPropertyConfigurationTest extends YardiTestBase {

    final String BuildingID = YardiBuildingManager.DEFAULT_PROPERTY_CODE;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();

        YardiMock.server().reset();
        // managers
        YardiMock.server().addManager(YardiBuildingManager.class);
        YardiMock.server().addManager(YardiConfigurationManager.class);
        // stubs
        YardiMock.addStub(YardiResidentTransactionsStub.class, YardiMockResidentTransactionsStubImpl.class);
        YardiMock.addStub(YardiILSGuestCardStub.class, YardiMockILSGuestCardStubImpl.class);
    }

    /*
     * Negative Flow Validation. Ensure NoAccess Exception results in building suspension.
     * - configure yardi property
     * - do first yardi import to create Building
     * - do second yardi import that generates No Access Exception
     * - ensure import completes and Building is suspended
     */
    public void testBuildingSuspendedOnNoAccess() throws Exception {
        // 1. Property setup
        // -----------------
        YardiMock.server().getManager(YardiBuildingManager.class).addDefaultBuilding();
        YardiMock.server().getManager(YardiConfigurationManager.class).addProperty(YardiILSGuestCardStub.class, BuildingID);
        YardiMock.server().getManager(YardiConfigurationManager.class).addProperty(YardiResidentTransactionsStub.class, BuildingID);
        // 2. Execution: first import
        // --------------------------
        yardiImportAll(getYardiCredential(BuildingID));
        // 3. Assert building
        // ------------------
        Building building = getBuilding(BuildingID);
        assertNotNull(building);
        assertFalse(building.suspended().getValue(false));
        // 4. Generate 'NoAccess' error by changing propertyCode
        YardiMockModelUtils.findBuilding(BuildingID).buildingId().setValue("x" + BuildingID);
        // 5. Execution: second import (1 erred)
        // -------------------------------------
        yardiImportAll(getYardiCredential(BuildingID), 1, 0);
        building = getBuilding(BuildingID);
        // 6. Assert building suspended
        assertTrue(building.suspended().getValue(false));
    }

    /*
     * Negative Flow Validation. Ensure RemoteException is properly handled to avoid building suspension.
     * - configure yardi property
     * - do first yardi import to create Building
     * - do second yardi import that generates RemoteException
     * - ensure import fails and Building is not suspended
     */
    public void testBuildingNotSuspendedOnYardiRemoteException() throws Exception {
        // 1. Property setup
        // -----------------
        YardiMock.server().getManager(YardiBuildingManager.class).addDefaultBuilding();
        YardiMock.server().getManager(YardiConfigurationManager.class).addProperty(YardiILSGuestCardStub.class, BuildingID);
        YardiMock.server().getManager(YardiConfigurationManager.class).addProperty(YardiResidentTransactionsStub.class, BuildingID);
        // 2. Execution: first import
        // --------------------------
        yardiImportAll(getYardiCredential(BuildingID));
        // 3. Assert building
        // ------------------
        Building building = getBuilding(BuildingID);
        assertNotNull(building);
        assertFalse(building.suspended().getValue(false));
        // 4. Simulate Error
        YardiMock.server().simulateException();
        // 5. Execution: second import
        // ---------------------------
        try {
            yardiImportAll(getYardiCredential(BuildingID));
            // 6. Assert import fails
            // ----------------------
            assertFalse("Second import must fail", true);
        } catch (RemoteException simulated) {
            // 7. Assert building not suspended
            // --------------------------------
            building = getBuilding(BuildingID);
            assertFalse(building.suspended().getValue(false));
        }
    }
}
