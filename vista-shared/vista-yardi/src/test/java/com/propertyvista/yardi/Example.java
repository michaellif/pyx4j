/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.ws.operations.GetPropertyConfigurations;
import com.yardi.ws.operations.GetUnitInformation_Login;
import com.yardi.ws.operations.Ping;
import com.yardi.ws.operations.PingResponse;

public class Example {

	private final static Logger log = LoggerFactory.getLogger(Example.class);

	public static void main(String[] args) {
		YardiClient c = new YardiClient();

		try {
			c.transactionId = 1L;

			// The Ping function accepts no parameters, but will return the
			// assembly name of the function being called. Use it to test
			// connectivity.
			Ping ping = new Ping();
			PingResponse pr = c.getResidentTransactionsService().ping(ping);
			log.info("result [{}]", pr.getPingResult());

			c.transactionId = 2L;
			{
				// Allows export of the Property Configuration with the
				// Database. The Unique Interface Entity name is needed in order
				// to return the Property ID's the third-party has access to.
				GetPropertyConfigurations l = new GetPropertyConfigurations();
				l.setUserName("propertyvistaws");
				l.setPassword("52673");
				l.setServerName("aspdb04");
				l.setDatabase("afqoml_live");
				l.setPlatform("SQL");
				l.setInterfaceEntity("Property Vista");
				c.getResidentTransactionsService().getPropertyConfigurations(l);
			}

			c.transactionId = 3L;
			{
				// Allows export of all units and corresponding occupancy status
				// for a given property/property list.
				GetUnitInformation_Login l = new GetUnitInformation_Login();
				l.setUserName("propertyvistaws");
				l.setPassword("52673");
				l.setServerName("aspdb04");
				l.setDatabase("afqoml_live");
				l.setPlatform("SQL");
				l.setYardiPropertyId("prvista1");
				l.setInterfaceEntity("Property Vista");
				c.getResidentTransactionsService().getUnitInformation_Login(l);
			}
		} catch (Throwable e) {
			log.error("error", e);
		}
	}
}
