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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.yardi.entity.guestcard40.PhoneInfo;
import com.yardi.entity.guestcard40.PhoneType;
import com.yardi.entity.guestcard40.Prospect;
import com.yardi.entity.guestcard40.Quote;
import com.yardi.entity.guestcard40.Quotes;
import com.yardi.entity.guestcard40.UnitType;
import com.yardi.entity.leaseapp30.AccountingData;
import com.yardi.entity.leaseapp30.Charge;
import com.yardi.entity.leaseapp30.ChargeSet;
import com.yardi.entity.leaseapp30.ChargeType;
import com.yardi.entity.leaseapp30.Frequency;
import com.yardi.entity.leaseapp30.LALease;
import com.yardi.entity.leaseapp30.LeaseApplication;
import com.yardi.entity.leaseapp30.PropertyType;
import com.yardi.entity.leaseapp30.ResidentType;
import com.yardi.entity.leaseapp30.Tenant;
import com.yardi.entity.mits.Information;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.domain.ref.ISOProvince;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Lease;
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
        addPhone(prospect, PhoneInfo.HOME, lease._applicant().customer().person().homePhone().getValue());
        addPhone(prospect, PhoneInfo.OFFICE, lease._applicant().customer().person().workPhone().getValue());
        addPhone(prospect, PhoneInfo.CELL, lease._applicant().customer().person().mobilePhone().getValue());
        addEmail(prospect, lease._applicant().customer().person().email().getValue());

        return prospect;
    }

    public Prospect getAllLeaseParticipants(Lease lease) {
        Prospect prospect = getProspect(lease);
        prospect.getCustomers().getCustomer().addAll(getCoApplicants(lease));
        prospect.getCustomers().getCustomer().addAll(getGuarantors(lease));
        return prospect;
    }

    public Prospect getProspect(Name name, InternationalAddress addr, String prospectId, String propertyId) {
        Prospect guest = new Prospect();
        guest.setLastUpdateDate(new Timestamp(new Date().getTime()));
        // add prospect
        Customers customers = new Customers();
        customers.getCustomer().add(getCustomer(CustomerInfo.PROSPECT, name, addr, propertyId, prospectId));
        guest.setCustomers(customers);

        return guest;
    }

    public void addPhone(Prospect guest, PhoneInfo type, String number) {
        if (number == null) {
            return;
        }
        Customer cust = guest.getCustomers().getCustomer().get(0);
        assert cust != null : "Prospect not initialized";

        PhoneType phone = new PhoneType();
        phone.setPhoneType(type);
        phone.setPhoneNumber(number);
        // use type.ordinal as list index to prevent duplicate types
        cust.getPhone().set(type.ordinal(), phone);
    }

    public void addEmail(Prospect guest, String email) {
        if (email == null) {
            return;
        }
        Customer cust = guest.getCustomers().getCustomer().get(0);
        assert cust != null : "Prospect not initialized";

        cust.setEmail(email);
    }

    public List<Customer> getCoApplicants(Lease lease) {
        List<Customer> result = new ArrayList<Customer>();

        Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.Attached);
        for (LeaseTermParticipant<?> ltp : lease.currentTerm().version().tenants()) {
            Persistence.ensureRetrieve(ltp.leaseParticipant(), AttachLevel.Attached);
            if (ltp.leaseParticipant().getPrimaryKey().equals(lease._applicant().getPrimaryKey())) {
                continue;
            }
            Role role = ltp.role().getValue();
            if (Role.roommates().contains(role)) {
                PersonRelationship relation = ((LeaseTermTenant) ltp).relationship().getValue();
                Customer roommate = getCustomer( //
                        PersonRelationship.Spouse.equals(relation) ? CustomerInfo.SPOUSE : CustomerInfo.ROOMMATE, //
                        ltp.leaseParticipant().customer().person().name(), //
                        getCurrentAddress(ltp.leaseParticipant().customer()), //
                        lease.unit().building().propertyCode().getValue(), //
                        ltp.leaseParticipant().getPrimaryKey().toString() // third-party room mate id
                );
                // check if the tenant is responsible for lease
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
        Persistence.ensureRetrieve(lease.currentTerm().version().guarantors(), AttachLevel.Attached);
        for (LeaseTermParticipant<?> ltp : lease.currentTerm().version().guarantors()) {
            Persistence.ensureRetrieve(ltp.leaseParticipant(), AttachLevel.Attached);
            result.add(getCustomer( //
                    CustomerInfo.GUARANTOR, //
                    ltp.leaseParticipant().customer().person().name(), //
                    getCurrentAddress(ltp.leaseParticipant().customer()), //
                    lease.unit().building().propertyCode().getValue(), //
                    ltp.leaseParticipant().getPrimaryKey().toString() //
            ));
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

    public YardiGuestProcessor addRentableItem(Prospect guest, String type, String itemCode) {
        AdditionalPreference item = new AdditionalPreference();
        item.setAdditionalPreferenceType(type);
        item.setValue(itemCode);
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

    private Customer getCustomer(CustomerInfo type, Name name, InternationalAddress addr, String propertyId, String thirdPartyId) {
        Customer customer = new Customer();
        customer.setType(type);
        customer.getIdentification().add(getThirdPartyId(thirdPartyId));
        customer.getIdentification().add(getPropertyId(propertyId));
        customer.setName(toNameType(name));
        customer.getAddress().add(getAddress(addr));
        // init phone holder list - use PhoneInfo.ordinal as index to prevent duplicate types
        customer.getPhone().addAll(Arrays.asList(new PhoneType[PhoneInfo.values().length]));
        return customer;
    }

    private InternationalAddress getCurrentAddress(com.propertyvista.domain.tenant.Customer customer) {
        try {
            return ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningDraftOrFinal(customer, AttachLevel.Attached).version()
                    .currentAddress();
        } catch (NullPointerException e) {
            return null;
        }
    }

    private AddressType getAddress(InternationalAddress ia) {
        AddressType addr = null;
        if (ia != null && !ia.isNull()) {
            addr = new AddressType();
            addr.setCountyName(ia.country().getValue().name);
            if (ISOCountry.UnitedStates.equals(ia.country().getValue())) {
                addr.setCountry(ISOCountry.UnitedStates.iso2);
                addr.setState(getStateCode(ia.province().getValue(), ISOCountry.UnitedStates));
            } else {
                addr.setProvince(ia.province().getValue());
            }
            addr.setPostalCode(ia.postalCode().getValue());
            addr.setCity(ia.city().getValue());
            addr.setAddressLine1(ia.streetNumber().getStringView() + " " + ia.streetName().getStringView());
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

    public LeaseApplication getLeaseApplication(Lease lease, String tId, List<Deposit> depositCharges) {
        Persistence.ensureRetrieve(lease._applicant(), AttachLevel.Attached);

        LeaseApplication app = new LeaseApplication();

        // tenant
        Tenant tenant = new Tenant();
        tenant.setResidentType(ResidentType.INDIVIDUAL);
        com.yardi.entity.leaseapp30.Identification id = new com.yardi.entity.leaseapp30.Identification();
        id.setIDType("tenant"); // TenantID
        id.setIDValue(tId);
        id.setIDType("thirdparty");
        id.setIDValue(lease.getPrimaryKey().toString());
        tenant.getIdentification().add(id);
        com.yardi.entity.leaseapp30.Name name = new com.yardi.entity.leaseapp30.Name();
        name.setFirstName(lease._applicant().customer().person().name().firstName().getValue());
        name.setLastName(lease._applicant().customer().person().name().lastName().getValue());
        tenant.setName(name);
        app.getTenant().add(tenant);

        //charges
        AccountingData charges = new AccountingData();
        ChargeSet chargeSet = new ChargeSet();
        chargeSet.setFrequency(Frequency.ONE_TIME);
        try {
            chargeSet.setStart(new SimpleDateFormat("yyyy-MM-dd").parse("0001-01-01"));
            chargeSet.setEnd(new SimpleDateFormat("yyyy-MM-dd").parse("0001-01-01"));
        } catch (ParseException e) {
            throw new Error(e);
        }
        for (Deposit deposit : depositCharges) {
            if (deposit.chargeCode().yardiChargeCodes().size() > 0) {
                chargeSet.getCharge().add( //
                        getAppFee( //
                                deposit.amount().getValue(), //
                                deposit.chargeCode().yardiChargeCodes().get(0).yardiChargeCode().getValue(), //
                                deposit.description().getValue() //
                        ));
            }
        }
        charges.getChargeSet().add(chargeSet);
        tenant.setAccountingData(charges);

        // lease
        LALease laLease = new LALease();
        laLease.getIdentification().add(tenant.getIdentification().get(0));
        PropertyType property = new PropertyType();
        com.yardi.entity.leaseapp30.Identification propId = new com.yardi.entity.leaseapp30.Identification();
        propId.setIDValue(lease.unit().building().propertyCode().getValue());
        property.getIdentification().add(propId);
        property.setMarketingName("");
        laLease.setProperty(property);
        app.getLALease().add(laLease);

        return app;
    }

    public List<Charge> getApplicationCharges(LeaseApplication la) {
        List<Charge> charges = new ArrayList<>();
        for (LALease lease : la.getLALease()) {
            if (lease == null || lease.getAccountingData() == null) {
                continue;
            }
            for (ChargeSet chargeSet : lease.getAccountingData().getChargeSet()) {
                charges.addAll(chargeSet.getCharge());
            }
        }
        return charges;
    }

    private Charge getAppFee(BigDecimal amount, String chargeCode, String description) {
        Charge charge = new Charge();
        com.yardi.entity.leaseapp30.Identification chargeId = new com.yardi.entity.leaseapp30.Identification();
        charge.setChargeType(ChargeType.APPLICATION_FEE);
        charge.setLabel(chargeCode);
        chargeId.setIDValue("0");
        chargeId.setOrganizationName(description);
        charge.getIdentification().add(chargeId);
        charge.setAmount(amount.toPlainString());
        return charge;
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

    private String getStateCode(String stateName, ISOCountry country) {
        ISOProvince prov = ISOProvince.forName(stateName, country);
        return prov == null ? null : prov.code;
    }
}
