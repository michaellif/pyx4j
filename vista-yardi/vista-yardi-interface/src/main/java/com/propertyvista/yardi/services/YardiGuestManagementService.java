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
import java.util.Arrays;
import java.util.List;

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

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.processors.YardiGuestProcessor;
import com.propertyvista.yardi.stubs.YardiGuestManagementStub;

public class YardiGuestManagementService extends YardiAbstractService {

    private static final Logger log = LoggerFactory.getLogger(YardiGuestManagementService.class);

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

    public String createFutureLease(PmcYardiCredential yc, Lease lease) throws YardiServiceException {
        String leaseId = null;

        String propertyCode = lease.unit().building().propertyCode().getValue();
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
            String msg = SimpleMessageFormat.format("Marketing Agent {0} is not configured.", ILS_AGENT);
            throw new YardiServiceException(msg);
        } else if (sourceName == null) {
            String msg = SimpleMessageFormat.format("Marketing Source {0} is not configured.", ILS_SOURCE);
            throw new YardiServiceException(msg);
        }

        YardiGuestProcessor guestProcessor = new YardiGuestProcessor(agentName, sourceName);
        // create guest, add rentable items
        Prospect guest = guestProcessor.getProspect(lease);
        for (String type : getLeaseProducts(lease)) {
            guestProcessor.addRentableItem(guest, type);
        }
        guestProcessor.addEvent(guest, guestProcessor.getNewEvent(EventTypes.OTHER, true));
        submitGuest(yc, guest);

        // add unit hold event
        EventType event = guestProcessor.getNewEvent(EventTypes.HOLD, false);
        Identification holdId = new Identification();
        holdId.setIDType(lease.unit().info().number().getValue());
        holdId.setIDValue("0");
        event.setEventID(holdId);
        guestProcessor.addEvent(guest, event);
        submitGuest(yc, guest);

        // create lease
        for (EventTypes type : Arrays.asList(EventTypes.APPLICATION, EventTypes.APPROVE, EventTypes.LEASE_SIGN)) {
            event = guestProcessor.getNewEvent(type, false);
            event.setQuotes(guestProcessor.getRentQuote(getRentPrice()));
            guestProcessor.addEvent(guest, event);
            submitGuest(yc, guest);
        }

        // do guest search to retrieve lease id
        String guestId = lease._applicant().getPrimaryKey().toString();
        leaseId = getTenantId(yc, propertyCode, guestId);
        // exception if null

        return leaseId;
    }

    public RentableItems getRentableItems(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        log.info("Getting RentableItems for property {}", propertyId);
        return ServerSideFactory.create(YardiGuestManagementStub.class).getRentableItems(yc, propertyId);
    }

    private BigDecimal getRentPrice() {
        // TODO: VladL - return rent price (service only)
        return new BigDecimal(950);
    }

    private List<String> getLeaseProducts(Lease lease) {
        // TODO: VladL - return list of rentable item types
        return Arrays.asList("parking", "locker");
    }

    private void submitGuest(PmcYardiCredential yc, Prospect guest) throws YardiServiceException {
        LeadManagement lead = new LeadManagement();
        lead.setProspects(new Prospects());
        lead.getProspects().getProspect().add(guest);
        ServerSideFactory.create(YardiGuestManagementStub.class).importGuestInfo(yc, lead);
    }

    private String getTenantId(PmcYardiCredential yc, String propertyCode, String guestId) throws YardiServiceException {
        Prospect t = null;
        String tenantId = null;
        LeadManagement guestActivity = ServerSideFactory.create(YardiGuestManagementStub.class).findGuest(yc, propertyCode, guestId);
        for (Prospect p : guestActivity.getProspects().getProspect()) {
            Customer c = p.getCustomers().getCustomer().get(0);
            for (Identification id : c.getIdentification()) {
                if ("ThirdPartyID".equals(id.getIDType()) && guestId.equals(id.getIDValue())) {
                    t = p;
                    break;
                }
            }
        }
        if (t == null) {
            return null;
        }
        Customer c = t.getCustomers().getCustomer().get(0);
        for (Identification id : c.getIdentification()) {
            if ("TenantID".equals(id.getIDType())) {
                tenantId = id.getIDValue();
                break;
            }
        }
        return tenantId;
    }
}
