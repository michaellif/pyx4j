/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-31
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident.weather.accuweather.utils;

import java.io.InputStream;
import java.util.Scanner;

import com.pyx4j.gwt.server.IOUtils;

public class AccuWeatherTestUtils {

    public static String getResourceAsString(Class<?> clazz, String resourceName) {
        Scanner sc = null;
        try {
            InputStream forecastMockResource = clazz.getResourceAsStream(resourceName);
            sc = new Scanner(forecastMockResource);
            sc.useDelimiter("\\A");
            return sc.next();
        } finally {
            IOUtils.closeQuietly(sc);
        }
    }

}
