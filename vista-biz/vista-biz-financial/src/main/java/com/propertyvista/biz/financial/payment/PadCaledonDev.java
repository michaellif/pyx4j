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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ApplicationVersion;
import com.pyx4j.gwt.server.IOUtils;

public class PadCaledonDev {

    private static final Logger log = LoggerFactory.getLogger(ApplicationVersion.class);

    static File getFile() {
        return new File(".", "caledon_file_creation_number.properties");
    }

    public static void saveFileCreationNumber(String companyId, int number) {
        String value = String.valueOf(number);

        BufferedWriter out = null;

        try {

            out = new BufferedWriter(new FileWriter(getFile()));
            out.write(value);

        } catch (FileNotFoundException e) {
            log.error("Error while saving file creation number", e);
        } catch (IOException e) {
            log.error("Error while saving file creation number", e);
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    IOUtils.closeQuietly(out);
                }
            } catch (IOException e) {
                log.error("Error while closing output stream", e);
            }
        }
    }

    public static int restoreFileCreationNumber(String companyId) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(getFile()));
            String result;
            if ((result = in.readLine()) != null) {
                IOUtils.closeQuietly(in);
                int number = Integer.parseInt(result);
                return number;
            }
        } catch (FileNotFoundException e) {
            log.error("Error while restoring file creation number", e);
        } catch (IOException e) {
            log.error("Error while restoring file creation number", e);
        } finally {

            if (in != null) {
                IOUtils.closeQuietly(in);
            }
        }
        return 0;
    }

}
