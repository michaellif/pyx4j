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
package com.propertvista.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertvista.generator.util.CommonsGenerator;
import com.propertvista.generator.util.CompanyVendor;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.PetChargeRule;
import com.propertyvista.domain.PetType;
import com.propertyvista.domain.charges.ChargeType;
import com.propertyvista.domain.contact.Address;
import com.propertyvista.domain.contact.Email;
import com.propertyvista.domain.contact.Phone;
import com.propertyvista.domain.financial.offering.PetCharge;
import com.propertyvista.domain.financial.offering.PetPrice;
import com.propertyvista.domain.financial.offeringnew.Feature;
import com.propertyvista.domain.marketing.AdvertisingBlurb;
import com.propertyvista.domain.property.StarlightPmc;
import com.propertyvista.domain.property.asset.AreaMeasurementUnit;
import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Elevator;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.Locker;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.ParkingSpot;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.domain.property.asset.unit.AptUnitInfo;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.domain.property.asset.unit.AptUnitType;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.portal.domain.ptapp.LeaseTerms;
import com.propertyvista.portal.domain.ptapp.PropertyProfile;
import com.propertyvista.server.common.generator.UnitRelatedData;

public class BuildingsGenerator {

    private final static Logger log = LoggerFactory.getLogger(BuildingsGenerator.class);

    private final long seed;

    public BuildingsGenerator() {
        this(DemoData.BUILDINGS_GENERATION_SEED);
    }

    public BuildingsGenerator(long seed) {
        DataGenerator.setRandomSeed(seed);
        this.seed = seed;
    }

    public List<Building> createBuildings(int numBuildings, Complex complex) {
        List<Building> buildings = new ArrayList<Building>();
        for (int b = 0; b < numBuildings; b++) {
            Building building = createBuilding(complex, b);
            buildings.add(building);
        }
        return buildings;
    }

    public Building createBuilding(Complex complex, int counter) {
        // building type
        BuildingInfo.Type buildingType = RandomUtil.random(BuildingInfo.Type.values());

        String website = "www.property" + (counter + 1) + ".com";

        // address
        Address address = CommonsGenerator.createAddress();

        // phones
        List<Phone> phones = new ArrayList<Phone>();
        phones.add(CommonsGenerator.createPhone());

        // email
        String emailAddress = "building" + (counter + 1) + "@propertyvista.com";
        Email email = CommonsGenerator.createEmail(emailAddress);

        // organization contacts - not many fields there at the moment, will do
        // this later
        String propertyCode = "A" + String.valueOf(counter);
        if (counter == 0) {
            // UI is looking for this building, see references!
            propertyCode = DemoData.REGISTRATION_DEFAULT_PROPERTY_CODE;
        }

        // property profile TODO - nobody is using this property profile right
        // now
        PropertyProfile propertyProfile = createPropertyProfile(counter);

        Building building = createBuilding(propertyCode, buildingType, complex, website, address, phones, email);
        // log.info("Created: " + building);

        return building;
    }

    private Building createBuilding(String propertyCode, BuildingInfo.Type buildingType, Complex complex, String website, Address address, List<Phone> phones,
            Email email) {
        Building building = EntityFactory.create(Building.class);

        building.complex().set(complex);
        building.propertyCode().setValue(propertyCode);

        building.info().name().setValue(RandomUtil.randomLetters(5));
        building.info().address().set(address);
        building.info().type().setValue(buildingType);
        building.info().shape().setValue(RandomUtil.random(BuildingInfo.Shape.values()));
        building.info().totalStoreys().setValue("" + (1 + RandomUtil.randomInt(20)));
        building.info().residentialStoreys().setValue("" + RandomUtil.randomInt(20));
        building.info().structureType().setValue(RandomUtil.random(BuildingInfo.StructureType.values()));
        building.info().structureBuildYear().setValue(RandomUtil.randomYear(1700, 2011));
        building.info().constructionType().setValue(RandomUtil.random(BuildingInfo.ConstructionType.values()));
        building.info().foundationType().setValue(RandomUtil.random(BuildingInfo.FoundationType.values()));
        building.info().floorType().setValue(RandomUtil.random(BuildingInfo.FloorType.values()));
        building.info().landArea().setValue(1000 + RandomUtil.randomInt(12000) + "sq Ft.");
        building.info().waterSupply().setValue(RandomUtil.random(BuildingInfo.WaterSupply.values()));
        building.info().centralAir().setValue(RandomUtil.randomBoolean());
        building.info().centralHeat().setValue(RandomUtil.randomBoolean());

        building.financial().dateAquired().setValue(RandomUtil.randomLogicalDate(1950, 2011));
        building.financial().purchasePrice().setValue(100d + RandomUtil.randomDouble(2000000));
        building.financial().marketPrice().setValue(100d + RandomUtil.randomDouble(2000000));
        building.financial().lastAppraisalDate().setValue(RandomUtil.randomLogicalDate(2000, 2011));
        building.financial().lastAppraisalValue().setValue(100d + RandomUtil.randomDouble(2000000));
        building.financial().currency().name().setValue("CAD");

        building.marketing().name().setValue(RandomUtil.randomLetters(4) + " " + RandomUtil.randomLetters(6));
        building.marketing().description().setValue(CommonsGenerator.lipsum());
        for (int i = 0; 1 < RandomUtil.randomInt(3); ++i) {
            AdvertisingBlurb item = EntityFactory.create(AdvertisingBlurb.class);
            item.content().setValue(CommonsGenerator.lipsum());
            building.marketing().adBlurbs().add(item);
        }

        for (Phone phone : phones) {
            building.contacts().phones().add(phone);
        }
        building.contacts().website().setValue(website);
        building.contacts().email().set(email); // not sure yet what to do about
                                                // the email and its type

        return building;
    }

// Mechanicals:    
    public List<Elevator> createElevators(Building owner, int num) {
        List<Elevator> items = new ArrayList<Elevator>();
        for (int i = 0; i < num; i++) {
            Elevator item = EntityFactory.create(Elevator.class);
            item.belongsTo().set(owner);

            item.type().setValue("Elevator");
            item.make().setValue("Bosh");
            item.model().setValue("Elevator" + RandomUtil.randomInt(100));
            item.build().setValue(RandomUtil.randomLogicalDate());
            item.description().setValue(RandomUtil.randomLetters(35).toLowerCase());
            item.notes().setValue(CommonsGenerator.lipsum());

            item.licence().number().setValue(String.valueOf(RandomUtil.randomInt(8)));
            item.licence().expiration().setValue(RandomUtil.randomLogicalDate());
            item.licence().renewal().setValue(RandomUtil.randomLogicalDate());

            item.warranty().set(CompanyVendor.createnWarranty());
            item.maintenance().set(CompanyVendor.createnMaintenance());

            item.isForMoveInOut().setValue(RandomUtil.randomBoolean());

            items.add(item);
        }
        return items;
    }

    public List<Boiler> createBoilers(Building owner, int num) {
        List<Boiler> items = new ArrayList<Boiler>();
        for (int i = 0; i < num; i++) {
            Boiler item = EntityFactory.create(Boiler.class);
            item.belongsTo().set(owner);

            item.type().setValue("Boiler");
            item.make().setValue("Electra");
            item.model().setValue("Boiler" + RandomUtil.randomInt(100));
            item.build().setValue(RandomUtil.randomLogicalDate());
            item.description().setValue(RandomUtil.randomLetters(35).toLowerCase());
            item.notes().setValue(CommonsGenerator.lipsum());

            item.licence().number().setValue(String.valueOf(RandomUtil.randomInt(8)));
            item.licence().expiration().setValue(RandomUtil.randomLogicalDate());
            item.licence().renewal().setValue(RandomUtil.randomLogicalDate());

            item.warranty().set(CompanyVendor.createnWarranty());
            item.maintenance().set(CompanyVendor.createnMaintenance());

            items.add(item);
        }
        return items;
    }

    @Translatable
    enum RoofType {
        GableRoof, CrossGabledRoof, MansardRoof, HipRoof, PyramidHipRoof, CrossHippedRoof, SaltboxRoof, GambrelRoof, FlatRoof, BonnetRoof;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    public List<Roof> createRoofs(Building owner, int num) {
        List<Roof> items = new ArrayList<Roof>();
        for (int i = 0; i < num; i++) {
            Roof item = EntityFactory.create(Roof.class);
            item.belongsTo().set(owner);

            item.type().setValue(RandomUtil.randomEnum(RoofType.class).toString());
            item.year().setValue(RandomUtil.randomLogicalDate());
            item.notes().setValue(CommonsGenerator.lipsum());

            item.warranty().set(CompanyVendor.createnWarranty());
            item.maintenance().set(CompanyVendor.createnMaintenance());

            items.add(item);
        }
        return items;
    }

// Lockers:
    public List<LockerArea> createLockerAreas(Building owner, int num) {
        List<LockerArea> items = new ArrayList<LockerArea>();
        for (int i = 0; i < num; i++) {
            items.add(createLockerArea(owner, (i + 1)));
        }
        return items;
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
        List<Locker> items = new ArrayList<Locker>();
        for (int i = 0; i < num; i++) {
            items.add(createLocker(owner, (i + 1)));
        }
        return items;
    }

    private Locker createLocker(LockerArea owner, int index) {
        Locker locker = EntityFactory.create(Locker.class);
        locker.belongsTo().set(owner);

        locker.name().setValue("Locker" + index);
        locker.type().setValue(RandomUtil.random(Locker.Type.values()));
        return locker;
    }

    // Parking:
    public List<Parking> createParkings(Building owner, int num) {
        List<Parking> items = new ArrayList<Parking>();
        for (int i = 0; i < num; i++) {
            items.add(createParking(owner, (i + 1)));
        }
        return items;
    }

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

    // Amenities
    public List<BuildingAmenity> createBuildingAmenities(Building owner, int num) {
        List<BuildingAmenity> items = new ArrayList<BuildingAmenity>();
        for (int i = 0; i < num; i++) {
            items.add(createBuildingAmenity(owner));
        }
        return items;
    }

    public BuildingAmenity createBuildingAmenity(Building building) {
        BuildingAmenity amenity = EntityFactory.create(BuildingAmenity.class);
        amenity.belongsTo().set(building);

        amenity.type().setValue(RandomUtil.randomEnum(BuildingAmenity.Type.class));

        amenity.name().setValue(RandomUtil.randomLetters(6));
        amenity.description().setValue(RandomUtil.randomLetters(20).toLowerCase());

        return amenity;
    }

// Floorplans:
    public List<FloorplanDTO> createFloorplans(Building building, int num) {
        List<FloorplanDTO> floorplans = new ArrayList<FloorplanDTO>();
        for (int i = 0; i < num; i++) {
            String floorplanName = building.propertyCode().getStringView() + "-" + i;
            if (i == 1) {
                floorplanName = DemoData.REGISTRATION_DEFAULT_FLOORPLAN;
            }

            FloorplanDTO floorplan = createFloorplan(floorplanName);
            floorplan.building().set(building);
            floorplans.add(floorplan);
        }
        return floorplans;
    }

    public FloorplanDTO createFloorplan(String name) {
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

        return floorplan;
    }

    public static FloorplanAmenity createFloorplanAmenity() {
        FloorplanAmenity amenity = EntityFactory.create(FloorplanAmenity.class);

        amenity.type().setValue(RandomUtil.random(FloorplanAmenity.Type.values()));

        amenity.name().setValue(RandomUtil.randomLetters(6));
        amenity.description().setValue(RandomUtil.randomLetters(35).toLowerCase());

        return amenity;
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

        unit.info().name().setValue(RandomUtil.randomLetters(4));
        unit.info().type().setValue(RandomUtil.random(AptUnitType.values()));
        unit.info().economicStatus().setValue(RandomUtil.random(AptUnitInfo.EconomicStatus.values()));
        unit.info().economicStatusDescription().setValue(RandomUtil.randomLetters(35).toLowerCase());

        unit.info().floor().setValue(floor);
        unit.info().number().setValue(suiteNumber);

        unit.info().bedrooms().setValue(bedrooms);
        unit.info().bathrooms().setValue(bathrooms);

        unit.info().area().setValue(area);
        unit.info().areaUnits().setValue(AreaMeasurementUnit.sqFeet);

        unit.financial().unitRent().setValue(800. + RandomUtil.randomInt(200));
        unit.financial().marketRent().setValue(900. + RandomUtil.randomInt(200));

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
        occupancy.status().setValue(AptUnitOccupancy.Status.available);
        occupancy.dateFrom().setValue(new LogicalDate(avalable.getTime().getTime()));
        occupancy.dateTo().setValue(new LogicalDate(avalable.getTime().getTime() + RandomUtil.randomInt()));
        occupancy.description().setValue(RandomUtil.randomLetters(25).toLowerCase());
        unit.occupancies().add(occupancy);

        unit.floorplan().set(floorplan);

        unit.marketing().set(floorplan); // copy floorplan marketing here?!..
        for (int i = 0; 1 < RandomUtil.randomInt(3); ++i) {
            AdvertisingBlurb item = EntityFactory.create(AdvertisingBlurb.class);
            item.content().setValue(CommonsGenerator.lipsum());
            unit.marketing().adBlurbs().add(item);
        }

        return unit;
    }

    public static AptUnitItem createUnitDetailItem(AptUnitItem.Type type) {
        AptUnitItem item = EntityFactory.create(AptUnitItem.class);

        item.type().setValue(type);
        item.description().setValue(RandomUtil.randomLetters(35).toLowerCase());
        item.conditionNotes().setValue(CommonsGenerator.lipsum());

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

    public Complex createComplex(String name) {
        Complex complex = EntityFactory.create(Complex.class);
        complex.name().setValue(name);

        return complex;
    }

    public StarlightPmc createPmc(String name) {
        StarlightPmc pmc = EntityFactory.create(StarlightPmc.class);
        pmc.name().setValue(name);

        return pmc;
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
