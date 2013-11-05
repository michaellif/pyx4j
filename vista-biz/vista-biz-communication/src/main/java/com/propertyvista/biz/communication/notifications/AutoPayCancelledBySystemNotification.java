/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 16, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.communication.notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.tenant.lease.Lease;

public class AutoPayCancelledBySystemNotification extends AbstractGroupPerBuildingNotification {

    private final Map<Lease, List<AutopayAgreement>> canceledAgreements = new HashMap<Lease, List<AutopayAgreement>>();

    public AutoPayCancelledBySystemNotification(Lease leaseId, List<AutopayAgreement> canceledAgreements) {
        super(Notification.NotificationType.AutoPayReviewRequired, leaseId);
        this.canceledAgreements.put(leaseId, new ArrayList<AutopayAgreement>(canceledAgreements));
    }

    @Override
    public boolean aggregate(AbstractNotification other) {
        if (super.aggregate(other)) {
            for (Map.Entry<Lease, List<AutopayAgreement>> me : ((AutoPayCancelledBySystemNotification) other).canceledAgreements.entrySet()) {
                if (canceledAgreements.containsKey(me.getKey())) {
                    List<AutopayAgreement> list = canceledAgreements.get(me.getKey());
                    list.addAll(me.getValue());
                } else {
                    canceledAgreements.put(me.getKey(), me.getValue());
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void send() {
        List<Employee> employees = NotificationsUtils.getNotificationTraget(getBuildingId(), Notification.NotificationType.AutoPayReviewRequired);
        if (!employees.isEmpty()) {
            ServerSideFactory.create(CommunicationFacade.class).sendAutoPayCancelledBySystemNotification(NotificationsUtils.toEmails(employees), getLeaseIds(),
                    canceledAgreements);
        }
    }

}
