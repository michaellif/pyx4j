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

import org.junit.Test;

import com.propertyvista.domain.legal.N4FormFieldsData;

public class N4GenerationFacadeImplTest {

    private final N4FormFieldsData mockFormData;

    public N4GenerationFacadeImplTest() {
        mockFormData = MockN4FormDataFactory.makeMockN4FormFieldsData("Tenant Tenantovic");
    }

    /** Just run the form fill procedure and see that nothing fails */
    @Test
    public void testSanity() {
        N4GenerationFacadeImpl facade = new N4GenerationFacadeImpl();
        facade.generateN4Letter(mockFormData);
    }

}
