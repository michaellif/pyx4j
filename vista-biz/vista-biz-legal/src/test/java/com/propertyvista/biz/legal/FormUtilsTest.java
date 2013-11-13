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
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.gwt.editor.client.Editor.Ignore;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.DateUtils;

public class FormUtilsTest {

    private final byte[] MOCK_FORM;

    private static final boolean DUMP_PDFS = false;

    public FormUtilsTest() throws IOException {
        MOCK_FORM = IOUtils.toByteArray(N4GenerationFacadeImpl.class.getResourceAsStream("MockForm.pdf"));
    }

    @Ignore
    @Test
    public void dont_testFillForm() throws IOException, DocumentException {
        MockFormFieldsData fieldsData = EntityFactory.create(MockFormFieldsData.class);
        fieldsData.field1().setValue("mock-mock");
        fieldsData.field2().setValue("124567890");

        byte[] filledForm = FormUtils.fillForm(fieldsData, MOCK_FORM, false);

        assertFieldEquals(filledForm, "field1", "mock-mock");

        assertFieldEquals(filledForm, "field2_1", "12");
        assertFieldEquals(filledForm, "field2_2", "456");
        assertFieldEquals(filledForm, "field2_3", "7890");

        dumpPdf("testFillForm", filledForm);
    }

    @Test
    public void testSplitCurrency() {

        {
            String[] splitted1 = FormUtils.splitCurrency(new BigDecimal("1000.00"), false);
            Assert.assertArrayEquals(new String[] { " 1", "000", "00" }, splitted1);
        }

        {
            String[] splitted1 = FormUtils.splitCurrency(new BigDecimal("1000.00"), true);
            Assert.assertArrayEquals(new String[] { "1", "000", "00" }, splitted1);
        }

        {
            String[] splitted2 = FormUtils.splitCurrency(new BigDecimal("10000.00"), false);
            Assert.assertArrayEquals(new String[] { "10", "000", "00" }, splitted2);
        }

        {
            String[] splitted3 = FormUtils.splitCurrency(new BigDecimal("100.00"), false);
            Assert.assertArrayEquals(new String[] { "  ", "100", "00" }, splitted3);
        }

        {
            String[] splitted3 = FormUtils.splitCurrency(new BigDecimal("100.00"), true);
            Assert.assertArrayEquals(new String[] { " ", "100", "00" }, splitted3);
        }

        {
            String[] splitted4 = FormUtils.splitCurrency(new BigDecimal("10.00"), false);
            Assert.assertArrayEquals(new String[] { "  ", " 10", "00" }, splitted4);
        }

        {
            String[] splitted4 = FormUtils.splitCurrency(new BigDecimal("10.00"), true);
            Assert.assertArrayEquals(new String[] { " ", " 10", "00" }, splitted4);
        }

        {
            String[] splitted4 = FormUtils.splitCurrency(new BigDecimal("1.00"), false);
            Assert.assertArrayEquals(new String[] { "  ", "  1", "00" }, splitted4);
        }
        {
            String[] splitted4 = FormUtils.splitCurrency(new BigDecimal("1.00"), true);
            Assert.assertArrayEquals(new String[] { " ", "  1", "00" }, splitted4);
        }
    }

    @Test
    public void testSplitDate() {
        String[] splitted = FormUtils.splitDate(DateUtils.detectDateformat("2013-04-01"));
        Assert.assertArrayEquals(new String[] { "01", "04", "2013" }, splitted);
    }

    @Test
    public void testSplitPhoneNumber() {
        String[] splitted = FormUtils.splitPhoneNumber("(647) 555-5555");
        Assert.assertArrayEquals(new String[] { "647", "555", "5555" }, splitted);
    }

    private void assertFieldEquals(byte[] form, String field, String expectedValue) throws IOException, DocumentException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(form);
        PdfStamper stamper = new PdfStamper(reader, bos);
        AcroFields fields = stamper.getAcroFields();

        Assert.assertEquals(expectedValue, fields.getField(field));
    }

    private void dumpPdf(String pdfName, byte[] filledForm) throws IOException {
        if (DUMP_PDFS) {
            FileOutputStream fos = new FileOutputStream(pdfName + ".pdf");
            fos.write(filledForm);
            fos.close();
        }
    }
}
