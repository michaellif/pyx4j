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
package com.propertyvista.eft.dbp.simulator;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.pyx4j.commons.CommonsStringUtils;

import com.propertyvista.eft.dbp.remcon.RemconField;
import com.propertyvista.eft.dbp.remcon.RemconFieldReflection;
import com.propertyvista.eft.dbp.remcon.RemconFile;
import com.propertyvista.eft.dbp.remcon.RemconRecord;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimFile;

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
                value = createDefaultValue(field, remconField);
            } else {
                value = padValue(value, field, remconField);
            }
            writer.append(value);
        }

        writer.append("\n");
    }

    private String createDefaultValue(Field field, RemconField remconField) {
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
            throw new IllegalArgumentException("invalid type " + remconField.type().toString() + " of filed " + field.getName());
        }
    }

    private String padValue(String value, Field field, RemconField remconField) {
        int filedLength = remconField.value();
        if (value.length() > filedLength) {
            throw new IllegalArgumentException("invalid filed " + field.getName() + " length");
        }
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
            throw new IllegalArgumentException("invalid type " + remconField.type().toString() + " of filed " + field.getName());
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
