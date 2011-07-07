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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.common.domain.RangeGroup;
import com.propertyvista.common.domain.contact.Address;
import com.propertyvista.common.domain.contact.Address.AddressType;
import com.propertyvista.common.domain.contact.Email;
import com.propertyvista.common.domain.contact.Email.Type;
import com.propertyvista.common.domain.contact.IAddressFull.StreetDirection;
import com.propertyvista.common.domain.contact.IAddressFull.StreetType;
import com.propertyvista.common.domain.contact.Phone;
import com.propertyvista.common.domain.person.Name;
import com.propertyvista.common.domain.person.Person;
import com.propertyvista.common.domain.ref.Province;
import com.propertyvista.portal.server.preloader.RandomUtil;
import com.propertyvista.server.common.reference.SharedData;

public class CommonsGenerator {

    static String[] lipsum;

    public static String lipsum() {
        if (lipsum == null) {
            lipsum = CSVLoad.loadFile(IOUtils.resourceFileName("lipsum.csv", CommonsGenerator.class), "description");
        }
        return lipsum[DataGenerator.nextInt(lipsum.length, "lipsum", 4)];
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

        person.homePhone().setValue(createPhone().getStringView());
        person.mobilePhone().setValue(createPhone().getStringView());
        person.workPhone().setValue(createPhone().getStringView());

        String email = name.firstName().getStringView() + "." + name.lastName().getStringView() + "@" + RandomUtil.random(DemoData.EMAIL_DOMAINS);
        person.email().setValue(createEmail(email).getStringView());

        return person;
    }

    public static Email createEmail(String emailAddress) {
        Email email = EntityFactory.create(Email.class);

        email.type().setValue(Type.work);
        email.address().setValue(emailAddress);

        return email;
    }

    public static Phone createPhone() {
        String code = RandomUtil.randomBoolean() ? "416" : "905";
        int digits = RandomUtil.randomInt(10) * 1000 + RandomUtil.randomInt(10) * 100 + RandomUtil.randomInt(10) * 10 + RandomUtil.randomInt(10);
        String phoneNumber = "(" + code + ") 555-" + digits;
        return createPhone(phoneNumber);
    }

    public static Phone createPhone(String phoneNumber) {
        Phone phone = EntityFactory.create(Phone.class);

        phone.type().setValue(Phone.Type.work);
        phone.number().setValue(phoneNumber);

        return phone;
    }

    public static Address createAddress() {
        Address address = EntityFactory.create(Address.class);

        address.addressType().setValue(AddressType.property);

        address.unitNumber().setValue(Integer.toString(RandomUtil.randomInt(1000)));
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
