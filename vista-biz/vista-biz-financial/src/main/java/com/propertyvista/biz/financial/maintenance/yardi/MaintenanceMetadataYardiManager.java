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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.financial.maintenance.MaintenanceMetadataAbstractManager;
import com.propertyvista.biz.system.YardiMaintenanceFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.YardiMaintenanceMetaOrigination;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.PmcYardiCredential;

public class MaintenanceMetadataYardiManager extends MaintenanceMetadataAbstractManager {

    private Date metaTS;

    private static class SingletonHolder {
        public static final MaintenanceMetadataYardiManager INSTANCE = new MaintenanceMetadataYardiManager();
    }

    static MaintenanceMetadataYardiManager instance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public MaintenanceRequestMetadata getMaintenanceMetadata(Building building) {
        Date lastMetaUpdate = ServerSideFactory.create(YardiMaintenanceFacade.class).getMetaTimestamp(building);
        if (metaTS == null || !metaTS.equals(lastMetaUpdate)) {
            invalidateMeta(building);
            metaTS = lastMetaUpdate;
        }
        return super.getMaintenanceMetadata(building);
    }

    @Override
    protected String[] getLevels() {
        return new String[] { "Category", "SubCategory" };
    }

    @Override
    protected String getRoot() {
        return "ROOT_YARDI";
    }

    @Override
    protected String getCacheKey(Building building) {
        // Cache per yardi interface
        return MaintenanceMetadataAbstractManager.cacheKey + "-" + VistaDeployment.getPmcYardiInterfaceId(building).toString();
    }

    @Override
    protected MaintenanceRequestMetadata retrieveMeta(Building building) {
        MaintenanceRequestMetadata meta = null;
        // We store one Meta instance per Yardi interface
        PmcYardiCredential yc = VistaDeployment.getPmcYardiCredential(building);
        EntityQueryCriteria<YardiMaintenanceMetaOrigination> criteria = EntityQueryCriteria.create(YardiMaintenanceMetaOrigination.class);
        criteria.eq(criteria.proto().yardiInterfaceId(), yc.getPrimaryKey());
        YardiMaintenanceMetaOrigination metaOrig = Persistence.service().retrieve(criteria);
        if (metaOrig != null) {
            Persistence.service().retrieve(metaOrig.metadata());
            // need to detach meta to make it accessible to client over the wire
            return metaOrig.metadata().detach();
        } else {
            // very first time - create empty meta entity that will be populated by yardi maintenance service
            meta = EntityFactory.create(MaintenanceRequestMetadata.class);
            meta.rootCategory().set(EntityFactory.create(MaintenanceRequestCategory.class));
            meta.rootCategory().name().setValue(getRoot());
            Persistence.service().persist(meta.rootCategory());
            Persistence.service().persist(meta);
            // add origination record
            metaOrig = EntityFactory.create(YardiMaintenanceMetaOrigination.class);
            metaOrig.metadata().set(meta);
            metaOrig.yardiInterfaceId().setValue(yc.getPrimaryKey());
            Persistence.service().persist(metaOrig);
            Persistence.service().commit();
        }
        return meta;
    }
}
