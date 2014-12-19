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
 */
package com.propertyvista.crm.server.services.customer.lead;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.services.customer.lead.AppointmentCrudService;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Appointment.Status;
import com.propertyvista.domain.tenant.lead.Lead;

public class AppointmentCrudServiceImpl extends AbstractCrudServiceImpl<Appointment> implements AppointmentCrudService {

    public AppointmentCrudServiceImpl() {
        super(Appointment.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected Appointment init(InitializationData initializationData) {
        Appointment entity = EntityFactory.create(Appointment.class);
        entity.status().setValue(Appointment.Status.planned);
        return entity;
    }

    @Override
    protected void enhanceRetrieved(Appointment bo, Appointment to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);
        Persistence.service().retrieve(to.lead());
    }

    @Override
    public void getActiveState(AsyncCallback<Boolean> callback, Lead leadId) {
        Persistence.service().retrieve(leadId);
        callback.onSuccess(leadId.status().getValue() != Lead.Status.closed);
    }

    @Override
    public void close(AsyncCallback<VoidSerializable> callback, String reason, Appointment appointmentId) {
        Persistence.service().retrieve(appointmentId);

        appointmentId.status().setValue(Status.closed);
        appointmentId.closeReason().setValue(reason);
        Persistence.service().merge(appointmentId);
        Persistence.service().commit();

        callback.onSuccess(null);
    }
}
