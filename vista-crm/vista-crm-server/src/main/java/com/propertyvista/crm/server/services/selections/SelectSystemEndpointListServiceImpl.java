/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 4, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.crm.server.services.selections;

import com.pyx4j.entity.server.AbstractListServiceImpl;

import com.propertyvista.crm.rpc.services.selections.SelectSystemEndpointListService;
import com.propertyvista.domain.communication.SystemEndpoint;

public class SelectSystemEndpointListServiceImpl extends AbstractListServiceImpl<SystemEndpoint> implements SelectSystemEndpointListService {

    public SelectSystemEndpointListServiceImpl() {
        super(SystemEndpoint.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

}
