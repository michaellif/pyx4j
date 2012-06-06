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

public class PadCaledonDev {

    static File getFile() {
        return new File(".", "caledon_file_creation_number.properties");
    }

    public static void saveFileCreationNumber(String companyId, int number) {
        String value = String.valueOf(number);

        BufferedWriter out = null;

        try {

            out = new BufferedWriter(new FileWriter(getFile()));
            out.write(value);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static int restoreFileCreationNumber(String companyId) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(getFile()));
            String result;
            if ((result = in.readLine()) != null) {
                in.close();
                int number = Integer.parseInt(result);
                return number;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return 0;
    }

}
