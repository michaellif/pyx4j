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
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.communication.Message;
import com.propertyvista.portal.rpc.portal.dto.TenantDashboardDTO;
import com.propertyvista.portal.rpc.portal.services.TenantDashboardService;

public class TenantDashboardServiceImpl implements TenantDashboardService {

    @Override
    public void retrieveTenantDashboard(AsyncCallback<TenantDashboardDTO> callback) {
        TenantDashboardDTO dashboard = EntityFactory.create(TenantDashboardDTO.class);

        for (int i = 0; i <= 3; i++) {
            Message msg = dashboard.notification().$();
            msg.subject().setValue("TODO");
            msg.acknowledged().setValue(Boolean.FALSE);
            msg.type().setValue(DataGenerator.randomEnum(Message.MessageType.class));
            msg.date().setValue(DataGenerator.randomDate(100));
            dashboard.notification().add(msg);
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
