/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 31, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.MessageCategoryCrudService;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;

public class MessageCategoryCrudServiceImpl extends AbstractCrudServiceImpl<MessageCategory> implements MessageCategoryCrudService {
    public MessageCategoryCrudServiceImpl() {
        super(MessageCategory.class);
    }

    @Override
    protected MessageCategory init(InitializationData initializationData) {
        MessageCategory dto = super.init(initializationData);
        dto.category().setValue(MessageGroupCategory.Custom);
        return dto;
    }

    @Override
    protected void enhanceRetrieved(MessageCategory bo, MessageCategory to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);
        Persistence.ensureRetrieve(bo.dispatchers(), AttachLevel.Attached);
        to.dispatchers().setAttachLevel(AttachLevel.Attached);
        to.dispatchers().set(bo.dispatchers());

        Persistence.ensureRetrieve(bo.roles(), AttachLevel.Attached);
        to.roles().setAttachLevel(AttachLevel.Attached);
        to.roles().set(bo.roles());

        to.topic().set(bo.topic());
        to.category().set(bo.category());
    }

    @Override
    protected boolean persist(MessageCategory bo, MessageCategory in) {
        boolean isNew = bo.id().isNull() || bo.isPrototype();
        if (isNew) {
            bo.category().setValue(MessageGroupCategory.Custom);
            in.topic().set(bo.topic());
        }
        bo.dispatchers().clear();
        bo.dispatchers().addAll(in.dispatchers());

        bo.roles().clear();
        bo.roles().addAll(in.roles());
        return super.persist(bo, in);
    }

    @Override
    protected void delete(MessageCategory group) {
        if (!MessageGroupCategory.Custom.equals(group.category().getValue())) {
            throw new Error("Cannot delete predefined message group");
        }
        group.deleted().setValue(true);
        Persistence.service().persist(group);

    }
}