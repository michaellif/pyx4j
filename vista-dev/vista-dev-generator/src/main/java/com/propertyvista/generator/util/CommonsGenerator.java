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
package com.propertyvista.generator.util;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.RangeGroup;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.contact.AddressStructured.StreetDirection;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.generator.PreloadData;

public class CommonsGenerator {

    private static List<Name> names;

    private static String[] employeeTitles;

    private static String[] lipsum;

    private static String[] lipsumShort;

    private static List<AddressStructured> adresses;

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
        contact.description().setValue(lipsumShort());
        contact.phone().setValue(DataGenerator.randomPhone(RandomUtil.randomBoolean() ? "416" : "905"));
        contact.email().setValue(createEmail(name));
        contact.visibility().setValue(RandomUtil.randomEnum(PublicVisibilityType.class));
        return contact;
    }

    private static void loadAddress() {
        if (adresses == null) {
            adresses = EntityCSVReciver.create(AddressStructured.class)
                    .loadResourceFile(IOUtils.resourceFileName("address-struct.csv", CommonsGenerator.class));
        }
    }

    public static AddressSimple createAddressSimple() {
        loadAddress();
        AddressStructured addressStructured = adresses.get(DataGenerator.nextInt(adresses.size(), "addresss", 10)).duplicate();
        AddressSimple address = EntityFactory.create(AddressSimple.class);
        address.street1().setValue(addressStructured.streetNumber().getValue() + " " + addressStructured.streetName().getValue());
        address.city().setValue(addressStructured.city().getValue());
        address.province().setValue(addressStructured.province().getValue());
        address.country().setValue(addressStructured.country().getValue());
        address.postalCode().setValue(addressStructured.postalCode().getValue());
        return address;
    }

    public static AddressStructured createAddressStructured() {
        loadAddress();
        AddressStructured addressStructured = adresses.get(DataGenerator.nextInt(adresses.size(), "addresss", 10)).duplicate();
        if (addressStructured.streetType().isNull()) {
            addressStructured.streetType().setValue(StreetType.other);
        }
        return addressStructured;
    }

    public static AddressStructured createAddressStructured(String provinceCode) {
        if (provinceCode == null) {
            return createAddressStructured();
        } else {
            loadAddress();
            List<AddressStructured> adressesFiltered = new ArrayList<AddressStructured>();
            for (AddressStructured addressStructured : adresses) {
                if (provinceCode.equalsIgnoreCase(addressStructured.province().code().getValue())) {
                    adressesFiltered.add(addressStructured);
                }
            }

            AddressStructured addressStructured = adressesFiltered.get(DataGenerator.randomInt(adressesFiltered.size())).duplicate();
            if (addressStructured.streetType().isNull()) {
                addressStructured.streetType().setValue(StreetType.other);
            }
            return addressStructured;
        }
    }

    public static AddressStructured createRandomAddress() {
        AddressStructured address = EntityFactory.create(AddressStructured.class);

        address.suiteNumber().setValue(Integer.toString(RandomUtil.randomInt(1000)));
        address.streetNumber().setValue(Integer.toString(RandomUtil.randomInt(10000)));
        address.streetNumberSuffix().setValue("");

        address.streetName().setValue(RandomUtil.random(PreloadData.STREETS));
        address.streetType().setValue(RandomUtil.random(StreetType.values()));
        address.streetDirection().setValue(RandomUtil.random(StreetDirection.values()));

        address.city().setValue(RandomUtil.random(PreloadData.CITIES));
        address.county().setValue("");

        address.province().code().setValue(RandomUtil.random(PreloadData.PROVINCES));
        address.country().name().setValue("Canada");

        // for now we support only two countries
        if (address.country().name().getValue().toLowerCase().startsWith("c")) {
            address.postalCode().setValue(RandomUtil.randomPostalCode());
        } else {
            address.postalCode().setValue(RandomUtil.randomZipCode());
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
