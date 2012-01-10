/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services;

import java.sql.Time;
import java.util.Date;
import java.util.GregorianCalendar;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.portal.rpc.portal.dto.MessageDTO;
import com.propertyvista.portal.rpc.portal.dto.ReservationDTO;
import com.propertyvista.portal.rpc.portal.dto.TenantDashboardDTO;
import com.propertyvista.portal.rpc.portal.services.TenantDashboardService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.portal.server.ptapp.util.Converter;
import com.propertyvista.server.common.security.VistaContext;

public class TenantDashboardServiceImpl implements TenantDashboardService {
    CrmUser currentUser = VistaContext.getCurrentUser();

    @Override
    public void retrieveTenantDashboard(AsyncCallback<TenantDashboardDTO> callback) {
        TenantDashboardDTO dashboard = EntityFactory.create(TenantDashboardDTO.class);

        // get tenant
        EntityQueryCriteria<Tenant> crit = EntityQueryCriteria.create(Tenant.class);
        crit.add(PropertyCriterion.eq(crit.proto().user(), VistaContext.getCurrentUser()));
        Tenant tenant = Persistence.service().retrieve(crit);

        Persistence.service().retrieve(tenant.user());
        dashboard.general().tenantName().set(tenant.user().name());

        TenantInLease tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenantInLease.lease());
        Persistence.service().retrieve(tenantInLease.lease().unit());
        Persistence.service().retrieve(tenantInLease.lease().unit().floorplan());
        Persistence.service().retrieve(tenantInLease.lease().unit().belongsTo());

        dashboard.general().floorplanName().set(tenantInLease.lease().unit().floorplan().marketingName());
        AddressStructured address = tenantInLease.lease().unit().belongsTo().info().address().duplicate();
        address.suiteNumber().set(tenantInLease.lease().unit().info().number());
        dashboard.general().tenantAddress().setValue(address.getStringView());

        dashboard.general().superIntendantPhone().setValue("(416) 333-22-44");

        // TODO get Data from DB, Now we just Generate some Data now
        {
            Object[][] messages = new Object[][] {
                    { Message.MessageType.paymnetPastDue, "Overdue September payment", new GregorianCalendar(2011, 8, 28).getTime() },

                    { Message.MessageType.communication, "Your maintanance call sceduled", new GregorianCalendar(2011, 9, 28).getTime() },

                    { Message.MessageType.communication, "Your Party Room reservation request received", new GregorianCalendar(2011, 9, 28).getTime() },

                    { Message.MessageType.maintananceAlert, "Elevator Maintanance", new GregorianCalendar(2011, 9, 26).getTime() },

                    { Message.MessageType.communication, "Your maintanance call received", new GregorianCalendar(2011, 9, 26).getTime() },

                    { Message.MessageType.maintananceAlert, "Stairs Renovation", new GregorianCalendar(2011, 9, 22).getTime() } };

            for (int i = 0; i < messages.length; i++) {
                MessageDTO msg = dashboard.notifications().$();
                msg.subject().setValue((String) messages[i][1]);
                msg.acknowledged().setValue(Boolean.FALSE);
                msg.type().setValue((Message.MessageType) messages[i][0]);
                msg.date().setValue(new LogicalDate((Date) messages[i][2]));
                dashboard.notifications().add(msg);
            }
        }

        dashboard.currentBill().message().setValue("You have unpaid October Rent");
        dashboard.currentBill().paid().setValue(Boolean.FALSE);
        dashboard.currentBill().dueDate().setValue(new LogicalDate(new GregorianCalendar(2011, 9, 28).getTime()));
        dashboard.currentBill().ammount().amount().setValue(1240.);
        dashboard.currentBill().lastPayment().amount().setValue(1231.);
        dashboard.currentBill().receivedOn().setValue(new LogicalDate(new GregorianCalendar(2011, 8, 29).getTime()));

        {
            Object[][] reservations = new Object[][] {

            { ReservationDTO.Status.Submitted, "Party Room", new GregorianCalendar(2011, 9, 28).getTime() },

            { ReservationDTO.Status.Completed, "Pool", new GregorianCalendar(2011, 9, 22).getTime() },

            { ReservationDTO.Status.Approved, "Party Room", new GregorianCalendar(2011, 6, 28).getTime() } };

            for (int i = 0; i < reservations.length; i++) {
                ReservationDTO r = dashboard.reservations().$();
                r.status().setValue((ReservationDTO.Status) reservations[i][0]);
                r.description().setValue((String) reservations[i][1]);
                r.date().setValue(new LogicalDate((Date) reservations[i][2]));
                r.time().setValue(new Time(19, 00, 00));
                dashboard.reservations().add(r);
            }
        }

        // add open issues
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.in(criteria.proto().status(), MaintenanceRequestStatus.Scheduled, MaintenanceRequestStatus.Submitted));
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), tenant));
        for (MaintenanceRequest mr : Persistence.service().query(criteria.desc(criteria.proto().submited()))) {
            dashboard.maintanances().add(Converter.convert(mr));
        }

        callback.onSuccess(dashboard);
    }

    @Override
    public void acknowledgeMessage(AsyncCallback<TenantDashboardDTO> callback, Key messageId) {
        retrieveTenantDashboard(callback);
    }

}
