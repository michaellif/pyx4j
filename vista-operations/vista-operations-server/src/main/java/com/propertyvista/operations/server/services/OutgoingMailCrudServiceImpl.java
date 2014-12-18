/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2013
 * @author VladL
 */
package com.propertyvista.operations.server.services;

import org.apache.commons.lang.SerializationUtils;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.operations.domain.mail.OutgoingMailQueue;
import com.propertyvista.operations.rpc.dto.OutgoingMailQueueDTO;
import com.propertyvista.operations.rpc.services.OutgoingMailCrudService;

public class OutgoingMailCrudServiceImpl extends AbstractCrudServiceDtoImpl<OutgoingMailQueue, OutgoingMailQueueDTO> implements OutgoingMailCrudService {

    public OutgoingMailCrudServiceImpl() {
        super(OutgoingMailQueue.class, OutgoingMailQueueDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(OutgoingMailQueue bo, OutgoingMailQueueDTO to, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        transferMessageData(bo, to);
    }

    private void transferMessageData(OutgoingMailQueue bo, OutgoingMailQueueDTO to) {
        MailMessage mailMessage = (MailMessage) SerializationUtils.deserialize(bo.data().getValue());
        to.subject().setValue(mailMessage.getSubject());
        to.message().setValue(mailMessage.getHtmlBody());
    }

}
