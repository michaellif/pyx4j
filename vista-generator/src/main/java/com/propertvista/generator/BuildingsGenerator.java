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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.propertvista.generator.gdo.AptUnitGDO;
import com.propertvista.generator.util.CommonsGenerator;
import com.propertvista.generator.util.CompanyVendor;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.marketing.AdvertisingBlurb;
import com.propertyvista.domain.marketing.PublicVisibilityType;
import com.propertyvista.domain.property.PropertyManager;
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
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitInfo;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.portal.domain.ptapp.LeaseTerms;

public class BuildingsGenerator {

    public BuildingsGenerator(long seed) {
        DataGenerator.setRandomSeed(seed);
    }

    public List<Building> createBuildings(int numBuildings) {
        List<Building> buildings = new ArrayList<Building>();
        for (int b = 0; b < numBuildings; b++) {
            Building building = createBuilding(b);
            buildings.add(building);
        }
        return buildings;
    }

    public Building createBuilding(int counter) {
        // building type
//        BuildingInfo.Type buildingType = RandomUtil.random(BuildingInfo.Type.values());
// randomator put very little 'residential' types!?
        BuildingInfo.Type[] types = { BuildingInfo.Type.residential, BuildingInfo.Type.mixed_residential };
        BuildingInfo.Type buildingType = RandomUtil.random(types);

        String website = "www.property" + (counter + 1) + ".com";

        // address
        AddressStructured address = CommonsGenerator.createAddress();

        // email
        String email = "building" + (counter + 1) + "@propertyvista.com";

        // organization contacts - not many fields there at the moment, will do
        // this later
        String propertyCode = "B" + String.valueOf(counter);

        Building building = createBuilding(propertyCode, buildingType, website, address, email);
        // log.info("Created: " + building);

        return building;
    }

    private String getName(AddressStructured address) {
        return address.streetName().getStringView().toLowerCase().replaceAll("ave", "").replaceAll("st", "").replaceAll("\\s+", "")
                + address.streetNumber().getStringView();
    }

    private Building createBuilding(String propertyCode, BuildingInfo.Type buildingType, String website, AddressStructured address, String email) {
        Building building = EntityFactory.create(Building.class);
        building.propertyCode().setValue(propertyCode);

        building.info().name().setValue(getName(address));
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
        building.info().landArea().setValue(1000 + RandomUtil.randomInt(12000) + " sq Ft.");
        building.info().waterSupply().setValue(RandomUtil.random(BuildingInfo.WaterSupply.values()));
        building.info().centralAir().setValue(RandomUtil.randomBoolean());
        building.info().centralHeat().setValue(RandomUtil.randomBoolean());

        building.financial().dateAcquired().setValue(RandomUtil.randomLogicalDate(1950, 2011));
        building.financial().purchasePrice().setValue(BigDecimal.valueOf(100d + RandomUtil.randomDouble(2000000))); //imprecize BigDecimals for fake data
        building.financial().marketPrice().setValue(BigDecimal.valueOf(100d + RandomUtil.randomDouble(2000000))); //imprecize BigDecimals for fake data
        building.financial().lastAppraisalDate().setValue(RandomUtil.randomLogicalDate(2000, 2011));
        building.financial().lastAppraisalValue().setValue(BigDecimal.valueOf(100d + RandomUtil.randomDouble(2000000))); //imprecize BigDecimals for fake data
        building.financial().currency().name().setValue("CAD");

        building.marketing().visibility().setValue(PublicVisibilityType.global);
        building.marketing().name().setValue(building.info().name().getStringView() + " mkt" + RandomUtil.randomLetters(2));
        building.marketing().description().setValue(CommonsGenerator.lipsum());
        for (int i = 0; 1 < RandomUtil.randomInt(3); i++) {
            AdvertisingBlurb item = EntityFactory.create(AdvertisingBlurb.class);
            item.content().setValue(CommonsGenerator.lipsum());
            building.marketing().adBlurbs().add(item);
        }

        for (int i = 0; i <= RandomUtil.randomInt(3); i++) {
            building.contacts().phones().add(CommonsGenerator.createPropertyPhone());
        }

        building.contacts().website().setValue(website);
        building.contacts().email().setValue(email); // not sure yet what to do about
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
            item.description().setValue("Elevator description here...");
            item.notes().setValue(CommonsGenerator.lipsum());

            item.license().number().setValue(String.valueOf(RandomUtil.randomInt(8)));
            item.license().expiration().setValue(RandomUtil.randomLogicalDate());
            item.license().renewal().setValue(RandomUtil.randomLogicalDate());

            item.warranty().set(CompanyVendor.createWarranty());
            item.maintenance().set(CompanyVendor.createMaintenance());

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
            item.description().setValue("Boiler description here...");
            item.notes().setValue(CommonsGenerator.lipsum());

            item.license().number().setValue(String.valueOf(RandomUtil.randomInt(8)));
            item.license().expiration().setValue(RandomUtil.randomLogicalDate());
            item.license().renewal().setValue(RandomUtil.randomLogicalDate());

            item.warranty().set(CompanyVendor.createWarranty());
            item.maintenance().set(CompanyVendor.createMaintenance());

            items.add(item);
        }
        return items;
    }

    @I18n
    enum RoofType {
        GableRoof, CrossGabledRoof, MansardRoof, HipRoof, PyramidHipRoof, CrossHippedRoof, SaltboxRoof, GambrelRoof, FlatRoof, BonnetRoof;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
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

            item.warranty().set(CompanyVendor.createWarranty());
            item.maintenance().set(CompanyVendor.createMaintenance());

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

        amenity.name().setValue(amenity.type().getStringView() + " " + RandomUtil.randomLetters(2));
        amenity.description().setValue(CommonsGenerator.lipsumShort());

        return amenity;
    }

// Floorplans:
    public List<FloorplanDTO> createFloorplans(Building building, int num) {
        List<FloorplanDTO> floorplans = new ArrayList<FloorplanDTO>();
        Set<String> uniqueFloorplanNames = new HashSet<String>();

        for (int i = 0; i < num; i++) {
/*
 * String floorplanName = building.propertyCode().getStringView() + "-" + i;
 * if (i == 1) {
 * floorplanName = DemoData.REGISTRATION_DEFAULT_FLOORPLAN;
 * }
 */
            FloorplanDTO floorplan;
            //produces limited number of names, for large amounts of data could go into infinite loop
            int attemptCounter = 0;
            do {
                attemptCounter++;
                if (attemptCounter > 10) {
                    throw new Error("Infinite loop protection");
                }
                floorplan = createFloorplan();
            } while (uniqueFloorplanNames.contains(floorplan.name().getValue()));

            uniqueFloorplanNames.add(floorplan.name().getValue());
            floorplan.building().set(building);
            floorplans.add(floorplan);

        }
        return floorplans;
    }

    public FloorplanDTO createFloorplan() {
        FloorplanDTO floorplan = EntityFactory.create(FloorplanDTO.class);

        floorplan.description().setValue(CommonsGenerator.lipsum());

        floorplan.floorCount().setValue(1 + DataGenerator.randomInt(2));
        floorplan.bedrooms().setValue(DataGenerator.randomInt(7));
        floorplan.dens().setValue(DataGenerator.randomInt(2));
        floorplan.bathrooms().setValue(1 + DataGenerator.randomInt(3));
        floorplan.halfBath().setValue(DataGenerator.randomInt(2));
        floorplan.marketingName().setValue(createMarketingName(floorplan.bedrooms().getValue(), floorplan.dens().getValue()));
        floorplan.name().setValue(
                floorplan.marketingName().getValue() + ' ' + floorplan.bathrooms().getValue() + ' ' + ((char) (1 + DataGenerator.randomInt(5) + 'A')));

        for (int i = 0; i < 2 + DataGenerator.randomInt(6); i++) {
            FloorplanAmenity amenity = BuildingsGenerator.createFloorplanAmenity();
            amenity.belongsTo().set(floorplan);
            floorplan.amenities().add(amenity);
        }

        floorplan.counters()._unitCount().setValue(0);
        floorplan.counters()._marketingUnitCount().setValue(0);

        return floorplan;
    }

    public String createMarketingName(int bedrooms, int dens) {
        String marketingName;

        if (bedrooms == 0) {
            marketingName = "Bachelor";
        } else if (bedrooms > 4) {
            marketingName = "Luxury " + bedrooms + "-bedroom";
        } else {
            marketingName = bedrooms + "-bedroom";
        }
        if (dens > 0) {
            marketingName = marketingName + " + den";
        }

        return marketingName;
    }

    public static FloorplanAmenity createFloorplanAmenity() {
        FloorplanAmenity amenity = EntityFactory.create(FloorplanAmenity.class);

        amenity.type().setValue(RandomUtil.random(FloorplanAmenity.Type.values()));

        amenity.name().setValue(RandomUtil.randomLetters(6));
        amenity.description().setValue(CommonsGenerator.lipsumShort());

        return amenity;
    }

// Units:
    public List<AptUnitGDO> createUnits(Building building, List<FloorplanDTO> floorplans, int numFloors, int numUnitsPerFloor) {
        List<AptUnitGDO> units = new ArrayList<AptUnitGDO>();
        // now create units for the building
        for (int floor = 1; floor < numFloors + 1; floor++) {
            // for each floor we want to create the same number of units
            for (int j = 0; j < numUnitsPerFloor + 1; j++) {

                String suiteNumber = "#" + (floor * 100 + j);
                Floorplan floorplan = floorplans.get(j % floorplans.size());
                if (floorplan == null) {
                    throw new IllegalStateException("No floorplan");
                }
                double uarea = CommonsGenerator.randomFromRange(CommonsGenerator.createRange(1200d, 2600d));
                AptUnitGDO unit = createUnit(building, suiteNumber, floor, uarea, floorplan);
                units.add(unit);
            }
        }
        return units;
    }

    private AptUnitGDO createUnit(Building building, String suiteNumber, int floor, double area, Floorplan floorplan) {
        AptUnitGDO data = EntityFactory.create(AptUnitGDO.class);
        AptUnit unit = data.unit();
        unit.belongsTo().set(building);

        unit.info().economicStatus().setValue(RandomUtil.random(AptUnitInfo.EconomicStatus.values()));
        unit.info().economicStatusDescription().setValue(RandomUtil.randomLetters(35).toLowerCase());

        unit.info().floor().setValue(floor);
        unit.info().number().setValue(suiteNumber);

        unit.info()._bedrooms().setValue(floorplan.bedrooms().getValue());
        unit.info()._bathrooms().setValue(floorplan.bathrooms().getValue());

        unit.info().area().setValue(area);
        unit.info().areaUnits().setValue(AreaMeasurementUnit.sqFeet);

        // The values are not set here! See ProductCatalogGenerator
        //unit.financial()._unitRent().setValue(800. + RandomUtil.randomInt(200));
        //unit.financial()._marketRent().setValue(900. + RandomUtil.randomInt(200));

        // info items
        if (RandomUtil.randomBoolean()) {
            data.details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        } else {
            data.details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            data.details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            data.details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            data.details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            data.details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        }
        for (AptUnitItem detail : data.details()) {
            detail.belongsTo().set(unit);
        }

        // TODO populate currentOccupancies and then set avalableForRent using
        // some ServerSideDomainUtils
        unit.availableForRent().setValue(RandomUtil.randomLogicalDate(2012, 2012));

        AptUnitOccupancySegment occupancy = EntityFactory.create(AptUnitOccupancySegment.class);
        occupancy.occupancy().unit().set(unit);
        occupancy.status().setValue(AptUnitOccupancySegment.Status.available);
        occupancy.dateFrom().setValue(unit.availableForRent().getValue());
        occupancy.dateTo().setValue(new LogicalDate(occupancy.dateFrom().getValue().getTime() + RandomUtil.randomInt()));
        occupancy.description().setValue(RandomUtil.randomLetters(25).toLowerCase());
        data.occupancies().add(occupancy);

        unit.floorplan().set(floorplan);

        unit.marketing().name().set(floorplan.marketingName()); // copy floorplan marketing here?!..
        for (int i = 0; 1 < RandomUtil.randomInt(3); ++i) {
            AdvertisingBlurb item = EntityFactory.create(AdvertisingBlurb.class);
            item.content().setValue(CommonsGenerator.lipsum());
            unit.marketing().adBlurbs().add(item);
        }

        return data;
    }

    public static AptUnitItem createUnitDetailItem(AptUnitItem.Type type) {
        AptUnitItem item = EntityFactory.create(AptUnitItem.class);

        item.type().setValue(type);
        item.description().setValue("UnitItem description here...");
        item.conditionNotes().setValue(CommonsGenerator.lipsum());

        item.flooringType().setValue(RandomUtil.random(AptUnitItem.FlooringType.values()));
        item.flooringInstallDate().setValue(RandomUtil.randomLogicalDate());
        item.flooringValue().setValue(BigDecimal.valueOf(1800 + RandomUtil.randomInt(200)));

        item.counterTopType().setValue(RandomUtil.random(AptUnitItem.CounterTopType.values()));
        item.counterTopInstallDate().setValue(RandomUtil.randomLogicalDate());
        item.counterTopValue().setValue(BigDecimal.valueOf(800 + RandomUtil.randomInt(200)));

        item.cabinetsType().setValue(RandomUtil.random(AptUnitItem.CabinetsType.values()));
        item.cabinetsInstallDate().setValue(RandomUtil.randomLogicalDate());
        item.cabinetsValue().setValue(BigDecimal.valueOf(1000 + RandomUtil.randomInt(200)));

        return item;
    }

// Property
    public Complex createComplex(String name) {
        Complex complex = EntityFactory.create(Complex.class);
        complex.name().setValue(name);
        complex.website().setValue("www." + name.replace("#", "").replace(" ", "") + ".com");

        return complex;
    }

    public PropertyManager createPropertyManager(String name) {
        PropertyManager pmc = EntityFactory.create(PropertyManager.class);
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
