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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.customer.lead.AppointmentCrudService;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Lead.Status;

public class AppointmentCrudServiceImpl extends AbstractCrudServiceImpl<Appointment> implements AppointmentCrudService {

    public AppointmentCrudServiceImpl() {
        super(Appointment.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Appointment entity, Appointment dto) {
        super.enhanceRetrieved(entity, dto);
        Persistence.service().retrieve(dto.lead());
    }

    @Override
    public void getParentState(AsyncCallback<Status> callback, Lead leadId) {
        Persistence.service().retrieve(leadId);
        callback.onSuccess(leadId.status().getValue());
    }
}
