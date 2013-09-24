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
 * Created on 2013-09-23
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfReader;

/**
 * 
 * This is a tool for converting an N4 form template downloaded from the Internet to the n4 form that will be used in our application
 * 
 * The changes that were made are: remove the first page (it doesn't have to be sent to tenants) and rename fields to make our form filling code more generic.
 * 
 */
public class N4PrepareForm {

    private static final String ORIG_FORM_NAME = "stel02_111567.pdf";

    private static final String MODDED_FORM_NAME = "n4.pdf";

    public static void main(String[] argv) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(N4PrepareForm.class.getResourceAsStream(ORIG_FORM_NAME));
        if (false) {
            // this doesn't work with sub fields and I don't know how to convert sub fields to "unsub" fields
            // so I will implment mapping using annotation and not using the same name as member
            renameFields(reader.getAcroFields());
        }
        removeInteractiveFields(reader.getAcroFields());

        String moddedDocFileName = makeModdedFileName();
        FileOutputStream fos = new FileOutputStream(moddedDocFileName);
        PdfCopyFields copyFields = new PdfCopyFields(fos);
        copyFields.addDocument(reader, "2,3"); // we don't need 
        copyFields.close();

        System.out.println("Successfully modded an N4 template and saved it at: " + moddedDocFileName);
    }

    private static String makeModdedFileName() {
        return "src/main/resources/".replace("/", File.separator) + N4PrepareForm.class.getPackage().getName().replace(".", File.separator) + File.separator
                + MODDED_FORM_NAME;
    }

    private static void renameFields(AcroFields fields) {
        fields.renameField("Text1", "to");
        fields.renameField("Text2", "from");
        fields.renameField("b12c96nfn4_app_street_no", "tenantStreetNumber");
        fields.renameField("b12c96nfn4_app_street_name", "tenantStreetName");
        fields.renameField("b12c96nfn4_app_street_label", "tenantStreetType");
        fields.renameField("b12c96nfn4_app_street_direction", "tenantStreetDirection");
        fields.renameField("b12c96nfn4_app_city", "tenantMunicipality");
        fields.renameField("b12c96nfn4_app_unit_no", "tenantUnit");

        fields.renameField("b12c96nfn4_app_postal_code_1", "tenantPostalCodeADA");
        fields.renameField("@@b12c96nfn4_app_postal_code_2.0", "@@b12c96nfn4_app_postal_code_2.tenantPostalCodeDAD");
    }

    private static void removeInteractiveFields(AcroFields fields) {
        fields.removeField("b12c96nfn4_total_rent_owed_g");
        fields.removeField("b12c96nfn4_termination_date_g");
    }

}
