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
 */
package com.propertyvista.generator.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.RangeGroup;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.domain.ref.ISOProvince;
import com.propertyvista.generator.BuildingsGenerator.BuildingsGeneratorConfig;
import com.propertyvista.generator.PreloadData;

public class CommonsGenerator {

    private static List<Name> names;

    private static String[] employeeTitles;

    private static String[] lipsum;

    private static String[] lipsumShort;

    private static String[] complexNames;

    private static BuildingAmenity[] amenities;

    private static BuildingUtility[] buildingUtilities;

    private static FloorplanAmenity[] floorPlanAmenities;

    private static Marketing[] buildingMarketings;

    private static Floorplan[] floorPlans;

    private static AptUnitItem[] unitItems;

    private static Map<String, List<InternationalAddress>> addresses = new HashMap<>();

    private static final String DEFAULT_ADDRESSES_RESOURCE_FILE = "address-intern.csv";

    public static String lipsum() {
        if (lipsum == null) {
            lipsum = CSVLoad.loadFile(IOUtils.resourceFileName("lipsum.csv", CommonsGenerator.class), "description");
        }
        return lipsum[DataGenerator.nextInt(lipsum.length, "lipsum", 4)];
    }

    public static String lipsumShort() {
        if (lipsumShort == null) {
            lipsumShort = CSVLoad.loadFile(IOUtils.resourceFileName("lipsum-short.csv", CommonsGenerator.class), "description");
        }
        return lipsumShort[DataGenerator.nextInt(lipsumShort.length, "lipsumShort", 4)];
    }

    public static String randomEmployeeTitle() {
        if (employeeTitles == null) {
            employeeTitles = CSVLoad.loadFile(IOUtils.resourceFileName("employee-titles.csv", CommonsGenerator.class), "title");
        }
        return employeeTitles[DataGenerator.nextInt(employeeTitles.length, "employeeTitles", 4)];
    }

    public static String randomComplexName() {
        if (complexNames == null) {
            complexNames = CSVLoad.loadFile(IOUtils.resourceFileName("complex-names.csv", CommonsGenerator.class), "name");
        }
        return complexNames[DataGenerator.nextInt(complexNames.length, "complexNames", 3)];
    }

    public static Marketing randomBuilding() {
        if (buildingMarketings == null) {
            List<Marketing> entities = EntityCSVReciver.create(Marketing.class).loadResourceFile(
                    IOUtils.resourceFileName("buildings.xlsx", CommonsGenerator.class));
            buildingMarketings = entities.toArray(new Marketing[entities.size()]);
        }
        return buildingMarketings[DataGenerator.nextInt(buildingMarketings.length, "buildings", 15)];
    }

    public static BuildingAmenity randomBuildingAmenity() {
        if (amenities == null) {
            List<BuildingAmenity> entities = EntityCSVReciver.create(BuildingAmenity.class).loadResourceFile(
                    IOUtils.resourceFileName("building-amenities.xlsx", CommonsGenerator.class));
            amenities = entities.toArray(new BuildingAmenity[entities.size()]);
        }
        return amenities[DataGenerator.nextInt(amenities.length, "amenities", 6)];
    }

    public static BuildingUtility randomBuildingUtility() {
        if (buildingUtilities == null) {
            List<BuildingUtility> entities = EntityCSVReciver.create(BuildingUtility.class).loadResourceFile(
                    IOUtils.resourceFileName("building-utilities.xlsx", CommonsGenerator.class));
            buildingUtilities = entities.toArray(new BuildingUtility[entities.size()]);
        }
        return buildingUtilities[DataGenerator.nextInt(buildingUtilities.length, "buildingUtilities", 4)];
    }

    public static FloorplanAmenity randomFloorPlanAmenity() {
        if (floorPlanAmenities == null) {
            List<FloorplanAmenity> entities = EntityCSVReciver.create(FloorplanAmenity.class).loadResourceFile(
                    IOUtils.resourceFileName("floorplan-amenities.xlsx", CommonsGenerator.class));
            floorPlanAmenities = entities.toArray(new FloorplanAmenity[entities.size()]);
        }
        return floorPlanAmenities[DataGenerator.nextInt(floorPlanAmenities.length, "floorPlanAmenities", 10)];
    }

    public static Floorplan randomFloorPlan() {
        if (floorPlans == null) {
            List<Floorplan> entities = EntityCSVReciver.create(Floorplan.class).loadResourceFile(
                    IOUtils.resourceFileName("floorplans.xlsx", CommonsGenerator.class));
            floorPlans = entities.toArray(new Floorplan[entities.size()]);
        }
        return floorPlans[DataGenerator.nextInt(floorPlans.length, "floorPlans", 6)];
    }

    public static AptUnitItem randomAptUnitItem() {
        if (unitItems == null) {
            List<AptUnitItem> entities = EntityCSVReciver.create(AptUnitItem.class).loadResourceFile(
                    IOUtils.resourceFileName("unit-items.xlsx", CommonsGenerator.class));
            unitItems = entities.toArray(new AptUnitItem[entities.size()]);
        }
        return unitItems[DataGenerator.nextInt(unitItems.length, "unitItems", 11)];
    }

    public static Name createName() {
        Name name = EntityFactory.create(Name.class);

        if (RandomUtil.randomInt() % 5 == 0) {
            name.namePrefix().setValue(RandomUtil.randomEnum(Name.Prefix.class));
        }

        name.firstName().setValue(DataGenerator.randomFirstName());
        name.lastName().setValue(DataGenerator.randomLastName());

        if (RandomUtil.randomInt() % 10 == 0) {
            name.middleName().setValue(DataGenerator.randomLetters(1));
        }

        if (RandomUtil.randomInt() % 15 == 0) {
            name.nameSuffix().setValue(RandomUtil.random(PreloadData.NAME_SUFFIX));
        }

//        IPrimitive<String> maidenName();

        return name;
    }

    public static Person createPerson() {
        Person person = EntityFactory.create(Person.class);

        Name name = createName();
        person.name().set(name);
        person.birthDate().setValue(RandomUtil.randomLogicalDate(1930, 1980));
        person.sex().setValue(RandomUtil.randomEnum(Person.Sex.class));

        person.homePhone().setValue(createPhone());
        person.mobilePhone().setValue(createPhone());
        person.workPhone().setValue(createPhone(DataGenerator.randomPhone("905"), "123"));

        person.email().setValue(createEmail(name));

        return person;
    }

    public static Name createEmployeeName() {
        if (names == null) {
            names = EntityCSVReciver.create(Name.class).loadResourceFile(IOUtils.resourceFileName("employee-names.csv", CommonsGenerator.class));
        }
        return names.get(DataGenerator.nextInt(names.size(), "names", 10)).duplicate(); //doesn't check for duplicate names
    }

    public static Person createEmployee() {
        Person person = createPerson();

        Name name = createEmployeeName(); //default person parameters are modified for employees
        person.name().set(name);
        return person;
    }

    public static String createEmail(Name person) {
        return (RandomUtil.randomPersonEmail(person)/* , Type.home */);
    }

    public static String createPhone() {
        return createPhone(DataGenerator.randomPhone(RandomUtil.randomBoolean() ? "416" : "905"));
    }

    public static String createPhone(String number) {
        return createPhone(number, null);
    }

    public static String createPhone(String number, String ext) {
        String phone = new String(number);

        if (!CommonsStringUtils.isEmpty(ext)) {
            phone += " x" + ext;
        }

        return phone;
    }

    public static PropertyContact createPropertyContact() {
        PropertyContact contact = EntityFactory.create(PropertyContact.class);
        Name name = createName();
        contact.type().setValue(RandomUtil.randomEnum(PropertyContact.PropertyContactType.class));
        contact.name().setValue(name.getStringView());
        contact.phone().setValue(DataGenerator.randomPhone(RandomUtil.randomBoolean() ? "416" : "905"));
        contact.email().setValue(createEmail(name));
        contact.visibility().setValue(RandomUtil.randomEnum(PublicVisibilityType.class));
        return contact;
    }

    private static void loadAddresses(ISOCountry country) {
        String addressesResourceFile = DEFAULT_ADDRESSES_RESOURCE_FILE;

        if (country == null) {
            // Set default country to Canada
            country = ISOCountry.Canada;
        }

        switch (country) {
        case UnitedStates:
            addressesResourceFile = "address-intern-US.csv";
            break;
        default:
            // do nothing
        }

        if (!addresses.containsKey(country.name)) {
            List<InternationalAddress> countryAddresses = EntityCSVReciver.create(InternationalAddress.class).loadResourceFile(
                    IOUtils.resourceFileName(addressesResourceFile, CommonsGenerator.class));
            addresses.put(country.name, countryAddresses);
        }

    }

    public static InternationalAddress createInternationalAddress() {
        loadAddresses(null);
        return getAddress(null);
    }

    public static InternationalAddress createInternationalAddress(ISOCountry country) {
        loadAddresses(country);
        return getAddress(country);
    }

    private static InternationalAddress getAddress(ISOCountry country) {
        List<InternationalAddress> countryAddresses = getCountryAddresses(country);
        return countryAddresses.get(DataGenerator.nextInt(countryAddresses.size(), "address", 15)).duplicate();
    }

    public static InternationalAddress createInternationalAddress(BuildingsGeneratorConfig config) {
        if (config.provinceCode != null) {
            ISOProvince prov = ISOProvince.forCode(config.provinceCode);
            if (prov != null) {
                loadAddresses(config.country);
                List<InternationalAddress> adressesFiltered = new ArrayList<>();
                for (InternationalAddress addr : getCountryAddresses(config.country)) {
                    if (prov.name.equalsIgnoreCase(addr.province().getValue())) {
                        adressesFiltered.add(addr);
                    }
                }
                return adressesFiltered.get(DataGenerator.randomInt(adressesFiltered.size())).duplicate();
            }
        }
        return createInternationalAddress(config.country);
    }

    private static List<InternationalAddress> getCountryAddresses(ISOCountry country) {
        if (country == null) {
            return addresses.get(ISOCountry.Canada.name);
        } else {
            return addresses.get(country.name);
        }
    }

    public static InternationalAddress createRandomInternationalAddress() {
        InternationalAddress address = EntityFactory.create(InternationalAddress.class);

        address.suiteNumber().setValue(Integer.toString(RandomUtil.randomInt(1000)));
        address.streetNumber().setValue(Integer.toString(RandomUtil.randomInt(10000)));

        address.streetName().setValue(RandomUtil.random(PreloadData.STREETS) + " " + RandomUtil.random(StreetType.values()));

        address.city().setValue(RandomUtil.random(PreloadData.CITIES));

        // for now we support only two countries
        ISOCountry country = RandomUtil.randomBoolean() ? ISOCountry.Canada : ISOCountry.UnitedStates;
        ISOProvince prov = RandomUtil.random(ISOProvince.forCountry(country));

        address.province().setValue(prov.name);
        address.country().setValue(country);

        if (country == ISOCountry.UnitedStates) {
            address.postalCode().setValue(RandomUtil.randomZipCode());
        } else {
            address.postalCode().setValue(RandomUtil.randomPostalCode());
        }

        return address;
    }

    public static RangeGroup createRange(double min, double max) {
        RangeGroup r = EntityFactory.create(RangeGroup.class);

        r.min().setValue(Math.ceil(min + RandomUtil.randomDouble(max)));
        r.max().setValue(Math.ceil(r.min().getValue() + RandomUtil.randomDouble(max - r.min().getValue())));

        return r;
    }

    public static double randomFromRange(RangeGroup r) {
        return r.min().getValue() + Math.ceil(RandomUtil.randomDouble(r.min().getValue() - r.min().getValue()));
    }
}
