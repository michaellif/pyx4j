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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.legal.N4FormFieldsData;
import com.propertyvista.domain.legal.PdfFormFieldName;

public class N4GenerationFacadeImpl implements N4GenerationFacade {

    private static final String N4_FORM_FILE = "n4.pdf";

    @Override
    public byte[] generateN4Letter(List<N4FormFieldsData> formData) {
        byte[] filledForm = null;
        try {
            byte[] formTemplate = IOUtils.toByteArray(N4GenerationFacadeImpl.class.getResourceAsStream(N4_FORM_FILE));
            List<Object> filledForms = new LinkedList<Object>();
            for (N4FormFieldsData fieldsData : formData) {
                filledForms.add(fillForm(fieldsData, formTemplate));
            }
            filledForm = joinPdfs(filledForms);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return filledForm;
    }

    @Override
    public N4FormFieldsData populateFormData() {
        // TODO Auto-generated method stub
        return null;
    }

    // TODO factor out this one 
    public byte[] fillForm(IEntity fieldsData, byte[] form) throws IOException, DocumentException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(form);
        PdfStamper stamper = new PdfStamper(reader, bos);
        AcroFields fields = stamper.getAcroFields();

        for (String memberName : fieldsData.getEntityMeta().getMemberNames()) {
            try {
                if (fieldsData.getMember(memberName).getValueClass().equals(String.class)) {
                    String value = fieldsData.getMember(memberName).isNull() ? "" : fieldsData.getMember(memberName).getValue().toString();
                    fields.setField(pdfFieldName(fieldsData, memberName), value);
                } else {
                    // TODO add handling of images
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
        bos.close();
        return bos.toByteArray();
    }

    private String pdfFieldName(IEntity fieldsData, String memberName) throws NoSuchMethodException, SecurityException {
        Class<?> fieldsDataClass = fieldsData.getInstanceValueClass();
        Method member = fieldsDataClass.getDeclaredMethod(memberName, (Class<?>[]) null);
        PdfFormFieldName fieldName = member.getAnnotation(PdfFormFieldName.class);
        return fieldName != null ? fieldName.value() : memberName;
    }

    public byte[] joinPdfs(List<?> pdfArray) {
        // TODO implement this one
        return (byte[]) pdfArray.get(0);
    }
}
