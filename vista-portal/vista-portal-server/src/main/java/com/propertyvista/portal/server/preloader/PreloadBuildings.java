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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.domain.Address;
import com.propertyvista.portal.domain.Address.AddressType;
import com.propertyvista.portal.domain.Building;
import com.propertyvista.portal.domain.Building.BuildingType;
import com.propertyvista.portal.domain.Complex;
import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.Email;
import com.propertyvista.portal.domain.Email.EmailType;
import com.propertyvista.portal.domain.Floorplan;
import com.propertyvista.portal.domain.Phone;
import com.propertyvista.portal.domain.Phone.PhoneType;
import com.propertyvista.portal.domain.Unit;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
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

    private Address createAddress(String line1, String zip) {
        Address address = EntityFactory.create(Address.class);

        address.addressType().setValue(AddressType.property);
        address.addressLine1().setValue(line1);
        address.city().setValue("Toronto");
        address.state().setValue("ON");
        address.country().name().setValue("Canada");
        address.zip().setValue(zip);

        return address;
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

    private Floorplan createFloorplan() {
        Floorplan floorplan = EntityFactory.create(Floorplan.class);

        floorplan.area().setValue(1200);
        floorplan.name().setValue("Luxury 2-bedroom");

        // for now save just one picture
        int imageIndex = RandomUtil.randomInt(3) + 1;
        String filename = "com/propertyvista/portal/server/preloader/apartment" + imageIndex + ".jpg";
        try {
            byte[] picture = IOUtils.getResource(filename);
            if (picture == null) {
                log.info("Could not find picture [" + filename + "] in classpath");
            } else {
                //            log.info("Picture size is: " + picture.length);
                floorplan.picture().setValue(picture);
            }
        } catch (Exception e) {
            log.error("Failed to read the file [" + filename + "]", e);
        }

        return floorplan;
    }

    private Building createBuilding(BuildingType buildingType, Complex complex, String website, Address address, List<Phone> phones, Email email) {
        Building building = EntityFactory.create(Building.class);

        building.buildingType().setValue(buildingType);
        //		building.complex().
        building.webSite().setValue(website);

        building.address().set(address);

        for (Phone phone : phones) {
            //          phone.setPrimaryKey(building.getPrimaryKey());
            building.phoneList().add(phone);
            PersistenceServicesFactory.getPersistenceService().persist(phone);
        }

        PersistenceServicesFactory.getPersistenceService().persist(building);

        building.email().set(email); // not sure yet what to do about the email and its type

        buildingCount++;
        return building;
    }

    private Unit createUnit(Building building, int floor, int area, float bedrooms, float bathrooms, Floorplan floorplan) {
        Unit unit = EntityFactory.create(Unit.class);

        unit.building().set(building);
        unit.floor().setValue(floor);
        unit.unitType().setValue("Unknown");
        unit.area().setValue(area);
        unit.bedrooms().setValue(bedrooms);
        unit.bathrooms().setValue(bathrooms);

        PersistenceServicesFactory.getPersistenceService().persist(floorplan);
        unit.floorplan().set(floorplan);

        PersistenceServicesFactory.getPersistenceService().persist(unit);

        unitCount++;
        return unit;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(Building.class, Unit.class);
        } else {
            return "This is production";
        }
    }

    @Override
    public String create() {

        for (int i = 0; i < DemoData.NUM_RESIDENTIAL_BUILDINGS; i++) {

            // building type
            BuildingType buildingType = RandomUtil.random(BuildingType.values());

            Complex complex = null;
            if (i % 3 == 0) {
                complex = createComplex(2);
            }

            String website = "www.property" + (i + 1) + ".com";

            // address
            String street = RandomUtil.randomInt(10000) + " Yonge St";
            String zip = "L" + (i + 1 % 10) + "C " + (i + 5 % 10) + "M" + (i + 7 % 10);
            Address address = createAddress(street, zip);

            // phones
            List<Phone> phones = new ArrayList<Phone>();
            phones.add(createPhone());

            // email
            String emailAddress = "building" + (i + 1) + "@propertyvista.com";
            Email email = createEmail(emailAddress);

            // organization contacts - not many fields there at the moment, will do this later

            Building building = createBuilding(buildingType, complex, website, address, phones, email);
            //			log.info("Created: " + building);

            // now create units for the building
            int numFloors = RandomUtil.randomInt(5);
            int numUnits = RandomUtil.randomInt(5);
            for (int floor = 1; floor < numFloors; floor++) {

                // for each floor we want to create the same number of units
                for (int j = 1; j < numUnits; j++) {
                    int area = RandomUtil.randomInt(1500);

                    float bedrooms = 2.0f;
                    float bathrooms = 2.0f;

                    // later floor plans should be more elaborate
                    Floorplan floorplan = createFloorplan();

                    createUnit(building, floor, area, bedrooms, bathrooms, floorplan);
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
                b.append(unit.floorplan().name()).append(" ").append(unit.floorplan().picture());
                b.append("\n");
            }
        }
        b.append("\n");
        log.info(b.toString());
    }
}
