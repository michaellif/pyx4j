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
package com.propertyvista.domain;

public final class PreloadConfig {

    private int maxCustomers = 20;

    private int maxPropertyManagers = 10;

    private int maxAdmin = 2;

    private int numResidentialBuildings = 10;

    private int numTenants = 10;

    private int numPotentialTenants = 4;

    private int numFloors = 4;

    private int numFloorplans = 2;

    private int numParkings = 1;

    private int numParkingSpots = 128;

    private int numLockerAreas = 2;

    private int numLockers = 35;

    private int numUnitsPerFloor = 4;

    private PreloadConfig() {
    }

    public static PreloadConfig createDefault() {
        return new PreloadConfig();
    }

    public static PreloadConfig createTest() {
        PreloadConfig config = new PreloadConfig();
        config.setMaxCustomers(2);
        config.setMaxPropertyManagers(2);
        config.setMaxAdmin(1);
        config.setNumResidentialBuildings(1);
        config.setNumTenants(3);
        config.setNumPotentialTenants(2);
        config.setNumFloors(2);
        config.setNumFloorplans(2);
        config.setNumParkings(1);
        config.setNumParkingSpots(3);
        config.setNumLockerAreas(1);
        config.setNumLockers(2);
        config.setNumUnitsPerFloor(2);
        return config;
    }

    public int getMaxCustomers() {
        return maxCustomers;
    }

    public void setMaxCustomers(int maxCustomers) {
        this.maxCustomers = maxCustomers;
    }

    public int getNumResidentialBuildings() {
        return numResidentialBuildings;
    }

    public void setNumResidentialBuildings(int numResidentialBuildings) {
        this.numResidentialBuildings = numResidentialBuildings;
    }

    public int getNumTenants() {
        return numTenants;
    }

    public void setNumTenants(int numTenants) {
        this.numTenants = numTenants;
    }

    public int getNumPotentialTenants() {
        return numPotentialTenants;
    }

    public void setNumPotentialTenants(int numPotentialTenants) {
        this.numPotentialTenants = numPotentialTenants;
    }

    public int getNumFloors() {
        return numFloors;
    }

    public void setNumFloors(int numFloors) {
        this.numFloors = numFloors;
    }

    public int getNumFloorplans() {
        return numFloorplans;
    }

    public void setNumFloorplans(int numFloorplans) {
        this.numFloorplans = numFloorplans;
    }

    public int getNumParkings() {
        return numParkings;
    }

    public void setNumParkings(int numParkings) {
        this.numParkings = numParkings;
    }

    public int getNumParkingSpots() {
        return numParkingSpots;
    }

    public void setNumParkingSpots(int numParkingSpots) {
        this.numParkingSpots = numParkingSpots;
    }

    public int getNumLockerAreas() {
        return numLockerAreas;
    }

    public void setNumLockerAreas(int numLockerAreas) {
        this.numLockerAreas = numLockerAreas;
    }

    public int getNumLockers() {
        return numLockers;
    }

    public void setNumLockers(int numLockers) {
        this.numLockers = numLockers;
    }

    public int getNumUnitsPerFloor() {
        return numUnitsPerFloor;
    }

    public void setNumUnitsPerFloor(int numUnitsPerFloor) {
        this.numUnitsPerFloor = numUnitsPerFloor;
    }

    public int getMaxPropertyManagers() {
        return maxPropertyManagers;
    }

    public void setMaxPropertyManagers(int maxPropertyManagers) {
        this.maxPropertyManagers = maxPropertyManagers;
    }

    public int getMaxAdmin() {
        return maxAdmin;
    }

    public void setMaxAdmin(int maxAdmin) {
        this.maxAdmin = maxAdmin;
    }
}
