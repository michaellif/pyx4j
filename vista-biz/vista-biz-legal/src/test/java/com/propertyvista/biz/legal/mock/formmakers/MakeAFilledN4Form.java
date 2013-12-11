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
package com.propertyvista.biz.legal.mock.formmakers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.propertyvista.biz.legal.N4GenerationFacadeImpl;
import com.propertyvista.biz.legal.mock.MockN4FormDataFactory;

/** this is just a utility to generate a document to see how it looks like */
public class MakeAFilledN4Form {

    public static void main(String args[]) throws FileNotFoundException, IOException {
        N4GenerationFacadeImpl facade = new N4GenerationFacadeImpl();
        byte[] pdf = facade.generateN4Letter(MockN4FormDataFactory.makeMockN4FormFieldsData("Tenant Tenantovic"));
        FileOutputStream fos = new FileOutputStream("n4filled-test.pdf");
        fos.write(pdf);
        fos.close();
    }

}
