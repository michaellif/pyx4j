/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.dbp.simulator;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.pyx4j.commons.CommonsStringUtils;

import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimFile;
import com.propertyvista.payment.dbp.remcon.RemconField;
import com.propertyvista.payment.dbp.remcon.RemconFieldReflection;
import com.propertyvista.payment.dbp.remcon.RemconFile;
import com.propertyvista.payment.dbp.remcon.RemconRecord;

public class RemconFileWriter implements Closeable {

    private final Writer writer;

    private RemconFileWriter(File file) throws IOException {
        writer = new FileWriter(file);
    }

    public static void write(DirectDebitSimFile directDebitSimFile, File file) throws IOException {
        RemconFile remconFile = RemconFileConvertor.createRemconFile(directDebitSimFile);
        RemconFileWriter fw = new RemconFileWriter(file);
        try {
            for (RemconRecord record : remconFile.records) {
                fw.writeRecord(record);
            }
        } finally {
            IOUtils.closeQuietly(fw);
        }
    }

    private void writeRecord(RemconRecord record) throws IOException {
        writer.append(record.recordType());

        List<Field> fieldList = RemconFieldReflection.getFileds(record.getClass());

        for (Field field : fieldList) {
            String value;
            try {
                value = (String) field.get(record);
            } catch (IllegalArgumentException e) {
                throw new Error(e);
            } catch (IllegalAccessException e) {
                throw new Error(e);
            }
            RemconField remconField = field.getAnnotation(RemconField.class);
            if (value == null) {
                value = createDefaultValue(remconField);
            } else {
                value = padValue(value, remconField);
            }
            writer.append(value);
        }

        writer.append("\n");
    }

    private String createDefaultValue(RemconField remconField) {
        int filedLength = remconField.value();
        switch (remconField.type()) {
        case Alphanumeric:
            return CommonsStringUtils.padding(filedLength, ' ');
        case Numeric:
            return CommonsStringUtils.padding(filedLength, '0');
        case DateYYMMDD:
            return CommonsStringUtils.padding(filedLength, '0');
        case DateMMDDYY:
            return CommonsStringUtils.padding(filedLength, '0');
        case Filler:
            return CommonsStringUtils.padding(filedLength, ' ');
        default:
            throw new IllegalArgumentException(remconField.type().toString());
        }
    }

    private String padValue(String value, RemconField remconField) {
        int filedLength = remconField.value();
        switch (remconField.type()) {
        case Alphanumeric:
            return CommonsStringUtils.paddingRight(value, filedLength, ' ');
        case Numeric:
            return CommonsStringUtils.paddingLeft(value, filedLength, '0');
        case DateYYMMDD:
            return value;
        case DateMMDDYY:
            return value;
        case Filler:
            return CommonsStringUtils.padding(filedLength, ' ');
        default:
            throw new IllegalArgumentException(remconField.type().toString());
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
