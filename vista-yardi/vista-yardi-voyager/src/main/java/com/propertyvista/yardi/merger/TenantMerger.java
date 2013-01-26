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

import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.yardi.mapper.TenantMapper;

public class TenantMerger {

    public boolean validateChanges(List<YardiCustomer> yardiCustomers, List<LeaseTermTenant> tenants) {
        for (YardiCustomer customer : yardiCustomers) {
            boolean isNew = true;
            for (LeaseTermTenant tenant : tenants) {
                if (compare(customer, tenant)) {
                    isNew = false;
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
        List<String> existing = getNfromT(tenants);
        List<String> imported = getNfromC(yardiCustomers);
        List<String> removed = new ArrayList<String>(existing);
        removed.removeAll(imported);
        List<String> added = new ArrayList<String>(imported);
        added.removeAll(existing);

        for (String name : removed) {
            LeaseTermTenant tenant = getT(tenants, name);
            term.version().tenants().remove(tenant);
        }
        for (String name : added) {
            YardiCustomer customer = getC(yardiCustomers, name);
            TenantMapper mapper = new TenantMapper();
            LeaseTermTenant tenant = mapper.map(customer);
            term.version().tenants().add(tenant);
        }
        return term;
    }

    private List<String> getNfromT(List<LeaseTermTenant> tenants) {
        List<String> names = new ArrayList<String>();
        for (LeaseTermTenant tenant : tenants) {
            String name = new String();
            name = tenant.leaseParticipant().customer().person().name().firstName().getValue() + "#"
                    + tenant.leaseParticipant().customer().person().name().lastName().getValue();
            names.add(name);
        }
        return names;
    }

    private List<String> getNfromC(List<YardiCustomer> customers) {
        List<String> names = new ArrayList<String>();
        for (YardiCustomer customer : customers) {
            String name = new String();
            name = customer.getName().getFirstName() + "#" + customer.getName().getLastName();
            names.add(name);
        }
        return names;
    }

    private boolean compare(YardiCustomer customer, LeaseTermTenant tenant) {
        if (customer.getName().getFirstName().equals(tenant.leaseParticipant().customer().person().name().firstName().getValue())
                && customer.getName().getLastName().equals(tenant.leaseParticipant().customer().person().name().lastName().getValue())) {
            return true;
        }
        return false;
    }

    private YardiCustomer getC(List<YardiCustomer> customers, String name) {
        for (YardiCustomer customer : customers) {
            if (customer.getName().getFirstName().equals(name.split("#")[0]) && customer.getName().getLastName().equals(name.split("#")[1])) {
                return customer;
            }
        }
        return null;
    }

    private LeaseTermTenant getT(List<LeaseTermTenant> tenants, String name) {
        for (LeaseTermTenant tenant : tenants) {
            if (tenant.leaseParticipant().customer().person().name().firstName().getValue().equals(name.split("#")[0])
                    && tenant.leaseParticipant().customer().person().name().lastName().getValue().equals(name.split("#")[1])) {
                return tenant;
            }
        }
        return null;
    }
}
