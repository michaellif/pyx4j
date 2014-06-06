/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.security.access;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.security.InstanceAccess;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class DashboardUserInstanceAccess implements InstanceAccess {

    private static final long serialVersionUID = 7082392064409139166L;

    @Override
    public boolean allow(IEntity entity) {//@formatter:off       
        return SecurityController.checkBehavior(VistaCrmBehavior.DashboardManager) | ((entity instanceof DashboardMetadata) && ((DashboardMetadata) entity).isShared().getValue(false)); 
    }//@formatter:on
}
