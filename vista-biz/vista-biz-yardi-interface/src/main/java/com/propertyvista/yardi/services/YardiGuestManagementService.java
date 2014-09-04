/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 26, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.guestcard40.AttachmentTypesAndChargeCodes;
import com.yardi.entity.guestcard40.ChargeCode;
import com.yardi.entity.guestcard40.Customer;
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
import com.yardi.entity.guestcard40.RentableItem;
import com.yardi.entity.guestcard40.RentableItemType;
import com.yardi.entity.guestcard40.RentableItems;
import com.yardi.entity.leaseapp30.LeaseApplication;
import com.yardi.entity.mits.Information;
import com.yardi.entity.mits.Name;
import com.yardi.entity.mits.YardiCustomer;
import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.payment.PaymentBillableUtils;
import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.financial.offering.YardiChargeCode;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.mappers.TenantMapper;
import com.propertyvista.yardi.processors.YardiGuestProcessor;
import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;
import com.propertyvista.yardi.stubs.YardiStubFactory;

public class YardiGuestManagementService extends YardiAbstractService {

    private static final Logger log = LoggerFactory.getLogger(YardiGuestManagementService.class);

    public enum IdentityType {
        Prospect("ProspectID"), Tenant("TenantID"), ThirdParty("ThirdPartyID");

        public final String ID;

        private IdentityType(String id) {
            this.ID = id;
        }
    }

    public interface SignLeaseResults {
        String getLeaseId();

        Map<Key, String> getParticipants();
    }

    private static class SingletonHolder {
        public static final YardiGuestManagementService INSTANCE = new YardiGuestManagementService();
    }

    private YardiGuestManagementService() {
    }

    public static YardiGuestManagementService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final String ILS_AGENT = YardiConstants.ILS_INTERFACE_ENTITY;

    private static final String ILS_SOURCE = "ILS";

    public String createNewProspect(PmcYardiCredential yc, Lease lease) throws YardiServiceException, RemoteException {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(lease._applicant(), AttachLevel.Attached);

        YardiGuestProcessor guestProcessor = new YardiGuestProcessor(ILS_AGENT, ILS_SOURCE);
        // create guest, preferred unit, and moveIn date
        Prospect guest = guestProcessor.getProspect(lease);
        guestProcessor //
                .addUnit(guest, getUnitInfo(lease.unit())) //
                .addLeaseTerm(guest, lease.leaseFrom().getValue(), lease.leaseTo().getValue()) //
                .addMoveInDate(guest, lease.expectedMoveIn().getValue()) //
                .setEvent(guest, guestProcessor.getNewEvent(EventTypes.OTHER, true));
        submitGuest(yc, guest);

        // do guest search to retrieve lease id
        // do tenant search to retrieve lease id
        String guestId = lease.getPrimaryKey().toString();
        String prospectId = getTenantId(yc, lease.unit().building().propertyCode().getValue(), guestId, IdentityType.Prospect);
        if (prospectId == null) {
            throw new YardiServiceException("Prospect not found: " + guestId);
        }
        log.info("Created Prospect tenant with rentable items: " + prospectId);

        return prospectId;
    }

    /**
     * Try to hold unit for the given lease. In case of failure will throw exception.
     * 
     * @throws RemoteException
     */
    public boolean holdUnit(PmcYardiCredential yc, Lease lease) throws YardiServiceException, RemoteException {
        if (!lease.leaseApplication().yardiApplicationId().getValue("").startsWith("p")) {
            throw new UserRuntimeException("Invalid Lease Application id: " + lease.leaseApplication().yardiApplicationId().getValue());
        }

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(lease._applicant(), AttachLevel.Attached);

        YardiGuestProcessor guestProcessor = new YardiGuestProcessor(ILS_AGENT, ILS_SOURCE);
        Prospect guest = guestProcessor.getProspect(lease);
        // add unit hold event
        guestProcessor.clearPreferences(guest);
        EventType event = guestProcessor.getNewEvent(EventTypes.HOLD, false);
        Identification holdId = new Identification();
        holdId.setIDType(lease.unit().info().number().getValue());
        holdId.setIDValue("0");
        event.setEventID(holdId);
        event.setEventDate(new Timestamp(new Date().getTime() + 2 * 24 * 3600 * 1000));
        guestProcessor.setEvent(guest, event);
        submitGuest(yc, guest);

        log.info("Reserved unit: {}", lease.unit().info().number().getValue());

        return true;
    }

    /**
     * Try to release unit for the given lease. In case of failure will throw exception.
     * Will fail if attempted to release unit that has no hold by this lease.
     * 
     * @throws RemoteException
     */
    public boolean releaseUnit(PmcYardiCredential yc, Lease lease) throws YardiServiceException, RemoteException {
        if (!lease.leaseApplication().yardiApplicationId().getValue("").startsWith("p")) {
            throw new UserRuntimeException("Invalid Lease Application id: " + lease.leaseApplication().yardiApplicationId().getValue());
        }

        YardiGuestProcessor guestProcessor = new YardiGuestProcessor(ILS_AGENT, ILS_SOURCE);
        Prospect guest = guestProcessor.getProspect(lease);
        // add unit hold event
        guestProcessor.clearPreferences(guest);
        EventType event = guestProcessor.getNewEvent(EventTypes.RELEASE, false);
        Identification holdId = new Identification();
        holdId.setIDType(lease.unit().info().number().getValue());
        holdId.setIDValue("0");
        event.setEventID(holdId);
        guestProcessor.setEvent(guest, event);
        submitGuest(yc, guest);

        log.info("Released unit: {}", lease.unit().info().number().getValue());

        return true;
    }

    public boolean cancelApplication(PmcYardiCredential yc, Lease lease) throws YardiServiceException, RemoteException {
        EventType event = null;
        YardiGuestProcessor guestProcessor = new YardiGuestProcessor(ILS_AGENT, ILS_SOURCE);
        if (!lease.leaseApplication().yardiApplicationId().getValue("").startsWith("t")) {
            // tenant - use cancel application event
            event = guestProcessor.getNewEvent(EventTypes.CANCEL_APPLICATION, false);
        } else if (!lease.leaseApplication().yardiApplicationId().getValue("").startsWith("p")) {
            // prospect - use cancel guest event
            event = guestProcessor.getNewEvent(EventTypes.CANCEL, false);
        } else {
            throw new UserRuntimeException("Invalid Lease Application id: " + lease.leaseApplication().yardiApplicationId().getValue());
        }

        Prospect guest = guestProcessor.getProspect(lease);
        // add cancel application event
        guestProcessor.clearPreferences(guest);
        Identification eventId = new Identification();
        eventId.setIDValue("0");
        event.setEventID(eventId);
        guestProcessor.setEvent(guest, event);
        submitGuest(yc, guest);

        log.info("Application canceled: {}", lease.leaseId().getValue());

        return true;
    }

    public boolean declineApplication(PmcYardiCredential yc, Lease lease) throws YardiServiceException, RemoteException {
        if (!lease.leaseApplication().yardiApplicationId().getValue("").startsWith("t")) {
            throw new UserRuntimeException("Invalid Lease Application id: " + lease.leaseApplication().yardiApplicationId().getValue());
        }

        YardiGuestProcessor guestProcessor = new YardiGuestProcessor(ILS_AGENT, ILS_SOURCE);
        Prospect guest = guestProcessor.getProspect(lease);
        // add cancel application event
        guestProcessor.clearPreferences(guest);
        EventType event = guestProcessor.getNewEvent(EventTypes.APPLICATION_DENIED, false);
        Identification eventId = new Identification();
        eventId.setIDValue("0");
        event.setEventID(eventId);
        guestProcessor.setEvent(guest, event);
        submitGuest(yc, guest);

        log.info("Application declined: {}", lease.leaseId().getValue());

        return true;
    }

    public Map<Key, String> addLeaseParticipants(PmcYardiCredential yc, Lease lease) throws YardiServiceException, RemoteException {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(lease._applicant(), AttachLevel.Attached);

        // create guest with co-tenants and guarantors
        YardiGuestProcessor guestProcessor = new YardiGuestProcessor(ILS_AGENT, ILS_SOURCE);
        Prospect guest = guestProcessor.getAllLeaseParticipants(lease);
        submitGuest(yc, guest);
        log.info("Added Lease Participants");

        // do guest search to retrieve lease id
        return getParticipants(yc, lease, null);
    }

    public SignLeaseResults signLease(final PmcYardiCredential yc, final Lease lease) throws YardiServiceException, RemoteException {
        String prospectId = lease.leaseApplication().yardiApplicationId().getValue("");
        if (!prospectId.startsWith("p")) {
            throw new UserRuntimeException("Invalid Lease Application id: " + lease.leaseApplication().yardiApplicationId().getValue());
        }

        YardiGuestProcessor guestProcessor = new YardiGuestProcessor(ILS_AGENT, ILS_SOURCE);

        String guestId = lease.getPrimaryKey().toString();

        // check if lease has already been signed by previous attempts; if not run lease sign flow
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);
        String propertyCode = lease.unit().building().propertyCode().getValue();
        Set<EventTypes> currentEvents = getEvents(yc, propertyCode, guestId);
        if (!currentEvents.contains(EventTypes.LEASE_SIGN)) {
            Prospect guest = guestProcessor.getProspect(lease);
            // get available rentable item ids per type
            Map<RentableItemKey, RentableItem> availableItems = getAvailableRentableItems(yc, lease);
            // add selected rentable items
            for (BillableItem product : getLeaseProducts(lease)) {
                String type = product.item().product().holder().yardiCode().getValue();
                RentableItemKey lookupKey = new RentableItemKey(type, product.agreedPrice().getValue());
                RentableItem item = availableItems.get(lookupKey);
                if (item == null) {
                    throw new UserRuntimeException(SimpleMessageFormat.format("No available ''{0}'' found for the price of ''{1}''", lookupKey.code,
                            lookupKey.price));
                }

                guestProcessor.addRentableItem(guest, type, item.getCode());
            }
            // ensure unit
            guestProcessor.addUnit(guest, getUnitInfo(lease.unit()));
            // create/update future lease
            for (EventTypes type : Arrays.asList(EventTypes.APPLICATION, EventTypes.APPROVE, EventTypes.LEASE_SIGN)) {
                if (currentEvents.contains(type)) {
                    continue;
                }
                EventType event = guestProcessor.getNewEvent(type, false);
                if (type == EventTypes.LEASE_SIGN) {
                    event.setQuotes(guestProcessor.getRentQuote(getRentPrice(lease)));
                }
                guestProcessor.setEvent(guest, event);

                // Ignore errors other than for LEASE_SIGN event
                try {
                    submitGuest(yc, guest);
                    log.info("Event Submitted: {}", type.name());
                } catch (YardiServiceException e) {
                    log.info("Event Failed: {}", type.name());
                    if (type == EventTypes.LEASE_SIGN) {
                        throw e;
                    }
                }
            }
        }
        // do tenant search to retrieve lease id
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);
        final String tenantId = getTenantId(yc, propertyCode, guestId, IdentityType.Tenant);
        if (tenantId == null) {
            throw new YardiServiceException("Tenant not found: " + guestId);
        }
        log.info("Created Lease: {}", tenantId);

        boolean useMasterDeposit = lease.currentTerm().version().leaseProducts().serviceItem().item().yardiDepositLMR().isNull();
        if (useMasterDeposit && VistaTODO.VISTA_4517_Master_Deposit_Completed) {
            ensureDepositChargeCodesConfigured(yc, lease);
            // make sure no application exists for the prospect
            LeaseApplication la = YardiStubFactory.create(YardiILSGuestCardStub.class).getApplication(yc, propertyCode, prospectId);
            if (la == null || guestProcessor.getApplicationCharges(la).isEmpty()) {
                // do ImportApplication to push Deposits back to Yardi as App Fees
                YardiStubFactory.create(YardiILSGuestCardStub.class).importApplication(yc,
                        guestProcessor.getLeaseApplication(lease, tenantId, getLeaseDeposits(lease)));
            } else {
                // TODO - what's the proper handling of existing application with possible app charges?
                // Options: do nothing, add Master Deposit charges, or merge Master Deposit charges...
                log.info("Found Application with fees - ImportApplication skipped for prospect: {}", tenantId);
            }
        }

        // call resident transactions to retrieve name-to-residentId mapping
        final Map<String, String> residentIds = getLeaseResidentIds(yc, lease.unit().building().propertyCode().getValue(), tenantId);

        return new SignLeaseResults() {

            final String leaseId = tenantId;

            final Map<Key, String> participants = YardiGuestManagementService.this.getParticipants(yc, lease, residentIds);

            @Override
            public String getLeaseId() {
                return leaseId;
            }

            @Override
            public Map<Key, String> getParticipants() {
                return participants;
            }

        };
    }

    public RentableItems getRentableItems(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        log.info("Getting RentableItems for property {}", propertyId);
        return YardiStubFactory.create(YardiILSGuestCardStub.class).getRentableItems(yc, propertyId);
    }

    public void validateSettings(PmcYardiCredential yc, String propertyCode) throws YardiServiceException, RemoteException {
        MarketingSources sources = YardiStubFactory.create(YardiILSGuestCardStub.class).getYardiMarketingSources(yc, propertyCode);

        String agentName = null;
        String sourceName = null;
        for (PropertyMarketingSources source : sources.getProperty()) {
            if (propertyCode.equals(source.getPropertyCode())) {
                for (MarketingAgent agent : source.getPropertyRequiredFields().getAgents().getAgentName()) {
                    if (ILS_AGENT.equals(agent.getValue())) {
                        agentName = agent.getValue();
                    }
                }
                for (MarketingSource src : source.getPropertyRequiredFields().getSources().getSourceName()) {
                    if (ILS_SOURCE.equals(src.getValue())) {
                        sourceName = src.getValue();
                    }
                }
            }
        }

        StringBuilder msg = new StringBuilder();
        if (agentName == null) {
            msg.append(SimpleMessageFormat.format("Yardi Marketing Agent ''{0}'' is not configured for property ''{1}''.\n", ILS_AGENT, propertyCode));
        }
        if (sourceName == null) {
            msg.append(SimpleMessageFormat.format("Yardi Marketing Source ''{0}'' is not configured for property ''{1}''.\n", ILS_SOURCE, propertyCode));
        }
        if (msg.length() > 0) {
            throw new UserRuntimeException(msg.toString());
        }
    }

    /**
     * Check for Deposit charge code mapping
     * - get list of configured charge codes for GuestCard service (use GetAttachmentTypesAndChargeCodes)
     * - check Deposit policy to ensure that charge code for each item belongs to the list above
     */
    private void ensureDepositChargeCodesConfigured(PmcYardiCredential yc, Lease lease) throws YardiServiceException, RemoteException {
        AttachmentTypesAndChargeCodes chargeCodeConfig = YardiStubFactory.create(YardiILSGuestCardStub.class).getConfiguredAttachmentsAndCharges(yc);
        StringBuilder unconfigured = new StringBuilder();
        // get lease master deposit codes, if any
        List<Deposit> leaseDeposits = getLeaseDeposits(lease);
        if (leaseDeposits == null || leaseDeposits.size() == 0) {
            log.warn("No deposits found for lease: {}", lease.leaseId().getValue());
        }
        for (Deposit leaseDeposit : leaseDeposits) {
            if (leaseDeposit.chargeCode().yardiChargeCodes().size() == 0) {
                // TODO - why do we need more than one charge code per ARCode? - which one to use for deposit charge?
                throw new UserRuntimeException(SimpleMessageFormat.format("No Yardi charge code found for ARCode ''{0}''", leaseDeposit.chargeCode()
                        .getStringView()));
            }
            for (YardiChargeCode code : leaseDeposit.chargeCode().yardiChargeCodes()) {
                boolean found = false;
                for (ChargeCode configured : chargeCodeConfig.getChargeCodes().getChargeCode()) {
                    if (configured.getID().equals(code.yardiChargeCode().getValue())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    unconfigured.append(unconfigured.length() == 0 ? "" : ", ").append(code.getStringView());
                }
            }
        }

        if (unconfigured.length() > 0) {
            throw new YardiServiceException(SimpleMessageFormat.format("Charge Code(s) ''{0}'' not configured in Yardi for interface: {1}",
                    unconfigured.toString(), YardiConstants.ILS_INTERFACE_ENTITY));
        }
    }

    private List<Deposit> getLeaseDeposits(Lease lease) {
        List<Deposit> deposits = new ArrayList<>();
        List<BillableItem> products = new ArrayList<>(lease.currentTerm().version().leaseProducts().featureItems());
        products.add(lease.currentTerm().version().leaseProducts().serviceItem());
        for (BillableItem product : products) {
            deposits.addAll(product.deposits());
        }
        return deposits;
    }

    private Information getUnitInfo(AptUnit unit) {
        Persistence.ensureRetrieve(unit.floorplan(), AttachLevel.Attached);

        Information unitInfo = new Information();
        unitInfo.setUnitBedrooms(new BigDecimal(unit.floorplan().bedrooms().getValue()));
        unitInfo.setUnitType(unit.floorplan().code().getValue());
        unitInfo.setUnitID(unit.info().number().getValue());
        return unitInfo;
    }

    private BigDecimal getRentPrice(Lease lease) {
        return PaymentBillableUtils.getActualPrice(lease.currentTerm().version().leaseProducts().serviceItem());
    }

    private List<BillableItem> getLeaseProducts(Lease lease) {
        return lease.currentTerm().version().leaseProducts().featureItems();
    }

    /**
     * get available rentable item codes
     * 
     * @throws RemoteException
     */
    private Map<RentableItemKey, RentableItem> getAvailableRentableItems(PmcYardiCredential yc, Lease lease) throws YardiServiceException, RemoteException {
        Map<RentableItemKey, RentableItem> availableCodes = new HashMap<RentableItemKey, RentableItem>();
        RentableItems rentableItems = getRentableItems(yc, lease.unit().building().propertyCode().getValue());
        for (RentableItemType type : rentableItems.getItemType()) {
            for (RentableItem item : type.getItem()) {
                if (item.isAvailable()) {
                    RentableItemKey key = new RentableItemKey(type.getCode(), new BigDecimal(item.getRent()));
                    if (!availableCodes.containsKey(key)) {
                        availableCodes.put(key, item);
                        break;
                    }
                }
            }
        }
        return availableCodes;
    }

    private void submitGuest(PmcYardiCredential yc, Prospect guest) throws YardiServiceException, RemoteException {
        LeadManagement lead = new LeadManagement();
        lead.setProspects(new Prospects());
        lead.getProspects().getProspect().add(guest);
        YardiStubFactory.create(YardiILSGuestCardStub.class).importGuestInfo(yc, lead);
    }

    private String getTenantId(PmcYardiCredential yc, String propertyCode, String guestId, IdentityType type) throws YardiServiceException, RemoteException {
        LeadManagement guestActivity = YardiStubFactory.create(YardiILSGuestCardStub.class).findGuest(yc, propertyCode, guestId);
        if (guestActivity == null || guestActivity.getProspects().getProspect().size() != 1) {
            throw new YardiServiceException(SimpleMessageFormat.format("Prospect not found: {0}", guestId));
        }
        Prospect p = guestActivity.getProspects().getProspect().get(0);
        for (Customer c : p.getCustomers().getCustomer()) {
            String tpId = null, tId = null;
            for (Identification id : c.getIdentification()) {
                if (IdentityType.ThirdParty.ID.equals(id.getIDType())) {
                    tpId = id.getIDValue();
                } else if (type.ID.equals(id.getIDType())) {
                    tId = id.getIDValue();
                }
                if (tpId != null && tId != null && guestId.equals(tpId)) {
                    return tId;
                }
            }
        }
        return null;
    }

    private Set<EventTypes> getEvents(PmcYardiCredential yc, String propertyCode, String guestId) throws YardiServiceException, RemoteException {
        LeadManagement guestActivity = YardiStubFactory.create(YardiILSGuestCardStub.class).findGuest(yc, propertyCode, guestId);
        if (guestActivity == null || guestActivity.getProspects().getProspect().size() != 1) {
            throw new YardiServiceException(SimpleMessageFormat.format("Prospect not found: {0}", guestId));
        }
        Set<EventTypes> result = new HashSet<>();
        Prospect p = guestActivity.getProspects().getProspect().get(0);
        for (EventType c : p.getEvents().getEvent()) {
            result.add(c.getEventType());
        }
        return result;
    }

    private Map<Key, String> getParticipants(PmcYardiCredential yc, Lease lease, Map<String, String> residentIds) throws YardiServiceException, RemoteException {
        Key tenantId = lease.getPrimaryKey();
        LeadManagement guestActivity = YardiStubFactory.create(YardiILSGuestCardStub.class).findGuest(yc, lease.unit().building().propertyCode().getValue(),
                tenantId.toString());
        if (guestActivity.getProspects().getProspect().size() != 1) {
            throw new YardiServiceException(SimpleMessageFormat.format("Prospect not found: {0}", tenantId));
        }
        Map<Key, String> participants = new HashMap<Key, String>();
        Prospect p = guestActivity.getProspects().getProspect().get(0);
        boolean tenantFound = false;
        for (Customer c : p.getCustomers().getCustomer()) {
            String tpId = null, pId = null, tId = null;
            for (Identification id : c.getIdentification()) {
                if (IdentityType.ThirdParty.ID.equals(id.getIDType())) {
                    tpId = id.getIDValue();
                } else if (IdentityType.Prospect.ID.equals(id.getIDType())) {
                    pId = id.getIDValue();
                } else if (IdentityType.Tenant.ID.equals(id.getIDType())) {
                    tId = id.getIDValue();
                }
            }
            if (tpId != null && pId != null) {
                Key tpKey = new Key(tpId);
                if (tpKey.equals(tenantId)) {
                    participants.put(lease._applicant().getPrimaryKey(), StringUtils.isEmpty(tId) ? pId : tId);
                    tenantFound = true;
                } else {
                    if (residentIds != null) {
                        String nameKey = getNameKey(c.getName());
                        pId = residentIds.get(nameKey);
                        if (pId == null) {
                            throw new YardiServiceException(SimpleMessageFormat.format("Prospect not found: {0}", nameKey));
                        }
                    }
                    participants.put(tpKey, pId);
                }
            }
        }

        // sanity checks
        if (!tenantFound) {
            throw new YardiServiceException(SimpleMessageFormat.format("Main applicant is missing: {0}", tenantId));
        }

        Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.IdOnly);
        Persistence.ensureRetrieve(lease.currentTerm().version().guarantors(), AttachLevel.IdOnly);
        int coApplicants = lease.currentTerm().version().tenants().size() + lease.currentTerm().version().guarantors().size();
        if (coApplicants != participants.size()) {
            String msg = SimpleMessageFormat.format("Missing or invalid participants: found {0} expecting {1}", participants.size(), coApplicants);
            throw new YardiServiceException(msg);
        }
        return participants;
    }

    private Map<String, String> getLeaseResidentIds(PmcYardiCredential yc, String propertyCode, String tenantId) throws YardiServiceException, RemoteException {
        Map<String, String> result = null;
        ResidentTransactions transaction = YardiStubFactory.create(YardiResidentTransactionsStub.class).getResidentTransactionsForTenant(yc, propertyCode,
                tenantId);
        if (transaction != null && !transaction.getProperty().isEmpty()) {
            Property property = transaction.getProperty().iterator().next();
            if (!property.getRTCustomer().isEmpty()) {
                result = new HashMap<String, String>();
                for (YardiCustomer customer : property.getRTCustomer().iterator().next().getCustomers().getCustomer()) {
                    result.put(getNameKey(customer.getName()), TenantMapper.getCustomerID(customer));
                }
            }
        }
        return result;
    }

    private String getNameKey(Name name) {
        StringBuilder key = new StringBuilder() //
                .append(name.getFirstName() + ".") //
                .append(name.getLastName() + ".");
        return key.toString();
    }

    // TODO - com.yardi.entity.guestcard40.NameType is identical to com.yardi.entity.mits.Name - consider xsd or reflection mapping
    private String getNameKey(NameType name) {
        StringBuilder key = new StringBuilder() //
                .append(name.getFirstName() + ".") //
                .append(name.getLastName() + ".");
        return key.toString();
    }

    private static class RentableItemKey {
        public final String code;

        public final BigDecimal price;

        public RentableItemKey(String code, BigDecimal price) {
            assert code != null && price != null : "Arguments cannot be null";
            this.code = code;
            this.price = price;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            } else if (other instanceof RentableItemKey) {
                RentableItemKey otherKey = (RentableItemKey) other;
                return code.equals(otherKey.code) && price.compareTo(otherKey.price) == 0;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return 17 * code.hashCode() ^ price.hashCode();
        }
    }
}