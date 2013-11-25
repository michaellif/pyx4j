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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.guestcard40.AdditionalPreference;
import com.yardi.entity.guestcard40.Customer;
import com.yardi.entity.guestcard40.CustomerInfo;
import com.yardi.entity.guestcard40.CustomerPreferences;
import com.yardi.entity.guestcard40.EventType;
import com.yardi.entity.guestcard40.EventTypes;
import com.yardi.entity.guestcard40.Identification;
import com.yardi.entity.guestcard40.LeadManagement;
import com.yardi.entity.guestcard40.MarketingAgent;
import com.yardi.entity.guestcard40.MarketingSource;
import com.yardi.entity.guestcard40.MarketingSources;
import com.yardi.entity.guestcard40.NameType;
import com.yardi.entity.guestcard40.PropertyMarketingSources;
import com.yardi.entity.guestcard40.Prospect;
import com.yardi.entity.guestcard40.Prospects;
import com.yardi.entity.guestcard40.RentableItemType;
import com.yardi.entity.guestcard40.RentableItems;
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

    private static YardiGuestManagementStub stub;

    private static PmcYardiCredential yc;

    static {
        ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(DatabaseType.HSQLDB));
        stub = ServerSideFactory.create(YardiGuestManagementStub.class);
        yc = getTestPmcYardiCredential();
    }

    private static final String AGENT = "Property Vista";

    private static final String SOURCE = "ILS";

    public static void main(String[] args) {

        try {

            // get marketing agents and sources
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
                        if (AGENT.equals(agent.getValue())) {
                            agentName = agent.getValue();
                        }
                    }
                    for (MarketingSource src : source.getPropertyRequiredFields().getSources().getSourceName()) {
                        if (SOURCE.equals(src.getValue())) {
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

            // retrieve guests
            LeadManagement guestActivity = stub.getGuestActivity(yc, yc.propertyListCodes().getValue());
            Map<String, Prospect> guests = new HashMap<String, Prospect>();
            for (Prospect guest : guestActivity.getProspects().getProspect()) {
                Customer c = guest.getCustomers().getCustomer().get(0);
                if (EnumSet.of(CustomerInfo.CURRENT_RESIDENT, CustomerInfo.FORMER_RESIDENT, CustomerInfo.FUTURE_RESIDENT, CustomerInfo.OTHER).contains(
                        c.getType())) {
                    // drop residents
                    continue;
                }
                NameType name = c.getName();
                guests.put(name.getFirstName() + " " + name.getLastName(), guest);
            }
            if (guests.size() > 0) {
                // update rentable items
                printIds(guests.keySet(), "Current Guests");
                String guestName = readLine("Update Guest: ");
                if (!StringUtils.isEmpty(guestName)) {
                    Prospect guest = guests.get(guestName);
//                    allocateUnit(guest);
                    updateRentableItems(guest);
                    executeEvents(guest);
                    System.out.println("Done - Exit.");
                    return;
                }
            }

            // create new guest
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
                printIds(units.keySet(), "Available Units");
            }

            ILSUnit ilsUnit = null;
            Date moveIn = null;
            String guestName = null;
            while (ilsUnit == null) {
                String unitNo = readLine("Enter Unit #: ");
                if (StringUtils.isEmpty(unitNo)) {
                    return;
                }
                ilsUnit = units.get(unitNo);
                if (ilsUnit == null) {
                    System.out.println("Unit not found: " + unitNo + " - please try again...");
                    continue;
                }

                String moveInStr = readLine("MoveIn (yyyy-mm-dd): ");
                if (StringUtils.isEmpty(moveInStr)) {
                    return;
                }
                moveIn = new SimpleDateFormat("yyyy-MM-dd").parse(moveInStr);
                if (moveIn == null) {
                    System.out.println("Invalid date: " + moveInStr + " - please try again...");
                    continue;
                }

                guestName = readLine("Guest Name: ");
                if (StringUtils.isEmpty(guestName)) {
                    return;
                }
                if (guestName == null || guestName.trim().split(" ").length != 2) {
                    System.out.println("Invalid name: " + guestName + " - please try again...");
                    continue;
                }
            }
            Prospect guest = new YardiGuestProcessor().getProspect(guestName, ilsUnit, moveIn, agentName, sourceName);
            updateGuest(guest);
            // allocate unit
            holdUnit(guest, ilsUnit);

            System.out.println("Guest Import Complete.");

            // Import Application
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    static void updateGuest(Prospect guest) {
        LeadManagement lead = new LeadManagement();
        lead.setProspects(new Prospects());
        lead.getProspects().getProspect().add(guest);
        try {
            stub.importGuestInfo(yc, lead);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    static PmcYardiCredential getTestPmcYardiCredential() {
        PmcYardiCredential cr = EntityFactory.create(PmcYardiCredential.class);
        cr.propertyListCodes().setValue("prvista2");
        cr.serviceURLBase().setValue("https://www.iyardiasp.com/8223third_17");
        cr.username().setValue("propertyvistadb");
        cr.password().number().setValue("52673");
        cr.serverName().setValue("aspdb04");
        cr.database().setValue("afqoml_live");
        cr.platform().setValue(PmcYardiCredential.Platform.SQL);
        return cr;
    }

    static void allocateUnit(Prospect guest) {
        Map<String, ILSUnit> units = new HashMap<String, ILSUnit>();
        PhysicalProperty property;
        try {
            property = stub.getPropertyMarketingInfo(yc, yc.propertyListCodes().getValue());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        for (ILSUnit ilsUnit : property.getProperty().get(0).getILSUnit()) {
            Availability avail = ilsUnit.getAvailability();
            if (avail == null) {
                continue;
            }
            units.put(ilsUnit.getUnit().getInformation().get(0).getUnitID(), ilsUnit);
        }
        printIds(units.keySet(), "Available Units");

        ILSUnit ilsUnit = null;
        String unitNo = readLine("Enter Unit #: ");
        if ((ilsUnit = units.get(unitNo)) != null) {
            holdUnit(guest, ilsUnit);
        }
    }

    static void holdUnit(Prospect guest, ILSUnit ilsUnit) {
        EventType event = new YardiGuestProcessor().getNewEvent(AGENT, SOURCE, EventTypes.HOLD, false);
        Identification holdId = new Identification();
        holdId.setIDType(ilsUnit.getUnit().getInformation().get(0).getUnitID());
        holdId.setIDValue("0");
        event.setEventID(holdId);

        guest.getEvents().getEvent().clear();
        guest.getEvents().getEvent().add(event);
        guest.getCustomers().getCustomer().get(0).setType(CustomerInfo.PROSPECT);

        updateGuest(guest);
    }

    static void updateRentableItems(Prospect guest) {
        // print already selected items
        List<String> selectedItems = new ArrayList<String>();
        for (AdditionalPreference item : guest.getCustomerPreferences().getCustomerAdditionalPreferences()) {
            selectedItems.add(item.getAdditionalPreferenceType());
        }
        printIds(selectedItems, "Selected Items");
        // print available (unused) items for select
        List<String> availItems = new ArrayList<String>();
        try {
            RentableItems items = stub.getRentableItems(yc, yc.propertyListCodes().getValue());
            for (RentableItemType type : items.getItemType()) {
                availItems.add(type.getCode());
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        if (availItems.size() == 0) {
            System.out.println("Rentable Items not available. Exit.");
            return;
        }
        printIds(availItems, "Available Items");
        // add selected item
        String items = readLine("Add Items: ");
        if (!StringUtils.isEmpty(items)) {
            guest.setCustomerPreferences(new CustomerPreferences());
            String[] itemNames = items.split(",");
            for (String itemName : itemNames) {
                AdditionalPreference item = new AdditionalPreference();
                item.setAdditionalPreferenceType(itemName.trim());
                item.setValue("02");
                guest.getCustomerPreferences().getCustomerAdditionalPreferences().add(item);
                item = new AdditionalPreference();
                item.setAdditionalPreferenceType(itemName.trim());
                item.setValue("03");
                guest.getCustomerPreferences().getCustomerAdditionalPreferences().add(item);
            }
            EventType event = new YardiGuestProcessor().getNewEvent(AGENT, SOURCE, EventTypes.OTHER, false);
            guest.getEvents().getEvent().clear();
            guest.getEvents().getEvent().add(event);
            guest.getCustomers().getCustomer().get(0).setType(CustomerInfo.PROSPECT);
            updateGuest(guest);
        }
    }

    static void executeEvents(Prospect guest) {
        // execute event
        printIds(EnumSet.of(EventTypes.APPLICATION, EventTypes.APPROVE, EventTypes.LEASE_SIGN), "Events");
        EventTypes type = null;
        do {
            String typeStr = readLine("Execute Event: ");
            if (StringUtils.isEmpty(typeStr)) {
                break;
            }
            try {
                type = EventTypes.valueOf(typeStr);
            } catch (Exception ignore) {
                // ignore
            }
            if (type != null) {
                EventType event = new YardiGuestProcessor().getNewEvent(AGENT, SOURCE, type, false);
                switch (type) {
                case LEASE_SIGN:
                    event.setQuotes(new YardiGuestProcessor().getNewQuotes(900));
                    break;
                default:
                    break;
                }
                guest.getEvents().getEvent().clear();
                guest.getEvents().getEvent().add(event);
                guest.getCustomers().getCustomer().get(0).setType(CustomerInfo.PROSPECT);
                updateGuest(guest);
            }
        } while (type != null);
    }

    static void printIds(Collection<?> ids, String prompt) {
        StringBuilder result = new StringBuilder();
        for (Object id : ids) {
            if (result.length() > 0) {
                result.append(", ");
            }
            if (id instanceof Enum) {
                result.append(((Enum<?>) id).name());
            } else {
                result.append(id);
            }
        }
        System.out.println(prompt + ": " + result);
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
