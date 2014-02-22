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
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.yardi.entity.guestcard40.RentableItems;
import com.yardi.entity.mits.Information;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.processors.YardiGuestProcessor;
import com.propertyvista.yardi.stubs.YardiGuestManagementStub;

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

    public String createNewProspect(PmcYardiCredential yc, Lease lease) throws YardiServiceException {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(lease._applicant(), AttachLevel.Attached);

        YardiGuestProcessor guestProcessor = new YardiGuestProcessor(ILS_AGENT, ILS_SOURCE);
        // create guest, add rentable items, preferred unit, and moveIn date
        Prospect guest = guestProcessor.getProspect(lease);
        for (String type : getLeaseProducts(lease)) {
            guestProcessor.addRentableItem(guest, type);
        }
        guestProcessor //
                .addUnit(guest, getUnitInfo(lease.unit())) //
                .addLeaseTerm(guest, lease.leaseFrom().getValue(), lease.leaseTo().getValue()) //
                .addMoveInDate(guest, lease.expectedMoveIn().getValue()) //
                .setEvent(guest, guestProcessor.getNewEvent(EventTypes.OTHER, true));
        submitGuest(yc, guest);

        // do guest search to retrieve lease id
        // do tenant search to retrieve lease id
        String guestId = lease.getPrimaryKey().toString();
        String prospectId = getTenantId(yc, lease.unit().building(), guestId, IdentityType.Prospect);
        if (prospectId == null) {
            throw new YardiServiceException("Prospect not found: " + guestId);
        }
        log.info("Created Prospect tenant with rentable items: " + prospectId);

        return prospectId;
    }

    /**
     * Try to hold unit for the given lease. In case of failure will throw exception.
     */
    public boolean holdUnit(PmcYardiCredential yc, Lease lease) throws YardiServiceException {
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
     */
    public boolean releaseUnit(PmcYardiCredential yc, Lease lease) throws YardiServiceException {
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

    public Map<Key, String> addLeaseParticipants(PmcYardiCredential yc, Lease lease) throws YardiServiceException {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(lease._applicant(), AttachLevel.Attached);

        // create guest with co-tenants and guarantors
        YardiGuestProcessor guestProcessor = new YardiGuestProcessor(ILS_AGENT, ILS_SOURCE);
        Prospect guest = guestProcessor.getAllLeaseParticipants(lease);
        submitGuest(yc, guest);
        log.info("Added Lease Participants");

        // do guest search to retrieve lease id
        return getParticipants(yc, lease);
    }

    public SignLeaseResults signLease(final PmcYardiCredential yc, final Lease lease) throws YardiServiceException {
        YardiGuestProcessor guestProcessor = new YardiGuestProcessor(ILS_AGENT, ILS_SOURCE);
        Prospect guest = guestProcessor.getProspect(lease);
        // create lease
        for (EventTypes type : Arrays.asList(EventTypes.APPLICATION, EventTypes.APPROVE, EventTypes.LEASE_SIGN)) {
            EventType event = guestProcessor.getNewEvent(type, false);
            if (type == EventTypes.LEASE_SIGN) {
                event.setQuotes(guestProcessor.getRentQuote(getRentPrice(lease)));
            }
            guestProcessor.setEvent(guest, event);
            submitGuest(yc, guest);
            log.info("Triggered event: {}", type.name());
        }
        // do tenant search to retrieve lease id
        String guestId = lease.getPrimaryKey().toString();
        final String tenantId = getTenantId(yc, lease.unit().building(), guestId, IdentityType.Tenant);
        if (tenantId == null) {
            throw new YardiServiceException("Tenant not found: " + guestId);
        }
        log.info("Created Lease: {}", tenantId);

        return new SignLeaseResults() {

            final String leaseId = tenantId;

            final Map<Key, String> participants = YardiGuestManagementService.this.getParticipants(yc, lease);

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
        return ServerSideFactory.create(YardiGuestManagementStub.class).getRentableItems(yc, propertyId);
    }

    public void validateSettings(PmcYardiCredential yc, String propertyCode) throws YardiServiceException {
        MarketingSources sources = ServerSideFactory.create(YardiGuestManagementStub.class).getYardiMarketingSources(yc, propertyCode);

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
            throw new YardiServiceException(msg.toString());
        }
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
        // TODO calculate adjustments?!
        return lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().getValue();
    }

    private List<String> getLeaseProducts(Lease lease) {
        List<String> productCodes = new ArrayList<>();

        for (BillableItem feature : lease.currentTerm().version().leaseProducts().featureItems()) {
            productCodes.add(feature.item().product().holder().yardiCode().getValue());
        }

        return productCodes;
    }

    private void submitGuest(PmcYardiCredential yc, Prospect guest) throws YardiServiceException {
        LeadManagement lead = new LeadManagement();
        lead.setProspects(new Prospects());
        lead.getProspects().getProspect().add(guest);
        ServerSideFactory.create(YardiGuestManagementStub.class).importGuestInfo(yc, lead);
    }

    private String getTenantId(PmcYardiCredential yc, Building building, String guestId, IdentityType type) throws YardiServiceException {
        LeadManagement guestActivity = ServerSideFactory.create(YardiGuestManagementStub.class).findGuest(yc, building.propertyCode().getValue(), guestId);
        if (guestActivity.getProspects().getProspect().size() != 1) {
            throw new YardiServiceException("Prospect not found: " + guestId);
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

    private Map<Key, String> getParticipants(PmcYardiCredential yc, Lease lease) throws YardiServiceException {
        Key tenantId = lease.getPrimaryKey();
        LeadManagement guestActivity = ServerSideFactory.create(YardiGuestManagementStub.class).findGuest(yc,
                lease.unit().building().propertyCode().getValue(), tenantId.toString());
        if (guestActivity.getProspects().getProspect().size() != 1) {
            throw new YardiServiceException(SimpleMessageFormat.format("Prospect not found: {0}", tenantId));
        }
        Map<Key, String> participants = new HashMap<Key, String>();
        Prospect p = guestActivity.getProspects().getProspect().get(0);
        for (Customer c : p.getCustomers().getCustomer()) {
            String tpId = null, pId = null;
            for (Identification id : c.getIdentification()) {
                if (IdentityType.ThirdParty.ID.equals(id.getIDType())) {
                    tpId = id.getIDValue();
                } else if (IdentityType.Prospect.ID.equals(id.getIDType())) {
                    pId = id.getIDValue();
                }
            }
            if (tpId != null && pId != null) {
                participants.put(new Key(tpId), pId);
            }
        }
        String pId = participants.get(tenantId);
        if (pId == null) {
            throw new YardiServiceException(SimpleMessageFormat.format("Main applicant is missing: {0}", tenantId));
        } else {
            participants.remove(tenantId);
            participants.put(lease._applicant().getPrimaryKey(), pId);

        }

        Persistence.ensureRetrieve(lease.leaseParticipants(), AttachLevel.Attached);
        if (lease.leaseParticipants().size() != participants.size()) {
            String msg = SimpleMessageFormat.format("Missing or invalid participants: found {0} instead of {1}", participants.size(), lease.leaseParticipants()
                    .size());
            throw new YardiServiceException(msg);
        }
        return participants;
    }
}
