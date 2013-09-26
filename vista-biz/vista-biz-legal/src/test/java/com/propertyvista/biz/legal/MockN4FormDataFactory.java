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
package com.propertyvista.biz.legal;

import java.math.BigDecimal;

import org.apache.commons.io.IOUtils;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.domain.contact.AddressStructured.StreetDirection;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.legal.N4FormFieldsData;
import com.propertyvista.domain.legal.N4FormFieldsData.SignedBy;
import com.propertyvista.domain.legal.N4LeaseData;
import com.propertyvista.domain.tenant.lease.Tenant;

public class MockN4FormDataFactory {

    private final static String SIGNATURE = "mock-signature.jpg";

    public static N4FormFieldsData makeMockN4FormFieldsData(String tenantName) {
        N4FormFieldsData mockFormData = EntityFactory.create(N4FormFieldsData.class);
        mockFormData.to().setValue(tenantName + "\n11-2222 Bathurst Street Toronto ON A9A 9A9");
        mockFormData.from().setValue("RedRiDge");

        mockFormData.tenantStreetNumber().setValue("2222");
        mockFormData.tenantStreetName().setValue("Bathurst");
        mockFormData.tenantStreetType().setValue("Street");
        mockFormData.tenantStreetDirection().setValue("North");
        mockFormData.tenantUnit().setValue("11");
        mockFormData.tenantMunicipality().setValue("Toronto");
        mockFormData.tenantPostalCodeADA().setValue("A9A");
        mockFormData.tenantPostalCodeDAD().setValue("9A9");

        mockFormData.terminationDateDD().setValue("31");
        mockFormData.terminationDateMM().setValue("12");
        mockFormData.terminationDateYYYY().setValue("2013");

        mockFormData.globalTotalOwedThousands().setValue(" 1");
        mockFormData.globalTotalOwedHundreds().setValue("234");
        mockFormData.globalTotalOwedCents().setValue("99");

        {
            mockFormData.owedFromDDA().setValue("01");
            mockFormData.owedFromMMA().setValue("01");
            mockFormData.owedFromYYYYA().setValue("2012");

            mockFormData.owedToDDA().setValue("31");
            mockFormData.owedToMMA().setValue("12");
            mockFormData.owedToYYYYA().setValue("2013");

            mockFormData.rentChargedThousandsA().setValue("1");
            mockFormData.rentChargedHundredsA().setValue("155");
            mockFormData.rentChargedCentsA().setValue("00");
            mockFormData.rentPaidThousandsA().setValue(" ");
            mockFormData.rentPaidHundredsA().setValue("235");
            mockFormData.rentPaidCentsA().setValue("17");
            mockFormData.rentOwingThousandsA().setValue("1");
            mockFormData.rentOwingHundredsA().setValue("235");
            mockFormData.rentOwingCentsA().setValue("15");
        }
        {
            mockFormData.owedFromDDB().setValue("01");
            mockFormData.owedFromMMB().setValue("01");
            mockFormData.owedFromYYYYB().setValue("2012");

            mockFormData.owedToDDB().setValue("31");
            mockFormData.owedToMMB().setValue("12");
            mockFormData.owedToYYYYB().setValue("2013");

            mockFormData.rentChargedThousandsB().setValue("1");
            mockFormData.rentChargedHundredsB().setValue("155");
            mockFormData.rentChargedCentsB().setValue("00");
            mockFormData.rentPaidThousandsB().setValue(" ");
            mockFormData.rentPaidHundredsB().setValue("235");
            mockFormData.rentPaidCentsB().setValue("17");
            mockFormData.rentOwingThousandsB().setValue("1");
            mockFormData.rentOwingHundredsB().setValue("235");
            mockFormData.rentOwingCentsB().setValue("15");
        }
        {
            mockFormData.owedFromDDC().setValue("01");
            mockFormData.owedFromMMC().setValue("01");
            mockFormData.owedFromYYYYC().setValue("2012");

            mockFormData.owedToDDC().setValue("31");
            mockFormData.owedToMMC().setValue("12");
            mockFormData.owedToYYYYC().setValue("2013");

            mockFormData.rentChargedThousandsC().setValue("1");
            mockFormData.rentChargedHundredsC().setValue("155");
            mockFormData.rentChargedCentsC().setValue("00");
            mockFormData.rentPaidThousandsC().setValue(" ");
            mockFormData.rentPaidHundredsC().setValue("235");
            mockFormData.rentPaidCentsC().setValue("17");
            mockFormData.rentOwingThousandsC().setValue("1");
            mockFormData.rentOwingHundredsC().setValue("235");
            mockFormData.rentOwingCentsC().setValue("15");
        }
        // TODO add signature
        mockFormData.signedBy().setValue(SignedBy.Agent);
        try {
            mockFormData.signature().setValue(IOUtils.toByteArray(N4GenerationFacadeImpl.class.getResourceAsStream(SIGNATURE)));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        mockFormData.signatureDate().setValue("31/12/2013");

        mockFormData.signatureFirstName().setValue("Foo");
        mockFormData.signatureLastName().setValue("Bar");
        mockFormData.signatureCompanyName().setValue("Red Ridge");

        mockFormData.signatureAddress().setValue("405 The West Mall");
        mockFormData.signatureUnit().setValue("1111");
        mockFormData.signatureMunicipality().setValue("Toronto");
        mockFormData.signatureProvince().setValue("ON");
        mockFormData.signaturePostalCode().setValue("A9A 9A9");
        mockFormData.signaturePhoneNumberAreaCode().setValue("647");
        mockFormData.signaturePhoneNumberCombA().setValue("647");
        mockFormData.signaturePhoneNumberCombB().setValue("555");
        mockFormData.signaturePhoneNumberCombB().setValue("5555");
        mockFormData.signatureFaxNumberAreaCode().setValue("647");
        mockFormData.signatureFaxNumberCombA().setValue("647");
        mockFormData.signatureFaxNumberCombB().setValue("555");
        mockFormData.signatureFaxNumberCombB().setValue("5555");
        mockFormData.signatureEmailAddress().setValue("foob@redridge.ca");

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
