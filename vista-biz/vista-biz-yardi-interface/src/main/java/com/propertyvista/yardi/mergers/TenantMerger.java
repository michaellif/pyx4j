/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 24, 2013
 * @author yuriyl
 */
package com.propertyvista.yardi.mergers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.YardiCustomer;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.utils.EntityGraph;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.yardi.mappers.TenantMapper;

public class TenantMerger {

    private final static Logger log = LoggerFactory.getLogger(TenantMapper.class);

    private final ExecutionMonitor executionMonitor;

    public TenantMerger(ExecutionMonitor executionMonitor) {
        assert (executionMonitor != null);
        this.executionMonitor = executionMonitor;
    }

    public boolean isChanged(List<YardiCustomer> yardiCustomers, Lease lease) {
        if (yardiCustomers.size() != (lease.currentTerm().version().tenants().size() + lease.currentTerm().version().guarantors().size())) {
            return true;
        }

        for (YardiCustomer customer : yardiCustomers) {
            if (findParticipant(lease.currentTerm(), TenantMapper.getCustomerID(customer)) == null) {
                return true;
            }
        }

        return false;
    }

    public void createTenants(List<YardiCustomer> yardiCustomers, Lease lease, LeaseTerm previousTerm) {
        for (YardiCustomer yardiCustomer : yardiCustomers) {
            lease.currentTerm().version().tenants().add(new TenantMapper(executionMonitor).createTenant(yardiCustomer, lease, previousTerm));
        }
    }

    public void updateTenants(List<YardiCustomer> yardiCustomers, Lease lease, LeaseTerm previousTerm) {
        Set<String> currentCustomerIDs = new HashSet<String>();

        for (YardiCustomer customer : yardiCustomers) {
            currentCustomerIDs.add(TenantMapper.getCustomerID(customer));

            if (findParticipant(lease.currentTerm(), TenantMapper.getCustomerID(customer)) == null) {
                // New tenant,  TODO implement new guarantor
                lease.currentTerm().version().tenants().add(new TenantMapper(executionMonitor).createTenant(customer, lease, previousTerm));
            }
        }

        // Find removed tenants and guarantors
        Iterator<LeaseTermTenant> ti = lease.currentTerm().version().tenants().iterator();
        while (ti.hasNext()) {
            LeaseTermTenant participant = ti.next();
            if (!currentCustomerIDs.contains(participant.leaseParticipant().participantId().getValue())) {
                ti.remove();
            }
        }
        Iterator<LeaseTermGuarantor> ig = lease.currentTerm().version().guarantors().iterator();
        while (ig.hasNext()) {
            LeaseTermGuarantor participant = ig.next();
            if (!currentCustomerIDs.contains(participant.leaseParticipant().participantId().getValue())) {
                ti.remove();
            }
        }
    }

    public boolean updateTenantsData(List<YardiCustomer> yardiCustomers, LeaseTerm term) {
        boolean updated = false;

        for (YardiCustomer customer : yardiCustomers) {
            LeaseTermParticipant<?> participant = findParticipant(term, TenantMapper.getCustomerID(customer));

            if (new TenantMapper(executionMonitor).updateCustomerData(customer, participant.leaseParticipant().customer())) {
                ServerSideFactory.create(CustomerFacade.class).persistCustomer(participant.leaseParticipant().customer());
            }

            LeaseTermParticipant.Role newRole = TenantMapper.getRole(customer, participant);

            if (participant.role().getValue() == LeaseTermParticipant.Role.Guarantor) {
                // Guarantors change not supported ...  it is not available in API
            } else {
                updated |= EntityGraph.updateMember(participant.role(), newRole);
            }
        }

        return updated;
    }

    private LeaseTermParticipant<?> findParticipant(LeaseTerm term, String id) {
        for (LeaseTermParticipant<?> participant : CollectionUtils.union(term.version().tenants(), term.version().guarantors())) {
            if (!participant.leaseParticipant().participantId().isNull() && participant.leaseParticipant().participantId().getValue().equals(id)) {
                return participant;
            }
        }
        return null;
    }
}
