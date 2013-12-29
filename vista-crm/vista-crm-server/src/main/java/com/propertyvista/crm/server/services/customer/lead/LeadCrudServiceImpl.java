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
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.tenant.LeadFacade;
import com.propertyvista.crm.rpc.services.customer.lead.LeadCrudService;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Lead.ConvertToLeaseAppraisal;
import com.propertyvista.domain.tenant.lead.Lead.Status;
import com.propertyvista.domain.tenant.lead.Showing;

public class LeadCrudServiceImpl extends AbstractCrudServiceImpl<Lead> implements LeadCrudService {

    public LeadCrudServiceImpl() {
        super(Lead.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected Lead init(InitializationData initializationData) {
        Lead entity = EntityFactory.create(Lead.class);
        entity.status().setValue(Status.active);
        return entity;
    }

    @Override
    protected void enhanceRetrieved(Lead bo, Lead to, RetrieveTarget retrieveTarget) {
        if (!to.floorplan().isNull()) {
            Persistence.service().retrieve(to.floorplan().building(), AttachLevel.ToStringMembers, false);
        }
        if (!to.lease().isNull()) {
            Persistence.service().retrieve(to.lease());
        }
    }

    @Override
    protected void enhanceListRetrieved(Lead entity, Lead dto) {
        enhanceRetrieved(entity, dto, null);
        // just clear unnecessary data before serialization: 
        dto.comments().setValue(null);
    }

    @Override
    public void updateValue(AsyncCallback<Floorplan> callback, Key floorplanId) {
        Floorplan item = Persistence.service().retrieve(Floorplan.class, floorplanId);
        Persistence.service().retrieve(item.building(), AttachLevel.ToStringMembers, false);
        callback.onSuccess(item);
    }

    @Override
    public void getInterestedUnits(AsyncCallback<Vector<AptUnit>> callback, Key leadId) {
        EntityQueryCriteria<Showing> criteria = new EntityQueryCriteria<Showing>(Showing.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().appointment().lead(), leadId));
        criteria.add(PropertyCriterion.ne(criteria.proto().appointment().status(), Appointment.Status.closed));
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), Showing.Status.seen));
        criteria.add(PropertyCriterion.eq(criteria.proto().result(), Showing.Result.interested));

        Vector<AptUnit> units = new Vector<AptUnit>();
        for (Showing showing : Persistence.secureQuery(criteria)) {
            if (!units.contains(showing.unit())) {
                Persistence.service().retrieve(showing.unit().building(), AttachLevel.ToStringMembers, false);
                units.add((AptUnit) showing.unit().detach());
            }
        }

        callback.onSuccess(units);
    }

    @Override
    public void convertToLeaseApprisal(AsyncCallback<ConvertToLeaseAppraisal> callback, Key leadId) {
        ConvertToLeaseAppraisal result = ConvertToLeaseAppraisal.Positive;

        EntityQueryCriteria<Appointment> criteriaApp = new EntityQueryCriteria<Appointment>(Appointment.class);
        criteriaApp.add(PropertyCriterion.eq(criteriaApp.proto().lead(), leadId));
        criteriaApp.add(PropertyCriterion.ne(criteriaApp.proto().status(), Appointment.Status.closed));
        Vector<Appointment> apps = Persistence.secureQuery(criteriaApp);
        if (apps.isEmpty()) {
            result = ConvertToLeaseAppraisal.NoAppointments;
        } else {
            EntityQueryCriteria<Showing> criteriaShw = new EntityQueryCriteria<Showing>(Showing.class);
            criteriaShw.add(PropertyCriterion.eq(criteriaShw.proto().appointment().lead(), leadId));
            criteriaShw.add(PropertyCriterion.ne(criteriaShw.proto().appointment().status(), Appointment.Status.closed));
            criteriaShw.add(PropertyCriterion.eq(criteriaShw.proto().status(), Showing.Status.seen));
            Vector<Showing> shws = Persistence.secureQuery(criteriaShw);
            if (shws.isEmpty()) {
                result = ConvertToLeaseAppraisal.NoShowings;
            }
        }

        callback.onSuccess(result);
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
        throw new Error("Facade should be used");
    }

    @Override
    protected void create(Lead dbo, Lead in) {
        ServerSideFactory.create(LeadFacade.class).init(dbo);
        ServerSideFactory.create(LeadFacade.class).persist(dbo);
    }

    @Override
    protected void save(Lead entity, Lead to) {
        ServerSideFactory.create(LeadFacade.class).persist(entity);
    }
}
