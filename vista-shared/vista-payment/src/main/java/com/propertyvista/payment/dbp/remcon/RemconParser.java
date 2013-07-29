/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.dbp.remcon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;

import javax.validation.ValidationException;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.payment.dbp.remcon.RemconField.RemconFieldType;

public class RemconParser {

    private final RemconFile remconFile = new RemconFile();

    public RemconParser() {

    }

    public RemconFile getRemconFile() {
        return remconFile;
    }

    public void load(InputStream in) {
        int lineNumber = 0;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("ISO-8859-1")));
            String line = null;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                parse(line);
            }
            reader.close();
        } catch (IOException ioe) {
            throw new RuntimeException("Remcon read error", ioe);
        } catch (Exception e) {
            throw new RuntimeException("Remcon read error, Line# " + lineNumber + "; " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private void parse(String line) {
        int length = line.length();
        if (length == 0) {
            throw new Error("Invalid or truncated file");
        }
        char recordType = line.charAt(0);
        RemconRecord record = RemconRecordFactory.createRemconRecord(recordType);

        List<Field> fieldList = RemconFieldReflection.getFileds(record.getClass());

        int cIndex = 1;
        for (Field field : fieldList) {
            RemconField remconField = field.getAnnotation(RemconField.class);
            int endIndex = cIndex + remconField.value();
            if (endIndex > length) {
                if (remconField.type() == RemconFieldType.Filler) {
                    endIndex = length;
                } else {
                    throw new Error("Invalid or truncated field '" + field.getName() + "'");
                }
            }
            String value = line.substring(cIndex, endIndex).trim();
            validate(field, remconField, value);

            try {
                field.set(record, value);
            } catch (ReflectiveOperationException e) {
                throw new Error(e);
            }
            cIndex += remconField.value();
        }

        remconFile.records.add(record);
    }

    private void validate(Field field, RemconField remconField, String value) {
        switch (remconField.type()) {
        case Alphanumeric:
            break;
        case Numeric:
            if (!value.matches("[0-9]+")) {
                throw new ValidationException(SimpleMessageFormat.format("Invalid field `{0}` value", field.getName()));
            }
            break;
        case DateYYMMDD:
            if (!value.matches("[0-9]+")) {
                throw new ValidationException(SimpleMessageFormat.format("Invalid field `{0}` value", field.getName()));
            }
            break;
        case DateMMDDYY:
            if (!value.matches("[0-9]+")) {
                throw new ValidationException(SimpleMessageFormat.format("Invalid field `{0}` value", field.getName()));
            }
            break;
        case Filler:
            if (!value.matches("[ ]*")) {
                throw new ValidationException(SimpleMessageFormat.format("Invalid field `{0}` value", field.getName()));
            }
            break;

        }
    }
}
