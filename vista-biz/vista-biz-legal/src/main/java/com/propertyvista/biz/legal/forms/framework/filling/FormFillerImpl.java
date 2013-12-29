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
 * Created on 2013-11-21
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal.forms.framework.filling;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.FieldPosition;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.biz.legal.forms.framework.mapping.Formatter;
import com.propertyvista.biz.legal.forms.framework.mapping.PdfFieldDescriptor;
import com.propertyvista.biz.legal.forms.framework.mapping.PdfFieldsMapping;

public class FormFillerImpl implements FormFiller {

    @Override
    public <Data extends IEntity> byte[] fillForm(byte[] form, PdfFieldsMapping<Data> mapping, Data fieldsData, boolean flatten) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(form);
        PdfStamper stamper = new PdfStamper(reader, bos);
        AcroFields pdfFormFields = stamper.getAcroFields();

        new PdfFormFillerWorker(pdfFormFields, stamper, mapping).fillForm(fieldsData);

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

    private static void setImageField(PdfFieldDescriptor fieldDescriptor, AcroFields fields, PdfStamper stamper, byte[] imageData) throws IOException,
            DocumentException {
        List<FieldPosition> fieldPositions = fields.getFieldPositions(fieldDescriptor.mappedFields().get(0));
        if (fieldPositions == null || fieldPositions.size() == 0) {
            return;
        }

        FieldPosition imageHolderPosition = fieldPositions.get(0);
        PdfContentByte canvas = stamper.getOverContent(imageHolderPosition.page);
        Image image = Image.getInstance(imageData);
        int[] scaledImageDimensions = ImageTransformUtils.scaleProportionallyToFit(//@formatter:off
                new int[] {(int) image.getWidth(), (int)image.getHeight()},
                new int[] {(int) imageHolderPosition.position.getWidth(), (int)imageHolderPosition.position.getHeight()}
        );//@formatter:on
        image.scaleAbsolute(scaledImageDimensions[0], scaledImageDimensions[1]);

        int[] upperLeftCorner = ImageTransformUtils.center(//@formatter:off
                scaledImageDimensions,
                new int[] {
                        (int) imageHolderPosition.position.getLeft(),
                        (int) imageHolderPosition.position.getBottom(),
                        (int) imageHolderPosition.position.getRight(),
                        (int) imageHolderPosition.position.getTop()
                }
        );//@formatter:on
        image.setAbsolutePosition(upperLeftCorner[0], upperLeftCorner[1]);
        canvas.addImage(image);

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

    private static class PdfFormFillerWorker {

        private final AcroFields pdfFormFields;

        private final PdfStamper stamper;

        private final PdfFieldsMapping<?> mapping;

        public PdfFormFillerWorker(AcroFields fields, PdfStamper stamper, PdfFieldsMapping<?> mapping) {
            this.pdfFormFields = fields;
            this.stamper = stamper;
            this.mapping = mapping;
        }

        public void fillForm(IEntity formData) {
            fill(this.mapping, formData);
        }

        private void fill(PdfFieldsMapping<?> mapping, IObject<?> field) {
            if (mapping == null || field.isNull()) {
                return;
            }

            if (field instanceof IPrimitive) {
                try {
                    PdfFieldDescriptor fieldDescriptor = mapping.getDescriptor(field);
                    if (isTextField(field)) {
                        setTextField(fieldDescriptor, pdfFormFields, field.getValue());

                    } else if (isCheckbox(field)) {
                        setCheckBox(fieldDescriptor, pdfFormFields, (Boolean) field.getValue());

                    } else if (field.getValueClass().isEnum()) {
                        // TODO add checks that field mapping doesn't have multiple mappings and no length
                        Class<?> enumType = field.getValueClass();
                        // TODO make something more clean to defining enums
                        String value = fieldDescriptor.states().isEmpty() ? field.getValue().toString() : fieldDescriptor.states().get(
                                ((Enum) field.getValue()).ordinal());
                        setEnumField(fieldDescriptor, pdfFormFields, value);
                    } else if (field.getValueClass().isArray()) {
                        // TODO add checks that field mapping doesn't have multiple mappings and no length
                        setImageField(fieldDescriptor, pdfFormFields, stamper, (byte[]) field.getValue());
                    }
                } catch (Throwable e) {
                    throw new RuntimeException("got error while processing filling field '" + field.getPath() + "' filled with '" + field.getValue() + "'", e);
                }

            } else if (field instanceof IEntity) {
                for (String memberName : ((IEntity) field).getEntityMeta().getMemberNames()) {
                    // TODO this alg. is not nice (I have to think about it again)
                    IObject<?> childMember = ((IEntity) field).getMember(memberName);
                    if (childMember instanceof IEntity) {
                        fill(mapping.getChildMapping((IEntity) childMember), childMember);
                    } else {
                        fill(mapping, childMember);
                    }
                }
            } else if (field instanceof IList) {
                IList<?> rows = (IList<?>) field;
                for (int rowIndex = 0; rowIndex < rows.size(); ++rowIndex) {
                    fill(mapping.getChildMapping(rows, rowIndex), rows.get(rowIndex));
                }
            }
        }

    }
}
