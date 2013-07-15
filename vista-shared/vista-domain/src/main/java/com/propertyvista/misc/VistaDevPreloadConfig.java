/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 18, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.misc;

import java.io.Serializable;

import com.propertyvista.domain.DemoData;

@SuppressWarnings("serial")
public final class VistaDevPreloadConfig implements Serializable {

    public long leaseGenerationSeed = 100;

    public long buildingsGenerationSeed = 100;

    public long tenantsGenerationSeed = 100;

    public boolean minimizePreloadTime = false;

    public boolean mockupData = false;

    public int updateArrearsHistorNumOfYearsBack = 4;

    public int maintenanceRequestsDaysBack = 60;

    public int maxPropertyManagers = DemoData.UserType.PM.getDefaultMax();

    public int maxPropertyManagementEmployee = DemoData.UserType.EMP.getDefaultMax();

    public int maxAdmin = DemoData.UserType.ADMIN.getDefaultMax();

    public int numTenants = DemoData.UserType.TENANT.getDefaultMax() + 50;

    /** make only one bill and one payment for the lease */
    public boolean oneBillOnePayment = false;

    public int numOfLeasesWithNoSimulation = 3;

    /** number of leases that will be generated by lease simulator running bills / payments */
    public int numOfPseudoRandomLeasesWithSimulatedBilling = 3;

    public int numPotentialTenants = DemoData.UserType.PTENANT.getDefaultMax() + 5;

    public int numPotentialTenants2CreditCheck = 20;

    public int numUnAssigendTenants = DemoData.UserType.NEW_TENANT.getDefaultMax();

    public int numTenantsInLease = 2;

    public int numComplexes = 3;

    public String province = null;

    public int numResidentialBuildings = 15;

    public int numLeads = 5;

    public int numMntRequests = 10;

    public int numFloors = 4;

    public int numFloorplans = 5;

    public int numParkings = 1;

    public int numParkingSpots = 128;

    public int numLockerAreas = 2;

    public int numLockers = 35;

    public int numUnitsPerFloor = 4;

    public int numElevators = 3;

    public int numBoilers = 2;

    public int numRoofs = 1;

    private VistaDevPreloadConfig() {
    }

    public static VistaDevPreloadConfig createDefault() {
        return new VistaDevPreloadConfig();
    }

    public static VistaDevPreloadConfig createMockup() {
        VistaDevPreloadConfig config = createDefault();
        config.mockupData = true;
        //config.numOfPseudoRandomLeasesWithSimulatedBilling = (config.numResidentialBuildings / 2) * config.numUnitsPerFloor * config.numFloorplans;
        config.numOfPseudoRandomLeasesWithSimulatedBilling = 10;
        return config;
    }

    public static VistaDevPreloadConfig createTest() {
        VistaDevPreloadConfig config = new VistaDevPreloadConfig();
        config.maxPropertyManagers = 1;
        config.maxAdmin = 1;
        config.numResidentialBuildings = 2;
        config.numUnAssigendTenants = 0;
        config.numPotentialTenants = 1;
        config.numPotentialTenants2CreditCheck = 1;
        config.numTenants = 1;
        config.numTenantsInLease = 2;
        config.numFloors = 2;
        config.numFloorplans = 2;
        config.numParkings = 1;
        config.numParkingSpots = 3;
        config.numLockerAreas = 1;
        config.numLockers = 2;
        config.numUnitsPerFloor = 2;
        config.numOfLeasesWithNoSimulation = 0;
        config.numOfPseudoRandomLeasesWithSimulatedBilling = 1;
        config.oneBillOnePayment = true;
        return config;
    }

    public static VistaDevPreloadConfig createUIDesignMini() {
        VistaDevPreloadConfig config = new VistaDevPreloadConfig();
        config.maxAdmin = 1;
        config.numResidentialBuildings = 10;
        config.numTenants = 1;
        config.numUnAssigendTenants = 0;
        config.numPotentialTenants = 1;
        config.numPotentialTenants2CreditCheck = 1;
        config.numTenantsInLease = 2;
        config.numFloors = 2;
        config.numFloorplans = 2;
        config.numParkings = 1;
        config.numParkingSpots = 3;
        config.numLockerAreas = 1;
        config.numLockers = 2;
        config.numUnitsPerFloor = 2;
        config.minimizePreloadTime = true;
        config.numOfLeasesWithNoSimulation = 0;
        config.numOfPseudoRandomLeasesWithSimulatedBilling = 1;
        config.oneBillOnePayment = true;
        return config;
    }

}
