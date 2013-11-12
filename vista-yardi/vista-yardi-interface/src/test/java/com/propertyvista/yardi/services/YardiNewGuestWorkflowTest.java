/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 11, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.ils.Availability;
import com.yardi.entity.ils.ILSUnit;
import com.yardi.entity.ils.MadeReadyDate;
import com.yardi.entity.ils.PhysicalProperty;
import com.yardi.entity.ils.VacateDate;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.stub.YardiGuestManagementStub;

/**
 * Proof of concept for PTAPP-Yardi integration that exercises the following scenario:
 * - get available units from yardi building
 * - accept input of a new guest info for subsequent yardi import
 * - call ImportYardiGuest_Login to import new guest to yardi
 * - accept input of a new lease application data for subsequent yardi import
 * - call ImportApplication_Login to import new lease application to yardi
 */
public class YardiNewGuestWorkflowTest {
    private final static Logger log = LoggerFactory.getLogger(YardiNewGuestWorkflowTest.class);

    private final static boolean mockMode = true;

    public static void main(String[] args) {
        ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(DatabaseType.HSQLDB));

        PmcYardiCredential yc = getTestPmcYardiCredential();
        try {
            YardiGuestManagementStub stub = ServerSideFactory.create(YardiGuestManagementStub.class);
            if (mockMode) {
                System.out.println("Available Units: #156 (2008-1-10),  #158 (2008-1-11),  #159 (2011-3-21), ");
            } else {
                PhysicalProperty property = stub.getPropertyMarketingInfo(yc, yc.propertyListCodes().getValue());
                log.info("PhysicalProperty: {}", property.getProperty().get(0).getPropertyID().getIdentification().getPrimaryID());
                printUnits(property.getProperty().get(0).getILSUnit());
            }

            String unitNo = readLine("Enter Unit #: ");
            String guestName = readLine("Guest Name: ");

            System.out.println("Application Complete.");
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    static PmcYardiCredential getTestPmcYardiCredential() {
        PmcYardiCredential cr = EntityFactory.create(PmcYardiCredential.class);
        cr.propertyListCodes().setValue("prvista2");
        cr.serviceURLBase().setValue("https://www.iyardiasp.com/8223thirddev");
        cr.username().setValue("propertyvista-ilsws");
        cr.password().number().setValue("55318");
        cr.serverName().setValue("aspdb04");
        cr.database().setValue("afqoml_live");
        cr.platform().setValue(PmcYardiCredential.Platform.SQL);
        return cr;
    }

    static void printUnits(List<ILSUnit> units) {
        System.out.print("Available Units:");
        for (ILSUnit ilsUnit : units) {
            Availability avail = ilsUnit.getAvailability();
            if (avail == null) {
                continue;
            }
            System.out.print(" #" + ilsUnit.getUnit().getInformation().get(0).getUnitID() + " (" + getDateAvail(avail) + "), ");
        }

    }

    static String getDateAvail(Availability avail) {
        String dateAvail = null;
        if (avail != null) {
            // use MadeReadyDate if set, otherwise VacateDate
            dateAvail = toDate(avail.getMadeReadyDate());
            if (dateAvail == null) {
                dateAvail = toDate(avail.getVacateDate());
            }
        }
        return dateAvail;
    }

    private static String toDate(VacateDate vacDate) {
        return vacDate.getYear() + "-" + vacDate.getMonth() + "-" + vacDate.getDay();
    }

    private static String toDate(MadeReadyDate rdyDate) {
        return rdyDate.getYear() + "-" + rdyDate.getMonth() + "-" + rdyDate.getDay();
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return bufferedReader.readLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
