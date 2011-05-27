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

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.ws.operations.GetResidentTransactions_ByChargeDate_Login;
import com.yardi.ws.operations.GetResidentTransactions_Login;
import com.yardi.ws.operations.GetResidentsLeaseCharges_Login;
import com.yardi.ws.operations.GetUnitInformation_Login;

public class YardiExample {

    private final static Logger log = LoggerFactory.getLogger(YardiExample.class);

    public static void main(String[] args) {
        YardiClient c = new YardiClient();

        try {
            YardiTransactions.ping(c);
            YardiTransactions.getPropertyConfigurations(c);

            c.transactionId = 3L;
            {
                // Allows export of all units and corresponding occupancy status
                // for a given property/property list.
                c.currentActionName = "GetUnitInformation";
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

            c.transactionId = 4L;
            {
                c.currentActionName = "GetResidentTransactions";
                GetResidentTransactions_Login l = new GetResidentTransactions_Login();
                l.setUserName("propertyvistaws");
                l.setPassword("52673");
                l.setServerName("aspdb04");
                l.setDatabase("afqoml_live");
                l.setPlatform("SQL");
                l.setYardiPropertyId("prvista1");
                l.setInterfaceEntity("Property Vista");

                c.getResidentTransactionsService().getResidentTransactions_Login(l);
            }

            c.transactionId = 5L;
            {
                c.currentActionName = "GetResidentTransactions_ByChargeDate";
                GetResidentTransactions_ByChargeDate_Login l = new GetResidentTransactions_ByChargeDate_Login();
                l.setUserName("propertyvistaws");
                l.setPassword("52673");
                l.setServerName("aspdb04");
                l.setDatabase("afqoml_live");
                l.setPlatform("SQL");
                l.setYardiPropertyId("prvista1");
                l.setInterfaceEntity("Property Vista");

                //Consumer may only query for a date range of 31 days or less
                Calendar from = GregorianCalendar.getInstance();
                from.set(Calendar.YEAR, 2011);
                from.set(Calendar.MONTH, 2);
                from.set(Calendar.DAY_OF_MONTH, 1);

                l.setFromDate(from);

                Calendar to = GregorianCalendar.getInstance();
                to.set(Calendar.YEAR, 2011);
                to.set(Calendar.MONTH, 2);
                to.set(Calendar.DAY_OF_MONTH, 28);

                l.setToDate(to);

                c.getResidentTransactionsService().getResidentTransactions_ByChargeDate_Login(l);
            }

            c.transactionId = 6L;
            {
                c.currentActionName = "GetResidentsLeaseCharges";
                GetResidentsLeaseCharges_Login l = new GetResidentsLeaseCharges_Login();
                l.setUserName("propertyvistaws");
                l.setPassword("52673");
                l.setServerName("aspdb04");
                l.setDatabase("afqoml_live");
                l.setPlatform("SQL");
                l.setYardiPropertyId("prvista1");
                l.setInterfaceEntity("Property Vista");

                Calendar when = GregorianCalendar.getInstance();
                when.set(Calendar.YEAR, 2011);
                when.set(Calendar.MONTH, 1);

                l.setPostMonth(when);

                c.getResidentTransactionsService().getResidentsLeaseCharges_Login(l);
            }

        } catch (Throwable e) {
            log.error("error", e);
        }
    }
}
