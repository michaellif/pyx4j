/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 5, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import java.util.List;

import com.pyx4j.entity.core.AttachLevel;

import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.domain.communication.SystemEndpoint;
import com.propertyvista.domain.communication.SystemEndpoint.SystemEndpointName;
import com.propertyvista.domain.company.Employee;

public class CommunicationMessageFacadeImpl implements CommunicationMessageFacade {

    @Override
    public MessageCategory getCommunicationGroupFromCache(MessageGroupCategory mgCategory) {
        return MessageGroupManager.instance().getCommunicationGroupFromCache(mgCategory);
    }

    @Override
    public SystemEndpoint getSystemEndpointFromCache(SystemEndpointName sep) {
        return SystemEndpointManager.instance().getSystemEndpointFromCache(sep);
    }

    @Override
    public List<MessageCategory> getDispatchedGroups(Employee employee, AttachLevel attachLevel) {
        return MessageGroupManager.instance().getDispatchedGroups(employee, attachLevel);
    }
}
