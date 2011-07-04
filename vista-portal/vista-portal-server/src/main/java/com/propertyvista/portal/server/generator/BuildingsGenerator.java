/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 19, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.common.domain.financial.ChargeType;
import com.propertyvista.common.domain.ref.PetType;
import com.propertyvista.domain.Address;
import com.propertyvista.domain.Email;
import com.propertyvista.domain.Phone;
import com.propertyvista.domain.financial.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ParkingRent;
import com.propertyvista.domain.financial.offering.PetCharge;
import com.propertyvista.domain.financial.offering.PetPrice;
import com.propertyvista.domain.financial.offering.ResidentialRent;
import com.propertyvista.domain.financial.offering.StorageRent;
import com.propertyvista.domain.property.asset.AreaMeasurementUnit;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.Locker;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.ParkingSpot;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.domain.property.asset.unit.AptUnitAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.domain.property.asset.unit.AptUnitType;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.portal.domain.ptapp.LeaseTerms;
import com.propertyvista.portal.domain.ptapp.PetChargeRule;
import com.propertyvista.portal.domain.ptapp.PropertyProfile;
import com.propertyvista.portal.server.preloader.RandomUtil;
import com.propertyvista.server.common.generator.UnitRelatedData;

public class BuildingsGenerator {

    private final static Logger log = LoggerFactory.getLogger(BuildingsGenerator.class);

    private final long seed;

    public BuildingsGenerator(long seed) {
        DataGenerator.setRandomSeed(seed);
        this.seed = seed;
    }

    public List<Building> createBuildings(int numBuildings) {
        List<Building> buildings = new ArrayList<Building>();
        for (int b = 0; b < numBuildings; b++) {
            Building building = createBuilding(b);
            buildings.add(building);
        }
        return buildings;
    }

    public Building createBuilding(int b) {
        // building type
        BuildingInfo.Type buildingType = RandomUtil.random(BuildingInfo.Type.values());

        Complex complex = null;
        if (b % 3 == 0) {
            complex = createComplex(2);
        }

        String website = "www.property" + (b + 1) + ".com";

        // address
        Address address = CommonsGenerator.createAddress();

        // phones
        List<Phone> phones = new ArrayList<Phone>();
        phones.add(CommonsGenerator.createPhone());

        // email
        String emailAddress = "building" + (b + 1) + "@propertyvista.com";
        Email email = CommonsGenerator.createEmail(emailAddress);

        // organization contacts - not many fields there at the moment, will do
        // this later
        String propertyCode = "A" + String.valueOf(b);
        if (b == 0) {
            // UI is looking for this building, see references!
            propertyCode = DemoData.REGISTRATION_DEFAULT_PROPERTY_CODE;
        }

        // property profile TODO - nobody is using this property profile right
        // now
        PropertyProfile propertyProfile = createPropertyProfile(b);

        Building building = createBuilding(propertyCode, buildingType, complex, website, address, phones, email);
        // log.info("Created: " + building);

        return building;
    }

    private Building createBuilding(String propertyCode, BuildingInfo.Type buildingType, Complex complex, String website, Address address, List<Phone> phones,
            Email email) {
        Building building = EntityFactory.create(Building.class);

        building.info().propertyCode().setValue(propertyCode);

        building.info().type().setValue(buildingType);
        // building.complex().
        building.contacts().website().setValue(website);

        building.info().address().set(address);

        for (Phone phone : phones) {
            building.contacts().phones().add(phone);
        }

        building.info().name().setValue(RandomUtil.randomLetters(3));

        building.marketing().description().setValue(CommonsGenerator.lipsum());

        building.marketing().name().setValue(RandomUtil.randomLetters(4) + " " + RandomUtil.randomLetters(6));

        building.contacts().email().set(email); // not sure yet what to do about
                                                // the email and its type

        return building;
    }

// Lockers:
    public List<LockerArea> createLockerAreas(Building owner, int num) {
        List<LockerArea> lockerAreas = new ArrayList<LockerArea>();
        for (int i = 0; i < num; i++) {
            lockerAreas.add(createLockerArea(owner, (i + 1)));
        }
        return lockerAreas;
    }

    private LockerArea createLockerArea(Building owner, int index) {
        LockerArea lockerArea = EntityFactory.create(LockerArea.class);
        lockerArea.belongsTo().set(owner);

        lockerArea.name().setValue("LockerArea" + index);
        lockerArea.isPrivate().setValue(RandomUtil.randomBoolean());
        lockerArea.levels().setValue((double) RandomUtil.randomInt(3));
        lockerArea.description().setValue(lockerArea.levels().getValue() + "-level locker" + index + " at " + owner.info().name().getValue());

        int total = 1 + RandomUtil.randomInt(100);
        int large = (int) (total * 0.1);
        int regular = (int) (total * 0.07);
        int small = total - (large + regular);
        lockerArea.totalLockers().setValue(total);
        lockerArea.largeLockers().setValue(large);
        lockerArea.regularLockers().setValue(regular);
        lockerArea.smallLockers().setValue(small);

        return lockerArea;
    }

    public List<Locker> createLockers(LockerArea owner, int num) {
        List<Locker> lockers = new ArrayList<Locker>();
        for (int i = 0; i < num; i++) {
            lockers.add(createLocker(owner, (i + 1)));
        }
        return lockers;
    }

    private Locker createLocker(LockerArea owner, int index) {
        Locker locker = EntityFactory.create(Locker.class);
        locker.belongsTo().set(owner);

        locker.name().setValue("Locker" + index);
        locker.type().setValue(RandomUtil.random(Locker.Type.values()));
        return locker;
    }

    public List<Parking> createParkings(Building owner, int numParkings) {
        List<Parking> parkings = new ArrayList<Parking>();
        for (int i = 0; i < numParkings; i++) {
            parkings.add(createParking(owner, (i + 1)));
        }
        return parkings;
    }

// Parking:
    private Parking createParking(Building building, int index) {
        Parking parking = EntityFactory.create(Parking.class);
        parking.belongsTo().set(building);

        int levels = 1 + RandomUtil.randomInt(5);
        parking.name().setValue("Parking" + index);
        parking.description().setValue(levels + "-level parking" + index + " at " + building.info().name().getValue());
        parking.type().setValue(RandomUtil.random(Parking.Type.values()));
        parking.levels().setValue((double) levels);

        int totalSpaces = 1 + RandomUtil.randomInt(100);
        int disabledSpaces = (int) (totalSpaces * 0.05);
        int doubleSpaces = (int) (totalSpaces * 0.1);
        int narrowSpaces = (int) (totalSpaces * 0.07);
        int regularSpaces = totalSpaces - (disabledSpaces + doubleSpaces + narrowSpaces);
        parking.totalSpaces().setValue(totalSpaces);
        parking.disabledSpaces().setValue(disabledSpaces);
        parking.regularSpaces().setValue(regularSpaces);
        parking.wideSpaces().setValue(doubleSpaces);
        parking.narrowSpaces().setValue(narrowSpaces);

        return parking;
    }

    public List<ParkingSpot> createParkingSpots(Parking owner, int num) {
        List<ParkingSpot> parkings = new ArrayList<ParkingSpot>();
        for (int i = 0; i < num; i++) {
            parkings.add(createParkingSpot(owner, (i + 1)));
        }
        return parkings;
    }

    private ParkingSpot createParkingSpot(Parking owner, int index) {
        ParkingSpot spot = EntityFactory.create(ParkingSpot.class);
        spot.belongsTo().set(owner);

        spot.name().setValue("Spot" + index);
        spot.type().setValue(RandomUtil.random(ParkingSpot.Type.values()));
        return spot;
    }

// Floorplans:
    public List<FloorplanDTO> createFloorplans(Building building, int num) {
        List<FloorplanDTO> floorplans = new ArrayList<FloorplanDTO>();
        for (int i = 0; i < num; i++) {
            String floorplanName = building.info().propertyCode().getStringView() + "-" + i;
            if (i == 1) {
                floorplanName = DemoData.REGISTRATION_DEFAULT_FLOORPLAN;
            }

            FloorplanDTO floorplan = createFloorplan(floorplanName);
            floorplan.building().set(building);
            floorplans.add(floorplan);
        }
        return floorplans;
    }

    private FloorplanDTO createFloorplan(String name) {
        FloorplanDTO floorplan = EntityFactory.create(FloorplanDTO.class);

        floorplan.name().setValue(name);
        floorplan.description().setValue(CommonsGenerator.lipsum());

        floorplan.floorCount().setValue(1 + DataGenerator.randomInt(2));
        floorplan.bedrooms().setValue(1 + (double) DataGenerator.randomInt(6));
        floorplan.bathrooms().setValue(1 + (double) DataGenerator.randomInt(3));

        for (int i = 0; i < 2 + DataGenerator.randomInt(6); i++) {
            FloorplanAmenity amenity = BuildingsGenerator.createFloorplanAmenity();
            amenity.belongsTo().set(floorplan);
            floorplan.amenities().add(amenity);
        }

        for (int i = 0; i < 3; i++) {
            Feature feature = createFloorplanFeature();
            if (feature != null) {
//                feature.belongsTo().set(floorplan);
                floorplan.features().add(feature);
            }
        }

        return floorplan;
    }

    public static FloorplanAmenity createFloorplanAmenity() {
        FloorplanAmenity amenity = EntityFactory.create(FloorplanAmenity.class);
        amenity.type().setValue(RandomUtil.random(FloorplanAmenity.Type.values()));
        amenity.subType().setValue(RandomUtil.random(FloorplanAmenity.SubType.values()));
        return amenity;
    }

    // various Feature types:
    public Feature createFloorplanFeature() {
        Feature feature = null;
        if (RandomUtil.randomBoolean()) {
            feature = createParkingRent();
        } else if (RandomUtil.randomBoolean()) {
            feature = createStorageRent();
        } else if (RandomUtil.randomBoolean()) {
            feature = createPetCharge();
        } else if (RandomUtil.randomBoolean()) {
            feature = createResidentialRent();
        }

        if (feature != null) {
            feature.start().setValue(DataGenerator.randomDate(2));
            feature.end().setValue(DataGenerator.randomDate(4));

            // concessions
            for (int i = 0; i < 1 + DataGenerator.randomInt(4); i++) {
                feature.concessions().add(createConcession());
            }
        }

        return feature;
    }

    private Feature createResidentialRent() {
        ResidentialRent feature = EntityFactory.create(ResidentialRent.class);

        // currently nothing here...

        return feature;
    }

    private Feature createParkingRent() {
        ParkingRent feature = EntityFactory.create(ParkingRent.class);

        double regularRent = 20d + RandomUtil.randomInt(30);
        feature.disableRent().setValue(regularRent * 0.8);
        feature.regularRent().setValue(regularRent);
        feature.wideRent().setValue(regularRent * 1.2);
        feature.narrowRent().setValue(regularRent * 0.9);
        feature.deposit().setValue(50d + RandomUtil.randomInt(50));

        return feature;
    }

    private Feature createStorageRent() {
        StorageRent feature = EntityFactory.create(StorageRent.class);

        double regularRent = 10d + RandomUtil.randomInt(20);
        feature.regularRent().setValue(regularRent);
        feature.largeRent().setValue(regularRent * 1.2);
        feature.smallRent().setValue(regularRent * 0.9);
        feature.deposit().setValue(20d + RandomUtil.randomInt(50));

        return feature;
    }

    private Feature createPetCharge() {
        PetCharge feature = EntityFactory.create(PetCharge.class);

        for (PetType pet : PetType.values()) {
            PetPrice price = EntityFactory.create(PetPrice.class);
            price.type().setValue(pet);
            price.price().setValue(5d + RandomUtil.randomInt(10));
        }

        return feature;
    }

    private Concession createConcession() {
        Concession concession = EntityFactory.create(Concession.class);

        concession.type().setValue(RandomUtil.random(Concession.Type.values()));

        if (concession.type().getValue() == Concession.Type.percentageOff) {
            concession.value().setValue(10d + RandomUtil.randomInt(90));
        } else if (concession.type().getValue() == Concession.Type.monetaryOff) {
            concession.value().setValue(50d + RandomUtil.randomInt(50));
        } else if (concession.type().getValue() == Concession.Type.gift) {
            concession.value().setValue(100d + RandomUtil.randomInt(100));
        } else if (concession.type().getValue() == Concession.Type.skipFirstPayment) {
            concession.value().setValue(1d + RandomUtil.randomInt(5));
        }

        concession.condition().setValue(RandomUtil.random(Concession.Condition.values()));
        concession.status().setValue(RandomUtil.random(Concession.Status.values()));

        if (concession.status().getValue() == Concession.Status.approved) {
            concession.approvedBy().setValue("Geoge W. Bush Jr.");
        }

        concession.start().setValue(DataGenerator.randomDate(2));
        concession.end().setValue(DataGenerator.randomDate(4));

        return concession;
    }

// Units:
    public List<UnitRelatedData> createUnits(Building building, List<FloorplanDTO> floorplans, int numFloors, int numUnitsPerFloor) {
        List<UnitRelatedData> units = new ArrayList<UnitRelatedData>();
        // now create units for the building
        for (int floor = 1; floor < numFloors + 1; floor++) {
            // for each floor we want to create the same number of units
            for (int j = 1; j < numUnitsPerFloor + 1; j++) {

                String suiteNumber = "#" + (floor * 100 + j);
                float bedrooms = 2.0f;
                float bathrooms = 2.0f;

                Floorplan floorplan = floorplans.get(j % floorplans.size());
                if (floorplan == null) {
                    throw new IllegalStateException("No floorplan");
                }

                double uarea = CommonsGenerator.randomFromRange(CommonsGenerator.createRange(1200d, 2600d));
                UnitRelatedData unit = createUnit(building, suiteNumber, floor, uarea, bedrooms, bathrooms, floorplan);
                units.add(unit);
            }
        }
        return units;
    }

    private UnitRelatedData createUnit(Building building, String suiteNumber, int floor, double area, double bedrooms, double bathrooms, Floorplan floorplan) {
        UnitRelatedData unit = EntityFactory.create(UnitRelatedData.class);

        unit.belongsTo().set(building);

        unit.info().number().setValue(suiteNumber);
        unit.info().floor().setValue(floor);
        unit.info().type().setValue(RandomUtil.random(AptUnitType.values()));
        unit.info().area().setValue(area);
        unit.info().areaUnits().setValue(AreaMeasurementUnit.sqFeet);
        unit.info().bedrooms().setValue(bedrooms);
        unit.info().bathrooms().setValue(bathrooms);

        unit.financial().unitRent().setValue(800. + RandomUtil.randomInt(200));
        unit.financial().marketRent().setValue(900. + RandomUtil.randomInt(200));

        // mandatory utilities
        unit.info().utilities().add(createUtility(Utility.Type.water));
        unit.info().utilities().add(createUtility(Utility.Type.heat));
        unit.info().utilities().add(createUtility(Utility.Type.gas));
        unit.info().utilities().add(createUtility(Utility.Type.electric));

        // optional utilities
        if (RandomUtil.randomBoolean()) {
            unit.info().utilities().add(createUtility(Utility.Type.cable));
        }
        if (RandomUtil.randomBoolean()) {
            unit.info().utilities().add(createUtility(Utility.Type.internet));
        }

        // info items
        if (RandomUtil.randomBoolean()) {
            unit.details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        } else {
            unit.details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            unit.details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            unit.details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            unit.details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            unit.details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        }

        Calendar avalable = new GregorianCalendar();
        // TODO Dima, We need to Use fixed date for values used in tests, and
        // current time for Ctrl+Q user :)
        avalable.setTime(new Date());
        avalable.add(Calendar.DATE, 5 + RandomUtil.randomInt(30));
        DateUtils.dayStart(avalable);

        // TODO populate currentOccupancies and then set avalableForRent using
        // some ServerSideDomainUtils
        unit.avalableForRent().setValue(new LogicalDate(avalable.getTime().getTime()));
        AptUnitOccupancy occupancy = EntityFactory.create(AptUnitOccupancy.class);
        occupancy.status().setValue(AptUnitOccupancy.StatusType.available);
        occupancy.dateFrom().setValue(new LogicalDate(avalable.getTime().getTime()));
        unit.occupancies().add(occupancy);

        unit.marketing().floorplan().set(floorplan);

        return unit;
    }

    public static AptUnitItem createUnitDetailItem(AptUnitItem.Type type) {
        AptUnitItem item = EntityFactory.create(AptUnitItem.class);
        item.type().setValue(type);

        item.flooringType().setValue(RandomUtil.random(AptUnitItem.FlooringType.values()));
        item.flooringInstallDate().setValue(RandomUtil.randomLogicalDate());
        item.flooringValue().setValue(1800. + RandomUtil.randomInt(200));

        item.counterTopType().setValue(RandomUtil.random(AptUnitItem.CounterTopType.values()));
        item.counterTopInstallDate().setValue(RandomUtil.randomLogicalDate());
        item.counterTopValue().setValue(800. + RandomUtil.randomInt(200));

        item.cabinetsType().setValue(RandomUtil.random(AptUnitItem.CabinetsType.values()));
        item.cabinetsInstallDate().setValue(RandomUtil.randomLogicalDate());
        item.cabinetsValue().setValue(1000. + RandomUtil.randomInt(200));

        return item;
    }

    public static AptUnitAmenity createUnitAmenity(AptUnitAmenity.Type type) {
        AptUnitAmenity amenity = EntityFactory.create(AptUnitAmenity.class);
        amenity.type().setValue(type);
        return amenity;
    }

    public static Utility createUtility(Utility.Type type) {
        Utility utility = EntityFactory.create(Utility.class);
        utility.type().setValue(type);
        return utility;
    }

// Property
    public static PropertyProfile createPropertyProfile(int index) {
        PropertyProfile propertyProfile = EntityFactory.create(PropertyProfile.class);

        PetChargeRule petCharge;
        if (index == 0) {
            petCharge = createPetCharge(ChargeType.deposit, 100);
        } else if (index == 1) {
            petCharge = createPetCharge(ChargeType.oneTime, 50);
        } else {
            petCharge = createPetCharge(ChargeType.monthly, 200);
        }
        propertyProfile.petCharge().set(petCharge);

        return propertyProfile;
    }

    public static PetChargeRule createPetCharge(ChargeType mode, int value) {
        PetChargeRule petCharge = EntityFactory.create(PetChargeRule.class);
        petCharge.chargeType().setValue(mode);
        petCharge.value().setValue(value);
        return petCharge;
    }

    private Complex createComplex(int numBuildings) {
        if (numBuildings == 0)
            return null;

        Complex complex = EntityFactory.create(Complex.class);

        for (int i = 0; i < numBuildings; i++) {
            // Building building
        }

        return complex;
    }

    public BuildingAmenity createBuildingAmenity(Building building) {
        BuildingAmenity amenity = EntityFactory.create(BuildingAmenity.class);

        amenity.belongsTo().set(building);
        amenity.type().setValue(RandomUtil.random(BuildingAmenity.Type.values()));
        amenity.subType().setValue(RandomUtil.random(BuildingAmenity.SubType.values()));

        return amenity;
    }

    public LeaseTerms createLeaseTerms() {
        LeaseTerms leaseTerms = EntityFactory.create(LeaseTerms.class);
        try {
            leaseTerms.text().setValue(IOUtils.getTextResource("leaseTerms.html", BuildingsGenerator.class));
        } catch (IOException e) {
            throw new Error(e);
        }
        return leaseTerms;
    }
}
