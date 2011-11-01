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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;

public class VistaDBPreloadTest extends VistaDBTestBase {

    private final static Logger log = LoggerFactory.getLogger(VistaDBPreloadTest.class);

    public void testDefaultPreload() {
        long start = System.currentTimeMillis();
        DataPreloaderCollection dp = new VistaDataPreloaders(VistaDevPreloadConfig.createTest());
        dp.setParameterValue(VistaDataPreloaderParameter.attachMedia.name(), Boolean.FALSE);
        dp.preloadAll();
        log.info("Preload time {}", TimeUtils.secSince(start));
    }
}
