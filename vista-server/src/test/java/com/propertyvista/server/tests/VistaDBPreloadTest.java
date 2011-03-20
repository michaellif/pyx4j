/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.tests;

import com.propertyvista.config.tests.VistaDBTestCase;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;

import com.pyx4j.commons.TimeUtils;

public class VistaDBPreloadTest extends VistaDBTestCase {

    public void TODO_testDefaultPreload() {
        long start = System.currentTimeMillis();
        System.out.println(new VistaDataPreloaders().preloadAll());
        System.out.println("Total time: " + TimeUtils.secSince(start));
    }
}
