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
package com.propertyvista.yardi.merger;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.YardiCustomer;
import com.yardi.entity.resident.RTCustomer;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.yardi.mapper.TenantMapper;

public class TenantMerger {

    private final static Logger log = LoggerFactory.getLogger(TenantMapper.class);

    private final ExecutionMonitor executionMonitor;

    public TenantMerger() {
        this(null);
    }

    public TenantMerger(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    public boolean isChanged(List<YardiCustomer> yardiCustomers, List<LeaseTermTenant> tenants) {
        if (yardiCustomers.size() != tenants.size()) {
            return true;
        }

        for (YardiCustomer customer : yardiCustomers) {
            boolean isNew = true;

            for (LeaseTermTenant tenant : tenants) {
                if (customer.getCustomerID().equals(tenant.leaseParticipant().participantId().getValue())) {
                    isNew = false;
                    break;
                }
            }

            if (isNew) {
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
        IList<LeaseTermTenant> tenants = term.version().tenants();

        // calculate:
        List<String> existing = fromT(tenants);
        List<String> imported = fromC(yardiCustomers);

        List<String> removed = new ArrayList<String>(existing);
        removed.removeAll(imported);

        List<String> added = new ArrayList<String>(imported);
        added.removeAll(existing);

        // process:
        for (String id : removed) {
            term.version().tenants().remove(toT(tenants, id));
        }

        for (String id : added) {
            term.version().tenants().add(new TenantMapper(executionMonitor).createTenant(toC(yardiCustomers, id), term.version().tenants()));
        }

        return term;
    }

    public boolean updateTenantsData(RTCustomer rtCustomer, Lease lease) {
        boolean updated = false;

        for (YardiCustomer customer : rtCustomer.getCustomers().getCustomer()) {
            for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
                if (tenant.leaseParticipant().participantId().getValue().equals(customer.getCustomerID())) {
                    updated |= new TenantMapper(executionMonitor).updateTenant(customer, tenant);
                    Persistence.service().merge(tenant.leaseParticipant().customer());
                }
            }
        }

        return updated;
    }

    private List<String> fromT(List<LeaseTermTenant> tenants) {
        List<String> ids = new ArrayList<String>();
        for (LeaseTermTenant tenant : tenants) {
            ids.add(tenant.leaseParticipant().participantId().getValue());
        }
        return ids;
    }

    private List<String> fromC(List<YardiCustomer> customers) {
        List<String> ids = new ArrayList<String>();
        for (YardiCustomer customer : customers) {
            ids.add(customer.getCustomerID());
        }
        return ids;
    }

    private YardiCustomer toC(List<YardiCustomer> customers, String id) {
        for (YardiCustomer customer : customers) {
            if (customer.getCustomerID().equals(id)) {
                return customer;
            }
        }
        return null;
    }

    private LeaseTermTenant toT(List<LeaseTermTenant> tenants, String id) {
        for (LeaseTermTenant tenant : tenants) {
            if (tenant.leaseParticipant().participantId().getValue().equals(id)) {
                return tenant;
            }
        }
        return null;
    }
}
