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

import com.yardi.entity.mits.YardiCustomer;
import com.yardi.entity.resident.RTCustomer;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.yardi.mapper.TenantMapper;

public class TenantMerger {

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

    public LeaseTerm updateTenants(List<YardiCustomer> yardiCustomers, LeaseTerm term) {
        IList<LeaseTermTenant> tenants = term.version().tenants();
        List<String> existing = fromT(tenants);
        List<String> imported = fromC(yardiCustomers);
        List<String> removed = new ArrayList<String>(existing);
        removed.removeAll(imported);
        List<String> added = new ArrayList<String>(imported);
        added.removeAll(existing);
        for (String id : removed) {
            LeaseTermTenant tenant = toT(tenants, id);
            term.version().tenants().remove(tenant);
        }
        for (String id : added) {
            YardiCustomer customer = toC(yardiCustomers, id);
            LeaseTermTenant tenant = new TenantMapper().map(customer, term.version().tenants());
            term.version().tenants().add(tenant);
        }
        return term;
    }

    public boolean sameName(YardiCustomer customer, LeaseTermTenant tenant) {
        if (customer.getName().getFirstName().equals(tenant.leaseParticipant().customer().person().name().firstName().getValue())
                && customer.getName().getLastName().equals(tenant.leaseParticipant().customer().person().name().lastName().getValue())) {
            return true;
        }
        return false;
    }

    private List<String> fromT(List<LeaseTermTenant> tenants) {
        List<String> ids = new ArrayList<String>();
        for (LeaseTermTenant tenant : tenants) {
            String id = tenant.leaseParticipant().participantId().getValue();
            ids.add(id);
        }
        return ids;
    }

    private List<String> fromC(List<YardiCustomer> customers) {
        List<String> ids = new ArrayList<String>();
        for (YardiCustomer customer : customers) {
            String id = customer.getCustomerID();
            ids.add(id);
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

    public boolean isNamesChanged(List<YardiCustomer> yardiCustomers, List<LeaseTermTenant> tenants) {
        for (YardiCustomer customer : yardiCustomers) {
            boolean isChanged = true;

            for (LeaseTermTenant tenant : tenants) {
                //@formatter:off
                if (CommonsStringUtils.equals(customer.getName().getFirstName(), tenant.leaseParticipant().customer().person().name().firstName().getValue()) &&
                    CommonsStringUtils.equals(customer.getName().getLastName(),  tenant.leaseParticipant().customer().person().name().lastName().getValue())) {
                //@formatter:on
                    isChanged = false;
                    break;
                }
            }

            if (isChanged) {
                return true;
            }
        }
        return false;
    }

    public void updateTenantNames(RTCustomer rtCustomer, Lease lease) {
        List<LeaseTermTenant> tenants = lease.currentTerm().version().tenants();
        if (new TenantMerger().isNamesChanged(rtCustomer.getCustomers().getCustomer(), tenants)) {
            for (YardiCustomer yardiCustomer : rtCustomer.getCustomers().getCustomer()) {
                for (LeaseTermTenant tenant : tenants) {
                    if (tenant.leaseParticipant().participantId().getValue().equals(yardiCustomer.getCustomerID())
                            && !new TenantMerger().sameName(yardiCustomer, tenant)) {

                        Customer cust = tenant.leaseParticipant().customer();

                        cust.person().name().firstName().setValue(yardiCustomer.getName().getFirstName());
                        cust.person().name().lastName().setValue(yardiCustomer.getName().getLastName());

                        Persistence.service().persist(cust);
                    }
                }
            }
        }
    }
}
