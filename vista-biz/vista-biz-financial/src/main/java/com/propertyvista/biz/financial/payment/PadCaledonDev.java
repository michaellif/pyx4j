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

public class PadCaledonDev {

    File getFile() {
        return new File(".", "caledon_file_creation_number.properties");
    }

    public static void saveFileCreationNumber(int value) {
        // TODO Auto-generated method stub
    }

    public static int restoreFileCreationNumber() {
        // TODO Auto-generated method stub
        return 1;
    }

}
