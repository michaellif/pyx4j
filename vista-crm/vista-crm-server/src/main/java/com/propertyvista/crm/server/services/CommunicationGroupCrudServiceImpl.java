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

import com.propertyvista.crm.rpc.services.CommunicationGroupCrudService;
import com.propertyvista.domain.communication.CommunicationGroup;
import com.propertyvista.domain.communication.CommunicationGroup.EndpointGroup;

public class CommunicationGroupCrudServiceImpl extends AbstractCrudServiceImpl<CommunicationGroup> implements CommunicationGroupCrudService {
    public CommunicationGroupCrudServiceImpl() {
        super(CommunicationGroup.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected CommunicationGroup init(InitializationData initializationData) {
        CommunicationGroup dto = super.init(initializationData);
        dto.isPredefined().setValue(false);
        return dto;
    }

    @Override
    protected void enhanceRetrieved(CommunicationGroup bo, CommunicationGroup to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);
        Persistence.ensureRetrieve(bo.portfolios(), AttachLevel.Attached);
        to.portfolios().setAttachLevel(AttachLevel.Attached);
        to.portfolios().set(bo.portfolios());

        Persistence.ensureRetrieve(bo.buildings(), AttachLevel.Attached);
        to.buildings().setAttachLevel(AttachLevel.Attached);
        to.buildings().set(bo.buildings());

        Persistence.ensureRetrieve(bo.roles(), AttachLevel.Attached);
        to.roles().setAttachLevel(AttachLevel.Attached);
        to.roles().set(bo.roles());

        to.type().set(bo.type());
        //to.scope().set(bo.scope());
        to.isPredefined().set(bo.isPredefined());
        to.name().set(bo.name());
        to.isPredefined().set(bo.isPredefined());
    }

    @Override
    protected boolean persist(CommunicationGroup bo, CommunicationGroup in) {
        boolean isNew = bo.id().isNull() || bo.isPrototype();
        if (isNew) {
            bo.isPredefined().setValue(false);
            in.isPredefined().set(bo.isPredefined());
            in.type().setValue(EndpointGroup.Custom);
        }
        bo.roles().clear();
        bo.roles().addAll(in.roles());

        bo.buildings().clear();
        bo.buildings().addAll(in.buildings());

        bo.portfolios().clear();
        bo.portfolios().addAll(in.portfolios());

        return super.persist(bo, in);
    }

    @Override
    protected void delete(CommunicationGroup group) {
        if (group.isPredefined().getValue()) {
            throw new Error("Cannot delete predefined group");
        }
        super.delete(group);

    }
}