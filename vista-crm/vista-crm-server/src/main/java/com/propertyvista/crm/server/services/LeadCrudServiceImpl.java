/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.rpc.services.LeadCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeadCrudServiceImpl extends GenericCrudServiceImpl<Lead> implements LeadCrudService {

    public LeadCrudServiceImpl() {
        super(Lead.class);
    }

    @Override
    public void convertToLease(AsyncCallback<Lease> callback, Key entityId) {

        Lead lead = EntityServicesImpl.secureRetrieve(dboClass, entityId);
        if (lead.convertedToLease().isBooleanTrue()) {
            callback.onFailure(new Error("The Lead is converted to Lease already!"));
        } else {
            Tenant tenant = EntityFactory.create(Tenant.class);
            tenant.type().setValue(Tenant.Type.person);
            tenant.person().set(lead.person());
            EntityServicesImpl.secureSave(tenant);

            Lease lease = EntityFactory.create(Lease.class);
            lease.leaseID().setValue(RandomUtil.randomLetters(10));
            lease.status().setValue(Lease.Status.Draft);
            lease.leaseFrom().setValue(lead.moveInDate().getValue());
            lease.expectedMoveIn().setValue(lead.moveInDate().getValue());
//        lease.tenants().add(tenantInLease);
            EntityServicesImpl.secureSave(lease);

            TenantInLease tenantInLease = EntityFactory.create(TenantInLease.class);
            tenantInLease.tenant().set(tenant);
            tenantInLease.status().setValue(TenantInLease.Status.Applicant);
            tenantInLease.lease().set(lease);
            EntityServicesImpl.secureSave(tenantInLease);

            // mark Lead as converted:
            lead.convertedToLease().setValue(true);
            EntityServicesImpl.secureSave(lead);

            callback.onSuccess(lease);
        }
    }
}
