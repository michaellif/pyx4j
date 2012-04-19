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
package com.propertyvista.crm.server.services.customer.lead;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.tenant.LeadFacade;
import com.propertyvista.crm.rpc.services.customer.lead.LeadCrudService;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Showing;

public class LeadCrudServiceImpl extends AbstractCrudServiceImpl<Lead> implements LeadCrudService {

    public LeadCrudServiceImpl() {
        super(Lead.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Lead entity, Lead dto) {
        if (!entity.floorplan().isNull()) {
            Persistence.service().retrieve(entity.floorplan().building());
            dto.building().set(entity.floorplan().building());
        }
    }

    @Override
    protected void enhanceListRetrieved(Lead entity, Lead dto) {
        enhanceRetrieved(entity, dto);
        // just clear unnecessary data before serialization: 
        dto.comments().setValue(null);
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
    public void convertToLease(AsyncCallback<VoidSerializable> callback, Key leadId, Key unitId) {
        ServerSideFactory.create(LeadFacade.class).convertToApplication(leadId, EntityFactory.createIdentityStub(AptUnit.class, unitId));
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void close(AsyncCallback<VoidSerializable> callback, Key leadId) {
        ServerSideFactory.create(LeadFacade.class).close(leadId);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    protected void persist(Lead dbo, Lead in) {
        // TODO ids move to created
        if (dbo.id().isNull()) {
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(dbo);
        }

        super.persist(dbo, in);
    }
}
