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
 * Created on 2013-11-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;

public class L1PrepareForm {

    private static final String ORIG_FORM_NAME = "stel02_111558.pdf";

    private static final String MODDED_FORM_NAME = "l1.pdf";

    public static void main(String[] argv) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(N4PrepareForm.class.getResourceAsStream(ORIG_FORM_NAME));
        AcroFields fields = reader.getAcroFields();

        System.out.println("This form contains the following fields:");
        System.out.println("========================================");
        for (Map.Entry<String, Item> field : fields.getFields().entrySet()) {
            System.out.println(field.getKey());

            String[] appearanceStates = fields.getAppearanceStates(field.getKey());
            if (appearanceStates != null && appearanceStates.length > 0) {
                System.out.println("\t\tstates: " + StringUtils.join(appearanceStates, ", "));
            }
        }
        System.out.println("========================================");

        tweakFields(fields);
        removeInteractiveFields(fields);

        String moddedDocFileName = makeModdedFileName();
        FileOutputStream fos = new FileOutputStream(moddedDocFileName);
        PdfCopyFields copyFields = new PdfCopyFields(fos);
        copyFields.addDocument(reader);
        copyFields.close();

        System.out.println("Successfully modded an L1 template and saved it at: " + moddedDocFileName);
    }

    private static String makeModdedFileName() {
        return "src/main/resources/".replace("/", File.separator) + N4PrepareForm.class.getPackage().getName().replace(".", File.separator) + File.separator
                + MODDED_FORM_NAME;
    }

    private static void removeInteractiveFields(AcroFields fields) {
//        fields.removeField("b12c96nfn4_total_rent_owed_g");
    }

    private static void tweakFields(AcroFields fields) {

        // set up comb flag for the rest of the defective fields that should have a comb flag set (in the order of appearance in the form)        
        setCombFlag(fields.getFieldItem("b12c96nfapp_city"));
        setCombFlag(fields.getFieldItem("b12c96nfapp_province"));
        setCombFlag(fields.getFieldItem("b12c96nfP2_last_name"));
        setCombFlag(fields.getFieldItem("b12c96nfP2_2_first_name"));
        setCombFlag(fields.getFieldItem("b12c96nfP2_2_last_name"));
        setCombFlag(fields.getFieldItem("b12c96nfP2_st_address"));
        setCombFlag(fields.getFieldItem("b12c96nfP2_unit_no"));
        setCombFlag(fields.getFieldItem("b12c96nfP2_city"));
        setCombFlag(fields.getFieldItem("b12c96nfP2_prov"));
        setCombFlag(fields.getFieldItem("b12c96nfP2_postal"));

        setCombFlag(fields.getFieldItem("b12c96nfP1_unit_no"));
        setCombFlag(fields.getFieldItem("b12c96nfP1_city"));
        setCombFlag(fields.getFieldItem("b12c96nfP1_prov"));
        setCombFlag(fields.getFieldItem("b12c96nfP1_postal"));

        setCombFlag(fields.getFieldItem("b12c96nforg_name"));
        setCombFlag(fields.getFieldItem("b12c96nforg_unit_no"));
        setCombFlag(fields.getFieldItem("b12c96nforg_city"));
        setCombFlag(fields.getFieldItem("b12c96nforg_prov"));
        setCombFlag(fields.getFieldItem("b12c96nforg_postal"));

    }

    private static void setCombFlag(Item fieldItem) {
        PdfNumber flags = fieldItem.getWidget(0).getAsNumber(PdfName.FF);
        if (flags == null) {
            flags = new PdfNumber(0);
        }
        flags = new PdfNumber(flags.intValue() | 0x1000000); // 25th bit is comb flag, i hope my mad hex skillz are correct
        fieldItem.getWidget(0).put(PdfName.FF, flags);
    }

}
