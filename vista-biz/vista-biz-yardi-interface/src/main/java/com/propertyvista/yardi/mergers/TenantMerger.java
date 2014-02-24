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
 * @version $Id$
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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityGraph;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.yardi.mappers.TenantMapper;

public class TenantMerger {

    private final static Logger log = LoggerFactory.getLogger(TenantMapper.class);

    private final ExecutionMonitor executionMonitor;

    public TenantMerger() {
        this(null);
    }

    public TenantMerger(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    public boolean isChanged(List<YardiCustomer> yardiCustomers, List<LeaseTermTenant> tenants, List<LeaseTermGuarantor> guarantors) {
        if (yardiCustomers.size() != (tenants.size() + guarantors.size())) {
            return true;
        }

        for (YardiCustomer customer : yardiCustomers) {
            if (findParticipant(tenants, guarantors, customer.getCustomerID()) == null) {
                return true;
            }
        }
        return false;
    }

    public LeaseTerm createTenants(List<YardiCustomer> yardiCustomers, LeaseTerm term) {
        for (YardiCustomer yardiCustomer : yardiCustomers) {
            term.version().tenants().add(new TenantMapper(executionMonitor).createTenant(yardiCustomer, term.version().tenants()));
        }

        return term;
    }

    public LeaseTerm updateTenants(List<YardiCustomer> yardiCustomers, LeaseTerm term) {
        Set<String> currentCustomerIDs = new HashSet<String>();

        for (YardiCustomer customer : yardiCustomers) {
            currentCustomerIDs.add(customer.getCustomerID());

            if (findParticipant(term.version().tenants(), term.version().guarantors(), customer.getCustomerID()) == null) {
                // New tenant,  TODO implement new guarantor
                term.version().tenants().add(new TenantMapper(executionMonitor).createTenant(customer, term.version().tenants()));
            }
        }

        // Find removed tenants and guarantors
        Iterator<LeaseTermTenant> ti = term.version().tenants().iterator();
        while (ti.hasNext()) {
            LeaseTermTenant participant = ti.next();
            if (!currentCustomerIDs.contains(participant.leaseParticipant().participantId().getValue())) {
                ti.remove();
            }
        }

        Iterator<LeaseTermGuarantor> ig = term.version().guarantors().iterator();
        while (ig.hasNext()) {
            LeaseTermGuarantor participant = ig.next();
            if (!currentCustomerIDs.contains(participant.leaseParticipant().participantId().getValue())) {
                ti.remove();
            }
        }

        return term;
    }

    public boolean updateTenantsData(List<YardiCustomer> yardiCustomers, LeaseTerm term) {
        boolean updated = false;

        for (YardiCustomer customer : yardiCustomers) {
            LeaseTermParticipant<?> participant = findParticipant(term.version().tenants(), term.version().guarantors(), customer.getCustomerID());

            if (new TenantMapper(executionMonitor).updateCustomerData(customer, participant.leaseParticipant().customer())) {
                Persistence.service().merge(participant.leaseParticipant().customer());
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

    private LeaseTermParticipant<?> findParticipant(List<LeaseTermTenant> tenants, List<LeaseTermGuarantor> guarantors, String id) {
        for (LeaseTermParticipant<?> participant : CollectionUtils.union(tenants, guarantors)) {
            if (!participant.leaseParticipant().participantId().isNull() && participant.leaseParticipant().participantId().getValue().equals(id)) {
                return participant;
            }
        }
        return null;
    }
}
