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
 * @version $Id$
 */
package com.propertyvista.biz.tenant.communityevent;

import java.util.List;

import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.domain.property.asset.building.Building;

public interface CommunityEventFacade {
    List<CommunityEvent> getCommunityEvents(Building building);
}
