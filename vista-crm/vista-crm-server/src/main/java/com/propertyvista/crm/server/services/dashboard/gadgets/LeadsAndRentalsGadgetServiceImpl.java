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
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;

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
    public void makeLeaseFilterCriteria(AsyncCallback<EntityListCriteria<LeaseDTO>> callback, Vector<Building> buildingsFilter, String leaseFilter) {

        // TODO create a special lister for leads that displays lease

        EntityListCriteria<LeaseDTO> criteria = EntityListCriteria.create(LeaseDTO.class);
        if (buildingsFilter != null && !buildingsFilter.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().unit().building(), buildingsFilter));
        }
        callback.onSuccess(criteria);
    }

    int countLeads(Vector<Building> buildingsFilter) {
        return Persistence.service().count(leadsCriteria(EntityQueryCriteria.create(Lead.class), buildingsFilter));
    }

    int countAppointments(Vector<Building> buildingsFilter) {
        return Persistence.service().count(appointmentsCriteria(EntityQueryCriteria.create(Appointment.class), buildingsFilter));
    }

    int countRentals(Vector<Building> buildingsFilter) {
        EntityQueryCriteria<Lead> leadsCriteria = leadsCriteria(EntityQueryCriteria.create(Lead.class), buildingsFilter);

        leadsCriteria.add(PropertyCriterion.ne(leadsCriteria.proto().lease(), null));
        leadsCriteria.add(PropertyCriterion.ne(leadsCriteria.proto().lease().status(), Lease.Status.Application));

        return Persistence.service().count(leadsCriteria);
    }

    <Criteria extends EntityQueryCriteria<? extends Lead>> Criteria leadsCriteria(Criteria criteria, Vector<Building> buildingsFilter) {

        criteria.add(PropertyCriterion.le(criteria.proto().createDate(), Utils.dayOfCurrentTransaction()));
        criteria.add(PropertyCriterion.ge(criteria.proto().createDate(), Utils.beginningOfMonth(Utils.dayOfCurrentTransaction())));

        if (buildingsFilter != null && !buildingsFilter.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().floorplan().building(), buildingsFilter));
        }

        return criteria;
    }

    <Criteria extends EntityQueryCriteria<? extends Appointment>> Criteria appointmentsCriteria(Criteria criteria, Vector<Building> buildingsFilter) {

        criteria.add(PropertyCriterion.le(criteria.proto().date(), Utils.dayOfCurrentTransaction()));
        criteria.add(PropertyCriterion.ge(criteria.proto().date(), Utils.beginningOfMonth(Utils.dayOfCurrentTransaction())));

        if (buildingsFilter != null && !buildingsFilter.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().lead().floorplan().building(), buildingsFilter));
        }

        return criteria;
    }

    <Criteria extends EntityQueryCriteria<? extends Lease>> Criteria rentals(Criteria criteria, Vector<Building> buildingsFilter) {
        return criteria;
    }

}
