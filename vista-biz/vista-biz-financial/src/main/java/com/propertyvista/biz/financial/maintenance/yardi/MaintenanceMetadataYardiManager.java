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

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.maintenance.MaintenanceMetadataAbstractManager;
import com.propertyvista.biz.system.YardiMaintenanceFacade;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;

public class MaintenanceMetadataYardiManager extends MaintenanceMetadataAbstractManager {

    private Date metaTS;

    private static class SingletonHolder {
        public static final MaintenanceMetadataYardiManager INSTANCE = new MaintenanceMetadataYardiManager();
    }

    static MaintenanceMetadataYardiManager instance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public MaintenanceRequestMetadata getMaintenanceMetadata(boolean levelsOnly) {
        Date lastMetaUpdate = ServerSideFactory.create(YardiMaintenanceFacade.class).getMetaTimestamp();
        if (metaTS == null || !metaTS.equals(lastMetaUpdate)) {
            invalidateMeta();
            metaTS = lastMetaUpdate;
        }
        return super.getMaintenanceMetadata(levelsOnly);
    }

    @Override
    protected String[] getLevels() {
        return new String[] { "Category", "SubCategory" };
    }

    @Override
    protected String getRoot() {
        return "ROOT_YARDI";
    }
}
