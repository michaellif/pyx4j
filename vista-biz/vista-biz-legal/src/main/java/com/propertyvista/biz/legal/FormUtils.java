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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.FieldPosition;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;

import com.propertyvista.domain.legal.utils.Formatter;
import com.propertyvista.domain.legal.utils.Partitioner;
import com.propertyvista.domain.legal.utils.PdfFormFieldFormatter;
import com.propertyvista.domain.legal.utils.PdfFormFieldMapping;
import com.propertyvista.domain.legal.utils.PdfFormFieldPartitioner;

public class FormUtils {

    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");

    private static final Pattern phonePattern = Pattern.compile("\\((\\d\\d\\d)\\) (\\d\\d\\d)-(\\d\\d\\d\\d)( x\\d+)?");

    public static String[] splitCurrency(BigDecimal amount, boolean oneDigitThousands) {
        if (amount.compareTo(new BigDecimal("99999.99")) > 0) {
            throw new Error("The current amount could not be formatted (greater than $99,999.99 and cannot fit into the form field)");
        }
        if (oneDigitThousands && amount.compareTo(new BigDecimal("9999.99")) > 0) {
            throw new Error("The current amount could not be formatted (greater than $99,999.99 and cannot fit into the form field)");
        }
        String spaces = "     ";
        String formattedAmount = SimpleMessageFormat.format("{0,number,#,##0.00}", amount);
        // pad with spaces
        formattedAmount = spaces.substring(0, 9 - formattedAmount.length()) + formattedAmount;

        String[] splittedCurrency = new String[3];
        int indexOfComma = formattedAmount.indexOf(",");
        splittedCurrency[0] = indexOfComma != -1 ? formattedAmount.substring(0, indexOfComma) : "  ";
        int indexOfDot = formattedAmount.indexOf(".");
        splittedCurrency[1] = formattedAmount.substring(indexOfDot - 3, indexOfDot);
        splittedCurrency[2] = formattedAmount.substring(indexOfDot + 1, indexOfDot + 3);

        if (oneDigitThousands && splittedCurrency[0].startsWith(" ") & splittedCurrency[0].length() > 1) {
            splittedCurrency[0] = splittedCurrency[0].substring(1, splittedCurrency[0].length());
        }
        return splittedCurrency;
    }

    public static String[] splitDate(Date date) {
        String[] splittedDate = new String[3];
        String formattedDate = dateFormat.format(date);
        splittedDate[0] = formattedDate.substring(0, 2);
        splittedDate[1] = formattedDate.substring(3, 5);
        splittedDate[2] = formattedDate.substring(6, 10);
        return splittedDate;
    }

    public static String[] splitPhoneNumber(String phoneNumber) {
        Matcher m = phonePattern.matcher(phoneNumber);
        if (m.matches()) {
            String[] splitNumber = new String[3];
            splitNumber[0] = m.group(1);
            splitNumber[1] = m.group(2);
            splitNumber[2] = m.group(3);
            return splitNumber;
        } else {
            return null;
        }
    }

    public static byte[] fillForm(IEntity fieldsData, PdfFieldsMapping<?> mapping, byte[] form) throws IOException, DocumentException {
        return fillForm(fieldsData, mapping, form, true);
    }

    public static byte[] fillForm(IEntity fieldsData, PdfFieldsMapping<?> mapping, byte[] form, boolean flatten) throws IOException, DocumentException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(form);
        PdfStamper stamper = new PdfStamper(reader, bos);
        AcroFields fields = stamper.getAcroFields();

        for (String memberName : fieldsData.getEntityMeta().getMemberNames()) {
            try {
                IObject<?> field = fieldsData.getMember(memberName);
                if (field.isNull()) {
                    continue;
                }

                PdfFieldDescriptor fieldDescriptor = null;
                if (mapping != null) {
                    fieldDescriptor = mapping.getDescriptor(memberName);
                } else {
                    List<Formatter> formatters = new LinkedList<Formatter>();
                    Partitioner partitioner = null;
                    List<String> mappedFields = null;

                    for (Annotation annotation : fieldsData.getInstanceValueClass().getDeclaredMethod(memberName, (Class<?>[]) null).getAnnotations()) {
                        if (annotation.annotationType().equals(PdfFormFieldMapping.class)) {
                            mappedFields = Arrays.asList(((PdfFormFieldMapping) annotation).value().split(","));
                        } else if (annotation.annotationType().equals(PdfFormFieldFormatter.class)) {
                            formatters.add(((PdfFormFieldFormatter) annotation).value().newInstance());
                        } else if (annotation.annotationType().equals(PdfFormFieldPartitioner.class)) {
                            partitioner = ((PdfFormFieldPartitioner) annotation).value().newInstance();
                        }
                    }
                    fieldDescriptor = new PdfFieldDescriptor(formatters, mappedFields, partitioner, Collections.<String> emptyList());
                }

                if (isTextField(field)) {
                    setTextField(fieldDescriptor, fields, field.getValue());
                } else if (isCheckbox(field)) {
                    setCheckBox(fieldDescriptor, fields, (Boolean) field.getValue());
                } else if (fieldsData.getMember(memberName).getValueClass().isEnum()) {
                    // TODO add checks that field mapping doesn't have multiple mappings and no length
                    Class<?> enumType = fieldsData.getMember(memberName).getValueClass();
                    // TODO make something more clean to defining enums
                    String value = fieldDescriptor.states().isEmpty() ? field.getValue().toString() : fieldDescriptor.states().get(
                            ((Enum) field.getValue()).ordinal());
                    setEnumField(fieldDescriptor, fields, value);

                } else if (fieldsData.getMember(memberName).getValueClass().isArray()) {
                    // TODO add checks that field mapping doesn't have multiple mappings and no length
                    setImageField(fieldDescriptor, fields, stamper, (byte[]) field.getValue());
                }

            } catch (Throwable e) {
                throw new RuntimeException("got error while processing filling field '" + memberName + "' filled with '"
                        + fieldsData.getMember(memberName).getValue() + "'", e);
            }
        }
        stamper.setFormFlattening(flatten);
        stamper.close();
        reader.close();
        bos.close();
        return bos.toByteArray();
    }

    private static void setTextField(PdfFieldDescriptor fieldDescriptor, AcroFields fields, Object object) throws IOException, DocumentException,
            InstantiationException, IllegalAccessException {
        String value = null;
        if (fieldDescriptor.formatters().isEmpty()) {
            value = object.toString();
        } else {
            Iterator<Formatter> i = fieldDescriptor.formatters().iterator();
            Formatter formatter = i.next();
            value = formatter.format(object);
            while (i.hasNext()) {
                formatter = i.next();
                value = formatter.format(value);
            }
        }

        List<String> mappedFields = fieldDescriptor.mappedFields();
        for (int partIndex = 0; partIndex < mappedFields.size(); ++partIndex) {
            String fieldNameAndLength = mappedFields.get(partIndex);
            int fieldLength = -1;
            String fieldName = fieldNameAndLength;
            if (fieldNameAndLength.contains("{")) {
                int openBracketIndex = fieldNameAndLength.indexOf("{");
                int closeBracketIndex = fieldNameAndLength.indexOf("}");
                fieldLength = Integer.parseInt(fieldNameAndLength.substring(openBracketIndex + 1, closeBracketIndex));
                fieldName = fieldNameAndLength.substring(0, openBracketIndex);
            }

            String part = fieldDescriptor.partitioner() == null ? value : fieldDescriptor.partitioner().getPart(value, partIndex);
            if (fieldLength != -1) {
                if (part.length() > fieldLength) {
                    throw new IllegalArgumentException("part '" + part + "' of field '" + fieldName + "' has greater length then allowed (allowed length is "
                            + fieldLength + ")");
                }
                while (part.length() != fieldLength) {
                    part = " " + part;
                }
            }
            fields.setField(fieldName, part);
        }

    }

    private static void setEnumField(PdfFieldDescriptor fieldDescriptor, AcroFields fields, String value) throws IOException, DocumentException {
        fields.setField(fieldDescriptor.mappedFields().get(0), value);
    }

    private static void setImageField(PdfFieldDescriptor fieldDescriptor, AcroFields fields, PdfStamper stamper, byte[] image) throws IOException,
            DocumentException {
        List<FieldPosition> fieldPositions = fields.getFieldPositions(fieldDescriptor.mappedFields().get(0));
        if (fieldPositions == null || fieldPositions.size() == 0) {
            return;
        }

        FieldPosition signaturePosition = fieldPositions.get(0);
        PdfContentByte canvas = stamper.getOverContent(signaturePosition.page);
        Image signature = Image.getInstance(image);
        signature.scaleAbsolute(signaturePosition.position.getWidth(), signaturePosition.position.getHeight());
        signature.setAbsolutePosition(signaturePosition.position.getLeft(), signaturePosition.position.getBottom());
        canvas.addImage(signature);

    }

    private static void setCheckBox(PdfFieldDescriptor fieldDescriptor, AcroFields fields, Boolean value) throws IOException, DocumentException {
        if (value) {
            fields.setField(fieldDescriptor.mappedFields().get(0), fieldDescriptor.states().get(0));
        }
    }

    private static boolean isTextField(IObject<?> field) {
        return field.getValueClass().equals(String.class) || field.getValueClass().equals(Integer.class) || field.getValueClass().equals(BigDecimal.class)
                || field.getValueClass().equals(LogicalDate.class) || field.getValueClass().equals(Date.class);
    }

    private static boolean isCheckbox(IObject<?> field) {
        return field.getValueClass().equals(Boolean.class);
    }

}
