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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

public class YardiGuestProcessor {

    public static final String PV_ORG_NAME = "PV";

    private final String agent, source;

    public YardiGuestProcessor(String agent, String source) {
        this.agent = agent;
        this.source = source;
    }

    public Prospect getProspect(Lease lease) {
        Persistence.ensureRetrieve(lease._applicant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);
        Prospect prospect = getProspect( //
                lease._applicant().customer().person().name(), //
                getCurrentAddress(lease._applicant().customer()), //
                lease.getPrimaryKey().toString(), // use primary key as third-party guest id
                lease.unit().building().propertyCode().getValue() //
        );
        return prospect;
    }

    public Prospect getAllLeaseParticipants(Lease lease) {
        Prospect prospect = getProspect(lease);
        prospect.getCustomers().getCustomer().addAll(getCoApplicants(lease));
        prospect.getCustomers().getCustomer().addAll(getGuarantors(lease));
        return prospect;
    }

    public Prospect getProspect(Name name, AddressStructured addr, String prospectId, String propertyId) {
        Prospect guest = new Prospect();
        guest.setLastUpdateDate(new Timestamp(new Date().getTime()));
        // add prospect
        Customers customers = new Customers();
        customers.getCustomer().add(getCustomer(CustomerInfo.PROSPECT, name, addr, propertyId, prospectId));
        guest.setCustomers(customers);

        return guest;
    }

    public List<Customer> getCoApplicants(Lease lease) {
        List<Customer> result = new ArrayList<Customer>();
        Persistence.ensureRetrieve(lease.leaseParticipants(), AttachLevel.Attached);
        for (LeaseParticipant<?> lp : lease.leaseParticipants()) {
            Persistence.ensureRetrieve(lp.leaseTermParticipants(), AttachLevel.Attached);
            LeaseTermParticipant<?> ltp = lp.leaseTermParticipants().iterator().next();
            Role role = ltp.role().getValue();
            if (Role.roommates().contains(role)) {
                PersonRelationship relation = ((LeaseTermTenant) ltp).relationship().getValue();
                Customer roommate = getCustomer( //
                        relation == PersonRelationship.Spouse ? CustomerInfo.SPOUSE : CustomerInfo.ROOMMATE, //
                        lp.customer().person().name(), //
                        getCurrentAddress(lp.customer()), //
                        lease.unit().building().propertyCode().getValue(), //
                        lp.getPrimaryKey().toString() // third-party room mate id
                );
                if (Role.CoApplicant.equals(role)) {
                    LeaseType leaseType = new LeaseType();
                    leaseType.setResponsibleForLease(true);
                    roommate.setLease(leaseType);
                }
                result.add(roommate);
            }
        }
        return result;
    }

    public List<Customer> getGuarantors(Lease lease) {
        List<Customer> result = new ArrayList<Customer>();
        Persistence.ensureRetrieve(lease.leaseParticipants(), AttachLevel.Attached);
        for (LeaseParticipant<?> lp : lease.leaseParticipants()) {
            Persistence.ensureRetrieve(lp.leaseTermParticipants(), AttachLevel.Attached);
            if (Role.Guarantor.equals(lp.leaseTermParticipants().iterator().next().role().getValue())) {
                result.add(getCustomer( //
                        CustomerInfo.GUARANTOR, //
                        lp.customer().person().name(), //
                        getCurrentAddress(lp.customer()), //
                        lease.unit().building().propertyCode().getValue(), //
                        lp.getPrimaryKey().toString() //
                ));
            }
        }
        return result;
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

    private Customer getCustomer(CustomerInfo type, Name name, AddressStructured addr, String propertyId, String thirdPartyId) {
        Customer customer = new Customer();
        customer.setType(type);
        customer.getIdentification().add(getThirdPartyId(thirdPartyId));
        customer.getIdentification().add(getPropertyId(propertyId));
        customer.setName(toNameType(name));
        customer.getAddress().add(getAddress(addr));
        return customer;
    }

    private AddressStructured getCurrentAddress(com.propertyvista.domain.tenant.Customer customer) {
        try {
            return ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningDraftOrFinal(customer, AttachLevel.Attached).version()
                    .currentAddress();
        } catch (NullPointerException e) {
            return null;
        }
    }

    private AddressType getAddress(AddressStructured as) {
        AddressType addr = null;
        if (as != null) {
            addr = new AddressType();
            addr.setCountry(as.country().name().getValue());
            if (addr.getCountry().equalsIgnoreCase("Canada")) {
                addr.setProvince(as.province().name().getValue());
            } else {
                addr.setState(as.province().name().getValue());
            }
            addr.setPostalCode(as.postalCode().getValue());
            addr.setCity(as.city().getValue());
            addr.setAddressLine1(as.streetNumber().getValue() + " " + as.streetName().getValue() + " " + as.streetType().getValue().name());
            addr.setAddressType(AddressInfo.CURRENT);
        }
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
        id.setIDValue(thirdPartyId == null ? PV_ORG_NAME + "-" + (new Date().getTime() / 1000) : thirdPartyId);
        id.setIDType("ThirdPartyID");
        id.setIDScopeType(IDScopeType.SENDER);
        id.setOrganizationName(PV_ORG_NAME);
        return id;
    }

    private Identification getPropertyId(String propertyId) {
        Identification id = new Identification();
        id.setIDValue(propertyId);
        id.setIDType("PropertyID");
        id.setIDScopeType(IDScopeType.SENDER);
        id.setOrganizationName(PV_ORG_NAME);
        return id;
    }

    private NameType toNameType(Name name) {
        NameType nameType = new NameType();
        nameType.setFirstName(name.firstName().getValue());
        nameType.setLastName(name.lastName().getValue());
        nameType.setMiddleName(name.middleName().getValue());
        nameType.setMaidenName(name.maidenName().getValue());
        nameType.setNamePrefix(name.namePrefix().isNull() ? null : name.namePrefix().getValue().toString());
        nameType.setNameSuffix(name.nameSuffix().getValue());
        return nameType;
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
