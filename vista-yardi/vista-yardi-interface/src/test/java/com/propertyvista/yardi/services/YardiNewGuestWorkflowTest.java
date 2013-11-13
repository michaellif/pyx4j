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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.guestcard40.LeadManagement;
import com.yardi.entity.guestcard40.MarketingAgent;
import com.yardi.entity.guestcard40.MarketingSource;
import com.yardi.entity.guestcard40.MarketingSources;
import com.yardi.entity.guestcard40.PropertyMarketingSources;
import com.yardi.entity.guestcard40.Prospects;
import com.yardi.entity.ils.Availability;
import com.yardi.entity.ils.ILSUnit;
import com.yardi.entity.ils.MadeReadyDate;
import com.yardi.entity.ils.PhysicalProperty;
import com.yardi.entity.ils.VacateDate;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

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

    private final static boolean mockMode = false;

    public static void main(String[] args) {
        ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(DatabaseType.HSQLDB));

        PmcYardiCredential yc = getTestPmcYardiCredential();
        try {
            YardiGuestManagementStub stub = ServerSideFactory.create(YardiGuestManagementStub.class);
            MarketingSources sources = null;
            if (mockMode) {
                String xml = getMarketingSourcesXml();
                sources = MarshallUtil.unmarshal(MarketingSources.class, xml);
            } else {
                sources = stub.getYardiMarketingSources(yc, yc.propertyListCodes().getValue());
            }

            String agentName = null;
            String sourceName = null;
            for (PropertyMarketingSources source : sources.getProperty()) {
                if (yc.propertyListCodes().getValue().equals(source.getPropertyCode())) {
                    for (MarketingAgent agent : source.getPropertyRequiredFields().getAgents().getAgentName()) {
                        if ("Property Vista".equals(agent.getValue())) {
                            agentName = agent.getValue();
                        }
                    }
                    for (MarketingSource src : source.getPropertyRequiredFields().getSources().getSourceName()) {
                        if ("ILS".equals(src.getValue())) {
                            sourceName = src.getValue();
                        }
                    }
                }
            }

            if (agentName == null) {
                System.out.println("Marketing Agent 'Property Vista' is not configured. Exit.");
                return;
            }

            if (sourceName == null) {
                System.out.println("Marketing Source 'ILS' is not configured. Exit.");
                return;
            }

            Map<String, ILSUnit> units = new HashMap<String, ILSUnit>();
            if (mockMode) {
                System.out.println("Available Units: #156 (2008-1-10),  #158 (2008-1-11),  #159 (2011-3-21), ");
            } else {
                PhysicalProperty property = stub.getPropertyMarketingInfo(yc, yc.propertyListCodes().getValue());
                log.info("PhysicalProperty: {}", property.getProperty().get(0).getPropertyID().getIdentification().getPrimaryID());
                for (ILSUnit ilsUnit : property.getProperty().get(0).getILSUnit()) {
                    Availability avail = ilsUnit.getAvailability();
                    if (avail == null) {
                        continue;
                    }
                    units.put(ilsUnit.getUnit().getInformation().get(0).getUnitID(), ilsUnit);
                }
                printUnits(units.keySet());
            }

            ILSUnit ilsUnit = null;
            Date moveIn = null;
            String guestName = null;
            while (ilsUnit == null) {
                String unitNo = readLine("Enter Unit #: ");
                ilsUnit = units.get(unitNo);
                if (ilsUnit == null) {
                    System.out.println("Unit not found: " + unitNo + " - please try again...");
                    continue;
                }

                String moveInStr = readLine("MoveIn (yyyy-mm-dd): ");
                moveIn = new SimpleDateFormat("yyyy-mm-dd").parse(moveInStr);
                if (moveIn == null) {
                    System.out.println("Invalid date: " + moveInStr + " - please try again...");
                    continue;
                }

                guestName = readLine("Guest Name: ");
                if (guestName == null || guestName.trim().split(" ").length != 2) {
                    System.out.println("Invalid name: " + guestName + " - please try again...");
                    continue;
                }
            }
            Prospects guestInfo = new Prospects();
            int beds = ilsUnit.getUnit().getInformation().get(0).getUnitBedrooms().intValue();
            guestInfo.getProspect().add(new YardiGuestProcessor().getProspect(guestName, moveIn, beds, agentName, sourceName));
            LeadManagement lead = new LeadManagement();
            lead.setProspects(guestInfo);
            stub.importGuestInfo(yc, lead);

            System.out.println("Guest Import Complete.");

            // Import Application
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

    static void printUnits(Collection<String> units) {
        System.out.print("Available Units:");
        for (String unit : units) {
            System.out.print(" #" + unit);
        }
        System.out.println();
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

    private static String getMarketingSourcesXml() throws IOException {
        Class<?> refClass = YardiILSServiceClientTest.class;
        String rcPath = refClass.getPackage().getName().replaceAll("\\.", "/") + "/AgentSources-Properties.xml";
        InputStream is = refClass.getClassLoader().getResourceAsStream(rcPath);
        return IOUtils.toString(is);
    }

}
