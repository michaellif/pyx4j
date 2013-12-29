/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2013
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.biz.financial.maintenance.yardi;

import java.util.Date;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.maintenance.MaintenanceRequest;

public class YardiMaintenanceIntegrationAgent {

    public static Date getLastModifiedDate() {
        EntityQueryCriteria<MaintenanceRequest> crit = EntityQueryCriteria.create(MaintenanceRequest.class);
        MaintenanceRequest lastReq = Persistence.service().retrieve(crit.desc(crit.proto().updated()));
        return lastReq == null ? null : lastReq.updated().getValue();
    }
}
