/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.sql.Timestamp;
import java.util.Date;

import com.yardi.entity.guestcard40.AddressType;
import com.yardi.entity.guestcard40.Agent;
import com.yardi.entity.guestcard40.AgentName;
import com.yardi.entity.guestcard40.Customer;
import com.yardi.entity.guestcard40.CustomerInfo;
import com.yardi.entity.guestcard40.CustomerPreferences;
import com.yardi.entity.guestcard40.Customers;
import com.yardi.entity.guestcard40.EventType;
import com.yardi.entity.guestcard40.EventTypes;
import com.yardi.entity.guestcard40.Events;
import com.yardi.entity.guestcard40.IDScopeType;
import com.yardi.entity.guestcard40.Identification;
import com.yardi.entity.guestcard40.NameType;
import com.yardi.entity.guestcard40.NumericRangeType;
import com.yardi.entity.guestcard40.Prospect;
import com.yardi.entity.guestcard40.UnitType;
import com.yardi.entity.ils.ILSUnit;
import com.yardi.entity.mits.Information;
import com.yardi.entity.mits.Unit;

public class YardiGuestProcessor {

    public Prospect getProspect(String name, ILSUnit ilsUnit, Date moveIn, String agent, String source) {
        Unit unit = ilsUnit.getUnit();

        Prospect guest = new Prospect();
        guest.setLastUpdateDate(new Timestamp(new Date().getTime()));
        // add prospect
        String[] names = name.split(" ", 2);
        Customers customers = new Customers();
        customers.getCustomer().add(getCustomer(CustomerInfo.PROSPECT, names[0], names[1], unit.getPropertyPrimaryID()));
        guest.setCustomers(customers);
        guest.setCustomerPreferences(getCustomerPreferences(moveIn, unit.getInformation().size() > 0 ? unit.getInformation().get(0) : null));
        // add first contact event
        Events events = new Events();
        events.getEvent().add(getNewEvent(agent, source, EventTypes.EMAIL, true));
        guest.setEvents(events);

        return guest;
    }

    private Customer getCustomer(CustomerInfo type, String firstName, String lastName, String propertyId) {
        Customer customer = new Customer();
        customer.setType(type);
        customer.getIdentification().add(getGuestId());
        customer.getIdentification().add(getPropertyId(propertyId));
        customer.setName(getName(firstName, lastName));
        customer.getAddress().add(getAddress());
        return customer;
    }

    private AddressType getAddress() {
        AddressType addr = new AddressType();
        addr.setCountry("US");
        addr.setState("CA");
        addr.setPostalCode("90123");
        addr.setCity("Hometown");
        addr.setAddressLine1("123 Main St");
        return addr;
    }

    private CustomerPreferences getCustomerPreferences(Date moveIn, Information unitInfo) {
        CustomerPreferences pref = new CustomerPreferences();
        pref.setTargetMoveInDate(moveIn);
        if (unitInfo != null) {
            NumericRangeType exactBeds = new NumericRangeType();
            int beds = unitInfo.getUnitBedrooms().intValue();
            exactBeds.setExact(beds);
            pref.setDesiredNumBedrooms(exactBeds);
            pref.setDesiredFloorplan(unitInfo.getUnitType());
            pref.getDesiredUnit().add(getUnitType(unitInfo));
        }
        return pref;
    }

    private UnitType getUnitType(Information unitInfo) {
        UnitType unit = new UnitType();
        unit.setMarketingName(unitInfo.getUnitID());
        return unit;
    }

    private EventType getNewEvent(String agent, String source, EventTypes type, boolean firstContact) {
        EventType appEvent = new EventType();
        appEvent.setEventType(type);
        appEvent.setEventDate(new Timestamp(new Date().getTime()));
        appEvent.setAgent(getAgent(agent));
        appEvent.setTransactionSource(source);
        appEvent.setFirstContact(firstContact);
        return appEvent;
    }

    private Identification getGuestId() {
        Identification id = new Identification();
        id.setIDValue("PV-" + new Date().getTime() / 1000);
        id.setIDType("ThirdPartyID");
        id.setIDScopeType(IDScopeType.SENDER);
        id.setOrganizationName("PV");
        return id;
    }

    private Identification getPropertyId(String propertyId) {
        Identification id = new Identification();
        id.setIDValue(propertyId);
        id.setIDType("PropertyID");
        id.setIDScopeType(IDScopeType.SENDER);
        id.setOrganizationName("PV");
        return id;
    }

    private NameType getName(String first, String last) {
        NameType name = new NameType();
        name.setFirstName(first);
        name.setLastName(last);
        return name;
    }

    private Agent getAgent(String name) {
        Agent agent = new Agent();
        AgentName agentName = new AgentName();
        agentName.setLastName(name);
        agent.setAgentName(agentName);
        return agent;
    }
}
