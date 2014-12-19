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
 */
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.MessageCategoryCrudService;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;

public class MessageCategoryCrudServiceImpl extends AbstractCrudServiceImpl<MessageCategory> implements MessageCategoryCrudService {
    public MessageCategoryCrudServiceImpl() {
        super(MessageCategory.class);
    }

    @Override
    protected MessageCategory init(InitializationData initializationData) {
        MessageCategory dto = super.init(initializationData);
        dto.categoryType().setValue(CategoryType.Message);
        return dto;
    }

    @Override
    protected void enhanceRetrieved(MessageCategory bo, MessageCategory to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);
        enhanceDbo(bo, to);
    }

    private void enhanceDbo(MessageCategory bo, MessageCategory to) {
        Persistence.ensureRetrieve(bo.dispatchers(), AttachLevel.Attached);
        to.dispatchers().setAttachLevel(AttachLevel.Attached);
        to.dispatchers().set(bo.dispatchers());

        Persistence.ensureRetrieve(bo.roles(), AttachLevel.Attached);
        to.roles().setAttachLevel(AttachLevel.Attached);
        to.roles().set(bo.roles());

        to.category().set(bo.category());
        to.categoryType().set(bo.categoryType());
    }

    @Override
    protected void enhanceListRetrieved(MessageCategory entity, MessageCategory dto) {
        enhanceDbo(entity, dto);
    }

    @Override
    protected boolean persist(MessageCategory bo, MessageCategory in) {
        boolean isNew = bo.id().isNull() || bo.isPrototype();
        if (isNew) {
            in.category().set(bo.category());
        }
        bo.dispatchers().clear();
        bo.dispatchers().addAll(in.dispatchers());

        bo.roles().clear();
        bo.roles().addAll(in.roles());
        return super.persist(bo, in);
    }

    @Override
    protected void delete(MessageCategory group) {
        if (CategoryType.Ticket.equals(group.categoryType().getValue())) {
            throw new Error("Cannot delete predefined message group");
        }
        group.deleted().setValue(true);
        Persistence.service().persist(group);

    }
}