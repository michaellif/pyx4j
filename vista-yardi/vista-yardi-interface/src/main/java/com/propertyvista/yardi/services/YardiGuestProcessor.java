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

import java.math.BigInteger;
import java.util.Date;

import com.yardi.entity.guestcard40.Agent;
import com.yardi.entity.guestcard40.AgentName;
import com.yardi.entity.guestcard40.Customer;
import com.yardi.entity.guestcard40.CustomerInfo;
import com.yardi.entity.guestcard40.CustomerPreferences;
import com.yardi.entity.guestcard40.EventType;
import com.yardi.entity.guestcard40.EventTypes;
import com.yardi.entity.guestcard40.IDScopeType;
import com.yardi.entity.guestcard40.Identification;
import com.yardi.entity.guestcard40.NameType;
import com.yardi.entity.guestcard40.NumericRangeType;
import com.yardi.entity.guestcard40.Prospect;

public class YardiGuestProcessor {

    public Prospect getProspect(String name, Date moveIn, int beds, String agent, String source) {
        Prospect guest = new Prospect();
        guest.setLastUpdateDate(new Date());
        String[] names = name.split(" ", 2);
        guest.getCustomers().getCustomer().add(getCustomer(CustomerInfo.PROSPECT, names[0], names[1]));
        guest.setCustomerPreferences(getCustomerPreferences(moveIn, beds));
        guest.getEvents().getEvent().add(getApplicationEvent(agent, source));
        return guest;
    }

    private Customer getCustomer(CustomerInfo type, String first, String last) {
        Customer customer = new Customer();
        customer.setType(type);
        customer.getIdentification().add(getIdentification());
        customer.setName(getName(first, last));
        return customer;
    }

    private CustomerPreferences getCustomerPreferences(Date moveIn, int beds) {
        CustomerPreferences pref = new CustomerPreferences();
        pref.setTargetMoveInDate(moveIn);
        NumericRangeType exactBeds = new NumericRangeType();
        exactBeds.setExact(BigInteger.valueOf(beds));
        pref.setDesiredNumBedrooms(exactBeds);
        return pref;
    }

    private EventType getApplicationEvent(String agent, String source) {
        EventType appEvent = new EventType();
        appEvent.setEventType(EventTypes.APPLICATION);
        appEvent.setEventDate(new Date());
        appEvent.setAgent(getAgent(agent));
        appEvent.setTransactionSource(source);
        return appEvent;
    }

    private Identification getIdentification() {
        Identification id = new Identification();
        id.setIDValue("PV-" + new Date().getTime() / 1000);
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
