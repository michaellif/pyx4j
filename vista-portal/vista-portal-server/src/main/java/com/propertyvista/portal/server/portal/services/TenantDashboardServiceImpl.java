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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.portal.rpc.portal.dto.TenantDashboardDTO;
import com.propertyvista.portal.rpc.portal.services.TenantDashboardService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.security.VistaContext;

public class TenantDashboardServiceImpl implements TenantDashboardService {

    @Override
    public void retrieveTenantDashboard(AsyncCallback<TenantDashboardDTO> callback) {
        TenantDashboardDTO dashboard = EntityFactory.create(TenantDashboardDTO.class);

        dashboard.general().tenantName().set(VistaContext.getCurrentUser().name());

        TenantInLease tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenantInLease.lease());
        Persistence.service().retrieve(tenantInLease.lease().unit());
        Persistence.service().retrieve(tenantInLease.lease().unit().floorplan());
        Persistence.service().retrieve(tenantInLease.lease().unit().belongsTo());

        dashboard.general().floorplanName().set(tenantInLease.lease().unit().floorplan().marketingName());
        AddressStructured address = tenantInLease.lease().unit().belongsTo().info().address().cloneEntity();
        address.suiteNumber().set(tenantInLease.lease().unit().info().number());
        dashboard.general().tenantAddress().setValue(address.getStringView());

        dashboard.general().superIntendantPhone().setValue("(416) 333-22-44");

        // TODO get Data from DB, Now we just Generate some Data now
        for (int i = 0; i <= 3; i++) {
            Message msg = dashboard.notifications().$();
            msg.subject().setValue("TODO");
            msg.acknowledged().setValue(Boolean.FALSE);
            msg.type().setValue(DataGenerator.randomEnum(Message.MessageType.class));
            msg.date().setValue(DataGenerator.randomDate(100));
            dashboard.notifications().add(msg);
        }

        dashboard.currentBill().paid().setValue(Boolean.FALSE);
        dashboard.currentBill().dueDate().setValue(new LogicalDate(2011, 11, 01));
        dashboard.currentBill().ammount().amount().setValue(1240.);

        callback.onSuccess(dashboard);
    }

    @Override
    public void acknowledgeMessage(AsyncCallback<TenantDashboardDTO> callback, Key messageId) {
        retrieveTenantDashboard(callback);
    }

}
