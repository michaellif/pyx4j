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

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;

public class VistaDBPreloadTest extends VistaDBTestBase {

	private final static Logger log = LoggerFactory
			.getLogger(VistaDBPreloadTest.class);

	public void testDefaultPreload() {

		// make things faster
		DemoData.NUM_RESIDENTIAL_BUILDINGS = 1;
		DemoData.NUM_FLOORPLANS = 2;
		DemoData.NUM_UNITS_PER_FLOOR = 2;
		DemoData.NUM_FLOORS = 2;
		DemoData.NUM_POTENTIAL_TENANTS = 2;

		long start = System.currentTimeMillis();
		new VistaDataPreloaders().preloadAll();
		log.info("Preload time {}", TimeUtils.secSince(start));
	}
}
