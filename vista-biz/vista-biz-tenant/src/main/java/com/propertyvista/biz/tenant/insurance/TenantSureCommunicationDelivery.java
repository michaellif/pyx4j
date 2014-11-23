/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 22, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AbstractOutgoingMailQueue.MailQueueStatus;
import com.pyx4j.server.mail.MailDeliveryCallback;
import com.pyx4j.server.mail.MailDeliveryStatus;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.domain.tenant.insurance.TenantSureCommunicationHistory;
import com.propertyvista.domain.tenant.insurance.TenantSureCommunicationHistory.TenantSureMessageType;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;

public class TenantSureCommunicationDelivery implements MailDeliveryCallback {

    private static final Logger log = LoggerFactory.getLogger(TenantSureCommunicationDelivery.class);

    @Override
    public void onDeliveryCompleted(MailMessage mailMessage, MailDeliveryStatus status, MailQueueStatus mailQueueStatus, int deliveryAttemptsMade) {
        if (mailMessage != null && status == MailDeliveryStatus.Success) {
            EntityQueryCriteria<TenantSureCommunicationHistory> criteria = EntityQueryCriteria.create(TenantSureCommunicationHistory.class);
            criteria.eq(criteria.proto().messageId(), mailMessage.getMailMessageObjectId());
            TenantSureCommunicationHistory communicationHistory = Persistence.service().retrieve(criteria);
            if (communicationHistory != null) {
                communicationHistory.sent().setValue(true);
                communicationHistory.messageId().setValue(mailMessage.getHeader("Message-ID"));
                communicationHistory.messageDate().setValue(mailMessage.getHeader("Date"));
                Persistence.service().persist(communicationHistory);
            } else {
                log.error("TenantSureCommunicationHistory MailMessageObjectId {} not found", mailMessage.getMailMessageObjectId());
            }
        }
    }

    static void recordDelivery(TenantSureInsurancePolicy insurance, TenantSureMessageType messageType, String mailMessageObjectId) {
        TenantSureCommunicationHistory communicationHistory = EntityFactory.create(TenantSureCommunicationHistory.class);
        communicationHistory.insurance().set(insurance);
        communicationHistory.messageType().setValue(messageType);
        communicationHistory.messageId().setValue(mailMessageObjectId);
        Persistence.service().persist(communicationHistory);
    }

    static boolean hasDelivery(TenantSureInsurancePolicy insurance, TenantSureMessageType messageType, LogicalDate after) {
        EntityQueryCriteria<TenantSureCommunicationHistory> criteria = EntityQueryCriteria.create(TenantSureCommunicationHistory.class);
        criteria.eq(criteria.proto().insurance(), insurance);
        criteria.eq(criteria.proto().messageType(), messageType);
        criteria.eq(criteria.proto().sent(), true);
        return Persistence.service().exists(criteria);
    }

}
