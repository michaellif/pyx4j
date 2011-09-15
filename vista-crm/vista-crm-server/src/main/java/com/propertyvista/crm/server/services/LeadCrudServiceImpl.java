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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.crm.rpc.services.LeadCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeadCrudServiceImpl extends GenericCrudServiceImpl<Lead> implements LeadCrudService {

    public LeadCrudServiceImpl() {
        super(Lead.class);
    }

    @Override
    protected void enhanceRetrieve(Lead entity, boolean fromList) {
        if (fromList) {
            // just clear unnecessary data before serialisation: 
            entity.comments().setValue(null);
        }
    }

    @Override
    public void convertToLease(AsyncCallback<Lease> callback, Key entityId) {

        Lead lead = Persistence.service().retrieve(dboClass, entityId);
        if (lead.convertedToLease().isBooleanTrue()) {
            callback.onFailure(new UserRuntimeException("The Lead is converted to Lease already!"));
        } else {
            Tenant tenant = EntityFactory.create(Tenant.class);
            tenant.type().setValue(Tenant.Type.person);
            tenant.person().set(lead.person());
            Persistence.service().merge(tenant);

            Lease lease = EntityFactory.create(Lease.class);
            lease.leaseID().setValue(RandomUtil.randomLetters(10));
            lease.type().setValue(RandomUtil.randomEnum(Service.Type.class));
            lease.status().setValue(Lease.Status.Draft);
            lease.leaseFrom().setValue(lead.moveInDate().getValue());
            lease.expectedMoveIn().setValue(lead.moveInDate().getValue());
            Persistence.service().merge(lease);

            TenantInLease tenantInLease = EntityFactory.create(TenantInLease.class);
            tenantInLease.tenant().set(tenant);
            tenantInLease.lease().set(lease);
            tenantInLease.status().setValue(TenantInLease.Status.Applicant);
            Persistence.service().merge(tenantInLease);

//            // update lease tenants list:
//            lease.tenants().add(tenantInLease);
//            Persistence.service().merge(lease);

            // mark Lead as converted:
            lead.convertedToLease().setValue(true);
            Persistence.service().merge(lead);

            callback.onSuccess(lease);
        }
    }
}
