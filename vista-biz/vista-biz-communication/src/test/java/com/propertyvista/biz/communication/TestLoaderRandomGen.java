/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 13, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;

public class TestLoaderRandomGen {
    private static Random random = new Random(new Date().getTime());

    private static String[] firstNames = { "John", "George", "David", "Allan", "Steve", "Bill", "Jack" };

    private static String[] lastNames = { "Smith", "Brown", "Robertson", "Willson", "Anderson", "Moore", "Davis" };

    private static String[] streetNames = { "Main", "Green", "Church", "Front", "Queen", "King", "College" };

    public static String getFirstName() {
        return firstNames[randomInt(firstNames.length)];
    }

    public static String getLastName() {
        return lastNames[randomInt(lastNames.length)];
    }

    public static String getStreetName() {
        return streetNames[randomInt(streetNames.length)];
    }

    public static String createPhone() {
        String[] areaCodes = { "416", "905", "647" };
        String areaCode = areaCodes[random.nextInt(areaCodes.length)];
        DecimalFormat nf = new DecimalFormat("0000000");
        String unformatedPhone = areaCode + nf.format((random.nextInt(10000000)));
        return unformatedPhone.subSequence(0, 3) + "-" + unformatedPhone.subSequence(3, 6) + "-" + unformatedPhone.subSequence(6, 10);
    }

    public static int randomInt() {
        return random.nextInt();
    }

    public static int randomInt(int upTo) {
        return random.nextInt(upTo);
    }
}
