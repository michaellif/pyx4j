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
package com.propertvista.generator.util;

import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.RangeGroup;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.contact.AddressStructured.StreetDirection;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.contact.Email;
import com.propertyvista.domain.contact.Email.Type;
import com.propertyvista.domain.contact.Phone;
import com.propertyvista.domain.marketing.PublicVisibilityType;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.property.PropertyPhone;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.server.common.reference.SharedData;

public class CommonsGenerator {

    static String[] lipsum;

    static String[] lipsumShort;

    static List<AddressStructured> adresses;

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

    public static Name createName() {
        Name name = EntityFactory.create(Name.class);

        if (RandomUtil.randomInt() % 3 == 0) {
            name.namePrefix().setValue(RandomUtil.random(DemoData.NAME_PREFIX));
        }

        name.firstName().setValue(RandomUtil.random(DemoData.FIRST_NAMES));
        name.lastName().setValue(RandomUtil.random(DemoData.LAST_NAMES));

        if (RandomUtil.randomInt() % 10 == 0) {
            name.middleName().setValue(RandomUtil.random(DemoData.FIRST_NAMES));
        }

        if (RandomUtil.randomInt() % 15 == 0) {
            name.nameSuffix().setValue(RandomUtil.random(DemoData.NAME_SUFFIX));
        }

//        IPrimitive<String> maidenName();

        return name;
    }

    public static Person createPerson() {
        Person person = EntityFactory.create(Person.class);

        Name name = createName();
        person.name().set(name);
        person.birthDate().setValue(RandomUtil.randomLogicalDate(1930, 1980));

        person.homePhone().set(createPhone());
        person.mobilePhone().set(createPhone());
        person.workPhone().set(createPhone(DataGenerator.randomPhone("905"), Phone.Type.work, 123));

        person.email().set(createEmail(name));

        return person;
    }

    public static Email createEmail(Name person) {
        return createEmail(RandomUtil.randomPersonEmail(person), Type.home);
    }

    public static Email createEmail(String address, Email.Type type) {
        Email email = EntityFactory.create(Email.class);

        email.type().setValue(type);
        email.address().setValue(address);

        return email;
    }

    public static Phone createPhone() {
        return createPhone(DataGenerator.randomPhone(RandomUtil.randomBoolean() ? "416" : "905"));
    }

    public static Phone createPhone(String number) {
        return createPhone(number, null, null);
    }

    public static Phone createPhone(String number, Phone.Type type, Integer ext) {
        Phone phone = EntityFactory.create(Phone.class);

        phone.type().setValue(type);
        phone.number().setValue(number);
        phone.extension().setValue(ext);

        return phone;
    }

    public static PropertyPhone createPropertyPhone() {
        PropertyPhone phone = EntityFactory.create(PropertyPhone.class);
        phone.number().setValue(DataGenerator.randomPhone(RandomUtil.randomBoolean() ? "416" : "905"));
        if (RandomUtil.randomBoolean()) {
            phone.extension().setValue(RandomUtil.randomInt(100));
        }
        phone.type().setValue(RandomUtil.randomEnum(PropertyPhone.Type.class));
        phone.visibility().setValue(RandomUtil.randomEnum(PublicVisibilityType.class));
        phone.description().setValue(lipsumShort());
        return phone;
    }

    public static AddressStructured createAddress() {
        boolean useNewAddress = false;
        if (useNewAddress) {
            if (adresses == null) {
                adresses = EntityCSVReciver.create(AddressStructured.class).loadFile(IOUtils.resourceFileName("address-struct.csv", CommonsGenerator.class));
            }
            return adresses.get(DataGenerator.nextInt(adresses.size(), "addresss", 10));
        } else {
            return createAddressWrong();
        }
    }

    public static AddressStructured createAddressWrong() {
        AddressStructured address = EntityFactory.create(AddressStructured.class);

        address.suiteNumber().setValue(Integer.toString(RandomUtil.randomInt(1000)));
        address.streetNumber().setValue(Integer.toString(RandomUtil.randomInt(10000)));
        address.streetNumberSuffix().setValue("");

        address.streetName().setValue(RandomUtil.random(DemoData.STREETS));
        address.streetType().setValue(RandomUtil.random(StreetType.values()));
        address.streetDirection().setValue(RandomUtil.random(StreetDirection.values()));

        address.city().setValue(RandomUtil.random(DemoData.CITIES));
        address.county().setValue("");

        Province province = RandomUtil.random(SharedData.getProvinces());
        address.province().set(province);
        address.country().set(province.country());

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
