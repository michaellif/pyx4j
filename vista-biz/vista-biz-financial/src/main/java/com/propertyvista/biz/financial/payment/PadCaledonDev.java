/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.financial.FundsTransferType;

public class PadCaledonDev {

    private static final Logger log = LoggerFactory.getLogger(PadCaledonDev.class);

    static File getFile() {
        return new File(".", "caledon_file_creation_number.properties");
    }

    private static Properties getProperties(File file) {
        Properties props = new Properties();
        FileInputStream is = null;
        try {
            props.load(is = new FileInputStream(getFile()));
            return props;
        } catch (FileNotFoundException e) {
            log.debug("File {} Not Found", file);
        } catch (IOException e) {
            log.error("IO Exception", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return new Properties();
    }

    public static void saveFileCreationNumber(String companyId, FundsTransferType fundsTransferType, int number) {
        saveFileProperty(fundsTransferType.getCode() + "." + companyId, number);
    }

    public static void saveFileProperty(String propertyName, int number) {
        Properties props = getProperties(getFile());
        FileOutputStream out = null;
        try {
            String value = String.valueOf(number);
            props.setProperty(propertyName, value);
            props.store(out = new FileOutputStream(getFile()), null);
        } catch (IOException e) {
            log.error("Error while saving file creation number", e);
            throw new Error(e.getLocalizedMessage());
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public static int restoreFileCreationNumber(String companyId, FundsTransferType fundsTransferType) {
        return restoreFileProperty(fundsTransferType.getCode() + "." + companyId);
    }

    public static int restoreFileProperty(String propertyName) {
        Properties props = getProperties(getFile());
        String result = props.getProperty(propertyName);
        if (result != null) {
            return Integer.parseInt(result);
        }
        return 0;
    }
}
