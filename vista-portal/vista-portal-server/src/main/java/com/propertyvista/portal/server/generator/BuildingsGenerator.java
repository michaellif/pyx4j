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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.common.domain.financial.ChargeType;
import com.propertyvista.domain.Address;
import com.propertyvista.domain.Email;
import com.propertyvista.domain.Phone;
import com.propertyvista.domain.Picture;
import com.propertyvista.domain.marketing.yield.AddOn;
import com.propertyvista.domain.marketing.yield.Concession;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnitInfo;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.portal.domain.ptapp.LeaseTerms;
import com.propertyvista.portal.domain.ptapp.PetChargeRule;
import com.propertyvista.portal.domain.ptapp.PropertyProfile;
import com.propertyvista.portal.server.preloader.RandomUtil;

public class BuildingsGenerator {

    private final static Logger log = LoggerFactory.getLogger(BuildingsGenerator.class);

    private final long seed;

    public BuildingsGenerator(long seed) {
        DataGenerator.setRandomSeed(seed);
        this.seed = seed;
    }

    private static String resourceFileName(String fileName) {
        return BuildingsGenerator.class.getPackage().getName().replace('.', '/') + "/" + fileName;
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

        // organization contacts - not many fields there at the moment, will do this later
        String propertyCode = "A" + String.valueOf(b);
        if (b == 0) {
            //UI is looking for this building, see references!
            propertyCode = DemoData.REGISTRATION_DEFAULT_PROPERTY_CODE;
        }

        // property profile TODO - nobody is using this property profile right now
        PropertyProfile propertyProfile = createPropertyProfile(b);

        Building building = createBuilding(propertyCode, buildingType, complex, website, address, phones, email);
        //			log.info("Created: " + building);

        return building;
    }

    public List<Floorplan> createFloorplans(Building building, int numFloorplans) {
        List<Floorplan> floorplans = new ArrayList<Floorplan>();
        // create floorplans
        for (int i = 0; i < numFloorplans; i++) {
            String floorplanName = building.info().propertyCode() + "-" + i;
            if (i == 1) {
                floorplanName = DemoData.REGISTRATION_DEFAULT_FLOORPLAN;
            }

            Floorplan floorplan = createFloorplan(floorplanName);
            floorplan.building().set(building);
            floorplans.add(floorplan);
        }
        return floorplans;
    }

    public List<AptUnit> createUnits(Building building, List<Floorplan> floorplans, int numFloors, int numUnitsPerFloor) {
        List<AptUnit> units = new ArrayList<AptUnit>();
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

                int uarea = floorplan.minArea().getValue() + RandomUtil.randomInt(10);
                AptUnit unit = createUnit(building, suiteNumber, floor, uarea, bedrooms, bathrooms, floorplan);
                units.add(unit);
            }
        }
        return units;
    }

    private Building createBuilding(String propertyCode, BuildingInfo.Type buildingType, Complex complex, String website, Address address, List<Phone> phones,
            Email email) {
        Building building = EntityFactory.create(Building.class);

        building.info().propertyCode().setValue(propertyCode);

        building.info().type().setValue(buildingType);
        //		building.complex().
        building.contacts().website().setValue(website);

        building.info().address().set(address);

        for (Phone phone : phones) {
            building.contacts().phoneList().add(phone);
        }

        building.info().name().setValue(RandomUtil.randomLetters(3));
        building.marketing().name().setValue(RandomUtil.randomLetters(4) + " " + RandomUtil.randomLetters(6));

        building.contacts().email().set(email); // not sure yet what to do about the email and its type

        return building;
    }

    private Floorplan createFloorplan(String name) {
        Floorplan floorplan = EntityFactory.create(Floorplan.class);

        floorplan.minArea().setValue(1200);
        floorplan.name().setValue(name);

        // for now save just one picture
        int imageIndex = RandomUtil.randomInt(3) + 1;
        String filename = resourceFileName("apartment" + imageIndex + ".jpg");
        try {
            byte[] picture = IOUtils.getResource(filename);
            if (picture == null) {
                log.warn("Could not find picture [{}] in classpath", filename);
            } else {
                //            log.info("Picture size is: " + picture.length);
                Picture blob = EntityFactory.create(Picture.class);
                blob.content().setValue(picture);
                floorplan.pictures().add(blob);
            }
        } catch (Exception e) {
            log.error("Failed to read the file [{}]", filename, e);
            throw new Error("Failed to read the file [" + filename + "]");
        }

        return floorplan;
    }

    public static AptUnitItem createUnitDetailItem(AptUnitItem.Type type) {
        AptUnitItem item = EntityFactory.create(AptUnitItem.class);
        item.type().setValue(type);

        item.flooringType().setValue(RandomUtil.random(AptUnitItem.FlooringType.values()));
        item.flooringInstallDate().setValue(RandomUtil.randomSqlDate());
        item.flooringValue().setValue(1800. + RandomUtil.randomInt(200));

        item.counterTopType().setValue(RandomUtil.random(AptUnitItem.CounterTopType.values()));
        item.counterTopInstallDate().setValue(RandomUtil.randomSqlDate());
        item.counterTopValue().setValue(800. + RandomUtil.randomInt(200));

        item.cabinetsType().setValue(RandomUtil.random(AptUnitItem.CabinetsType.values()));
        item.cabinetsInstallDate().setValue(RandomUtil.randomSqlDate());
        item.cabinetsValue().setValue(1000. + RandomUtil.randomInt(200));

        return item;
    }

    public static Concession createConcession(Concession.AppliedTo appliedTo, double months, double percentage) {
        Concession concession = EntityFactory.create(Concession.class);

        StringBuilder sb = new StringBuilder();

        if (appliedTo == Concession.AppliedTo.monthly) {
            sb.append(months).append(" free month");
            if (months != 1) {
                sb.append("s");
            }
        } else if (appliedTo == Concession.AppliedTo.amount) {
            sb.append(percentage).append("% discount for ");
            sb.append(months).append(" month");
            if (months != 1) {
                sb.append("s");
            }
        }

        concession.type().setValue(sb.toString());
        concession.termType().setValue("month");
        concession.numberOfTerms().setValue(months);
        concession.percentage().setValue(percentage);

        return concession;
    }

    public static Utility createUtility(Utility.Type type) {
        Utility utility = EntityFactory.create(Utility.class);
        utility.type().setValue(type);
        return utility;
    }

    public static AptUnitAmenity createUnitAmenity(AptUnitAmenity.Type type) {
        AptUnitAmenity amenity = EntityFactory.create(AptUnitAmenity.class);
        amenity.type().setValue(type);
        return amenity;
    }

    public static AddOn createAddOn(String name, double monthlyCost) {
        AddOn addOn = EntityFactory.create(AddOn.class);
        addOn.type().setValue(name);
        addOn.value().setValue(monthlyCost);
        return addOn;
    }

    private AptUnit createUnit(Building building, String suiteNumber, int floor, double area, double bedrooms, double bathrooms, Floorplan floorplan) {
        AptUnit unit = EntityFactory.create(AptUnit.class);

        unit.info().number().setValue(suiteNumber);
        unit.info().building().set(building);
        unit.info().floor().setValue(floor);
        unit.info().type().setValue(RandomUtil.random(AptUnitInfo.Type.values()));
        unit.info().area().setValue(area);
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

        // amenity, all optional
        if (RandomUtil.randomBoolean()) {
            unit.amenities().add(createUnitAmenity(RandomUtil.random(AptUnitAmenity.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            unit.amenities().add(createUnitAmenity(RandomUtil.random(AptUnitAmenity.Type.values())));
        }

        // info items
        if (RandomUtil.randomBoolean()) {
            unit.info().details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        } else {
            unit.info().details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            unit.info().details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            unit.info().details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            unit.info().details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            unit.info().details().add(createUnitDetailItem(RandomUtil.random(AptUnitItem.Type.values())));
        }

        // concessions
        if (RandomUtil.randomBoolean()) {
            unit.concessions().add(createConcession(RandomUtil.random(Concession.AppliedTo.values()), 1.0 + RandomUtil.randomInt(3), 0));
        }
        if (RandomUtil.randomBoolean()) {
            unit.concessions().add(createConcession(RandomUtil.random(Concession.AppliedTo.values()), 1.0 + RandomUtil.randomInt(11), 15.8));
        }

        // add-ons
        if (RandomUtil.randomBoolean()) {
            unit.addOns().add(createAddOn("2nd Parking", 50));
            if (RandomUtil.randomBoolean()) {
                unit.addOns().add(createAddOn("3rd Parking", 50));
            }
        }
        if (RandomUtil.randomBoolean()) {
            unit.addOns().add(createAddOn("2nd Locker", 30));
            if (RandomUtil.randomBoolean()) {
                unit.addOns().add(createAddOn("3rd Locker", 20));
            }
        }
        if (RandomUtil.randomBoolean()) {
            unit.addOns().add(createAddOn("Conditioner", 100));
        }
        if (RandomUtil.randomBoolean()) {
            unit.addOns().add(createAddOn("Espresso Machine", 30));
        }
        if (RandomUtil.randomBoolean()) {
            unit.addOns().add(createAddOn("Dishwasher", 30));
        }

        Calendar avalable = new GregorianCalendar();
        // TODO Dima, We need to Use fixed date for values used in tests, and current time for Ctrl+Q user :)
        avalable.setTime(new Date());
        avalable.add(Calendar.DATE, 5 + RandomUtil.randomInt(30));
        DateUtils.dayStart(avalable);

        //TODO populate currentOccupancies and then set avalableForRent using some ServerSideDomainUtils
        unit.avalableForRent().setValue(new java.sql.Date(avalable.getTime().getTime()));

        unit.marketing().floorplan().set(floorplan);

        return unit;
    }

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
            //			Building building 
        }

        return complex;
    }

    public LeaseTerms createLeaseTerms() {
        LeaseTerms leaseTerms = EntityFactory.create(LeaseTerms.class);
        try {
            leaseTerms.text().setValue(IOUtils.getTextResource(resourceFileName("leaseTerms.html")));
        } catch (IOException e) {
            throw new Error(e);
        }
        return leaseTerms;
    }
}
