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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.rpc.dto.tenant.ShowingDTO;
import com.propertyvista.crm.rpc.services.customer.lead.ShowingCrudService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Showing;

public class ShowingCrudServiceImpl extends AbstractCrudServiceDtoImpl<Showing, ShowingDTO> implements ShowingCrudService {

    public ShowingCrudServiceImpl() {
        super(Showing.class, ShowingDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceRetrieved(Showing entity, ShowingDTO dto, RetrieveTarget retrieveTarget) {
        enhanceListRetrieved(entity, dto);
        retrieveUnitFilterCriteria(dto);
    }

    @Override
    protected void enhanceListRetrieved(Showing entity, ShowingDTO dto) {
        if (!dto.unit().isNull()) {
            Persistence.service().retrieve(dto.unit().building(), AttachLevel.ToStringMembers);
            Persistence.service().retrieve(dto.unit().floorplan(), AttachLevel.ToStringMembers);
        }

        Persistence.service().retrieve(dto.appointment());
        Persistence.service().retrieve(dto.appointment().lead());
    }

    @Override
    protected void create(Showing entity, ShowingDTO dto) {
        if (!entity.result().isNull()) {
            entity.status().setValue(Showing.Status.seen);
        }
        super.create(entity, dto);
    }

    @Override
    protected void save(Showing entity, ShowingDTO dto) {
        if (!entity.result().isNull()) {
            entity.status().setValue(Showing.Status.seen);
        }
        super.save(entity, dto);
    }

    @Override
    public void updateValue(AsyncCallback<AptUnit> callback, Key unitId) {
        AptUnit unit = Persistence.service().retrieve(AptUnit.class, unitId);
        Persistence.service().retrieve(unit.building(), AttachLevel.ToStringMembers);
        Persistence.service().retrieve(unit.floorplan(), AttachLevel.ToStringMembers);
        callback.onSuccess(unit);
    }

    @Override
    public void createNew(AsyncCallback<ShowingDTO> callback, Appointment parentAppointmentStub) {
        ShowingDTO newShowing = EntityFactory.create(ShowingDTO.class);
        newShowing.appointment().set(parentAppointmentStub);
        newShowing.status().setValue(Showing.Status.planned);
        retrieveUnitFilterCriteria(newShowing);
        callback.onSuccess(newShowing);
    }

    private static void retrieveUnitFilterCriteria(ShowingDTO showingDTO) {
        Appointment appointment = Persistence.secureRetrieve(Appointment.class, showingDTO.appointment().getPrimaryKey());
        Persistence.service().retrieve(appointment.lead());

        showingDTO.floorplan().set(appointment.lead().floorplan());
        showingDTO.moveInDate().setValue(appointment.lead().moveInDate().getValue());
        showingDTO.building().set(
                Persistence.service().retrieve(Building.class, appointment.lead().floorplan().building().getPrimaryKey(), AttachLevel.ToStringMembers));
    }

    @Override
    public void getActiveState(AsyncCallback<Boolean> callback, Appointment appointmentId) {
        Persistence.service().retrieve(appointmentId);
        Persistence.service().retrieve(appointmentId.lead());

        callback.onSuccess(appointmentId.status().getValue() != Appointment.Status.closed && appointmentId.lead().status().getValue() != Lead.Status.closed);
    }
}
