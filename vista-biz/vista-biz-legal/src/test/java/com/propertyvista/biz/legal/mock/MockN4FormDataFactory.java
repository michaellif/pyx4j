/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Sep 26, 2013
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.biz.legal.mock;

import java.math.BigDecimal;

import org.apache.commons.io.IOUtils;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.legal.N4GenerationFacadeImpl;
import com.propertyvista.domain.contact.AddressStructured.StreetDirection;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.legal.ltbcommon.RentOwingForPeriod;
import com.propertyvista.domain.legal.n4.N4FormFieldsData;
import com.propertyvista.domain.legal.n4.N4LeaseData;
import com.propertyvista.domain.legal.n4.N4Signature.SignedBy;
import com.propertyvista.domain.tenant.lease.Tenant;

public class MockN4FormDataFactory {

    private final static String SIGNATURE = "mock-signature.jpg";

    public static N4FormFieldsData makeMockN4FormFieldsData(String tenantName) {
        N4FormFieldsData mockFormData = EntityFactory.create(N4FormFieldsData.class);
        mockFormData.to().setValue(tenantName + "\n11-2222 Bathurst Street Toronto ON A9A 9A9");
        mockFormData.from().setValue("RedRiDge");

        mockFormData.rentalUnitAddress().streetNumber().setValue("2222");
        mockFormData.rentalUnitAddress().streetName().setValue("Bathurst");
        mockFormData.rentalUnitAddress().streetType().setValue("Street");
        mockFormData.rentalUnitAddress().direction().setValue("North");
        mockFormData.rentalUnitAddress().unit().setValue("11");
        mockFormData.rentalUnitAddress().municipality().setValue("Toronto");
        mockFormData.rentalUnitAddress().postalCode().setValue("A9A 9A9");

        mockFormData.terminationDate().setValue(new LogicalDate(DateUtils.detectDateformat("2013-12-31")));
        mockFormData.totalRentOwed().setValue(new BigDecimal("11234.99"));

        mockFormData.owedRent().totalRentOwing().setValue(new BigDecimal("21234.99"));
        RentOwingForPeriod period1 = mockFormData.owedRent().rentOwingBreakdown().$();
        period1.from().setValue(new LogicalDate(DateUtils.detectDateformat("2012-01-01")));
        period1.to().setValue(new LogicalDate(DateUtils.detectDateformat("2013-12-31")));
        period1.rentCharged().setValue(new BigDecimal("1155.00"));
        period1.rentPaid().setValue(new BigDecimal("113.44"));
        period1.rentOwing().setValue(new BigDecimal("1234.55"));
        mockFormData.owedRent().rentOwingBreakdown().add(period1);

        RentOwingForPeriod period2 = mockFormData.owedRent().rentOwingBreakdown().$();
        period2.from().setValue(new LogicalDate(DateUtils.detectDateformat("2013-01-01")));
        period2.to().setValue(new LogicalDate(DateUtils.detectDateformat("2014-12-31")));
        period2.rentCharged().setValue(new BigDecimal("5555.55"));
        period2.rentPaid().setValue(new BigDecimal("13.44"));
        period2.rentOwing().setValue(new BigDecimal("1234.55"));
        mockFormData.owedRent().rentOwingBreakdown().add(period2);

        RentOwingForPeriod period3 = mockFormData.owedRent().rentOwingBreakdown().$();
        period3.from().setValue(new LogicalDate(DateUtils.detectDateformat("2015-01-01")));
        period3.to().setValue(new LogicalDate(DateUtils.detectDateformat("2016-12-31")));
        period3.rentCharged().setValue(new BigDecimal("7777.77"));
        period3.rentPaid().setValue(new BigDecimal("1113.44"));
        period3.rentOwing().setValue(new BigDecimal("234.55"));
        mockFormData.owedRent().rentOwingBreakdown().add(period3);

        mockFormData.signature().signedBy().setValue(SignedBy.Agent);
        try {
            mockFormData.signature().signature().setValue(IOUtils.toByteArray(N4GenerationFacadeImpl.class.getResourceAsStream(SIGNATURE)));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        mockFormData.signature().signatureDate().setValue(new LogicalDate(DateUtils.detectDateformat("2013-12-31")));

        mockFormData.landlordsContactInfo().firstName().setValue("Foo");
        mockFormData.landlordsContactInfo().lastName().setValue("Bar");
        mockFormData.landlordsContactInfo().companyName().setValue("Red Ridge");

        mockFormData.landlordsContactInfo().mailingAddress().setValue("405 The West Mall");
        mockFormData.landlordsContactInfo().unit().setValue("1111");
        mockFormData.landlordsContactInfo().municipality().setValue("Toronto");
        mockFormData.landlordsContactInfo().province().setValue("ON");
        mockFormData.landlordsContactInfo().postalCode().setValue("A9A 9A9");
        mockFormData.landlordsContactInfo().phoneNumber().setValue("(647) 555-5555");
        mockFormData.landlordsContactInfo().faxNumber().setValue("(647) 444-4444");
        mockFormData.landlordsContactInfo().email().setValue("foob@redridge.ca");

        return mockFormData;
    }

    public static N4LeaseData makeLeaseData() {
        N4LeaseData leaseData = EntityFactory.create(N4LeaseData.class);
        Tenant tenant1 = EntityFactory.create(Tenant.class);
        tenant1.customer().person().name().firstName().setValue("Tenant");
        tenant1.customer().person().name().lastName().setValue("Tenantovic");
        leaseData.leaseTenants().add(tenant1);

        leaseData.rentalUnitAddress().streetNumber().setValue("2222");
        leaseData.rentalUnitAddress().streetNumberSuffix().setValue("b");
        leaseData.rentalUnitAddress().streetName().setValue("Bathurst");
        leaseData.rentalUnitAddress().streetType().setValue(StreetType.street);
        leaseData.rentalUnitAddress().streetDirection().setValue(StreetDirection.north);
        leaseData.rentalUnitAddress().suiteNumber().setValue("11");
        leaseData.rentalUnitAddress().city().setValue("Toronto");
        leaseData.rentalUnitAddress().postalCode().setValue("A9A 9A9");

        leaseData.terminationDate().setValue(new LogicalDate(DateUtils.detectDateformat("31/12/2013")));

        leaseData.terminationDate().setValue(new LogicalDate(DateUtils.detectDateformat("31/12/2013")));

        leaseData.totalRentOwning().setValue(new BigDecimal("1234.99"));

        return leaseData;
    }
}
