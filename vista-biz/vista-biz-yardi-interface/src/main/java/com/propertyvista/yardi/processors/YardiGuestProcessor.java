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
package com.propertyvista.yardi.processors;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.yardi.entity.guestcard40.AdditionalPreference;
import com.yardi.entity.guestcard40.AddressInfo;
import com.yardi.entity.guestcard40.AddressType;
import com.yardi.entity.guestcard40.Agent;
import com.yardi.entity.guestcard40.AgentName;
import com.yardi.entity.guestcard40.CurrencyRangeType;
import com.yardi.entity.guestcard40.Customer;
import com.yardi.entity.guestcard40.CustomerInfo;
import com.yardi.entity.guestcard40.CustomerPreferences;
import com.yardi.entity.guestcard40.Customers;
import com.yardi.entity.guestcard40.EventType;
import com.yardi.entity.guestcard40.EventTypes;
import com.yardi.entity.guestcard40.Events;
import com.yardi.entity.guestcard40.IDScopeType;
import com.yardi.entity.guestcard40.Identification;
import com.yardi.entity.guestcard40.LeaseType;
import com.yardi.entity.guestcard40.NameType;
import com.yardi.entity.guestcard40.NumericRangeType;
import com.yardi.entity.guestcard40.Prospect;
import com.yardi.entity.guestcard40.Quote;
import com.yardi.entity.guestcard40.Quotes;
import com.yardi.entity.guestcard40.UnitType;
import com.yardi.entity.mits.Information;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.tenant.lease.Lease;

public class YardiGuestProcessor {
    private final String agent, source;

    public YardiGuestProcessor(String agent, String source) {
        this.agent = agent;
        this.source = source;
    }

    public Prospect getProspect(Lease lease) {
        return getProspect( //
                lease._applicant().customer().person().name().firstName().getValue(), //
                lease._applicant().customer().person().name().lastName().getValue(), //
                getCurrentAddress(lease), //
                lease._applicant().getPrimaryKey().toString(), // use primary key as third-party guest id
                lease.unit().building().propertyCode().getValue() //
        );
    }

    public Prospect getProspect(String firstName, String lastName, AddressStructured addr, String prospectId, String propertyId) {
        Prospect guest = new Prospect();
        guest.setLastUpdateDate(new Timestamp(new Date().getTime()));
        // add prospect
        Customers customers = new Customers();
        customers.getCustomer().add(getCustomer(firstName, lastName, addr, propertyId, prospectId));
        guest.setCustomers(customers);

        return guest;
    }

    public YardiGuestProcessor clearPreferences(Prospect guest) {
        guest.setCustomerPreferences(new CustomerPreferences());
        return this;
    }

    public YardiGuestProcessor addUnit(Prospect guest, Information unitInfo) {
        CustomerPreferences pref = guest.getCustomerPreferences();
        if (pref == null) {
            guest.setCustomerPreferences(pref = new CustomerPreferences());
        }
        if (unitInfo != null) {
            NumericRangeType exactBeds = new NumericRangeType();
            int beds = unitInfo.getUnitBedrooms().intValue();
            exactBeds.setExact(beds);
            pref.setDesiredNumBedrooms(exactBeds);
            pref.setDesiredFloorplan(unitInfo.getUnitType());
            pref.getDesiredUnit().add(getUnitType(unitInfo));
        }

        return this;
    }

    public YardiGuestProcessor addLeaseTerm(Prospect guest, Date from, Date to) {
        Customer cust = guest.getCustomers().getCustomer().get(0);
        // lease from-to
        LeaseType ilsLease = new LeaseType();
        ilsLease.setLeaseFromDate(from);
        ilsLease.setLeaseToDate(to);
        cust.setLease(ilsLease);
        return this;
    }

    public YardiGuestProcessor addMoveInDate(Prospect guest, Date moveIn) {
        CustomerPreferences pref = guest.getCustomerPreferences();
        if (pref == null) {
            guest.setCustomerPreferences(pref = new CustomerPreferences());
        }
        pref.setTargetMoveInDate(moveIn);
        return this;
    }

    public YardiGuestProcessor addRentableItem(Prospect guest, String type) {
        AdditionalPreference item = new AdditionalPreference();
        item.setAdditionalPreferenceType(type);
        CustomerPreferences pref = guest.getCustomerPreferences();
        if (pref == null) {
            guest.setCustomerPreferences(pref = new CustomerPreferences());
        }
        pref.getCustomerAdditionalPreferences().add(item);

        return this;
    }

    public YardiGuestProcessor setEvent(Prospect guest, EventType event) {
        // add first contact event
        Events events = new Events();
        events.getEvent().add(event);
        guest.setEvents(events);

        return this;
    }

    public EventType getNewEvent(EventTypes type, boolean firstContact) {
        EventType appEvent = new EventType();
        appEvent.setEventType(type);
        appEvent.setEventDate(new Timestamp(new Date().getTime()));
        appEvent.setAgent(getAgent(agent));
        appEvent.setTransactionSource(source);
        appEvent.setFirstContact(firstContact);
        return appEvent;
    }

    private Customer getCustomer(String firstName, String lastName, AddressStructured addr, String propertyId, String thirdPartyId) {
        Customer customer = new Customer();
        customer.setType(CustomerInfo.PROSPECT);
        customer.getIdentification().add(getThirdPartyId(thirdPartyId));
        customer.getIdentification().add(getPropertyId(propertyId));
        customer.setName(getName(firstName, lastName));
        customer.getAddress().add(getAddress(addr));
        return customer;
    }

    private AddressStructured getCurrentAddress(Lease lease) {
        // TODO: VladL - update address retrieval logic as needed
        return lease._applicant().customer().personScreening().version().currentAddress();
    }

    private AddressType getAddress(AddressStructured as) {
        AddressType addr = new AddressType();
        addr.setCountry(as.county().getValue());
        addr.setState(as.province().name().getValue());
        addr.setPostalCode(as.postalCode().getValue());
        addr.setCity(as.city().getValue());
        addr.setAddressLine1(as.streetNumber().getValue() + " " + as.streetName().getValue() + " " + as.streetType().getValue().name());
        addr.setAddressType(AddressInfo.CURRENT);
        return addr;
    }

    private UnitType getUnitType(Information unitInfo) {
        UnitType unit = new UnitType();
        unit.setMarketingName(unitInfo.getUnitID());
        return unit;
    }

    public Quotes getRentQuote(BigDecimal amount) {
        Quote quote = new Quote();
        CurrencyRangeType value = new CurrencyRangeType();
        value.setExact(String.valueOf(amount));
        quote.setQuotedRent(value);
        Quotes quotes = new Quotes();
        quotes.getQuote().add(quote);
        return quotes;
    }

    private Identification getThirdPartyId(String thirdPartyId) {
        Identification id = new Identification();
        id.setIDValue(thirdPartyId == null ? "PV-" + (new Date().getTime() / 1000) : thirdPartyId);
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
        agentName.setFirstName("");
        agentName.setLastName(name);
        agent.setAgentName(agentName);
        return agent;
    }
}
