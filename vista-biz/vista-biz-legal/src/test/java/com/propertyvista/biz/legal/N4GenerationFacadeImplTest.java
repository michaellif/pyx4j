/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-09-24
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.legal.N4FormFieldsData;

public class N4GenerationFacadeImplTest {

    private final N4FormFieldsData mockFormData;

    public N4GenerationFacadeImplTest() {
        mockFormData = makeMockN4FormFieldsData("Tenant Tenantovic");
    }

    /** Just run the form fill procedure and see that nothing fails */
    @Test
    public void testSanity() {
        N4GenerationFacadeImpl facade = new N4GenerationFacadeImpl();
        facade.generateN4Letter(Arrays.asList(mockFormData));
    }

    /**
     * Generate a document filled with mock data to see how it looks like
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void main(String args[]) throws FileNotFoundException, IOException {
        N4GenerationFacadeImpl facade = new N4GenerationFacadeImpl();
        byte[] pdf = facade.generateN4Letter(Arrays.asList(makeMockN4FormFieldsData("Tenant Tenantovic")));
        FileOutputStream fos = new FileOutputStream("n4filled-test.pdf");
        fos.write(pdf);
        fos.close();
    }

    private static N4FormFieldsData makeMockN4FormFieldsData(String tenantName) {
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

        return mockFormData;
    }
}
