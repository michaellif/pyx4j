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
package com.propertyvista.crm.server.services.tenant.lead;

import java.util.Date;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.crm.rpc.services.tenant.lead.LeadCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lead.Guest;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Showing;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.common.util.LeaseManager;

public class LeadCrudServiceImpl extends GenericCrudServiceImpl<Lead> implements LeadCrudService {

    public LeadCrudServiceImpl() {
        super(Lead.class);
    }

    @Override
    protected void enhanceRetrieved(Lead entity, boolean fromList) {
        if (!entity.floorplan().isNull()) {
            Persistence.service().retrieve(entity.floorplan().building());
            entity.building().set(entity.floorplan().building());
        }

        if (fromList) {
            // just clear unnecessary data before serialization: 
            entity.comments().setValue(null);
        }
    }

    @Override
    public void setSelectedFloorplan(AsyncCallback<Floorplan> callback, Key floorplanId) {
        Floorplan item = Persistence.service().retrieve(Floorplan.class, floorplanId);
        Persistence.service().retrieve(item.building());
        callback.onSuccess(item);
    }

    @Override
    public void getInterestedUnits(AsyncCallback<Vector<AptUnit>> callback, Key leadId) {
        EntityQueryCriteria<Showing> criteria = new EntityQueryCriteria<Showing>(Showing.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().appointment().lead(), leadId));

        Vector<AptUnit> units = new Vector<AptUnit>();
        for (Showing showing : Persistence.secureQuery(criteria)) {
            if (!showing.result().isNull() && showing.result().getValue() == Showing.Result.interested) {
                if (!units.contains(showing.unit())) {
                    units.add((AptUnit) showing.unit().detach());
                }
            }
        }

        callback.onSuccess(units);
    }

    @Override
    public void convertToLease(AsyncCallback<Lease> callback, Key leadId, Key unitId) {
        Lead lead = Persistence.service().retrieve(dboClass, leadId);
        if (!lead.lease().isNull()) {
            callback.onFailure(new UserRuntimeException("The Lead is converted to Lease already!"));
        } else {
            Date leaseEnd = null;
            switch (lead.leaseTerm().getValue()) {
            case months6:
                leaseEnd = DateUtils.monthAdd(lead.moveInDate().getValue(), 6 + 1);
                break;
            case months12:
                leaseEnd = DateUtils.monthAdd(lead.moveInDate().getValue(), 12 + 1);
                break;
            case months18:
                leaseEnd = DateUtils.monthAdd(lead.moveInDate().getValue(), 18 + 1);
                break;
            case other:
                leaseEnd = DateUtils.monthAdd(lead.moveInDate().getValue(), 12 + 1);
                break;
            }

            LeaseManager lm = new LeaseManager();
            // TODO get
            Lease lease = lm.create(RandomUtil.randomLetters(10), lead.leaseType().getValue(), Persistence.service().retrieve(AptUnit.class, unitId), lead
                    .moveInDate().getValue(), new LogicalDate(leaseEnd));
            lease.version().expectedMoveIn().setValue(lead.moveInDate().getValue());

            boolean asApplicant = true;
            for (Guest guest : lead.guests()) {
                Tenant tenant = EntityFactory.create(Tenant.class);
                tenant.type().setValue(Tenant.Type.person);
                tenant.person().set(guest.person());
                Persistence.service().persist(tenant);

                TenantInLease tenantInLease = EntityFactory.create(TenantInLease.class);
                tenantInLease.tenant().set(tenant);
                tenantInLease.role().setValue(asApplicant ? TenantInLease.Role.Applicant : TenantInLease.Role.CoApplicant);
                lease.version().tenants().add(tenantInLease);
                asApplicant = false;
            }

            lm.save(lease);

            // mark Lead as converted:
            lead.lease().set(lease);
            Persistence.service().merge(lead);

            Persistence.service().commit();
            callback.onSuccess(lease);
        }
    }

}
