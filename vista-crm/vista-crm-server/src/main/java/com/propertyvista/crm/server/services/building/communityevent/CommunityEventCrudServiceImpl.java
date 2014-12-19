/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 22, 2014
 * @author smolka
 */
package com.propertyvista.crm.server.services.building.communityevent;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.crm.rpc.services.building.communityevent.CommunityEventCrudService;
import com.propertyvista.domain.property.asset.CommunityEvent;

public class CommunityEventCrudServiceImpl extends AbstractCrudServiceImpl<CommunityEvent> implements CommunityEventCrudService {

    public CommunityEventCrudServiceImpl() {
        super(CommunityEvent.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();

    }
}
