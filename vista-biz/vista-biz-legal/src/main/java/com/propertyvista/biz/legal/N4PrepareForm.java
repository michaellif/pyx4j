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
import java.util.Map;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
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
        AcroFields fields = reader.getAcroFields();

        tweakFields(fields);

        for (Map.Entry<String, Item> field : fields.getFields().entrySet()) {
            System.out.println(field.getKey());
        }
        removeInteractiveFields(fields);

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

        fields.removeField("b12c96nfn4_a1_start");
        fields.removeField("b12c96nfn4_a1_end");
        fields.removeField("b12c96nfn4_a1_charged");
        fields.removeField("b12c96nfn4_a1_owing");
        fields.removeField("b12c96nfn4_a1_paid");

        fields.removeField("b12c96nfn4_a2_start");
        fields.removeField("b12c96nfn4_a2_end");
        fields.removeField("b12c96nfn4_a2_charged");
        fields.removeField("b12c96nfn4_a2_owing");
        fields.removeField("b12c96nfn4_a2_paid");

        fields.removeField("b12c96nfn4_a3_start");
        fields.removeField("b12c96nfn4_a3_end");
        fields.removeField("b12c96nfn4_a3_charged");
        fields.removeField("b12c96nfn4_a3_owing");
        fields.removeField("b12c96nfn4_a3_paid");

        fields.removeField("b12c96nfn4_total_rent_owed");

        fields.removeField("b12c96nfn4_personnel_phone");
        fields.removeField("b12c96nfn4_personnel_fax_number");

    }

    private static void tweakFields(AcroFields fields) {
        System.out.println("Adjusting colors and formatting of 'personnel first name' field");
        // we have to do this since in the form for some reason the background of personnel first name field is not transparent and hides lines.
        // also 'personnel first name field' is not comb (letters displayed apart and separated by lines), which is defined in FF dictionary  
        Item lastNameField = fields.getFieldItem("b12c96nfn4_personnel_last_name");
        PdfDictionary colorsDict = lastNameField.getWidget(0).getAsDict(PdfName.MK);
        PdfDictionary appearanceDict = lastNameField.getWidget(0).getAsDict(PdfName.AP);
        PdfNumber flagsDict = lastNameField.getWidget(0).getAsNumber(PdfName.FF);

        Item firstNameField = fields.getFieldItem("b12c96nfn4_personnel_first_name");
        firstNameField.getWidget(0).put(PdfName.MK, colorsDict);
        firstNameField.getWidget(0).put(PdfName.AP, appearanceDict);
        firstNameField.getWidget(0).put(PdfName.FF, flagsDict);

        // now set up comb flag for the rest of the defective fields that should have a comb flag set (in the order of appearance in the form)        
        setCombFlag(fields.getFieldItem("b12c96nfn4_app_province"));
        setCombFlag(fields.getFieldItem("b12c96nfn4_personnel_first_name"));
        setCombFlag(fields.getFieldItem("b12c96nfn4_org_unit_no"));
        setCombFlag(fields.getFieldItem("b12c96nfn4_org_prov"));
    }

    private static void setCombFlag(Item fieldItem) {
        PdfNumber flags = fieldItem.getWidget(0).getAsNumber(PdfName.FF);
        if (flags == null) {
            flags = new PdfNumber(0);
        }
        flags = new PdfNumber(flags.intValue() | 0x800000); // 25th bit is comb flag, i hope my mad hex skillz are correct
        fieldItem.getWidget(0).put(PdfName.FF, flags);
    }

}
