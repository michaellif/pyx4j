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
import com.yardi.entity.guestcard40.PropertyMarketingSources;
import com.yardi.entity.guestcard40.Prospect;
import com.yardi.entity.guestcard40.Prospects;
import com.yardi.entity.guestcard40.RentableItem;
import com.yardi.entity.guestcard40.RentableItemType;
import com.yardi.entity.guestcard40.RentableItems;
import com.yardi.entity.leaseapp30.LeaseApplication;
import com.yardi.entity.mits.Information;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.system.yardi.YardiProspectNotEditableException;
import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.financial.offering.YardiChargeCode;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.processors.YardiGuestProcessor;
import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;
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

    private static class SingletonHolder {
        public static final YardiGuestManagementService INSTANCE = new YardiGuestManagementService();
    }

    private YardiGuestManagementService() {
    }

    public static YardiGuestManagementService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static final String ILS_AGENT = YardiConstants.ILS_INTERFACE_ENTITY;

    public static final String ILS_SOURCE = "ILS";

    /** Create new Guest and return guest ID. This method is safe to call multiple times. */
    public String createNewProspect(PmcYardiCredential yc, Lease lease) throws YardiServiceException, RemoteException {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(lease._applicant(), AttachLevel.Attached);

        String guestId = lease.getPrimaryKey().toString();

        YardiGuestProcessor guestProcessor = new YardiGuestProcessor(ILS_AGENT, ILS_SOURCE);
        // create guest, preferred unit, and moveIn date
        Prospect guest = guestProcessor.getProspect(lease);
        guestProcessor //
                .addUnit(guest, getUnitInfo(lease.unit())) //
                .addLeaseTerm(guest, lease.leaseFrom().getValue(), lease.leaseTo().getValue()) //
                .addMoveInDate(guest, lease.expectedMoveIn().getValue()) //
                .setEvent(guest, guestProcessor.getNewEvent(EventTypes.OTHER, true));
        try {
            submitGuest(yc, guest);
            log.debug("Imported New Prospect: {}", guestId);
        } catch (YardiProspectNotEditableException e) {
            log.warn("Could not import New Prospect: " + guestId, e);
            // ignore since the guest may have been already imported by previous attempt; see if we can retrieve it
        }

        // do tenant search to retrieve prospect id
        String prospectId = getTenantId(yc, lease.unit().building().propertyCode().getValue(), guestId, IdentityType.Prospect);
        if (prospectId == null) {
            throw new YardiServiceException("Prospect not created: " + guestId);
        }
        log.info("Created New Prospect: {}", prospectId);

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

    /** Add lease participants to an existing Guest and return participant IDs. This method is safe to call multiple times. */
    public Map<Key, String> addLeaseParticipants(PmcYardiCredential yc, Lease lease) throws YardiServiceException, RemoteException {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(lease._applicant(), AttachLevel.Attached);

        String guestId = lease.getPrimaryKey().toString();

        // create guest with co-tenants and guarantors
        YardiGuestProcessor guestProcessor = new YardiGuestProcessor(ILS_AGENT, ILS_SOURCE);
        Prospect guest = guestProcessor.getAllLeaseParticipants(lease);
        // This should update the guest and will not produce any errors if done multiple times
        try {
            submitGuest(yc, guest);
            log.debug("Added Lease Participants for guest: {}", guestId);
        } catch (YardiProspectNotEditableException e) {
            log.warn("Could not Added Lease Participants for guest: " + guestId, e);
            // ignore since the participants may have been already imported by previous attempt; see if we can retrieve it
        }

        // do guest search to retrieve lease participants
        Map<Key, String> participants = retrieveLeaseParticipants(yc, lease);
        log.info("Added Lease Participants: {}", Arrays.toString(participants.values().toArray()));

        return participants;
    }

    /** Run Lease Sign flow and return future lease id. This method is safe to call multiple times. */
    public String signLease(final PmcYardiCredential yc, final Lease lease) throws YardiServiceException, RemoteException {
        ensureProspectId(lease);

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
                RentableItemKey lookupKey = new RentableItemKey(type, product.agreedPrice().getValue(BigDecimal.ZERO));
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
                if (!currentEvents.contains(type)) {
                    EventType event = guestProcessor.getNewEvent(type, false);
                    if (type == EventTypes.LEASE_SIGN) {
                        event.setQuotes(guestProcessor.getRentQuote(getRentPrice(lease)));
                    }
                    guestProcessor.setEvent(guest, event);

                    submitGuest(yc, guest);
                    log.info("Event Submitted: {}", type.name());
                }

                if (type == EventTypes.APPLICATION) {
                    // can post app fees now
                    importApplicationFees(yc, lease);
                }
            }
        }
        // do tenant search to retrieve lease id
        final String tenantId = getTenantId(yc, propertyCode, guestId, IdentityType.Tenant);
        if (tenantId == null) {
            throw new YardiServiceException("Tenant not found: " + guestId);
        }
        log.info("Created Lease: {}", tenantId);
        return tenantId;
    }

    public Map<Key, String> retrieveLeaseParticipants(PmcYardiCredential yc, Lease lease) throws YardiServiceException, RemoteException {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);

        Key tenantId = lease.getPrimaryKey();
        LeadManagement guestActivity = YardiStubFactory.create(YardiILSGuestCardStub.class).findGuest(yc, lease.unit().building().propertyCode().getValue(),
                tenantId.toString());
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

    public boolean isLeaseSigned(final PmcYardiCredential yc, final Lease lease) throws YardiServiceException {
        try {
            boolean leaseSigned = getWorkflowEvents(yc, lease).contains(EventTypes.LEASE_SIGN);
            log.info("Lease application {} is {} editable", lease.leaseApplication().yardiApplicationId(), leaseSigned ? "not" : "still");
            return leaseSigned;
        } catch (RemoteException e) {
            throw new UserRuntimeException("Yardi communication error: " + e.getMessage(), e);
        }
    }

    // ------------ Internals -----------
    private void importApplicationFees(final PmcYardiCredential yc, final Lease lease) throws YardiServiceException, RemoteException {
        String prospectId = ensureProspectId(lease);

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);
        String propertyCode = lease.unit().building().propertyCode().getValue();

        YardiGuestProcessor guestProcessor = new YardiGuestProcessor(ILS_AGENT, ILS_SOURCE);

        boolean useMasterDeposit = lease.currentTerm().version().leaseProducts().serviceItem().item().yardiDepositLMR().isNull();
        List<Deposit> deposits = getLeaseDeposits(lease);
        if (useMasterDeposit && !deposits.isEmpty()) {
            ensureDepositChargeCodesConfigured(yc, lease);
            // make sure no application exists for the prospect
            LeaseApplication yardiApp = YardiStubFactory.create(YardiILSGuestCardStub.class).getApplication(yc, propertyCode, prospectId);
            if (yardiApp == null || guestProcessor.getApplicationCharges(yardiApp).isEmpty()) {
                // do ImportApplication to push Deposits back to Yardi as App Fees
                YardiStubFactory.create(YardiILSGuestCardStub.class).importApplication(yc, guestProcessor.getLeaseApplication(lease, deposits));
                log.info("Application Fees Submitted: {}", deposits.size());
            } else {
                // TODO - what's the proper handling of existing application with possible app charges?
                // Options: do nothing, add Master Deposit charges, or merge Master Deposit charges...
                log.info("Found Application with fees - ImportApplication skipped for prospect: {}", prospectId);
            }
        } else {
            log.info("No LMR deposit found to submit for lease: {}", lease.getPrimaryKey().toString());
        }

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

    public String getLeaseId(PmcYardiCredential yc, Lease lease) throws YardiServiceException, RemoteException {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);
        String propertyCode = lease.unit().building().propertyCode().getValue();
        String guestId = lease.getPrimaryKey().toString();

        return getTenantId(yc, propertyCode, guestId, IdentityType.Tenant);
    }

    public Set<EventTypes> getWorkflowEvents(PmcYardiCredential yc, Lease lease) throws YardiServiceException, RemoteException {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);
        String propertyCode = lease.unit().building().propertyCode().getValue();
        String guestId = lease.getPrimaryKey().toString();

        return getEvents(yc, propertyCode, guestId);
    }

    private String ensureProspectId(Lease lease) {
        String prospectId = lease.leaseApplication().yardiApplicationId().getValue("");
        if (!prospectId.startsWith("p")) {
            throw new UserRuntimeException("Invalid Lease Application id: " + lease.leaseApplication().yardiApplicationId().getValue());
        }
        return prospectId;
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
        return ServerSideFactory.create(BillingFacade.class).getActualPrice(lease.currentTerm().version().leaseProducts().serviceItem());
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
        Set<EventTypes> result = new HashSet<>();
        Prospect p = guestActivity.getProspects().getProspect().get(0);
        for (EventType c : p.getEvents().getEvent()) {
            result.add(c.getEventType());
        }
        return result;
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