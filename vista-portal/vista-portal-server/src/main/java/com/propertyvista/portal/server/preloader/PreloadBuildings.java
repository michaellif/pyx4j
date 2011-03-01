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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.domain.AddOn;
import com.propertyvista.portal.domain.Address;
import com.propertyvista.portal.domain.Amenity;
import com.propertyvista.portal.domain.Building;
import com.propertyvista.portal.domain.Building.BuildingType;
import com.propertyvista.portal.domain.ChargeType;
import com.propertyvista.portal.domain.Complex;
import com.propertyvista.portal.domain.Concession;
import com.propertyvista.portal.domain.Concession.ConcessionType;
import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.Email;
import com.propertyvista.portal.domain.Email.EmailType;
import com.propertyvista.portal.domain.Floorplan;
import com.propertyvista.portal.domain.MarketRent;
import com.propertyvista.portal.domain.Phone;
import com.propertyvista.portal.domain.Phone.PhoneType;
import com.propertyvista.portal.domain.Picture;
import com.propertyvista.portal.domain.Unit;
import com.propertyvista.portal.domain.UnitInfoItem;
import com.propertyvista.portal.domain.Utility;
import com.propertyvista.portal.domain.pt.LeaseTerms;
import com.propertyvista.portal.domain.pt.PetChargeRule;
import com.propertyvista.portal.domain.pt.PropertyProfile;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.gwt.server.IOUtils;

public class PreloadBuildings extends AbstractDataPreloader {

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

        floorplan.area().setValue(1200);
        floorplan.name().setValue(name);

        // for now save just one picture
        int imageIndex = RandomUtil.randomInt(3) + 1;
        String filename = resourceFileName("apartment" + imageIndex + ".jpg");
        try {
            byte[] picture = IOUtils.getResource(filename);
            if (picture == null) {
                log.info("Could not find picture [" + filename + "] in classpath");
            } else {
                //            log.info("Picture size is: " + picture.length);
                Picture blob = EntityFactory.create(Picture.class);
                blob.content().setValue(picture);
                floorplan.pictures().add(blob);
            }
        } catch (Exception e) {
            log.error("Failed to read the file [" + filename + "]", e);
        }

        return floorplan;
    }

    public static Utility createUtility(String name) {
        Utility utility = EntityFactory.create(Utility.class);
        utility.name().setValue(name);
        persist(utility);
        return utility;
    }

    public static Amenity createAmenity(String name) {
        Amenity amenity = EntityFactory.create(Amenity.class);
        amenity.name().setValue(name);
        persist(amenity);
        return amenity;
    }

    public static UnitInfoItem createUnitInfoItem(String name) {
        UnitInfoItem item = EntityFactory.create(UnitInfoItem.class);
        item.name().setValue(name);
        persist(item);
        return item;
    }

    public static Concession createConcession(ConcessionType type, float freeMonths, float percentage) {
        Concession concession = EntityFactory.create(Concession.class);
        concession.name().setValue(type.toString()); // TODO this has to be changed to use proper type
        if (freeMonths > 0) {
            concession.freeMonths().setValue("" + freeMonths);
        }
        if (percentage > 0) {
            concession.percentage().setValue("" + percentage);
        }
        persist(concession);
        return concession;
    }

    public static AddOn createAddOn(String name, double monthlyCost) {
        AddOn addOn = EntityFactory.create(AddOn.class);
        addOn.name().setValue(name);
        addOn.monthlyCost().setValue(monthlyCost);
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

    private Building createBuilding(String propertyCode, BuildingType buildingType, Complex complex, String website, Address address, List<Phone> phones,
            Email email, PropertyProfile propertyProfile) {
        Building building = EntityFactory.create(Building.class);

        building.propertyCode().setValue(propertyCode);

        building.buildingType().setValue(buildingType);
        //		building.complex().
        building.website().setValue(website);

        building.address().set(address);

        for (Phone phone : phones) {
            building.phoneList().add(phone);
        }

        building.propertyProfile().set(propertyProfile);

        PersistenceServicesFactory.getPersistenceService().persist(building);

        building.email().set(email); // not sure yet what to do about the email and its type

        buildingCount++;
        return building;
    }

    private Unit createUnit(Building building, String suiteNumber, int floor, int area, double bedrooms, double bathrooms, Floorplan floorplan,
            LeaseTerms leaseTerms) {
        Unit unit = EntityFactory.create(Unit.class);

        unit.suiteNumber().setValue(suiteNumber);
        unit.building().set(building);
        unit.floor().setValue(floor);
        unit.unitType().setValue(floor + "-230" + RandomUtil.randomInt(10));
        unit.area().setValue(area);
        unit.bedrooms().setValue(bedrooms);
        unit.bathrooms().setValue(bathrooms);
        unit.newLeaseTerms().set(leaseTerms);

        double rent = 900 + RandomUtil.randomInt(200);
        for (int i = 1; i < 4; i++) {
            MarketRent marketRent = EntityFactory.create(MarketRent.class);
            marketRent.leaseTerm().setValue(i * 6);
            marketRent.rent().amount().setValue(rent - 35 * i);
            unit.marketRent().add(marketRent);
        }

        // mandatory utilities
        unit.utilities().add(createUtility("Water"));
        unit.utilities().add(createUtility("Heat"));
        unit.utilities().add(createUtility("Gas"));
        unit.utilities().add(createUtility("Hydro"));

        // optional utilities
        if (RandomUtil.randomBoolean()) {
            unit.utilities().add(createUtility("Cable"));
        }
        if (RandomUtil.randomBoolean()) {
            unit.utilities().add(createUtility("Internet"));
        }

        // amenity, all optional
        if (RandomUtil.randomBoolean()) {
            unit.amenities().add(createAmenity("1 Indoor Parking"));
        }
        if (RandomUtil.randomBoolean()) {
            unit.amenities().add(createAmenity("Double Locker"));
        }

        // info items
        if (RandomUtil.randomBoolean()) {
            unit.infoDetails().add(createUnitInfoItem("Partially Furnished"));
        } else {
            unit.infoDetails().add(createUnitInfoItem("Fully Furnished"));
        }
        if (RandomUtil.randomBoolean()) {
            unit.infoDetails().add(createUnitInfoItem("Colour TV"));
        }
        if (RandomUtil.randomBoolean()) {
            unit.infoDetails().add(createUnitInfoItem("Breathtaking View"));
        }
        if (RandomUtil.randomBoolean()) {
            unit.infoDetails().add(createUnitInfoItem("Walk-in Closet"));
        }
        if (RandomUtil.randomBoolean()) {
            unit.infoDetails().add(createUnitInfoItem("Jacuzzi"));
        }

        // concessions
        if (RandomUtil.randomBoolean()) {
            unit.concessions().add(createConcession(ConcessionType.freeMonths, 2f, 0));
        }
        if (RandomUtil.randomBoolean()) {
            unit.concessions().add(createConcession(ConcessionType.percentDiscount, 0, 15));
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

        unit.requiredDeposit().setValue(150d + 5 * RandomUtil.randomInt(20));

        Calendar avalable = new GregorianCalendar();
        avalable.setTime(new Date());
        avalable.add(Calendar.DATE, 5 + RandomUtil.randomInt(30));
        DateUtils.dayStart(avalable);

        unit.avalableForRent().setValue(avalable.getTime());

        unit.floorplan().set(floorplan);

        PersistenceServicesFactory.getPersistenceService().persist(unit);

        unitCount++;
        return unit;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(Building.class, Unit.class, Floorplan.class, Email.class, Phone.class, Complex.class, Utility.class, UnitInfoItem.class,
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
            BuildingType buildingType = RandomUtil.random(BuildingType.values());

            Complex complex = null;
            if (b % 3 == 0) {
                complex = createComplex(2);
            }

            String website = "www.property" + (b + 1) + ".com";

            // address
            String street = RandomUtil.randomInt(10000) + " Yonge St";
            String zip = "L" + (b + 1 % 10) + "C " + (b + 5 % 10) + "M" + (b + 7 % 10);
            Address address = PreloadUtil.createAddress(street, zip);

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

                    int uarea = floorplan.area().getValue() + RandomUtil.randomInt(10);
                    createUnit(building, suiteNumber, floor, uarea, bedrooms, bathrooms, floorplan, leaseTerms);
                }
            }
        }

        // for now load the units from here, this can be later be part of the preloader
        load();

        StringBuilder b = new StringBuilder();
        b.append("Created " + buildingCount + " buildings, " + unitCount + " units");
        return b.toString();
    }

    public void load() {
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
        sb.append("\n\nLoaded " + buildings.size() + " buildings\n\n");
        for (Building building : buildings) {
            //            b.append(building.getStringView());
            sb.append(building.buildingType().getStringView());
            sb.append("\t");
            sb.append(building.address().addressLine1().getStringView()).append(", ");
            sb.append(building.address().city().getStringView()).append(" ").append(building.address().state().getStringView()).append(", ");
            sb.append(building.address().zip().getStringView()).append(", ").append(building.address().country().getStringView());

            // property profile
            sb.append("\nProperty Profile\n");
            sb.append(building.propertyProfile());
            sb.append("\t");

            sb.append("\nPet Charge");
            sb.append(building.propertyProfile().petCharge());
            sb.append("\t");

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
            EntityQueryCriteria<Unit> criteria = new EntityQueryCriteria<Unit>(Unit.class);
            criteria.add(new PropertyCriterion("building", Restriction.EQUAL, building.getPrimaryKey()));
            List<Unit> units = PersistenceServicesFactory.getPersistenceService().query(criteria);
            sb.append("\tBuilding has " + units.size() + " units\n");

            for (Unit unit : units) {
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
                sb.append("\t\t").append(unit.infoDetails()).append("\n");
                sb.append("\t\t").append(unit.concessions()).append("\n");
                sb.append("\t\t").append(unit.addOns()).append("\n");
            }
        }
        sb.append("\n");
        log.info(sb.toString());
    }

    private static void persist(IEntity entity) {
        PersistenceServicesFactory.getPersistenceService().persist(entity);
    }
}
