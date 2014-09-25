/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 18, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.stub.impl;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yardi.entity.guestcard40.AdditionalPreference;
import com.yardi.entity.guestcard40.Agent;
import com.yardi.entity.guestcard40.AgentName;
import com.yardi.entity.guestcard40.Agents;
import com.yardi.entity.guestcard40.AttachmentTypesAndChargeCodes;
import com.yardi.entity.guestcard40.ChargeCode;
import com.yardi.entity.guestcard40.ChargeCodes;
import com.yardi.entity.guestcard40.CurrencyRangeType;
import com.yardi.entity.guestcard40.Customer;
import com.yardi.entity.guestcard40.CustomerInfo;
import com.yardi.entity.guestcard40.Customers;
import com.yardi.entity.guestcard40.EventType;
import com.yardi.entity.guestcard40.EventTypes;
import com.yardi.entity.guestcard40.Events;
import com.yardi.entity.guestcard40.LeadManagement;
import com.yardi.entity.guestcard40.MarketingAgent;
import com.yardi.entity.guestcard40.MarketingSource;
import com.yardi.entity.guestcard40.MarketingSources;
import com.yardi.entity.guestcard40.NameType;
import com.yardi.entity.guestcard40.PropertyMarketingSources;
import com.yardi.entity.guestcard40.PropertyRequiredFields;
import com.yardi.entity.guestcard40.Prospect;
import com.yardi.entity.guestcard40.Prospects;
import com.yardi.entity.guestcard40.Quote;
import com.yardi.entity.guestcard40.Quotes;
import com.yardi.entity.guestcard40.RentableItemType;
import com.yardi.entity.guestcard40.RentableItems;
import com.yardi.entity.guestcard40.Sources;
import com.yardi.entity.ils.Amount;
import com.yardi.entity.ils.DepositType;
import com.yardi.entity.ils.Floorplan;
import com.yardi.entity.ils.ILSUnit;
import com.yardi.entity.ils.MarketRentRange;
import com.yardi.entity.ils.PhysicalProperty;
import com.yardi.entity.ils.Property;
import com.yardi.entity.ils.RoomType;
import com.yardi.entity.leaseapp30.AccountingData;
import com.yardi.entity.leaseapp30.Charge;
import com.yardi.entity.leaseapp30.ChargeSet;
import com.yardi.entity.leaseapp30.LALease;
import com.yardi.entity.leaseapp30.LeaseApplication;
import com.yardi.entity.leaseapp30.Tenant;
import com.yardi.entity.mits.Address;
import com.yardi.entity.mits.Addressinfo;
import com.yardi.entity.mits.Identification;
import com.yardi.entity.mits.Information;
import com.yardi.entity.mits.PropertyIDType;
import com.yardi.entity.mits.Unit;
import com.yardi.entity.mits.Unitleasestatusinfo;
import com.yardi.entity.mits.Unitoccpstatusinfo;

import com.pyx4j.config.server.SystemDateManager;

import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.yardi.beans.Messages;
import com.propertyvista.yardi.mock.model.YardiMock;
import com.propertyvista.yardi.mock.model.domain.YardiAddress;
import com.propertyvista.yardi.mock.model.domain.YardiBuilding;
import com.propertyvista.yardi.mock.model.domain.YardiFee;
import com.propertyvista.yardi.mock.model.domain.YardiFloorplan;
import com.propertyvista.yardi.mock.model.domain.YardiGuestEvent;
import com.propertyvista.yardi.mock.model.domain.YardiLease;
import com.propertyvista.yardi.mock.model.domain.YardiRentableItem;
import com.propertyvista.yardi.mock.model.domain.YardiTenant;
import com.propertyvista.yardi.mock.model.domain.YardiUnit;
import com.propertyvista.yardi.mock.model.manager.YardiConfigurationManager;
import com.propertyvista.yardi.mock.model.manager.YardiGuestManager;
import com.propertyvista.yardi.mock.model.manager.YardiGuestManager.ApplicationBuilder;
import com.propertyvista.yardi.mock.model.manager.YardiGuestManager.GuestBuilder;
import com.propertyvista.yardi.mock.model.manager.impl.YardiMockModelUtils;
import com.propertyvista.yardi.services.YardiGuestManagementService;
import com.propertyvista.yardi.services.YardiHandledErrorMessages;
import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;

public class YardiMockILSGuestCardStubImpl extends YardiMockStubBase implements YardiILSGuestCardStub {

    @Override
    public AttachmentTypesAndChargeCodes getConfiguredAttachmentsAndCharges(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        // return attachments and charge codes configured for interface vendor
        AttachmentTypesAndChargeCodes chargeCodes = new AttachmentTypesAndChargeCodes();
        chargeCodes.setChargeCodes(new ChargeCodes());
        for (String chargeCode : YardiMock.server().getManager(YardiConfigurationManager.class).getChargeCodes(YardiILSGuestCardStub.class)) {
            chargeCodes.getChargeCodes().getChargeCode().add(getChargeCode(chargeCode));
        }
        return chargeCodes;
    }

    @Override
    public RentableItems getRentableItems(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        RentableItems rentableItems = new RentableItems();
        YardiBuilding building = getYardiBuilding(propertyId);
        if (building == null) {
            Messages.throwYardiResponseException(YardiHandledErrorMessages.errorMessage_NoAccess + ":" + propertyId);
        }
        for (YardiRentableItem item : building.rentableItems()) {
            rentableItems.getItemType().add(getRentableItemType(item));
        }
        return rentableItems;
    }

    @Override
    public PhysicalProperty getPropertyMarketingInfo(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        YardiBuilding building = getYardiBuilding(propertyId);
        if (building == null) {
            Messages.throwYardiResponseException(YardiHandledErrorMessages.errorMessage_NoAccess + ":" + propertyId);
        }
        PhysicalProperty marketingInfo = new PhysicalProperty();
        marketingInfo.getProperty().add(getProperty(building));
        return marketingInfo;
    }

    @Override
    public MarketingSources getYardiMarketingSources(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        MarketingSources marketingSources = new MarketingSources();
        YardiBuilding building = getYardiBuilding(propertyId);
        if (building == null) {
            Messages.throwYardiResponseException(YardiHandledErrorMessages.errorMessage_NoAccess + ":" + propertyId);
        }
        // building id
        PropertyMarketingSources pms = new PropertyMarketingSources();
        pms.setPropertyCode(building.buildingId().getValue());
        pms.setPropertyRequiredFields(new PropertyRequiredFields());
        // agents
        pms.getPropertyRequiredFields().setAgents(new Agents());
        MarketingAgent agent = new MarketingAgent();
        agent.setValue(YardiGuestManagementService.ILS_AGENT);
        pms.getPropertyRequiredFields().getAgents().getAgentName().add(agent);
        // sources
        pms.getPropertyRequiredFields().setSources(new Sources());
        MarketingSource source = new MarketingSource();
        source.setValue(YardiGuestManagementService.ILS_SOURCE);
        pms.getPropertyRequiredFields().getSources().getSourceName().add(source);

        marketingSources.getProperty().add(pms);
        return marketingSources;
    }

    @Override
    public LeadManagement getGuestActivity(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LeadManagement findGuest(PmcYardiCredential yc, String propertyId, String guestId) throws YardiServiceException, RemoteException {
        // find building
        YardiBuilding building = getYardiBuilding(propertyId);
        if (building == null) {
            Messages.throwYardiResponseException(YardiHandledErrorMessages.errorMessage_NoAccess + ":" + propertyId);
        }
        // find guest
        YardiTenant guest = null;
        YardiLease lease = null;
        for (YardiLease _lease : building.leases()) {
            if ((guest = YardiMockModelUtils.findGuest(_lease, guestId)) != null) {
                lease = _lease;
                break;
            }
        }
        if (guest == null) {
            Messages.throwYardiResponseException(YardiHandledErrorMessages.errorMessage_GuestNotFound + ":" + guestId);
        }
        // build result
        LeadManagement guestInfo = new LeadManagement();
        guestInfo.setProspects(new Prospects());
        Prospect guestCard = new Prospect();
        guestInfo.getProspects().getProspect().add(guestCard);
        // customers
        guestCard.setCustomers(new Customers());
        for (YardiTenant t : lease.tenants()) {
            guestCard.getCustomers().getCustomer().add(toCustomer(t, building, lease));
        }
        // events
        guestCard.setEvents(new Events());
        for (YardiGuestEvent e : lease.application().events()) {
            guestCard.getEvents().getEvent().add(toEvent(e));
        }
        // TODO - customer preferences (unit, rentable items)?
        return guestInfo;
    }

    @Override
    public void importGuestInfo(PmcYardiCredential yc, LeadManagement leadInfo) throws YardiServiceException, RemoteException {
        YardiGuestManager guestManager = YardiMock.server().getManager(YardiGuestManager.class);
        for (Prospect guestCard : leadInfo.getProspects().getProspect()) {
            YardiBuilding building = null;
            ApplicationBuilder leaseApp = null;
            String leaseAppId = null;
            // add customers
            for (Customer cust : guestCard.getCustomers().getCustomer()) {
                String propertyId = findIdentity(cust.getIdentification(), "PropertyID");
                if (propertyId == null) {
                    Messages.throwYardiResponseException("Missing valid property identification");
                }
                // first customer should be the main tenant
                if (CustomerInfo.PROSPECT.equals(cust.getType())) {
                    building = getYardiBuilding(propertyId);
                    if (building == null) {
                        Messages.throwYardiResponseException(propertyId + ":" + YardiHandledErrorMessages.errorMessage_InvalidProperty);
                    }
                } else if (building == null || !propertyId.equals(building.buildingId().getValue())) {
                    Messages.throwYardiResponseException(propertyId + ":" + YardiHandledErrorMessages.errorMessage_InvalidProperty);
                }
                String guestId = findIdentity(cust.getIdentification(), "ThirdPartyID");
                if (guestId == null) {
                    Messages.throwYardiResponseException("Missing valid prospect identification");
                }
                String prospectId = "p_" + guestId;
                if (leaseApp == null) {
                    leaseAppId = prospectId;
                    leaseApp = guestManager.addApplication(propertyId, leaseAppId);
                }
                GuestBuilder guest = leaseApp.getGuest(guestId);
                if (guest == null) {
                    guest = leaseApp.addGuest(guestId, cust.getName().getFirstName() + " " + cust.getName().getLastName());
                }
                guest.setProspectId(prospectId) //
                        .setType(toTenantType(cust.getType())) //
                        .setEmail(cust.getEmail()) //
                        .setResponsibleForLease(true);
            }
            // set unit, rentable items
            if (guestCard.getCustomerPreferences() != null) {
                if (guestCard.getCustomerPreferences().getDesiredUnit().size() > 0) {
                    leaseApp.setUnit(guestCard.getCustomerPreferences().getDesiredUnit().get(0).getMarketingName());
                }
                for (AdditionalPreference item : guestCard.getCustomerPreferences().getCustomerAdditionalPreferences()) {
                    leaseApp.addRentableItem(item.getAdditionalPreferenceType());
                }
            }
            // events
            if (guestCard.getEvents() != null) {
                for (EventType event : guestCard.getEvents().getEvent()) {
                    leaseApp.addEvent(toEventType(event.getEventType())) //
                            .setEventId(event.getEventID() == null ? null : event.getEventID().getIDValue()) //
                            .setDate(YardiMockModelUtils.format(event.getEventDate())) //
                            .setAgent(event.getAgent().getAgentName().getFirstName() + " " + event.getAgent().getAgentName().getLastName()) //
                            .setQuote(toQuotedRent(event.getQuotes()));
                }
            }
        }
    }

    @Override
    public LeaseApplication getApplication(PmcYardiCredential yc, String propertyId, String prospectId) throws YardiServiceException, RemoteException {
        LeaseApplication leaseApp = new LeaseApplication();
        YardiBuilding building = getYardiBuilding(propertyId);
        if (building == null) {
            Messages.throwYardiResponseException(propertyId + ":Invalid Yardi Property Code");
        }
        // find lease
        // TODO - we should keep both p-id, and t-id
        YardiLease lease = null;
        for (YardiLease _lease : building.leases()) {
            if (YardiMockModelUtils.findProspect(_lease, prospectId) != null) {
                lease = _lease;
                break;
            }
        }
        if (lease == null) {
            // TODO - actually, yardi returns error inside data element here:
            // <LeaseApplication><Error>Invalid prospect Id</Error></LeaseApplication>
            // need a way to model that
            Messages.throwYardiResponseException("Invalid prospect Id");
        }

        // for now just need app charges section
        LALease laLease = new LALease();
        laLease.setAccountingData(new AccountingData());
        if (!lease.application().charges().isEmpty()) {
            ChargeSet charges = new ChargeSet();
            for (YardiFee appFee : lease.application().charges()) {
                charges.getCharge().add(toCharge(appFee));
            }
            laLease.getAccountingData().getChargeSet().add(charges);
        }
        leaseApp.getLALease().add(laLease);
        return leaseApp;
    }

    @Override
    public void importApplication(PmcYardiCredential yc, LeaseApplication leaseApp) throws YardiServiceException, RemoteException {
        // find lease and add app fees
        LALease leaseInfo = leaseApp.getLALease().get(0);
        // find building
        String propertyId = leaseInfo.getProperty().getIdentification().get(0).getIDValue();
        YardiBuilding building = getYardiBuilding(propertyId);
        if (building == null) {
            Messages.throwYardiResponseException(propertyId + ":Invalid Yardi Property Code");
        }
        // find lease
        Tenant tenant = leaseApp.getTenant().get(0);
        String guestId = findLeaseIdentity(tenant.getIdentification(), "thirdparty");
        YardiLease lease = null;
        for (YardiLease _lease : building.leases()) {
            if (YardiMockModelUtils.findGuest(_lease, guestId) != null) {
                lease = _lease;
                break;
            }
        }
        if (lease == null) {
            Messages.throwYardiResponseException(guestId + ":Prospect Not Found");
        }
        // add fees
        if (tenant.getAccountingData() != null && tenant.getAccountingData().getChargeSet().size() > 0
                && tenant.getAccountingData().getChargeSet().get(0).getCharge().size() > 0) {
            YardiGuestManager guestManager = YardiMock.server().getManager(YardiGuestManager.class);
            ApplicationBuilder appBuilder = guestManager.getApplication(propertyId, lease.leaseId().getValue());
            for (Charge charge : tenant.getAccountingData().getChargeSet().get(0).getCharge()) {
                appBuilder.addFee( //
                        charge.getAmount(), //
                        charge.getLabel(), //
                        charge.getIdentification().size() > 0 ? charge.getIdentification().get(0).getOrganizationName() : null //
                        );
            }
        }
    }

    // utility methods
    private String toQuotedRent(Quotes quotes) {
        return quotes == null || quotes.getQuote().isEmpty() ? null : quotes.getQuote().get(0).getQuotedRent().getExact();

    }

    private Charge toCharge(YardiFee appFee) {
        Charge charge = new Charge();
        charge.setAmount(YardiMockModelUtils.format(appFee.amount().getValue()));
        charge.setLabel(appFee.chargeCode().getValue());
        com.yardi.entity.leaseapp30.Identification desc = new com.yardi.entity.leaseapp30.Identification();
        desc.setOrganizationName(appFee.description().getValue());
        charge.getIdentification().add(desc);
        return charge;
    }

    private Customer toCustomer(YardiTenant guest, YardiBuilding building, YardiLease lease) {
        Customer cust = new Customer();
        cust.getIdentification().add(toIdentity(guest.guestId().getValue(), "ThirdPartyID"));
        if (!guest.prospectId().isNull()) {
            cust.getIdentification().add(toIdentity(guest.prospectId().getValue(), "ProspectID"));
        }
        if (!guest.tenantId().isNull()) {
            cust.getIdentification().add(toIdentity(guest.tenantId().getValue(), "TenantID"));
        }
        cust.getIdentification().add(toIdentity(building.buildingId().getValue(), "PropertyID"));
        cust.setType(guest.tenantId().equals(lease.leaseId()) ? CustomerInfo.PROSPECT : CustomerInfo.ROOMMATE);
        cust.setName(new NameType());
        cust.getName().setFirstName(guest.firstName().getValue());
        cust.getName().setLastName(guest.lastName().getValue());
        return cust;
    }

    private com.yardi.entity.guestcard40.Identification toIdentity(String id, String type) {
        com.yardi.entity.guestcard40.Identification identity = new com.yardi.entity.guestcard40.Identification();
        identity.setIDValue(id);
        identity.setIDType(type);
        return identity;
    }

    private EventType toEvent(YardiGuestEvent ge) {
        EventType event = new EventType();
        event.setEventType(toEventType(ge.type().getValue()));
        if (!ge.date().isNull()) {
            event.setEventDate(new Timestamp(ge.date().getValue().getTime()));
        }
        if (!ge.eventId().isNull()) {
            event.setEventID(toIdentity(ge.eventId().getValue(), ""));
        }
        if (!ge.agentName().isNull()) {
            String[] names = ge.agentName().getValue().split(" ", 2);
            event.setAgent(new Agent());
            event.getAgent().setAgentName(new AgentName());
            event.getAgent().getAgentName().setFirstName(names[0]);
            event.getAgent().getAgentName().setLastName(names.length > 1 ? names[1] : null);
        }
        if (!ge.rentQuote().isNull()) {
            event.setQuotes(new Quotes());
            Quote quote = new Quote();
            quote.setQuotedRent(new CurrencyRangeType());
            quote.getQuotedRent().setExact(YardiMockModelUtils.format(ge.rentQuote().getValue()));
            event.getQuotes().getQuote().add(quote);
        }
        return event;
    }

    private EventTypes toEventType(YardiGuestEvent.Type type) {
        try {
            return EventTypes.valueOf(type.name());
        } catch (Exception ignore) {
            return EventTypes.OTHER;
        }
    }

    private YardiGuestEvent.Type toEventType(EventTypes type) {
        try {
            return YardiGuestEvent.Type.valueOf(type.name());
        } catch (Exception ignore) {
            return YardiGuestEvent.Type.OTHER;
        }
    }

    private YardiTenant.Type toTenantType(CustomerInfo guestType) {
        switch (guestType) {
        case PROSPECT:
            return YardiTenant.Type.PROSPECT;
        default:
            return YardiTenant.Type.GUEST;
        }
    }

    private String findLeaseIdentity(List<com.yardi.entity.leaseapp30.Identification> idList, String idType) {
        if (idList == null || idList.isEmpty()) {
            return null;
        }
        for (com.yardi.entity.leaseapp30.Identification id : idList) {
            if (idType.equals(id.getIDType())) {
                return id.getIDValue();
            }
        }
        return null;
    }

    private String findIdentity(List<com.yardi.entity.guestcard40.Identification> idList, String idType) {
        if (idList == null || idList.isEmpty()) {
            return null;
        }
        for (com.yardi.entity.guestcard40.Identification id : idList) {
            if (idType.equals(id.getIDType())) {
                return id.getIDValue();
            }
        }
        return null;
    }

    private ChargeCode getChargeCode(String yardiCode) {
        ChargeCode chargeCode = new ChargeCode();
        chargeCode.setID(yardiCode);
        return chargeCode;
    }

    private RentableItemType getRentableItemType(YardiRentableItem item) {
        RentableItemType itemType = new RentableItemType();
        itemType.setCode(item.itemId().getValue());
        itemType.setChargeCode(item.chargeCode().getValue());
        itemType.setRent(YardiMockModelUtils.format(item.price().getValue()));
        itemType.setDescription(item.description().getValue());
        return itemType;
    }

    private Property getProperty(YardiBuilding building) {
        Property property = new Property();
        // identification
        property.setPropertyID(new PropertyIDType());
        property.getPropertyID().setIdentification(new Identification());
        property.getPropertyID().getIdentification().setPrimaryID(building.buildingId().getValue());
        property.getPropertyID().getAddress().add(getAddress(building.address()));
        // floorplans
        for (YardiFloorplan fp : building.floorplans()) {
            property.getFloorplan().add(getFloorplan(fp, building));
        }
        // units
        for (YardiUnit unit : building.units()) {
            property.getILSUnit().add(getILSUnit(unit, building));
        }
        return property;
    }

    private ILSUnit getILSUnit(YardiUnit unit, YardiBuilding building) {
        ILSUnit ilsUnit = new ILSUnit();
        ilsUnit.setId(unit.unitId().getValue());
        ilsUnit.setUnit(getUnit(unit, building));
        ilsUnit.setDeposit(getDeposit(unit.depositLMR().getValue()));
        return ilsUnit;
    }

    private DepositType getDeposit(BigDecimal amount) {
        DepositType deposit = new DepositType();
        deposit.setType("deposit");
        deposit.setAmount(new Amount());
        deposit.getAmount().setType("Actual");
        deposit.getAmount().setValue(amount);
        return deposit;
    }

    private Unit getUnit(YardiUnit unit, YardiBuilding building) {
        Unit mitsUnit = new Unit();
        mitsUnit.setPropertyPrimaryID(building.buildingId().getValue());
        Information info = new Information();
        info.setUnitID(unit.unitId().getValue());
        info.setUnitType(unit.floorplan().floorplanId().getValue());
        info.setUnitBedrooms(new BigDecimal(unit.floorplan().bedrooms().getValue()));
        info.setUnitBathrooms(new BigDecimal(unit.floorplan().bathrooms().getValue()));
        info.setMarketRent(unit.rent().getValue());
        info.setUnitOccupancyStatus(getUnitOccupancy(unit, building));
        info.setUnitLeasedStatus(getUnitLeased(unit, building));
        mitsUnit.getInformation().add(info);
        return mitsUnit;
    }

    private Unitoccpstatusinfo getUnitOccupancy(YardiUnit unit, YardiBuilding building) {
        for (YardiLease lease : building.leases()) {
            if (lease.unit() == unit) {
                Date now = SystemDateManager.getDate();
                if (!now.before(lease.leaseFrom().getValue()) && (!now.after(lease.leaseTo().getValue()) || lease.leaseTo().isNull())) {
                    return Unitoccpstatusinfo.OCCUPIED;
                } else {
                    return Unitoccpstatusinfo.VACANT;
                }
            }
        }
        return Unitoccpstatusinfo.VACANT;
    }

    private Unitleasestatusinfo getUnitLeased(YardiUnit unit, YardiBuilding building) {
        for (YardiLease lease : building.leases()) {
            if (lease.unit() == unit) {
                Date now = SystemDateManager.getDate();
                if (!now.before(lease.leaseFrom().getValue()) && (!now.after(lease.leaseTo().getValue()) || lease.leaseTo().isNull())) {
                    // TODO set according lease status
                    return Unitleasestatusinfo.LEASED;
                } else {
                    return Unitleasestatusinfo.AVAILABLE;
                }
            }
        }
        return Unitleasestatusinfo.AVAILABLE;
    }

    private Floorplan getFloorplan(YardiFloorplan yfp, YardiBuilding building) {
        Floorplan fp = new Floorplan();
        fp.setId(yfp.floorplanId().getValue());
        fp.setName(yfp.name().getValue());
        // unit info
        List<YardiUnit> units = new ArrayList<>();
        for (YardiUnit unit : building.units()) {
            if (unit.floorplan() == yfp) {
                units.add(unit);
            }
        }
        fp.setUnitCount(units.size());
        fp.setMarketRent(getMarketRentRange(units));
        // rooms
        RoomType bedroom = new RoomType();
        bedroom.setType("Bedroom");
        bedroom.setCount(new BigDecimal(yfp.bedrooms().getValue()));
        fp.getRoom().add(bedroom);
        RoomType bathroom = new RoomType();
        bathroom.setType("Bathroom");
        bathroom.setCount(new BigDecimal(yfp.bathrooms().getValue()));
        fp.getRoom().add(bathroom);
        return fp;
    }

    private MarketRentRange getMarketRentRange(List<YardiUnit> units) {
        MarketRentRange rentRange = null;
        BigDecimal minPrice = null, maxPrice = null;
        for (YardiUnit u : units) {
            BigDecimal price = u.rent().getValue();
            minPrice = DomainUtil.min(minPrice, price);
            maxPrice = DomainUtil.max(maxPrice, price);
        }
        if (minPrice != null && maxPrice != null) {
            rentRange = new MarketRentRange();
            rentRange.setMin(YardiMockModelUtils.format(minPrice));
            rentRange.setMax(YardiMockModelUtils.format(maxPrice));
        }
        return rentRange;
    }

    private Address getAddress(YardiAddress ya) {
        Address addr = new Address();
        addr.setType(Addressinfo.CURRENT);
        addr.setAddress1(ya.street().getValue());
        addr.setCity(ya.city().getValue());
        addr.setProvince(ya.province().getValue());
        addr.setState(ya.province().getValue());
        addr.setCountry(ya.country().getValue());
        addr.setPostalCode(ya.postalCode().getValue());
        return addr;
    }
}
