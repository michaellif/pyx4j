/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.dto.gadgets.LeadsAndRentalsGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeadsAndRentalsGadgetService;
import com.propertyvista.crm.server.services.dashboard.util.Util;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeadsAndRentalsGadgetServiceImpl implements LeadsAndRentalsGadgetService {

    @Override
    public void countData(AsyncCallback<LeadsAndRentalsGadgetDataDTO> callback, Vector<Building> buildingsFilter) {
        LeadsAndRentalsGadgetDataDTO data = EntityFactory.create(LeadsAndRentalsGadgetDataDTO.class);

        data.leads().setValue(countLeads(buildingsFilter));
        data.appointments().setValue(countAppointments(buildingsFilter));
        data.rentals().setValue(countRentals(buildingsFilter));

        callback.onSuccess(data);
    }

    @Override
    public void makeLeadFilterCriteria(AsyncCallback<EntityListCriteria<Lead>> callback, Vector<Building> buildings, String filterPreset) {
        callback.onSuccess(leadsCriteria(EntityListCriteria.create(Lead.class), buildings));
    }

    @Override
    public void makeAppointmentsCriteria(AsyncCallback<EntityListCriteria<Appointment>> callback, Vector<Building> buildingsFilter, String filterPreset) {
        callback.onSuccess(appointmentsCriteria(EntityListCriteria.create(Appointment.class), buildingsFilter));
    }

    @Override
    public void makeLeaseFromLeadCriteria(AsyncCallback<EntityListCriteria<Lead>> callback, Vector<Building> buildings, String encodedFilterPreset) {
        callback.onSuccess(leasesFromLeadsCriteria(EntityListCriteria.create(Lead.class), buildings));
    }

    int countLeads(Vector<Building> buildingsFilter) {
        EntityQueryCriteria<Lead> criteria = leadsCriteria(EntityQueryCriteria.create(Lead.class), buildingsFilter);
        Persistence.applyDatasetAccessRule(criteria);
        return Persistence.service().count(criteria);
    }

    int countAppointments(Vector<Building> buildingsFilter) {
        EntityQueryCriteria<Appointment> criteria = appointmentsCriteria(EntityQueryCriteria.create(Appointment.class), buildingsFilter);
        Persistence.applyDatasetAccessRule(criteria);
        return Persistence.service().count(criteria);
    }

    int countRentals(Vector<Building> buildingsFilter) {
        EntityQueryCriteria<Lead> criteria = leasesFromLeadsCriteria(EntityQueryCriteria.create(Lead.class), buildingsFilter);
        Persistence.applyDatasetAccessRule(criteria);
        return Persistence.service().count(criteria);
    }

    <Criteria extends EntityQueryCriteria<? extends Lead>> Criteria leadsCriteria(Criteria criteria, Vector<Building> buildingsFilter) {

        criteria.add(PropertyCriterion.le(criteria.proto().createDate(), Util.dayOfCurrentTransaction()));
        criteria.add(PropertyCriterion.ge(criteria.proto().createDate(), Util.beginningOfMonth(Util.dayOfCurrentTransaction())));

        if (buildingsFilter != null && !buildingsFilter.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().floorplan().building(), buildingsFilter));
        }

        return criteria;
    }

    <Criteria extends EntityQueryCriteria<? extends Lead>> Criteria leasesFromLeadsCriteria(Criteria criteria, Vector<Building> buildingsFilter) {
        leadsCriteria(criteria, buildingsFilter);
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().lease()));
        criteria.add(PropertyCriterion.ne(criteria.proto().lease().status(), Lease.Status.Application));

        return criteria;
    }

    <Criteria extends EntityQueryCriteria<? extends Appointment>> Criteria appointmentsCriteria(Criteria criteria, Vector<Building> buildingsFilter) {

        criteria.add(PropertyCriterion.le(criteria.proto().date(), Util.dayOfCurrentTransaction()));
        criteria.add(PropertyCriterion.ge(criteria.proto().date(), Util.beginningOfMonth(Util.dayOfCurrentTransaction())));

        if (buildingsFilter != null && !buildingsFilter.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().lead().floorplan().building(), buildingsFilter));
        }

        return criteria;
    }

    <Criteria extends EntityQueryCriteria<? extends Lease>> Criteria rentals(Criteria criteria, Vector<Building> buildingsFilter) {
        if (buildingsFilter != null && !buildingsFilter.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().unit().building(), buildingsFilter));
        }
        return criteria;
    }

}
