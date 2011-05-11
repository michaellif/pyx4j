/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.propertyvista.common.domain.DemoData;

import com.pyx4j.essentials.server.preloader.DataGenerator;

public class BusinessDataGenerator {
    public static String createEmail() {
        String email = DataGenerator.randomLastName().toLowerCase() + DataGenerator.randomInt(Integer.MAX_VALUE) + DemoData.USERS_DOMAIN;
        return email;
    }
}
