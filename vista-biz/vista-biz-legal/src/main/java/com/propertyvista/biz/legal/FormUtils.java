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
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.legal.PdfFormFieldName;

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

    public static byte[] fillForm(IEntity fieldsData, byte[] form) throws IOException, DocumentException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(form);
        PdfStamper stamper = new PdfStamper(reader, bos);
        AcroFields fields = stamper.getAcroFields();

        for (String memberName : fieldsData.getEntityMeta().getMemberNames()) {
            try {
                if (fieldsData.getMember(memberName).getValueClass().equals(String.class)) {
                    String value = fieldsData.getMember(memberName).isNull() ? "" : fieldsData.getMember(memberName).getValue().toString();
                    fields.setField(pdfFieldName(fieldsData, memberName), value);
                } else if (fieldsData.getMember(memberName).getValueClass().isEnum()) {
                    // this code should work the same as for sting but it was left here just to denote it's a different field type
                    String value = fieldsData.getMember(memberName).isNull() ? "" : fieldsData.getMember(memberName).getValue().toString();
                    fields.setField(pdfFieldName(fieldsData, memberName), value);

                } else if (fieldsData.getMember(memberName).getValueClass().isArray()) {
                    do {
                        if (fieldsData.getMember(memberName).isNull()) {
                            break;
                        }

                        List<FieldPosition> fieldPositions = fields.getFieldPositions(pdfFieldName(fieldsData, memberName));
                        if (fieldPositions == null || fieldPositions.size() == 0) {
                            break;
                        }

                        FieldPosition signaturePosition = fieldPositions.get(0);
                        PdfContentByte canvas = stamper.getOverContent(signaturePosition.page);
                        Image signature = Image.getInstance((byte[]) fieldsData.getMember(memberName).getValue());
                        signature.scaleAbsolute(signaturePosition.position.getWidth(), signaturePosition.position.getHeight());
                        signature.setAbsolutePosition(signaturePosition.position.getLeft(), signaturePosition.position.getBottom());
                        canvas.addImage(signature);
                    } while (false);

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

    private static String pdfFieldName(IEntity fieldsData, String memberName) throws NoSuchMethodException, SecurityException {
        Class<?> fieldsDataClass = fieldsData.getInstanceValueClass();
        Method member = fieldsDataClass.getDeclaredMethod(memberName, (Class<?>[]) null);
        PdfFormFieldName fieldName = member.getAnnotation(PdfFormFieldName.class);
        return fieldName != null ? fieldName.value() : memberName;
    }

}
