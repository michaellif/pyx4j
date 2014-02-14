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

    public enum ApplicantType {
        Prospect("ProspectID"), Tenant("TenantID");

        public final String id;

        private ApplicantType(String id) {
            this.id = id;
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

    private static final String ILS_AGENT = YardiConstants.ILS_INTERFACE_ENTITY;

    private static final String ILS_SOURCE = "ILS";

    public Map<String, String> createNewProspect(PmcYardiCredential yc, Lease lease) throws YardiServiceException {
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
        log.info("Created Prospect tenant with rentable items");

        // do guest search to retrieve lease id
        return getParticipants(yc, lease);
    }

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
        // TODO - handle negative case
        return true;
    }

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
        log.info("Reserved unit: {}", lease.unit().info().number().getValue());
        // TODO - handle negative case
        return true;
    }

    public String signLease(PmcYardiCredential yc, Lease lease) throws YardiServiceException {
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
        String tenantId = getTenantId(yc, lease.unit().building(), guestId, ApplicantType.Tenant);
        if (tenantId == null) {
            throw new YardiServiceException("Prospect not found: " + guestId);
        }
        log.info("Created Lease: {}", tenantId);

        return tenantId;
    }

    public String createFutureLease(PmcYardiCredential yc, Lease lease) throws YardiServiceException {
        Map<String, String> participants = createNewProspect(yc, lease);

        String pId = participants.get(lease.getPrimaryKey().toString());
        log.info("Created Prospect: {}", pId);

        holdUnit(yc, lease);
        log.info("Unit held for: {}", pId);

        if (true) {
            return pId;
        } else {
            String tId = signLease(yc, lease);
            log.info("Signed lease: {}", tId);

            return tId;
        }
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

        if (agentName == null) {
            String msg = SimpleMessageFormat.format("Yardi Marketing Agent {0} is not configured for property {1}.", ILS_AGENT, propertyCode);
            throw new YardiServiceException(msg);
        } else if (sourceName == null) {
            String msg = SimpleMessageFormat.format("Yardi Marketing Source {0} is not configured for property {1}.", ILS_SOURCE, propertyCode);
            throw new YardiServiceException(msg);
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

    private String getTenantId(PmcYardiCredential yc, Building building, String guestId, ApplicantType type) throws YardiServiceException {
        LeadManagement guestActivity = ServerSideFactory.create(YardiGuestManagementStub.class).findGuest(yc, building.propertyCode().getValue(), guestId);
        if (guestActivity.getProspects().getProspect().size() != 1) {
            throw new YardiServiceException("Prospect not found: " + guestId);
        }
        String tenantId = null;
        Prospect p = guestActivity.getProspects().getProspect().get(0);
        for (Customer c : p.getCustomers().getCustomer()) {
            for (Identification id : c.getIdentification()) {
                if ("ThirdPartyID".equals(id.getIDType()) && guestId.equals(id.getIDValue()) && type.id.equals(id.getIDType())) {
                    tenantId = id.getIDValue();
                    break;
                }
            }
        }
        return tenantId;
    }

    private Map<String, String> getParticipants(PmcYardiCredential yc, Lease lease) throws YardiServiceException {
        String tenantId = lease.getPrimaryKey().toString();
        LeadManagement guestActivity = ServerSideFactory.create(YardiGuestManagementStub.class).findGuest(yc,
                lease.unit().building().propertyCode().getValue(), tenantId);
        if (guestActivity.getProspects().getProspect().size() != 1) {
            throw new YardiServiceException(SimpleMessageFormat.format("Prospect not found: {0}", tenantId));
        }
        Map<String, String> participants = new HashMap<String, String>();
        Prospect p = guestActivity.getProspects().getProspect().get(0);
        for (Customer c : p.getCustomers().getCustomer()) {
            String tpId = null, pId = null;
            for (Identification id : c.getIdentification()) {
                if ("ThirdPartyID".equals(id.getIDType())) {
                    tpId = id.getIDValue();
                } else if ("ProspectID".equals(id.getIDType())) {
                    pId = id.getIDValue();
                }
            }
            if (tpId != null && pId != null) {
                participants.put(tpId, pId);
            }
        }
        String pId = participants.get(tenantId);
        if (pId == null) {
            throw new YardiServiceException(SimpleMessageFormat.format("Main applicant is missing: {0}", tenantId));
        }
        if (lease.leaseParticipants().size() != participants.size()) {
            String msg = SimpleMessageFormat.format("Missing or invalid participants: found {0} instead of {1}", participants.size(), lease.leaseParticipants()
                    .size());
            throw new YardiServiceException(msg);
        }
        return participants;
    }
}
