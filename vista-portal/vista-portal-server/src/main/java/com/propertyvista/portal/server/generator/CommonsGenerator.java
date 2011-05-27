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

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.common.domain.IAddressFull.StreetDirection;
import com.propertyvista.common.domain.IAddressFull.StreetType;
import com.propertyvista.common.domain.ref.Province;
import com.propertyvista.domain.Address;
import com.propertyvista.domain.Address.AddressType;
import com.propertyvista.domain.Email;
import com.propertyvista.domain.Email.Type;
import com.propertyvista.domain.Phone;
import com.propertyvista.portal.server.preloader.RandomUtil;

public class CommonsGenerator {

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
}
