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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.domain.Address;
import com.propertyvista.portal.domain.Amenity;
import com.propertyvista.portal.domain.Building;
import com.propertyvista.portal.domain.Building.BuildingType;
import com.propertyvista.portal.domain.Complex;
import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.Email;
import com.propertyvista.portal.domain.Email.EmailType;
import com.propertyvista.portal.domain.Floorplan;
import com.propertyvista.portal.domain.MarketRent;
import com.propertyvista.portal.domain.Phone;
import com.propertyvista.portal.domain.Phone.PhoneType;
import com.propertyvista.portal.domain.Picture;
import com.propertyvista.portal.domain.Unit;
import com.propertyvista.portal.domain.Utility;

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
        String filename = "com/propertyvista/portal/server/preloader/apartment" + imageIndex + ".jpg";
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

    private Building createBuilding(String propertyCode, BuildingType buildingType, Complex complex, String website, Address address, List<Phone> phones,
            Email email) {
        Building building = EntityFactory.create(Building.class);

        building.propertyCode().setValue(propertyCode);

        building.buildingType().setValue(buildingType);
        //		building.complex().
        building.website().setValue(website);

        building.address().set(address);

        for (Phone phone : phones) {
            building.phoneList().add(phone);
        }

        PersistenceServicesFactory.getPersistenceService().persist(building);

        building.email().set(email); // not sure yet what to do about the email and its type

        buildingCount++;
        return building;
    }

    private Unit createUnit(Building building, int floor, int area, double bedrooms, double bathrooms, Floorplan floorplan) {
        Unit unit = EntityFactory.create(Unit.class);

        unit.building().set(building);
        unit.floor().setValue(floor);
        unit.unitType().setValue(floor + "-230" + RandomUtil.randomInt(10));
        unit.area().setValue(area);
        unit.bedrooms().setValue(bedrooms);
        unit.bathrooms().setValue(bathrooms);

        for (int i = 1; i < 4; i++) {
            MarketRent marketRent = EntityFactory.create(MarketRent.class);
            marketRent.leaseTerm().setValue(i * 6);
            marketRent.rent().amount().setValue(565D - 5 * i + RandomUtil.randomInt(100));
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

        unit.requiredDeposit().setValue(150D);

        Calendar avalable = new GregorianCalendar();
        avalable.setTime(new Date());
        avalable.add(Calendar.DATE, 5 + RandomUtil.randomInt(60));
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
            return deleteAll(Building.class, Unit.class, Floorplan.class);
        } else {
            return "This is production";
        }
    }

    @Override
    public String create() {

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
                propertyCode = DemoData.REGISTRATION_DEFAULT_BUILDINGNAME;
            }
            Building building = createBuilding(propertyCode, buildingType, complex, website, address, phones, email);
            //			log.info("Created: " + building);

            // now create units for the building
            for (int floor = 1; floor < DemoData.NUM_FLOORS + 1; floor++) {

                // for each floor we want to create the same number of units
                for (int j = 1; j < DemoData.NUM_UNITS_PER_FLOOR + 1; j++) {
                    int area = RandomUtil.randomInt(1500);

                    float bedrooms = 2.0f;
                    float bathrooms = 2.0f;

                    // later floor plans should be more elaborate
                    String floorplanName = b + "-" + floor + "-" + j;
                    if ((floor == 1) && (j == 1)) {
                        // UI is looking for this plan, see references!
                        floorplanName = DemoData.REGISTRATION_DEFAULT_FLOORPLAN;
                    }
                    Floorplan floorplan = createFloorplan(floorplanName);
                    floorplan.building().set(building);
                    PersistenceServicesFactory.getPersistenceService().persist(floorplan);

                    for (int u = 0; u < 3; u++) {
                        int uarea = area + RandomUtil.randomInt(10);
                        createUnit(building, floor, uarea, bedrooms, bathrooms, floorplan);
                    }
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
        List<Building> buildings = PersistenceServicesFactory.getPersistenceService().query(new EntityQueryCriteria<Building>(Building.class));
        StringBuilder b = new StringBuilder();
        b.append("\n\nLoaded " + buildings.size() + " buildings\n\n");
        for (Building building : buildings) {
            //            b.append(building.getStringView());
            b.append(building.buildingType().getStringView());
            b.append("\t");
            b.append(building.address().addressLine1().getStringView()).append(", ");
            b.append(building.address().city().getStringView()).append(" ").append(building.address().state().getStringView()).append(", ");
            b.append(building.address().zip().getStringView()).append(", ").append(building.address().country().getStringView());

            // phones
            b.append("\t");

            for (Phone phone : building.phoneList()) {
                b.append(phone.phoneNumber().getStringView());
                b.append("/").append(phone.phoneType().getStringView());
            }

            //            // email
            //            b.append("\t");
            //            b.append(building.email().getStringView());

            b.append("\n");

            // get the units
            EntityQueryCriteria<Unit> criteria = new EntityQueryCriteria<Unit>(Unit.class);
            criteria.add(new PropertyCriterion("building", Restriction.EQUAL, building.getPrimaryKey()));
            List<Unit> units = PersistenceServicesFactory.getPersistenceService().query(criteria);
            b.append("\tBuilding has " + units.size() + " units\n");

            for (Unit unit : units) {
                b.append("\t");
                b.append(unit.floor().getStringView()).append(" floor");
                b.append(" ");
                b.append(unit.area().getStringView()).append(" sq. ft.");
                b.append(" ");
                b.append(unit.building().propertyCode().getStringView());
                b.append(" ");
                b.append(unit.floorplan());
                b.append(" | ");
                b.append(unit.floorplan().name().getStringView()); //.append(" ").append(unit.floorplan().pictures());
                b.append("\n");
                b.append("\t\t").append(unit.utilities()).append("\n");
                b.append("\t\t").append(unit.amenities()).append("\n");
            }
        }
        b.append("\n");
        log.info(b.toString());
    }

    private static void persist(IEntity entity) {
        PersistenceServicesFactory.getPersistenceService().persist(entity);
    }
}
