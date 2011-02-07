/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author dmitry
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.server.preloader;

import java.util.Random;

public class RandomUtil {

    //	private static Random random = new Random(System.currentTimeMillis());
    private static Random random = new Random(100); // we want pseudo-randomness, same results each time

    public static boolean randomBoolean() {
        return random.nextBoolean();
    }

    public static int randomInt(int max) {
        return random.nextInt(max);
    }

    public static <T> T random(T[] array) {
        if (array.length == 0)
            return null;
        int index = random.nextInt(array.length);
        return array[index];
    }
}
