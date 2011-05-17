/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author dmitry
 */
package com.propertyvista.portal.server.preloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.common.domain.financial.ChargeType;
import com.propertyvista.domain.Address;
import com.propertyvista.domain.Email;
import com.propertyvista.domain.Email.EmailType;
import com.propertyvista.domain.Phone;
import com.propertyvista.domain.Phone.PhoneType;
import com.propertyvista.domain.Picture;
import com.propertyvista.domain.marketing.yield.AddOn;
import com.propertyvista.domain.marketing.yield.Amenity;
import com.propertyvista.domain.marketing.yield.Concession;
import com.propertyvista.domain.property.asset.AptUnit;
import com.propertyvista.domain.property.asset.AptUnitAmenity;
import com.propertyvista.domain.property.asset.AptUnitDetail;
import com.propertyvista.domain.property.asset.Building;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.portal.domain.ptapp.LeaseTerms;
import com.propertyvista.portal.domain.ptapp.PetChargeRule;
import com.propertyvista.portal.domain.ptapp.PropertyProfile;

public class PreloadBuildings extends BaseVistaDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PreloadBuildings.class);

    private int buildingCount;

    private int unitCount;

    private static String resourceFileName(String fileName) {
        return PreloadBuildings.class.getPackage().getName().replace('.', '/') + "/" + fileName;
    }

    private Email createEmail(String emailAddress) {
        Email email = EntityFactory.create(Email.class);

        email.emailType().setValue(EmailType.work);
        email.emailAddress().setValue(emailAddress);

        return email;
    }

    private Phone createPhone() {
        Phone phone = EntityFactory.create(Phone.class);

        phone.phoneType().setValue(PhoneType.work);
        String code = RandomUtil.randomBoolean() ? "416" : "905";
        int digits = RandomUtil.randomInt(10) * 1000 + RandomUtil.randomInt(10) * 100 + RandomUtil.randomInt(10) * 10 + RandomUtil.randomInt(10);
        phone.phoneNumber().setValue("(" + code + ") 555-" + digits);

        return phone;
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

    public static Utility createUtility(Utility.Type type) {
        Utility utility = EntityFactory.create(Utility.class);
        utility.type().setValue(type);
        persist(utility);
        return utility;
    }

    public static AptUnitAmenity createUnitAmenity(AptUnitAmenity.Type type) {
        AptUnitAmenity amenity = EntityFactory.create(AptUnitAmenity.class);
        amenity.type().setValue(type);
        persist(amenity);
        return amenity;
    }

    public static AptUnitDetail createUnitDetailItem(AptUnitDetail.Type type) {
        AptUnitDetail item = EntityFactory.create(AptUnitDetail.class);
        item.type().setValue(type);

        item.flooringType().setValue(RandomUtil.random(AptUnitDetail.FlooringType.values()));
        item.flooringInstallDate().setValue(RandomUtil.randomSqlDate());
        item.flooringValue().setValue(1800. + RandomUtil.randomInt(200));

        item.counterTopType().setValue(RandomUtil.random(AptUnitDetail.CounterTopType.values()));
        item.counterTopInstallDate().setValue(RandomUtil.randomSqlDate());
        item.counterTopValue().setValue(800. + RandomUtil.randomInt(200));

        item.cabinetsType().setValue(RandomUtil.random(AptUnitDetail.CabinetsType.values()));
        item.cabinetsInstallDate().setValue(RandomUtil.randomSqlDate());
        item.cabinetsValue().setValue(1000. + RandomUtil.randomInt(200));

        persist(item);
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

        persist(concession);
        return concession;
    }

    public static AddOn createAddOn(String name, double monthlyCost) {
        AddOn addOn = EntityFactory.create(AddOn.class);
        addOn.type().setValue(name);
        addOn.value().setValue(monthlyCost);
        persist(addOn);
        return addOn;
    }

    public static PetChargeRule createPetCharge(ChargeType mode, int value) {
        PetChargeRule petCharge = EntityFactory.create(PetChargeRule.class);
        petCharge.chargeType().setValue(mode);
        petCharge.value().setValue(value);
        persist(petCharge);
        return petCharge;
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

        persist(propertyProfile);
        return propertyProfile;
    }

    private Building createBuilding(String propertyCode, Building.Type buildingType, Complex complex, String website, Address address, List<Phone> phones,
            Email email, PropertyProfile propertyProfile) {
        Building building = EntityFactory.create(Building.class);

        building.propertyCode().setValue(propertyCode);

        building.type().setValue(buildingType);
        //		building.complex().
        building.website().setValue(website);

        building.address().set(address);

        for (Phone phone : phones) {
            building.phoneList().add(phone);
        }

        building.name().setValue(RandomUtil.randomLetters(3));
        building.marketingName().setValue(RandomUtil.randomLetters(4) + " " + RandomUtil.randomLetters(6));

        building.email().set(email); // not sure yet what to do about the email and its type

        PersistenceServicesFactory.getPersistenceService().persist(building);

        buildingCount++;
        return building;
    }

    private AptUnit createUnit(Building building, String suiteNumber, int floor, double area, double bedrooms, double bathrooms, Floorplan floorplan) {
        AptUnit unit = EntityFactory.create(AptUnit.class);

        unit.number().setValue(suiteNumber);
        unit.building().set(building);
        unit.floor().setValue(floor);
        unit.type().setValue(RandomUtil.random(AptUnit.Type.values()));
        unit.area().setValue(area);
        unit.bedrooms().setValue(bedrooms);
        unit.bathrooms().setValue(bathrooms);

        unit.unitRent().setValue(800. + RandomUtil.randomInt(200));
        unit.netRent().setValue(1200. + RandomUtil.randomInt(200));
        unit.marketRent().setValue(900. + RandomUtil.randomInt(200));

        // mandatory utilities
        unit.utilities().add(createUtility(Utility.Type.water));
        unit.utilities().add(createUtility(Utility.Type.heat));
        unit.utilities().add(createUtility(Utility.Type.gas));
        unit.utilities().add(createUtility(Utility.Type.electric));

        // optional utilities
        if (RandomUtil.randomBoolean()) {
            unit.utilities().add(createUtility(Utility.Type.cable));
        }
        if (RandomUtil.randomBoolean()) {
            unit.utilities().add(createUtility(Utility.Type.internet));
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
            unit.details().add(createUnitDetailItem(RandomUtil.random(AptUnitDetail.Type.values())));
        } else {
            unit.details().add(createUnitDetailItem(RandomUtil.random(AptUnitDetail.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            unit.details().add(createUnitDetailItem(RandomUtil.random(AptUnitDetail.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            unit.details().add(createUnitDetailItem(RandomUtil.random(AptUnitDetail.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            unit.details().add(createUnitDetailItem(RandomUtil.random(AptUnitDetail.Type.values())));
        }
        if (RandomUtil.randomBoolean()) {
            unit.details().add(createUnitDetailItem(RandomUtil.random(AptUnitDetail.Type.values())));
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

        unit.floorplan().set(floorplan);

        PersistenceServicesFactory.getPersistenceService().persist(unit);

        unitCount++;
        return unit;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(Building.class, AptUnit.class, Floorplan.class, Email.class, Phone.class, Complex.class, Utility.class, AptUnitDetail.class,
                    Amenity.class, Concession.class, AddOn.class, LeaseTerms.class);
        } else {
            return "This is production";
        }
    }

    @Override
    public String create() {

        LeaseTerms leaseTerms = EntityFactory.create(LeaseTerms.class);
        try {
            leaseTerms.text().setValue(IOUtils.getTextResource(resourceFileName("leaseTerms.html")));
        } catch (IOException e) {
            throw new Error(e);
        }
        PersistenceServicesFactory.getPersistenceService().persist(leaseTerms);

        for (int b = 0; b < DemoData.NUM_RESIDENTIAL_BUILDINGS; b++) {

            // building type
            Building.Type buildingType = RandomUtil.random(Building.Type.values());

            Complex complex = null;
            if (b % 3 == 0) {
                complex = createComplex(2);
            }

            String website = "www.property" + (b + 1) + ".com";

            // address
            Address address = createAddress();

            // phones
            List<Phone> phones = new ArrayList<Phone>();
            phones.add(createPhone());

            // email
            String emailAddress = "building" + (b + 1) + "@propertyvista.com";
            Email email = createEmail(emailAddress);

            // organization contacts - not many fields there at the moment, will do this later
            String propertyCode = "A" + String.valueOf(b);
            if (b == 0) {
                //UI is looking for this building, see references!
                propertyCode = DemoData.REGISTRATION_DEFAULT_PROPERTY_CODE;
            }

            // property profile
            PropertyProfile propertyProfile = createPropertyProfile(b);

            Building building = createBuilding(propertyCode, buildingType, complex, website, address, phones, email, propertyProfile);
            //			log.info("Created: " + building);

            // create floorplans
            Map<Integer, Floorplan> floorplans = new HashMap<Integer, Floorplan>();
            for (int i = 0; i < DemoData.NUM_FLOORPLANS; i++) {
                String floorplanName = b + "-" + i;
                if (i == 1) {
                    floorplanName = DemoData.REGISTRATION_DEFAULT_FLOORPLAN;
                }

                Floorplan floorplan = createFloorplan(floorplanName);
                floorplan.building().set(building);
                PersistenceServicesFactory.getPersistenceService().persist(floorplan);
                floorplans.put(i, floorplan);
            }

            // now create units for the building
            for (int floor = 1; floor < DemoData.NUM_FLOORS + 1; floor++) {

                // for each floor we want to create the same number of units
                for (int j = 1; j < DemoData.NUM_UNITS_PER_FLOOR + 1; j++) {

                    String suiteNumber = "#" + (floor * 100 + j);
                    float bedrooms = 2.0f;
                    float bathrooms = 2.0f;

                    Floorplan floorplan = floorplans.get(j % DemoData.NUM_FLOORPLANS);
                    if (floorplan == null) {
                        throw new IllegalStateException("No floorplan");
                    }

                    int uarea = floorplan.minArea().getValue() + RandomUtil.randomInt(10);
                    createUnit(building, suiteNumber, floor, uarea, bedrooms, bathrooms, floorplan);
                }
            }
        }

        StringBuilder b = new StringBuilder();
        b.append("Created ").append(buildingCount).append(" buildings, ").append(unitCount).append(" units");
        return b.toString();
    }

    @Override
    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n");

        List<Floorplan> floorplans = PersistenceServicesFactory.getPersistenceService().query(new EntityQueryCriteria<Floorplan>(Floorplan.class));
        sb.append(floorplans.size()).append(" floorplans\n");
        for (Floorplan floorplan : floorplans) {
            sb.append("\t");
            sb.append(floorplan);
            sb.append("\n");
        }

        //        EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
        //        floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().name(), DemoData.REGISTRATION_DEFAULT_FLOORPLAN));
        //        floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().propertyCode(), DemoData.REGISTRATION_DEFAULT_PROPERTY_CODE));
        //        Floorplan floorplan = PersistenceServicesFactory.getPersistenceService().retrieve(floorplanCriteria);
        //        sb.append("Floorplan: ").append(floorplan);

        List<Building> buildings = PersistenceServicesFactory.getPersistenceService().query(new EntityQueryCriteria<Building>(Building.class));
        sb.append("\n\nLoaded ").append(buildings.size()).append(" buildings\n\n");
        for (Building building : buildings) {
            //            b.append(building.getStringView());
            sb.append(building.type().getStringView());
            sb.append("\t");
            sb.append(building.address().streetNumber().getStringView()).append(", ");
            sb.append(building.address().streetName().getStringView()).append(", ");
            sb.append(building.address().streetType().getStringView()).append(", ");
            sb.append(building.address().city().getStringView()).append(" ").append(building.address().province().getStringView()).append(", ");
            sb.append(building.address().postalCode().getStringView()).append(", ").append(building.address().country().getStringView());

            // phones
            sb.append("\t");

            for (Phone phone : building.phoneList()) {
                sb.append(phone.phoneNumber().getStringView());
                sb.append("/").append(phone.phoneType().getStringView());
            }

            //            // email
            //            b.append("\t");
            //            b.append(building.email().getStringView());

            sb.append("\n");

            // get the units
            EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
            criteria.add(new PropertyCriterion("building", Restriction.EQUAL, building.getPrimaryKey()));
            List<AptUnit> units = PersistenceServicesFactory.getPersistenceService().query(criteria);
            sb.append("\tBuilding has ").append(units.size()).append(" units\n");

            for (AptUnit unit : units) {
                sb.append("\t");
                sb.append(unit.floor().getStringView()).append(" floor");
                sb.append(" ");
                sb.append(unit.area().getStringView()).append(" sq. ft.");
                sb.append(" ");
                sb.append(unit.building().propertyCode().getStringView());
                sb.append(" ");
                sb.append(unit.floorplan());
                sb.append(" | ");
                sb.append(unit.floorplan().name().getStringView()); //.append(" ").append(unit.floorplan().pictures());
                sb.append("\n");
                sb.append("\t\t").append(unit.utilities()).append("\n");
                sb.append("\t\t").append(unit.amenities()).append("\n");
                sb.append("\t\t").append(unit.details()).append("\n");
                sb.append("\t\t").append(unit.concessions()).append("\n");
                sb.append("\t\t").append(unit.addOns()).append("\n");
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    private static void persist(IEntity entity) {
        PersistenceServicesFactory.getPersistenceService().persist(entity);
    }
}
